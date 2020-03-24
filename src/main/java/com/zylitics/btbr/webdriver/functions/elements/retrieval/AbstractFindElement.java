package com.zylitics.btbr.webdriver.functions.elements.retrieval;

import com.zylitics.btbr.config.APICoreProperties;
import com.zylitics.btbr.model.BuildCapability;
import com.zylitics.btbr.webdriver.functions.AbstractWebdriverFunction;
import com.zylitics.zwl.datatype.ZwlValue;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.io.PrintStream;
import java.util.List;
import java.util.function.Supplier;

public abstract class AbstractFindElement extends AbstractWebdriverFunction {
  
  public AbstractFindElement(APICoreProperties.Webdriver wdProps,
                             BuildCapability buildCapability,
                             RemoteWebDriver driver,
                             PrintStream printStream) {
    super(wdProps, buildCapability, driver, printStream);
  }
  
  @Override
  public int minParamsCount() {
    return 1;
  }
  
  @Override
  public int maxParamsCount() {
    return 2;
  }
  
  @Override
  public ZwlValue invoke(List<ZwlValue> args, Supplier<ZwlValue> defaultValue,
                         Supplier<String> lineNColumn) {
    super.invoke(args, defaultValue, lineNColumn);
    
    int argsCount = args.size();
    if (argsCount == 0) {
      throw unexpectedEndOfFunctionOverload(argsCount);
    }
    boolean noWait = argsCount == 2 ? parseBoolean(1, args.get(1)) : false;
    return handleWDExceptions(() -> find(tryCastString(0, args.get(0)), !noWait));
  }
  
  protected abstract ZwlValue find(String selector, boolean wait);
}
