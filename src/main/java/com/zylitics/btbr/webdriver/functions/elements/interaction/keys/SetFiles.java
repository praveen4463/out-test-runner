package com.zylitics.btbr.webdriver.functions.elements.interaction.keys;

import com.google.cloud.storage.Storage;
import com.zylitics.btbr.config.APICoreProperties;
import com.zylitics.btbr.model.BuildCapability;
import com.zylitics.btbr.webdriver.functions.AbstractWebdriverFunction;
import com.zylitics.zwl.datatype.ZwlValue;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.RemoteWebElement;

import java.io.PrintStream;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Meant to be used only when the file input's multiple attribute is set to 'true'.
 */
public class SetFiles extends AbstractWebdriverFunction {
  
  private final Storage storage;
  
  private final String userAccountBucket;
  
  private final String pathToUploadedFiles;

  public SetFiles(APICoreProperties.Webdriver wdProps,
                 BuildCapability buildCapability,
                 RemoteWebDriver driver,
                 PrintStream printStream,
                 Storage storage,
                 String userAccountBucket,
                 String pathToUploadedFiles) {
    super(wdProps, buildCapability, driver, printStream);
    this.storage = storage;
    this.userAccountBucket = userAccountBucket;
    this.pathToUploadedFiles = pathToUploadedFiles;
  }
  
  @Override
  public String getName() {
    return "setFiles";
  }
  
  @Override
  public int minParamsCount() {
    return 1;
  }
  
  @Override
  public int maxParamsCount() {
    return Integer.MAX_VALUE;
  }
  
  @Override
  public ZwlValue invoke(List<ZwlValue> args, Supplier<ZwlValue> defaultValue,
                         Supplier<String> lineNColumn) {
    super.invoke(args, defaultValue, lineNColumn);
  
    writeCommandUpdate(withArgsCommandUpdateText(args));
    int argsCount = args.size();
    
    if (argsCount >= 2) {
      RemoteWebElement element = getElement(tryCastString(0, args.get(0)));
      Set<String> filesOnCloud = args.subList(1, args.size())
          .stream().map(Objects::toString).collect(Collectors.toSet());
      // don't cast to string, may be possible the file is named like 322323 with no extension and
      // user sent it that way.
      Set<String> localFilePathsAfterDownload =
          new FileInputFilesProcessor(storage, userAccountBucket, pathToUploadedFiles,
              filesOnCloud).process();
      return handleWDExceptions(() -> {
        element.sendKeys(String.join("\n", localFilePathsAfterDownload));
        // per the spec https://w3c.github.io/webdriver/#dfn-dispatch-actions-for-a-string
        // , concat file paths with newline character.
        return _void;
      });
    }
    
    throw unexpectedEndOfFunctionOverload(argsCount);
  }
}
