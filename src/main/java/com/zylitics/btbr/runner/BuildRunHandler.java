package com.zylitics.btbr.runner;

import com.google.cloud.storage.Storage;
import com.google.common.base.Preconditions;
import com.zylitics.btbr.SecretsManager;
import com.zylitics.btbr.config.APICoreProperties;
import com.zylitics.btbr.http.RequestBuildRun;
import com.zylitics.btbr.model.*;
import com.zylitics.btbr.runner.provider.*;
import com.zylitics.btbr.util.CallbackOnlyPrintStream;
import com.zylitics.btbr.util.DateTimeUtil;
import com.zylitics.btbr.webdriver.logs.WebdriverLogHandler;
import com.zylitics.zwl.antlr4.StoringErrorListener;
import com.zylitics.zwl.api.ZwlApi;
import com.zylitics.zwl.api.ZwlWdTestProperties;
import com.zylitics.zwl.exception.ZwlLangException;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.io.PrintStream;
import java.nio.file.Path;
import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class BuildRunHandler {
  
  private static final Logger LOG = LoggerFactory.getLogger(BuildRunHandler.class);
  
  private final RequestBuildRun requestBuildRun;
  
  private final APICoreProperties apiCoreProperties;
  private final APICoreProperties.Webdriver wdProps;
  private final Storage storage;
  
  // db providers
  private final BuildProvider buildProvider;
  private final BuildStatusProvider buildStatusProvider;
  private final ImmutableMapProvider immutableMapProvider;
  
  // esdb providers
  private final ZwlProgramOutputProvider zwlProgramOutputProvider;
  
  // retrieved data
  private final Build build;
  private final BuildCapability buildCapability;
  private final List<TestVersion> testVersions;
  
  // handlers
  private final CaptureShotHandler captureShotHandler;
  private final VMDeleteHandler vmDeleteHandler;
  private final WebdriverLogHandler webdriverLogHandler;
  private final LocalAssetsToCloudHandler localAssetsToCloudHandler;
  
  // started webdriver
  private final RemoteWebDriver driver;
  
  private final Path buildDir;
  
  private final PrintStream printStream;
  
  // providers
  private final ExceptionTranslationProvider exceptionTranslationProvider;
  
  // clock
  private final Clock clock;
  
  // -----------program state start---------------
  private final CurrentTestVersion currentTestVersion = new CurrentTestVersion();
  private final Map<Integer, TestStatus> testVersionsStatus = new HashMap<>();
  
  /*
  it's fine to have a single storingErrorListener for all test versions, even if more than one
  test version fails, fields gets reset with consecutive failure, this happens because we run
  tests in sequence never parallel.
   */
  private final StoringErrorListener storingErrorListener = new StoringErrorListener();
  
  // instants
  private Instant lastLogCheckAt;
  private Instant lastBuildStatusLineUpdateAt;
  
  // timeouts for later resetting
  private final int storedPageLoadTimeout;
  private final int storedScriptTimeout;
  private final int storedElementAccessTimeout;
  // -----------program state ends----------------
  
  private BuildRunHandler(RequestBuildRun requestBuildRun,
                          APICoreProperties apiCoreProperties,
                          SecretsManager secretsManager,
                          Storage storage,
                          BuildProvider buildProvider,
                          BuildStatusProvider buildStatusProvider,
                          BuildVMProvider buildVMProvider,
                          ImmutableMapProvider immutableMapProvider,
                          ShotMetadataProvider shotMetadataProvider,
                          ZwlProgramOutputProvider zwlProgramOutputProvider,
                          Build build,
                          List<TestVersion> testVersions,
                          CaptureShotHandler.Factory captureShotHandlerFactory,
                          RemoteWebDriver driver,
                          Path buildDir) {
    this(requestBuildRun,
        apiCoreProperties,
        storage,
        buildProvider,
        buildStatusProvider,
        immutableMapProvider,
        shotMetadataProvider,
        zwlProgramOutputProvider,
        build,
        testVersions,
        captureShotHandlerFactory,
        new VMDeleteHandler(apiCoreProperties, secretsManager, buildVMProvider),
        new WebdriverLogHandler(driver, apiCoreProperties.getWebdriver(),
            build.getBuildCapability(), buildDir),
        new LocalAssetsToCloudHandler(apiCoreProperties.getWebdriver(), storage, buildDir),
        driver,
        buildDir,
        Clock.systemUTC());
  }
  
  BuildRunHandler(RequestBuildRun requestBuildRun,
                  APICoreProperties apiCoreProperties,
                  Storage storage,
                  BuildProvider buildProvider,
                  BuildStatusProvider buildStatusProvider,
                  ImmutableMapProvider immutableMapProvider,
                  ShotMetadataProvider shotMetadataProvider,
                  ZwlProgramOutputProvider zwlProgramOutputProvider,
                  Build build,
                  List<TestVersion> testVersions,
                  CaptureShotHandler.Factory captureShotHandlerFactory,
                  VMDeleteHandler vmDeleteHandler,
                  WebdriverLogHandler webdriverLogHandler,
                  LocalAssetsToCloudHandler localAssetsToCloudHandler,
                  RemoteWebDriver driver,
                  Path buildDir,
                  Clock clock) {
    this.requestBuildRun = requestBuildRun;
    this.apiCoreProperties = apiCoreProperties;
    wdProps = apiCoreProperties.getWebdriver();
    this.storage = storage;
    this.buildProvider = buildProvider;
    this.buildStatusProvider = buildStatusProvider;
    this.immutableMapProvider = immutableMapProvider;
    this.zwlProgramOutputProvider = zwlProgramOutputProvider;
    this.build = build;
    buildCapability = build.getBuildCapability();
    this.testVersions = testVersions;
    captureShotHandler = captureShotHandlerFactory.create(apiCoreProperties,
        shotMetadataProvider,
        storage,
        build,
        driver.getSessionId().toString(),
        buildCapability.getShotBucketSessionStorage(),
        currentTestVersion);
    this.vmDeleteHandler = vmDeleteHandler;
    this.webdriverLogHandler = webdriverLogHandler;
    this.localAssetsToCloudHandler = localAssetsToCloudHandler;
    this.driver = driver;
    this.buildDir = buildDir;
    printStream = new CallbackOnlyPrintStream(this::sendOutput);
    exceptionTranslationProvider = new ExceptionTranslationProvider(storingErrorListener);
    this.clock = clock;
    storedPageLoadTimeout = buildCapability.getWdTimeoutsPageLoad();
    storedScriptTimeout = buildCapability.getWdTimeoutsScript();
    storedElementAccessTimeout = buildCapability.getWdTimeoutsElementAccess();
  }
  
  void handle() {
    Thread.currentThread().setUncaughtExceptionHandler((t, e) -> LOG.error(e.getMessage(), e));
    boolean stopOccurred = false;
    LOG.debug("Build should start shortly");
    try {
      run();
    } catch(Throwable t) {
      LOG.debug("An exception was thrown while running the build {}.{}",
          t.getClass().getSimpleName(), t.getMessage());
      LOG.error(t.getMessage(), t);
      if (t instanceof StopRequestException) {
        stopOccurred = true;
        // a stop has arrived while the build was running
        updateBuildStatusOnStop();
      } else {
        updateBuildStatusOnError();
      }
    } finally {
      LOG.debug("Finishing the build");
      onBuildFinish(stopOccurred);
    }
  }
  
  private void run() {
    LOG.debug("Initializing ZwlWdTestProperties");
    // get ZwlWdTestProperties
    ZwlWdTestProperties zwlWdTestProperties = new ZwlWdTestPropertiesImpl(
        wdProps,
        storage,
        build,
        driver,
        printStream,
        immutableMapProvider.getMapFromTable("zwl_preferences").orElse(null),
        buildDir,
        immutableMapProvider.getMapFromTable("zwl_globals").orElse(null));
    
    // let's start the build
    boolean firstTest = true;
    for (TestVersion testVersion : testVersions) {
      LOG.debug("Starting testVersion {}", getTestVersionIdentifier(testVersion));
      if (!firstTest) {
        LOG.debug("Going to sanitize before running next test");
        // sanitize only after the first version is completed
        sanitizeBetweenTests();
      }
      // keep it after 'sanitizeBetweenTests' cause it sets new test versions that need to be
      // set after extra windows are closed and blank url and page is shown so that shots can start
      // new version with blank screen.
      onTestVersionStart(testVersion);
      if (firstTest) {
        LOG.debug("Going to perform one time actions during testVersion(s) run");
        // run only for the first time, keep it after 'onTestVersionStart' as this starts shot
        // process that need test version detail.
        onBuildStart();
        firstTest = false;
      }
      String code = testVersion.getZwlProgram().getCode();
      LOG.debug("Going to run the code {} for testVersion {}", code,
          getTestVersionIdentifier(testVersion));
      ZwlApi zwlApi = new ZwlApi(code, Collections.singletonList(storingErrorListener));
      try {
        // handle exceptions only while reading the code, other exceptions will be relayed to
        // handle()
        zwlApi.interpret(zwlWdTestProperties,
            z -> z.setLineChangeListener(this::onZwlProgramLineChanged));
      } catch (Throwable t) {
        LOG.debug("An exception occurred while running testVersion {}: {}.{}",
            getTestVersionIdentifier(testVersion), t.getClass().getSimpleName(), t.getMessage());
        onTestVersionFailed(testVersion, t);
        // try to run other versions only when the exception is a ZwlLangException, cause it's very
        // unlikely any other test will pass when there is a problem in our application that caused
        // an unknown exception.
        if (t instanceof ZwlLangException && !buildCapability.isBuildAbortOnFailure()) {
          LOG.debug("Will continue running from next testVersion after an error in {}",
              getTestVersionIdentifier(testVersion));
          // when we continue, log the exception.
          LOG.error(t.getMessage(), t);
          continue;
        }
        LOG.debug("Will not continue to next testVersion, throwing exception");
        throw t; // handle() will catch it
      }
      LOG.debug("testVersion {} was successful", getTestVersionIdentifier(testVersion));
      onTestVersionSuccess(testVersion);
    }
    // once build is completed, even with errors, handle() will take care of it.
  }
  
  // order of actions matter, they are in priority
  private void onZwlProgramLineChanged(int currentLine) {
    LOG.debug("onZwlProgramLineChanged invoked for line {}", currentLine);
    // check if we can't move forward
    if (Thread.currentThread().getName().equals(
        RunnerController.STOPPED_BUILD_MAIN_THREAD_STARTS_WITH + build.getBuildId())) {
      LOG.debug("A stop request has arrived while running {}",
          currentTestVersion.getTestVersionId());
      // a stop request arrived, handle() will catch the thrown exception.
      throw new StopRequestException("A STOP was requested");
    }
    
    // set line to currentTestVersion so that shots process can take it.
    currentTestVersion.setControlAtLineInProgram(currentLine);
    
    // push build status line update after a delay
    if (ChronoUnit.MILLIS.between(lastBuildStatusLineUpdateAt, clock.instant()) >=
        apiCoreProperties.getRunner().getUpdateLineBuildStatusAfter()) {
      LOG.debug("Pushing a line update for testVersion {}, line {}",
          currentTestVersion.getTestVersionId(), currentLine);
      int result = buildStatusProvider.updateLine(new BuildStatusUpdateLine(build.getBuildId(),
          currentTestVersion.getTestVersionId(), currentLine));
      validateSingleRowDbCommit(result);
      // reset to current instant
      lastBuildStatusLineUpdateAt = clock.instant();
    }
    
    // !! I don't think we need a line change message to be pushed as all webdriver functions
    // push a message on begin with line number, but if required do it from here.
    
    // for webdriver logs, check if sufficient time has been passed since we last captured logs, if
    // so capture them again
    if (ChronoUnit.MILLIS.between(lastLogCheckAt, clock.instant()) >=
        wdProps.getWaitBetweenLogsCapture()) {
      LOG.debug("Capturing logs in onZwlProgramLineChanged");
      webdriverLogHandler.capture();
      // reset to current instant
      lastLogCheckAt = clock.instant();
    }
  }
  
  private void sanitizeBetweenTests() {
    if (buildCapability.isBuildAetKeepSingleWindow()) {
      LOG.debug("Cleaning up opened windows, maximizing browser..");
      // delete any open windows and leave just one with about:blank, delete all cookies before
      // reading new test
      List<String> winHandles = new ArrayList<>(driver.getWindowHandles());
      LOG.debug("Found total windows {}, will close extra windows and keep single",
          winHandles.size());
      for (int i = 0; i < winHandles.size(); i++) {
        driver.switchTo().window(winHandles.get(i));
        if (i < winHandles.size() - 1) {
          driver.close();
        }
      }
      // maximizing and resetting url takes affect only when keep single window is true.
      if (buildCapability.isWdBrwStartMaximize()) {
        LOG.debug("Maximizing the window");
        driver.manage().window().maximize();
      }
      if (buildCapability.isBuildAetUpdateUrlBlank()) {
        LOG.debug("Setting up blank url to window");
        driver.get("about:blank"); // "about local scheme" can be given to 'get' per webdriver spec
      }
    }
    if (buildCapability.isBuildAetDeleteAllCookies()) {
      LOG.debug("Deleting all cookies");
      driver.manage().deleteAllCookies(); // delete all cookies
    }
    if (buildCapability.isBuildAetResetTimeouts()) {
      LOG.debug("Resetting timeouts");
      // rest driver timeouts to their default
      driver.manage().timeouts().pageLoadTimeout(wdProps.getDefaultTimeoutPageLoad(),
          TimeUnit.MILLISECONDS);
      driver.manage().timeouts().setScriptTimeout(wdProps.getDefaultTimeoutScript(),
          TimeUnit.MILLISECONDS);
      // reset build capability timeouts to the stored timeouts
      buildCapability.setWdTimeoutsElementAccess(storedElementAccessTimeout);
      buildCapability.setWdTimeoutsPageLoad(storedPageLoadTimeout);
      buildCapability.setWdTimeoutsScript(storedScriptTimeout);
    }
  }
  
  // Order is precise, db interactions are not top so that if it fails, we don't mark the version
  // Running
  private void onTestVersionStart(TestVersion testVersion) {
    LOG.debug("onTestVersionStart invoked for testVersion {}",
        getTestVersionIdentifier(testVersion));
    
    validateSingleRowDbCommit(buildStatusProvider.saveOnStart(
        new BuildStatusSaveOnStart(build.getBuildId(), testVersion.getTestVersionId(),
            TestStatus.RUNNING, DateTimeUtil.getCurrentUTC())));
  
    // set the line to 0 when a new version starts, we do this after test is sanitize and just
    // one window is there with blank url, thus it's safe to change the version. It's ok if a few
    // shots go with line 0 as the test has not really yet started, once it has started line would
    // already have changed.
    currentTestVersion.setTestVersionId(testVersion.getTestVersionId())
        .setControlAtLineInProgram(0);
  
    testVersionsStatus.put(testVersion.getTestVersionId(), TestStatus.RUNNING);
    
    printStream.println("Executing test version " + getTestVersionIdentifier(testVersion));
    
    LOG.debug("onTestVersionStart completed for testVersion {}",
        getTestVersionIdentifier(testVersion));
  }
  
  // do things that require only one time execution/invocation on build start
  private void onBuildStart() {
    LOG.debug("onBuildStart invoked");
    // maximize the driver window if user didn't say otherwise
    if (buildCapability.isWdBrwStartMaximize()) {
      LOG.debug("Maximizing browser window");
      driver.manage().window().maximize();
    }
    
    // begin capturing shot
    LOG.debug("Starting shots capture");
    captureShotHandler.startShot();
    
    // assign current instant to log capture instant, so that log capture waits for sometime
    // from now before trying capturing.
    lastLogCheckAt = clock.instant();
    
    // assign an instant so that first time line update go without any wait
    lastBuildStatusLineUpdateAt = clock.instant()
        .minusMillis(apiCoreProperties.getRunner().getUpdateLineBuildStatusAfter());
  }
  
  private void onTestVersionFailed(TestVersion testVersion, Throwable t) {
    LOG.debug("onTestVersionFailed invoked for testVersion {}, exception {}",
        getTestVersionIdentifier(testVersion),
        t.getMessage());
    // we do this to make sure the version we're marking error was first marked running and
    // actually had an entry in BuildStatus
    validateTestVersionRunning(testVersion);
    
    String exMessage = exceptionTranslationProvider.get(t);
    LOG.debug("Translated error message is {}", exMessage);
    // update build status
    updateBuildStatus(testVersion.getTestVersionId(), TestStatus.ERROR, exMessage);
    
    // once a version's execution is done, push a message, don't use printStream as we need to send
    // another argument.
    String outputMsg = "Exception occurred during execution of test version " +
        getTestVersionIdentifier(testVersion);
    sendOutput(outputMsg + ":\n" + exMessage, true);
    
    // Now mark this test version as error
    testVersionsStatus.put(testVersion.getTestVersionId(), TestStatus.ERROR);
    LOG.debug("current testVersionStatus is {}", testVersionsStatus);
  }
  
  private void onTestVersionSuccess(TestVersion testVersion) {
    LOG.debug("onTestVersionSuccess invoked for testVersion {}",
        getTestVersionIdentifier(testVersion));
    // we do this to make sure the version we're marking success was first marked running and
    // actually had an entry in BuildStatus
    validateTestVersionRunning(testVersion);
    
    // update build status
    updateBuildStatus(testVersion.getTestVersionId(), TestStatus.SUCCESS, null);
    
    // once a version's execution is done, push a message, don't use printStream as we need to send
    // another argument.
    sendOutput("Completed execution for test version " + getTestVersionIdentifier(testVersion),
        true);
    
    // Now mark this test version as completed
    testVersionsStatus.put(testVersion.getTestVersionId(), TestStatus.SUCCESS);
  }
  
  private void validateTestVersionRunning(TestVersion testVersion) {
    LOG.debug("Validating testVersion {} is actually in {} state",
        getTestVersionIdentifier(testVersion), TestStatus.RUNNING);
    TestStatus currentStatus = testVersionsStatus.get(testVersion.getTestVersionId());
    Preconditions.checkNotNull(currentStatus, "testVersionId " + testVersion.getTestVersionId() +
        " doesn't have a state right now");
    
    // validate the version we're marking as success was actually RUNNING
    if (currentStatus != TestStatus.RUNNING) {
      throw new RuntimeException(String.format("Can't change state of testVersionId: %s because" +
              "  it's not in RUNNING status. testVersionsStatus: %s",
          testVersion.getTestVersionId(), testVersionsStatus));
    }
  }
  
  // none of the task should throw exception so that next one can run
  // all tasks should run independent of the result of any of previous task
  // !! the order of execution is precise and based on priority
  // The first priorities are shots and output because these are the things user may be watching
  // while test is running and should be committed asap. Capturing logs can wait as it's not needed
  // in real time. Quitting the driver can happen late as it doesn't matter even if the browser
  // window is left open, we're not running anything after reaching this step anyway.
  /*
  TODO: Sometimes when last test version finishes, it's very last commands might not have resulted
   in browser render yet, for example code tries to change something on page, webdriver detects the
   change but hasn't rendered the change yet. Post last version, shots are stopped immediately, thus
   the final render may have left from capturing. I feel we need to wait for sometime before
   stopping the shots so that any unrendered change has rendered and captured for user's eyes.
   Right now, I can only think of a raw wait for some random time because we can't know what needs
   to be rendered to wait for. For now, let's not add any wait, but if you notice this happening
   to user's production tests, add it just before stopping the shots, perhaps 2-5 seconds is enough.
   I could've also stopped the shots after
   */
  private void onBuildFinish(boolean stopOccurred) {
    LOG.debug("onBuildFinish was invoked");
    
    // update build, very quick
    updateBuildOnFinish(stopOccurred);
    
    // flush program output, blocks.
    LOG.debug("pushing program output and waiting");
    zwlProgramOutputProvider.processRemainingAndTearDown();
    // blocks until all output is pushed, should take lesser time than waiting for shots to process
    // because we push output in small bulk while shots in larger. Also shots needs to go into
    // cloud but this is just text.
  
    /*
    Reason why shots are stopped after pushing output and not in beginning:
    Sometimes when last test version finishes, it's very last commands might not have resulted
    in browser render yet, for example code tries to change something on page, webdriver detects the
    change but hasn't rendered the change yet. Post last version, if shots are stopped immediately,
    the final render may have left from capturing. I feel we need to wait for sometime before
    stopping the shots so that any unrendered change has rendered and captured for user's eyes.
    This may be a perfect place to do so, without having to put a raw wait, while program output is
    being saved into ESDB, shots may continue to capture the opened browser window. This may
    result in some redundant shots when program output saving takes more time than expected and
    shots continue to come of the same browser state due to everything already loaded. I am
    expecting ESDB output save to be quick thus preventing lot of redundant shots.
    TODO: Keep an eye here
     */
    // stop shots
    LOG.debug("Shots are going to stop");
    captureShotHandler.stopShot(); // takes no time
    
    // flush shots, may block long time.
    LOG.debug("pushing shots and waiting");
    captureShotHandler.blockUntilFinish();
    
    // capture logs final time before quit
    LOG.debug("capturing logs one last time");
    webdriverLogHandler.capture();
    
    // quit the driver.
    LOG.debug("Quitting the driver");
    driver.quit();
    
    // store logs
    LOG.debug("storing capture logs to cloud");
    localAssetsToCloudHandler.store();
    
    // delete VM
    LOG.debug("deleting the VM");
    vmDeleteHandler.delete(build.getBuildVMId(), requestBuildRun.getVmDeleteUrl());
  }
  
  private void updateBuildOnFinish(boolean stopOccurred) {
    LOG.debug("updateBuildOnFinish was invoked");
    boolean isSuccess = false;
    String exMsg = null;
    boolean allSuccess = testVersionsStatus.values().stream()
        .allMatch(e -> e == TestStatus.SUCCESS);
    if (allSuccess && testVersionsStatus.keySet().containsAll(testVersions.stream()
        .map(TestVersion::getTestVersionId).collect(Collectors.toList()))) {
      isSuccess = true;
    } else {
      if (stopOccurred) {
        exMsg = "A STOP request was issued";
      } else if (currentTestVersion.getTestVersionId() == 0) {
        exMsg = "Unexpected exception occurred before any test version could start running";
      } else {
        exMsg = "An exception occurred, check test version(s) of this build for details";
      }
    }
    LOG.debug("was the build succeeded? {}, if no, the derived error is {}", isSuccess, exMsg);
    // don't throw an exception from here
    try {
      validateSingleRowDbCommit(buildProvider.updateOnComplete(new BuildUpdateOnComplete(
          build.getBuildId(), DateTimeUtil.getCurrentUTC(), isSuccess, exMsg)));
    } catch (Throwable t) {
      LOG.error(t.getMessage(), t);
    }
  }
  
  private void updateBuildStatusOnStop() {
    LOG.debug("updateBuildStatusOnStop was invoked");
    Optional<Integer> running = getRunningVersion();
    if (!running.isPresent()) {
      // when a stop comes, the current thread's name change is checked during onLineChange handler,
      // which can be invoked during a program run, thus some version must be running on STOP.
      LOG.error("A running test wasn't found on STOP, this should be present");
    } else {
      updateBuildStatus(running.get(), TestStatus.STOPPED, "Forcefully stopped while running");
    }
    saveTestVersionsNotRun(TestStatus.STOPPED);
    // status is not aborted, because when stop was requested, all tests in queue were also forced
    // to stop, this is an explicit request rather than implicit error that causes abort.
  }
  
  private void updateBuildStatusOnError() {
    LOG.debug("updateBuildStatusOnError was invoked");
    Optional<Integer> running = getRunningVersion();
    running.ifPresent(t -> {
      // I don't expect a Running version while an exception occurs because when it happens,
      // onTestVersionFailed runs that does all updates and update the status ERROR, thus log this
      // to keep a watch if this happens.
      // Although onTestVersionStart and onBuildStart run after marking the current version as
      // Running and before actually submitting code to interpreter but there is nothing that can
      // fail (atleast at the time of writing this), onTestVersionStart's db calls are placed before
      // marking version as Running, thus it's very unlikely this may happen and not being
      // handled.
      LOG.error("A running test is found on exception whereas it should've already processed");
    });
    saveTestVersionsNotRun(TestStatus.ABORTED);
  }
  
  private Optional<Integer> getRunningVersion() {
    List<Integer> all = testVersionsStatus.entrySet().stream()
        .filter(e -> e.getValue() == TestStatus.RUNNING).map(Map.Entry::getKey)
        .collect(Collectors.toList());
    // shouldn't happen but still log
    if (all.size() > 1) {
      LOG.error("There are more than one running versions found, testVersionsStatus: "
          + testVersionsStatus);
    }
    return all.size() == 1 ? Optional.of(all.get(0)) : Optional.empty();
  }
  
  // end date, start date, error are null for tests that couldn't run. status could be either
  // ABORTED or STOPPED
  private void saveTestVersionsNotRun(TestStatus status) {
    LOG.debug("Going to save testVersions couldn't run and assigning status {}", status);
    testVersions.forEach(t -> {
      if (!testVersionsStatus.containsKey(t.getTestVersionId())) {
        LOG.debug("testVersionId {} couldn't be run, saving", t.getTestVersionId());
        validateSingleRowDbCommit(buildStatusProvider.saveWontStart(
            new BuildStatusSaveWontStart(build.getBuildId(), t.getTestVersionId(), status)));
      }
    });
  }
  
  private void updateBuildStatus(int testVersionId, TestStatus status, @Nullable String error) {
    LOG.debug("Updating buildStatus for testVersionId {} to status {}, error {}", testVersionId,
        status, error);
    validateSingleRowDbCommit(buildStatusProvider.updateOnEnd(new BuildStatusUpdateOnEnd(
        build.getBuildId(), testVersionId, status, DateTimeUtil.getCurrentUTC(), error)));
  }
  
  private void sendOutput(String message) {
    sendOutput(message, false);
  }
  
  // Runner should push a message with versionEndedMessage=true when it has fully executed a test
  // version, something like "Completed execution for test version <name>"
  private void sendOutput(String message, boolean versionEndedMessage) {
    LOG.debug("Sending output message {}, last message for version? {}", message,
        versionEndedMessage);
    ZwlProgramOutput zwlProgramOutput = new ZwlProgramOutput()
        .setBuildId(build.getBuildId())
        .setTestVersionId(currentTestVersion.getTestVersionId())
        .setOutput(message)
        .setCreateDate(DateTimeUtil.getCurrentUTC())
        .setEnded(versionEndedMessage);
    zwlProgramOutputProvider.saveAsync(zwlProgramOutput);
  }
  
  private void validateSingleRowDbCommit(int result) {
    if (result != 1) {
      throw new RuntimeException("Expected one row to be affected but it was " + result);
    }
  }
  
  // Test version names are not unique across versions, thus identifier for a test version will have
  // it's id and name separated by a colon such as 1:v-1.
  private String getTestVersionIdentifier(TestVersion testVersion) {
    return String.format("%s:%s", testVersion.getTestVersionId(), testVersion.getName());
  }
  
  static class Factory {
    
    BuildRunHandler create(RequestBuildRun requestBuildRun,
                           APICoreProperties apiCoreProperties,
                           SecretsManager secretsManager,
                           Storage storage,
                           BuildProvider buildProvider,
                           BuildStatusProvider buildStatusProvider,
                           BuildVMProvider buildVMProvider,
                           ImmutableMapProvider immutableMapProvider,
                           ShotMetadataProvider shotMetadataProvider,
                           ZwlProgramOutputProvider zwlProgramOutputProvider,
                           Build build,
                           List<TestVersion> testVersions,
                           CaptureShotHandler.Factory captureShotHandlerFactory,
                           RemoteWebDriver driver,
                           Path buildDir) {
      return new BuildRunHandler(requestBuildRun,
          apiCoreProperties,
          secretsManager,
          storage,
          buildProvider,
          buildStatusProvider,
          buildVMProvider,
          immutableMapProvider,
          shotMetadataProvider,
          zwlProgramOutputProvider,
          build,
          testVersions,
          captureShotHandlerFactory,
          driver,
          buildDir);
    }
  }
}