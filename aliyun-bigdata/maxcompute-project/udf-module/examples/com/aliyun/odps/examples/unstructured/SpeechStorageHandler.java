package com.aliyun.odps.examples.unstructured;

import com.aliyun.odps.udf.Extractor;
import com.aliyun.odps.udf.OdpsStorageHandler;
import com.aliyun.odps.udf.Outputer;

public class SpeechStorageHandler extends OdpsStorageHandler {

  @Override
  public Class<? extends Extractor> getExtractorClass() {
    return SpeechSentenceSnrExtractor.class;
  }

  @Override
  public Class<? extends Outputer> getOutputerClass() {
    throw new UnsupportedOperationException();
  }
}
