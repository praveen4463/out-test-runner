package com.zylitics.btbr.esdb;

import com.zylitics.btbr.config.APICoreProperties;
import com.zylitics.btbr.runner.provider.BulkSaveProvider;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

abstract class AbstractBulkSaveProvider<T> implements BulkSaveProvider<T> {
  
  private static final Logger LOG = LoggerFactory.getLogger(AbstractBulkSaveProvider.class);
  
  private static final int ESDB_BULK_AWAIT_CLOSE_SEC = 120;
  
  private final BulkProcessor bulkProcessor;
  
  final APICoreProperties apiCoreProperties;
  
  private volatile boolean isTurnedDown = false;
  
  AbstractBulkSaveProvider(Function<Listener, BulkProcessor> bulkProcessorSupplier,
                           APICoreProperties apiCoreProperties) {
    this.bulkProcessor = bulkProcessorSupplier.apply(new Listener());
    this.apiCoreProperties = apiCoreProperties;
  }
  
  @Override
  public void saveAsync(T obj) throws RuntimeException {
    if (isTurnedDown) {
      throwOnDown();
      return;
    }
    
    IndexRequest request;
    try {
      request = new IndexRequest(getIndex()).source(getAsXContentBuilder(obj));
      bulkProcessor.add(request);
    } catch (Exception ex) {
      closeNow();
      String msg = "Exception while creating IndexRequest for: " + obj;
      if (throwOnException()) {
        throw new RuntimeException(msg, ex);
      } else {
        LOG.error(msg, ex);
      }
    }
  }
  
  @Override
  public void processRemaining() throws RuntimeException {
    if (isTurnedDown) {
      throwOnDown();
      return;
    }
    bulkProcessor.flush();
  }
  
  @Override
  public void processRemainingAndTearDown() throws RuntimeException {
    if (isTurnedDown) {
      throwOnDown();
      return;
    }
    
    try {
      if (!bulkProcessor.awaitClose(ESDB_BULK_AWAIT_CLOSE_SEC, TimeUnit.SECONDS)) {
        String msg = "bulkProcessor couldn't complete within timeout of " +
            ESDB_BULK_AWAIT_CLOSE_SEC;
        if (throwOnException()) {
          closeNow();
          throw new RuntimeException(msg);
        } else {
          LOG.error(msg);
        }
      }
    } catch (InterruptedException i) {
      LOG.error("Thread interrupted while waiting for bulkProcessor to finish request");
    }
    closeNow(); // invoke even if close normally to mark it down.
  }
  
  private void throwOnDown() {
    if (throwIfTurnedDown()) {
      throw new RuntimeException("Already turned down, can't process request.");
    }
  }
  
  private void closeNow() {
    if (isTurnedDown) {
      return;
    }
    
    bulkProcessor.close(); // immediately closes the processor with a 0 nanosecond timeout.
    isTurnedDown = true;
  }
  
  abstract String getIndex();
  
  abstract XContentBuilder getAsXContentBuilder(T obj) throws IOException;
  
  class Listener implements BulkProcessor.Listener {
    
    @Override
    public void beforeBulk(long executionId, BulkRequest request) {
      LOG.debug("Executing bulk operation with executionId {} for total {} request. Index {}",
          executionId, request.numberOfActions(), getIndex());
    }
    
    @Override
    public void afterBulk(long executionId, BulkRequest request, BulkResponse response) {
      if (response.hasFailures()) {
        LOG.error("Bulk operation with executionId {} Index {} completed with failures, after" +
            " this error, all failures will be logged separately.", executionId, getIndex());
        for (BulkItemResponse bulkItemResponse : response) {
          if (bulkItemResponse.isFailed()) {
            BulkItemResponse.Failure failure = bulkItemResponse.getFailure();
            LOG.error(bulkItemResponse.getFailureMessage(), failure.getCause());
          }
        }
      } else {
        LOG.debug("Bulk operation with executionId {} took {} millis to finish for index {}"
            , executionId, response.getTook().getMillis(), getIndex());
      }
    }
    
    @Override
    public void afterBulk(long executionId, BulkRequest request, Throwable t) {
      LOG.error("Failed to execute bulk, closing down esdb store for index " + getIndex(), t);
      closeNow();
    }
  }
}
