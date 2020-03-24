package com.zylitics.btbr.webdriver.functions.navigation;

import com.zylitics.btbr.config.APICoreProperties;
import com.zylitics.btbr.model.BuildCapability;
import com.zylitics.btbr.webdriver.functions.AbstractWebdriverFunction;
import com.zylitics.zwl.datatype.ZwlValue;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.io.PrintStream;
import java.util.List;
import java.util.function.Supplier;

public abstract class BackForward extends AbstractWebdriverFunction {
  
  public BackForward(APICoreProperties.Webdriver wdProps,
              BuildCapability buildCapability,
              RemoteWebDriver driver,
              PrintStream printStream) {
    super(wdProps, buildCapability, driver, printStream);
  }
  
  @Override
  public int minParamsCount() {
    return 0;
  }
  
  @Override
  public int maxParamsCount() {
    return 1;
  }
  
  @Override
  public ZwlValue invoke(List<ZwlValue> args, Supplier<ZwlValue> defaultValue,
                         Supplier<String> lineNColumn) {
    super.invoke(args, defaultValue, lineNColumn);
    
    return handleWDExceptions(() -> {
      int times = 1;
      if (args.size() > 0) {
        times = parseDouble(0, args.get(0)).intValue();
      }
      for (int i = 0; i < times; i++) {
        goInHistory();
      }
      return _void;
    });
  }
  
  protected abstract void goInHistory();
}
