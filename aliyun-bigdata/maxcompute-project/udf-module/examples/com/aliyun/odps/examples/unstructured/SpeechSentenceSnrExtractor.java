package com.aliyun.odps.examples.unstructured;

import com.aliyun.odps.Column;
import com.aliyun.odps.OdpsType;
import com.aliyun.odps.data.ArrayRecord;
import com.aliyun.odps.data.Record;
import com.aliyun.odps.io.InputStreamSet;
import com.aliyun.odps.io.SourceInputStream;
import com.aliyun.odps.udf.DataAttributes;
import com.aliyun.odps.udf.ExecutionContext;
import com.aliyun.odps.udf.Extractor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.HashMap;

public class SpeechSentenceSnrExtractor extends Extractor {
  private final static Log logger = LogFactory.getLog(SpeechSentenceSnrExtractor.class);

  private static final String MLF_FILE_ATTRIBUTE_KEY = "mlfFileName";
  private static final String SPEECH_SAMPLE_RATE_KEY = "speechSampleRateInKHz";

  private String mlfFileName;
  private HashMap<String, UtteranceLabel> utteranceLabels;
  private InputStreamSet inputs;
  private DataAttributes attributes;
  private double sampleRateInKHz;

  public SpeechSentenceSnrExtractor(){
    this.utteranceLabels = new HashMap<String, UtteranceLabel>();
  }

  @Override
  public void setup(ExecutionContext ctx, InputStreamSet inputs, DataAttributes attributes){
    this.inputs = inputs;
    this.attributes = attributes;
    this.mlfFileName = this.attributes.getValueByKey(MLF_FILE_ATTRIBUTE_KEY);
    if (this.mlfFileName == null){
      throw new IllegalArgumentException("A mlf file must be specified in extractor attribute.");
    }
    String sampleRateInKHzStr = this.attributes.getValueByKey(SPEECH_SAMPLE_RATE_KEY);
    if (sampleRateInKHzStr == null){
      throw new IllegalArgumentException("The speech sampling rate must be specified in extractor attribute.");
    }
    this.sampleRateInKHz = Double.parseDouble(sampleRateInKHzStr);
    try {
      BufferedInputStream inputStream = ctx.readResourceFileAsStream(mlfFileName);
      loadMlfLabelsFromResource(inputStream);
      inputStream.close();
    } catch (IOException e) {
      throw new RuntimeException("reading model from mlf failed with exception " + e.getMessage());
    }
  }

  @Override
  public Record extract() throws IOException {
    SourceInputStream inputStream = inputs.next();
    if (inputStream == null){
      return null;
    }

    String fileName = inputStream.getFileName();
    fileName = fileName.substring(fileName.lastIndexOf('/') + 1);
    logger.info("Processing wav file " + fileName);
    // full file path: path/to/XXX.wav => XXX as id
    String id = fileName.substring(0, fileName.lastIndexOf('.'));

    long fileSize = inputStream.getFileSize();
    if (fileSize > Integer.MAX_VALUE){
      // technically a larger file can be read via multiple batches,
      // but we simply do not support it in this example.
      throw new IllegalArgumentException("Do not support speech file larger than 2G bytes");
    }
    byte[] buffer = new byte[(int)fileSize];

    Column[] outputColumns = this.attributes.getRecordColumns();
    ArrayRecord record = new ArrayRecord(outputColumns);
    if (outputColumns.length != 2 || outputColumns[0].getType() != OdpsType.DOUBLE
        || outputColumns[1].getType() != OdpsType.STRING){
      throw new IllegalArgumentException("Expecting output to of schema double|string.");
    }
    int readSize = inputStream.readToEnd(buffer);
    inputStream.close();
    double snr = computeSnr(id, buffer, readSize);
    record.setDouble(0, snr);
    record.setString(1, id);
    logger.info(String.format("file [%s] snr computed to be [%f]db", fileName, snr));
    return record;
  }

  @Override
  public void close(){
    //no-op
  }

  private void loadMlfLabelsFromResource(BufferedInputStream fileInputStream)
      throws IOException {
    BufferedReader br = new BufferedReader(new InputStreamReader(fileInputStream));
    String line;
    String id = "";
    // here we relies on the particular format of the mlf to load labels from the file
    while ((line = br.readLine()) != null) {
      if (line.trim().isEmpty()){
        continue;
      }
      if (line.startsWith("id:")){
        id = line.split(":")[1].trim();
      }
      else{
        // in this branch, line must be the label
        this.utteranceLabels.put(id, new UtteranceLabel(id, line, " "));
      }
    }
  }

  // compute the snr of the speech sentence, assuming the input buffer contains the entire content of a wav file
  private double computeSnr(String id, byte[] buffer, int validBufferLen){
    final int headerLength = 44;
    if (validBufferLen < headerLength){
      throw new IllegalArgumentException("A wav buffer must be at least larger than standard wav header size.");
    }
    // each frame is 10 ms
    int sampleCountPerFrame = (int)this.sampleRateInKHz * 10;
    // each data point denoted by a short integer (2 bytes)
    int dataLen = (validBufferLen - headerLength) / 2;

    if (dataLen % sampleCountPerFrame != 0){
      throw new IllegalArgumentException(
          String.format("Invalid wav file where dataLen %d does not divide sampleCountPerFrame %d",
              dataLen, sampleCountPerFrame));
    }
    // total number of frames in the wav file
    int frameCount = dataLen / sampleCountPerFrame;

    UtteranceLabel utteranceLabel = this.utteranceLabels.get(id);
    if (utteranceLabel == null){
      throw new IllegalArgumentException(String.format("Cannot find label of id %s from MLF.", id));
    }
    ArrayList<Long> labels = utteranceLabel.getLabels();
    // usually frameCount should be larger than labels.size() by a small margin
    // in our sample data, this margin is 2.
    if (labels.size()  + 2 != frameCount){
      throw new IllegalArgumentException(String.format("Mismatched frame labels size % d and frameCount %d.",
          labels.size() + 2, frameCount ));
    }
    int offset = headerLength;
    short data[] = new short[sampleCountPerFrame];
    double energies[] = new double[frameCount];
    for (int i = 0; i < frameCount; i++ ){
      ByteBuffer.wrap(buffer, offset, sampleCountPerFrame * 2)
          .order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(data);
      double frameEnergy = 0;
      for (int j = 0; j < sampleCountPerFrame; j++){
        frameEnergy += data[j] * data[j];
      }
      energies[i] = frameEnergy;
      offset += sampleCountPerFrame * 2;
    }

    double averageSpeechPower = 0;
    double averageNoisePower  = 0.00000001;
    int speechframeCount = 0;
    int noiseframeCount = 0;

    for (int i = 0; i < labels.size(); i++){
      if (labels.get(i) == 0){
        averageNoisePower += energies[i];
        noiseframeCount++;
      } else {
        averageSpeechPower += energies[i];
        speechframeCount++;
      }
    }

    if (noiseframeCount > 0){
      averageNoisePower /= noiseframeCount;
    } else {
      // no noise, pure speech snr = max of 100db
      return 100;
    }

    if (speechframeCount > 0) {
      averageSpeechPower /= speechframeCount;
    } else {
      // no speech, pure noise, snr = min  of -100db
      return -100;
    }

    return 10 * Math.log10(averageSpeechPower/averageNoisePower);
  }
}


class UtteranceLabel {
  private String id; // id is the same as file name
  private ArrayList<Long> labels;
  private long labelIndex;
  private long frameCount;

  public String getId(){
    return id;
  }

  public ArrayList<Long> getLabels(){
    return this.labels;
  }

  UtteranceLabel(String id, String labelString, String labelDelimiter){
    // note: no error checking here
    this.labels = new ArrayList<Long>();
    this.id = id;
    final String[] splits = labelString.split(labelDelimiter);
    if (splits.length < 2){
      throw new InvalidParameterException("Invalid label line: at least index and length should be provided.");
    }
    this.labelIndex = Long.parseLong(splits[0]);
    this.frameCount = Long.parseLong(splits[1]);
    if (splits.length != frameCount + 2){
      throw new InvalidParameterException("Label length mismatches label header meta.");
    }
    for (int i = 2; i < splits.length; i++){
      long label = Long.parseLong(splits[i]);
      // normalize vector entry to denote voice/non-voice, we only need this for snr computation
      if (label >= 2057 && label <= 2059){
        label = 0;
      } else {
        label = 1;
      }
      labels.add(label);
    }
  }
}