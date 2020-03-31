package com.zylitics.btbr.webdriver.functions.until;

import com.zylitics.btbr.config.APICoreProperties;
import com.zylitics.btbr.model.BuildCapability;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.RemoteWebElement;

import java.io.PrintStream;

public class UntilAllDisabled extends AbstractVisibleEnableMulti {
  
  public UntilAllDisabled(APICoreProperties.Webdriver wdProps,
                         BuildCapability buildCapability,
                         RemoteWebDriver driver,
                         PrintStream printStream) {
    super(wdProps, buildCapability, driver, printStream);
  }
  
  @Override
  public String getName() {
    return "untilAllDisabled";
  }
  
  @Override
  boolean desiredState(RemoteWebElement element) {
    return !element.isEnabled();
  }
}