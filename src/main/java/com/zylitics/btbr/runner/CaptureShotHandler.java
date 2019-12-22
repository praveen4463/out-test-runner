package com.zylitics.btbr.runner;

import com.google.cloud.storage.Storage;
import com.zylitics.btbr.config.APICoreProperties;
import com.zylitics.btbr.model.Build;
import com.zylitics.btbr.runner.provider.ShotMetadataProvider;
import com.zylitics.btbr.shot.CaptureShotHandlerImpl;

/**
 * Handler for shot process, should be created and started after session to webdriver has been
 * successfully created. Order of actions must be following:
 * <p>
 * 1. Wait for webdriver session to acquire, once done, instantiate this handler using factory
 *    method {@link CaptureShotHandler.Factory#create}
 * </p><p>
 * 2. {@link CurrentTestCommand} should be instantiated just once and the reference should be held
 *    globally, on every new command, it should be updated rather than instantiated again, because
 *    this handler will hold a reference of it too to get latest command run updates, it will use it
 *    to create {@link com.zylitics.btbr.model.ShotMetadata} for every captured shot.
 * </p><p>
 * 3. Invoke {@link CaptureShotHandler#startShot()}
 * </p><p>
 * 4. Once webdriver session is about to delete, invoke {@link CaptureShotHandler#stopShot()}
 * </p><p>
 * 5. After webdriver session is deleted and all pending tasks are finished, invoke
 *    {@link CaptureShotHandler#blockUntilFinish()} in the end to perform a wait for shots.
 * </p>
 */
interface CaptureShotHandler {
  
  /**
   * starts taking and processing shots. Runs asynchronously and returns immediately.
   */
  void startShot();
  
  /**
   * stops taking more shots and returns immediately. Should be run 'before' DELETE command is sent
   * to webdriver.
   */
  void stopShot();
  
  /**
   * Blocks until all taken shots are processed, should be run 'after' DELETE command is sent to
   * webdriver. Preferably run this after all tasks are finished because it may block the main
   * thread for a long time.
   */
  void blockUntilFinish();
  
  interface Factory {
    
    static Factory getDefault() {
      return new CaptureShotHandlerImpl.Factory();
    }
    
    CaptureShotHandler create(APICoreProperties apiCoreProperties,
                              ShotMetadataProvider shotMetadataProvider,
                              Storage storage,
                              Build build,
                              String sessionKey,
                              String bucketSessionStorage,
                              CurrentTestCommand currentTestCommand);
  }
}
