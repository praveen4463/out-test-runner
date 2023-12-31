package com.zylitics.btbr.webdriver.session;

import com.google.common.base.Preconditions;
import com.zylitics.btbr.config.APICoreProperties;
import com.zylitics.btbr.model.Build;
import com.zylitics.btbr.model.BuildCapability;
import com.zylitics.btbr.runner.provider.BrowserProvider;
import org.openqa.selenium.ie.*;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.nio.file.Path;
import java.time.Duration;

public class IEDriverSessionProvider extends AbstractDriverSessionProvider {
  
  public IEDriverSessionProvider(Build build, APICoreProperties.Webdriver wdProps
      , BuildCapability buildCapability, Path buildDir, BrowserProvider browserProvider) {
    super(build, wdProps, buildCapability, buildDir, browserProvider);
  }
  
  @Override
  public RemoteWebDriver createSession() {
    setDriverExe();
    Preconditions.checkNotNull(
        System.getProperty(InternetExplorerDriverService.IE_DRIVER_EXE_PROPERTY),
            "ie driver exe path must be set as system property");
  
    InternetExplorerDriverService.Builder driverServiceBuilder =
        new InternetExplorerDriverService.Builder()
            .usingAnyFreePort()
            .withLogFile(getDriverLogFile());
    if (build.isCaptureDriverLogs()) {
      InternetExplorerDriverLogLevel logLevel = null;
      if (buildCapability.getWdIeLogLevel() != null) {
        for (InternetExplorerDriverLogLevel b : InternetExplorerDriverLogLevel.values()) {
          if (buildCapability.getWdIeLogLevel().equalsIgnoreCase(b.toString())) {
            logLevel = b;
            break;
          }
        }
      }
      driverServiceBuilder.withLogLevel(logLevel);
    } else {
      driverServiceBuilder.withSilent(true);
    }
    

    // IE driver has lot of custom capabilities available as ie options, their description could
    // be found from the changelog
    // https://raw.githubusercontent.com/SeleniumHQ/selenium/master/cpp/iedriverserver/CHANGELOG
    // Also read the known issues and details of some workarounds from
    // https://github.com/SeleniumHQ/selenium/wiki/InternetExplorerDriver
    // Three files are important, InternetExplorerOptions, InternetExplorerDriver and
    // InternetExplorerDriverService
    InternetExplorerOptions ie = new InternetExplorerOptions();
    ie.merge(commonCapabilities);
    ie.withAttachTimeout(Duration.ofMillis(wdProps.getIeDefaultBrowserAttachTimeout()));
    ie.waitForUploadDialogUpTo(Duration.ofMillis(wdProps.getIeDefaultFileUploadDialogTimeout()));
    ElementScrollBehavior scrollBehavior =
        ElementScrollBehavior.fromString(buildCapability.getWdIeElementScrollBehavior());
    if (scrollBehavior != null) {
      ie.elementScrollTo(scrollBehavior);
    }
    if (buildCapability.isWdIeEnablePersistentHovering()) {
      // Useful in tests that need to hover over elements to bring them into visibility.
      ie.enablePersistentHovering();
    }
    /*
      Let's not enable this by default and give it to use to decide, give mostly all IE caps to them
      to decide as there are of uncertainties and different use case may require different set of
      capabilities. requireWindowFocus still have problems with element.sendKeys and doesn't send
      all keys which is very important requirement for every test. Jim said in a post he has fixed
      it so it now doesn't truncate keys but this doesn't seems to be true. When not using it there
      are some problems in mouse related tests, no all mouse related tests work well like drag and
      drop but most others do and like other drivers the mouse pointer doesn't shows while moving,
      on the other hand when using it, it shows a mouse moving and have better control (although
      much slower). Let's leave it on user to decide what to do since we can't fix it, users that
      may want better mouse control perhaps use actions.sendKeys instead while using this
      capability. Note that the browser window should always be in focus while the test is running.
      */
    if (buildCapability.isWdIeRequireWindowFocus()) {
      ie.requireWindowFocus();
    }
    if (buildCapability.isWdIeDisableNativeEvents()) {
      ie.disableNativeEvents();
    }
    if (buildCapability.isWdIeDestructivelyEnsureCleanSession()) {
      // holds up browser start and shows dialog that
      // 'browser history being cleaned". We can do this on shutdown, so let's not use it.
      // enable if one machine runs multiple builds
      ie.destructivelyEnsureCleanSession();
    }
    // useCreateProcessApiToLaunchIe, useShellWindowsApiToAttachToIe not using for now until
    // we get some problem in launch.
    
    return new InternetExplorerDriver(driverServiceBuilder.build(), ie);
  }
  
  @Override
  protected String getDriverExeSysProp() {
    return InternetExplorerDriverService.IE_DRIVER_EXE_PROPERTY;
  }
  
  @Override
  protected String getDriverWinExeName() {
    return "IEDriverServer.exe";
  }
}
