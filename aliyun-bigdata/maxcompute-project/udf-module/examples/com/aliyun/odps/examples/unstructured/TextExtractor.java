package com.aliyun.odps.examples.unstructured;

import com.aliyun.odps.Column;
import com.aliyun.odps.data.ArrayRecord;
import com.aliyun.odps.data.Record;
import com.aliyun.odps.io.InputStreamSet;
import com.aliyun.odps.udf.DataAttributes;
import com.aliyun.odps.udf.ExecutionContext;
import com.aliyun.odps.udf.Extractor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Text extractor that extract schematized records from formatted plain-text(csv, tsv etc.)
 **/
public class TextExtractor extends Extractor {

  private InputStreamSet inputs;
  private String columnDelimiter;
  private DataAttributes attributes;
  private BufferedReader currentReader;
  private boolean firstRead = true;

  public TextExtractor() {
    // default to ",", this can be overwritten if a specific delimiter is provided (via DataAttributes)
    this.columnDelimiter = ",";
  }

  // no particular usage for execution context in this example
  @Override
  public void setup(ExecutionContext ctx, InputStreamSet inputs, DataAttributes attributes) {
    this.inputs = inputs;
    this.attributes = attributes;
    // check if "delimiter" attribute is supplied via SQL query
    String columnDelimiter = this.attributes.getValueByKey("delimiter");
    if ( columnDelimiter != null)
    {
      this.columnDelimiter = columnDelimiter;
    }
    System.out.println("TextExtractor using delimiter [" + this.columnDelimiter + "].");
    // note: more properties can be inited from attributes if needed
  }

  @Override
  public Record extract() throws IOException {
    String line = readNextLine();
    if (line == null) {
      return null;
    }
    return textLineToRecord(line);
  }

  @Override
  public void close(){
    // no-op
  }

  private Record textLineToRecord(String line) throws IllegalArgumentException
  {
    Column[] outputColumns = this.attributes.getRecordColumns();
    ArrayRecord record = new ArrayRecord(outputColumns);
    if (this.attributes.getRecordColumns().length != 0){
      // string copies are needed, not the most efficient one, but suffice as an example here
      String[] parts = line.split(columnDelimiter);
      int[] outputIndexes = this.attributes.getNeededIndexes();
      if (outputIndexes == null){
        throw new IllegalArgumentException("No outputIndexes supplied.");
      }
      if (outputIndexes.length != outputColumns.length){
        throw new IllegalArgumentException("Mismatched output schema: Expecting "
            + outputColumns.length + " columns but get " + parts.length);
      }
      int index = 0;
      for(int i = 0; i < parts.length; i++){
        // only parse data in columns indexed by output indexes
        if (index < outputIndexes.length && i == outputIndexes[index]){
          switch (outputColumns[index].getType()) {
            case STRING:
              record.setString(index, parts[i]);
              break;
            case BIGINT:
              record.setBigint(index, Long.parseLong(parts[i]));
              break;
            case BOOLEAN:
              record.setBoolean(index, Boolean.parseBoolean(parts[i]));
              break;
            case DOUBLE:
              record.setDouble(index, Double.parseDouble(parts[i]));
              break;
            case DATETIME:
            case DECIMAL:
            case ARRAY:
            case MAP:
            default:
              throw new IllegalArgumentException("Type " + outputColumns[index].getType() + " not supported for now.");
          }
          index++;
        }
      }
    }
    return record;
  }

  /**
   * Read next line from underlying input streams.
   * @return The next line as String object. If all of the contents of input
   * streams has been read, return null.
   */
  private String readNextLine() throws IOException {
    if (firstRead) {
      firstRead = false;
      // the first read, initialize things
      currentReader = moveToNextStream();
      if (currentReader == null) {
        // empty input stream set
        return null;
      }
    }
    while (currentReader != null) {
      String line = currentReader.readLine();
      if (line != null) {
        return line;
      }
      currentReader = moveToNextStream();
    }
    return null;
  }

  private BufferedReader moveToNextStream() throws IOException {
    InputStream stream = inputs.next();
    if (stream == null) {
      return null;
    } else {
      return new BufferedReader(new InputStreamReader(stream));
    }
  }
}