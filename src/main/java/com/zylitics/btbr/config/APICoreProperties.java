package com.zylitics.btbr.config;

import com.google.common.collect.ImmutableSet;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.annotation.concurrent.ThreadSafe;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.Set;

/**
 * All setters in this class allow only first time access by container, after that no values can
 * be mutated.
 * @author Praveen Tiwari
 *
 */
@ThreadSafe
@Component
@ConfigurationProperties(prefix="api-core")
@Validated
@SuppressWarnings("unused")
public class APICoreProperties {
  
  @NotBlank
  private String projectId;
  
  public String getProjectId() {
    return projectId;
  }
  
  public void setProjectId(String projectId) {
    if (this.projectId == null) {
      this.projectId = projectId;
    }
  }
  
  @NotBlank
  private String frontEndBaseUrl;
  
  public String getFrontEndBaseUrl() {
    return frontEndBaseUrl;
  }
  
  public void setFrontEndBaseUrl(String frontEndBaseUrl) {
    if (this.frontEndBaseUrl == null) {
      this.frontEndBaseUrl = frontEndBaseUrl;
    }
  }
  
  @Valid
  private final DataSource dataSource = new DataSource();
  
  public DataSource getDataSource() { return dataSource; }
  
  @Valid
  private final CloudKms cloudKms = new CloudKms();
  
  public CloudKms getCloudKms() { return cloudKms; }
  
  @Valid
  private final Esdb esdb = new Esdb();
  
  public Esdb getEsdb() { return esdb; }
  
  @Valid
  private final Runner runner = new Runner();
  
  public Runner getRunner() { return runner; }
  
  @Valid
  private final Shot shot = new Shot();
  
  public Shot getShot() { return shot; }
  
  @Valid
  private final Webdriver webdriver = new Webdriver();
  
  public Webdriver getWebdriver() { return webdriver; }
  
  @Valid
  private final Services services = new Services();
  
  public Services getServices() { return services; }
  
  @Valid
  private final Email email = new Email();
  
  public Email getEmail() { return email; }
  
  public static class DataSource {
    
    @NotBlank
    private String dbName;
  
    @NotBlank
    private String userName;
  
    @NotBlank
    private String userSecretCloudFile;
  
    @NotBlank
    private String privateHostCloudFile;
    
    @Min(1)
    private Short minIdleConnPool;
    
    @NotBlank
    private String envVarLocalHost;
  
    public String getDbName() {
      return dbName;
    }
  
    public void setDbName(String dbName) {
      if (this.dbName == null) {
        this.dbName = dbName;
      }
    }
  
    public String getUserName() {
      return userName;
    }
  
    public void setUserName(String userName) {
      if (this.userName == null) {
        this.userName = userName;
      }
    }
  
    public String getUserSecretCloudFile() {
      return userSecretCloudFile;
    }
  
    public void setUserSecretCloudFile(String userSecretCloudFile) {
      if (this.userSecretCloudFile == null) {
        this.userSecretCloudFile = userSecretCloudFile;
      }
    }
  
    public String getPrivateHostCloudFile() {
      return privateHostCloudFile;
    }
  
    public void setPrivateHostCloudFile(String privateHostCloudFile) {
      if (this.privateHostCloudFile == null) {
        this.privateHostCloudFile = privateHostCloudFile;
      }
    }
  
    public Short getMinIdleConnPool() {
      return minIdleConnPool;
    }
  
    public void setMinIdleConnPool(Short minIdleConnPool) {
      if (this.minIdleConnPool == null) {
        this.minIdleConnPool = minIdleConnPool;
      }
    }
  
    public String getEnvVarLocalHost() {
      return envVarLocalHost;
    }
    
    public void setEnvVarLocalHost(String envVarLocalHost) {
      if (this.envVarLocalHost == null) {
        this.envVarLocalHost = envVarLocalHost;
      }
    }
  }
  
  public static class CloudKms {
  
    @NotBlank
    private String keyRing;
  
    @NotBlank
    private String key;
  
    @NotBlank
    private String keyBucket;
  
    public String getKeyRing() {
      return keyRing;
    }
  
    public void setKeyRing(String keyRing) {
      if (this.keyRing == null) {
        this.keyRing = keyRing;
      }
    }
  
    public String getKey() {
      return key;
    }
  
    public void setKey(String key) {
      if (this.key == null) {
        this.key = key;
      }
    }
  
    public String getKeyBucket() {
      return keyBucket;
    }
  
    public void setKeyBucket(String keyBucket) {
      if (this.keyBucket == null) {
        this.keyBucket = keyBucket;
      }
    }
  }
  
  public static class Esdb {
    
    @NotBlank
    private String authUser;
    
    @NotBlank
    private String authUserSecretCloudFile;
    
    @Min(1)
    private Short maxRetries;
    
    @NotBlank
    private String shotMetadataIndex;
    
    @NotBlank
    private String zwlProgramOutputIndex;
  
    @NotBlank
    private String browserIndex;
    
    @NotBlank
    private String envVarHost;
  
    public String getAuthUser() {
      return authUser;
    }
  
    public void setAuthUser(String authUser) {
      if (this.authUser == null) {
        this.authUser = authUser;
      }
    }
  
    public String getAuthUserSecretCloudFile() {
      return authUserSecretCloudFile;
    }
  
    public void setAuthUserSecretCloudFile(String authUserSecretCloudFile) {
      if (this.authUserSecretCloudFile == null) {
        this.authUserSecretCloudFile = authUserSecretCloudFile;
      }
    }
  
    public Short getMaxRetries() {
      return maxRetries;
    }
  
    public void setMaxRetries(Short maxRetries) {
      if (this.maxRetries == null) {
        this.maxRetries = maxRetries;
      }
    }
  
    public String getShotMetadataIndex() {
      return shotMetadataIndex;
    }
  
    public void setShotMetadataIndex(String shotMetadataIndex) {
      if (this.shotMetadataIndex == null) {
        this.shotMetadataIndex = shotMetadataIndex;
      }
    }
  
    public String getZwlProgramOutputIndex() {
      return zwlProgramOutputIndex;
    }
  
    public void setZwlProgramOutputIndex(String zwlProgramOutputIndex) {
      if (this.zwlProgramOutputIndex == null) {
        this.zwlProgramOutputIndex = zwlProgramOutputIndex;
      }
    }
  
    public String getBrowserIndex() {
      return browserIndex;
    }
  
    public void setBrowserIndex(String browserIndex) {
      if (this.browserIndex == null) {
        this.browserIndex = browserIndex;
      }
    }
  
    public String getEnvVarHost() {
      return envVarHost;
    }
  
    public void setEnvVarHost(String envVarHost) {
      if (this.envVarHost == null) {
        this.envVarHost = envVarHost;
      }
    }
  }
  
  public static class Runner {
  
    @Min(1)
    private Integer programOutputFlushNo;
    
    @Min(1)
    private Integer updateLineBuildStatusAfter;
  
    @NotBlank
    private String btbrAuthUser;
  
    @NotBlank
    private String btbrAuthSecretCloudFile;
  
    @NotBlank
    private String wzgpEndpoint;
  
    @NotBlank
    private String wzgpVersion;
    
    @NotBlank
    private String wzgpAuthUser;
  
    @NotBlank
    private String wzgpAuthSecretCloudFile;
  
    @NotBlank
    private String winServerBuildStartupScriptPath;
  
    public Integer getProgramOutputFlushNo() {
      return programOutputFlushNo;
    }
  
    public void setProgramOutputFlushNo(Integer programOutputFlushNo) {
      if (this.programOutputFlushNo == null) {
        this.programOutputFlushNo = programOutputFlushNo;
      }
    }
  
    public Integer getUpdateLineBuildStatusAfter() {
      return updateLineBuildStatusAfter;
    }
  
    public void setUpdateLineBuildStatusAfter(Integer updateLineBuildStatusAfter) {
      if (this.updateLineBuildStatusAfter == null) {
        this.updateLineBuildStatusAfter = updateLineBuildStatusAfter;
      }
    }
  
    public String getBtbrAuthUser() {
      return btbrAuthUser;
    }
  
    public void setBtbrAuthUser(String btbrAuthUser) {
      if (this.btbrAuthUser == null) {
        this.btbrAuthUser = btbrAuthUser;
      }
    }
  
    public String getBtbrAuthSecretCloudFile() {
      return btbrAuthSecretCloudFile;
    }
  
    public void setBtbrAuthSecretCloudFile(String btbrAuthSecretCloudFile) {
      if (this.btbrAuthSecretCloudFile == null) {
        this.btbrAuthSecretCloudFile = btbrAuthSecretCloudFile;
      }
    }
  
    public String getWzgpEndpoint() {
      return wzgpEndpoint;
    }
  
    public void setWzgpEndpoint(String wzgpEndpoint) {
      if (this.wzgpEndpoint == null) {
        this.wzgpEndpoint = wzgpEndpoint;
      }
    }
  
    public String getWzgpVersion() {
      return wzgpVersion;
    }
  
    public void setWzgpVersion(String wzgpVersion) {
      if (this.wzgpVersion == null) {
        this.wzgpVersion = wzgpVersion;
      }
    }
  
    public String getWzgpAuthUser() {
      return wzgpAuthUser;
    }
  
    public void setWzgpAuthUser(String wzgpAuthUser) {
      if (this.wzgpAuthUser == null) {
        this.wzgpAuthUser = wzgpAuthUser;
      }
    }
  
    public String getWzgpAuthSecretCloudFile() {
      return wzgpAuthSecretCloudFile;
    }
  
    public void setWzgpAuthSecretCloudFile(String wzgpAuthSecretCloudFile) {
      if (this.wzgpAuthSecretCloudFile == null) {
        this.wzgpAuthSecretCloudFile = wzgpAuthSecretCloudFile;
      }
    }
  
    public String getWinServerBuildStartupScriptPath() {
      return winServerBuildStartupScriptPath;
    }
  
    public void setWinServerBuildStartupScriptPath(String winServerBuildStartupScriptPath) {
      if (this.winServerBuildStartupScriptPath == null) {
        this.winServerBuildStartupScriptPath = winServerBuildStartupScriptPath;
      }
    }
  }
  
  public static class Shot {
    
    @NotBlank
    private String ext;
  
    @NotBlank
    private String contentType;
    
    @Min(60)
    private Integer maxShotFinishSec;
    
    @NotBlank
    private String errorShot;
  
    @NotBlank
    private String eosShot;
  
    @Min(10)
    private Integer shotMetadataFlushRecords;
  
    public String getExt() {
      return ext;
    }
  
    public void setExt(String ext) {
      if (this.ext == null) {
        this.ext = ext;
      }
    }
  
    public String getContentType() {
      return contentType;
    }
  
    public void setContentType(String contentType) {
      if (this.contentType == null) {
        this.contentType = contentType;
      }
    }
  
    public int getMaxShotFinishSec() {
      return maxShotFinishSec;
    }
  
    public void setMaxShotFinishSec(Integer maxShotFinishSec) {
      if (this.maxShotFinishSec == null) {
        this.maxShotFinishSec = maxShotFinishSec;
      }
    }
  
    public String getErrorShot() {
      return errorShot;
    }
  
    public void setErrorShot(String errorShot) {
      if (this.errorShot == null) {
        this.errorShot = errorShot;
      }
    }
  
    public String getEosShot() {
      return eosShot;
    }
  
    public void setEosShot(String eosShot) {
      if (this.eosShot == null) {
        this.eosShot = eosShot;
      }
    }
  
    public Integer getShotMetadataFlushRecords() {
      return shotMetadataFlushRecords;
    }
  
    public void setShotMetadataFlushRecords(Integer shotMetadataFlushRecords) {
      if (this.shotMetadataFlushRecords == null) {
        this.shotMetadataFlushRecords = shotMetadataFlushRecords;
      }
    }
  }
  
  public static class Webdriver {
  
    @NotBlank
    private String serverLogsBucket;
  
    @NotBlank
    private String elemShotsBucket;
  
    @NotBlank
    private String userDataBucket;
    
    @NotBlank
    private String userUploadsStorageDirTmpl;
    
    @NotEmpty
    private Set<String> supportedBrowsers;
  
    @NotEmpty
    private Set<String> supportedPlatforms;
  
    @NotBlank
    private String defaultPageLoadStrategy;
    
    @Min(1000)
    private Integer defaultTimeoutElementAccess;
  
    @Min(1000)
    private Integer defaultTimeoutPageLoad;
  
    @Min(1000)
    private Integer defaultTimeoutScript;
  
    @Min(1000)
    private Integer defaultTimeoutNewWindow;
  
    @Min(1000)
    private Integer ieDefaultBrowserAttachTimeout;
  
    @Min(1000)
    private Integer ieDefaultFileUploadDialogTimeout;
    
    @NotBlank
    private String enableProfilerLogsProp;
  
    @NotBlank
    private String verboseClientLogsProp;
  
    @NotBlank
    private String browserPerfLogsDir;
  
    @NotBlank
    private String internalLogsDir;
    
    @NotBlank
    private String driverLogsDir;
  
    @NotBlank
    private String browserPerfLogsFile;
  
    @NotBlank
    private String clientLogsFile;
  
    @NotBlank
    private String profilerLogsFile;
  
    @NotBlank
    private String driverLogsFile;
  
    @Min(10)
    private Integer waitBetweenLogsCapture;
  
    @NotBlank
    private String elementShotDir;
  
    public String getServerLogsBucket() {
      return serverLogsBucket;
    }
  
    public void setServerLogsBucket(String serverLogsBucket) {
      if (this.serverLogsBucket == null) {
        this.serverLogsBucket = serverLogsBucket;
      }
    }
  
    public String getElemShotsBucket() {
      return elemShotsBucket;
    }
  
    public void setElemShotsBucket(String elemShotsBucket) {
      if (this.elemShotsBucket == null) {
        this.elemShotsBucket = elemShotsBucket;
      }
    }
  
    public String getUserDataBucket() {
      return userDataBucket;
    }
  
    public void setUserDataBucket(String userDataBucket) {
      if (this.userDataBucket == null) {
        this.userDataBucket = userDataBucket;
      }
    }
  
    public String getUserUploadsStorageDirTmpl() {
      return userUploadsStorageDirTmpl;
    }
  
    public void setUserUploadsStorageDirTmpl(String userUploadsStorageDirTmpl) {
      if (this.userUploadsStorageDirTmpl == null) {
        this.userUploadsStorageDirTmpl = userUploadsStorageDirTmpl;
      }
    }
  
    public Set<String> getSupportedBrowsers() {
      return supportedBrowsers;
    }
  
    public void setSupportedBrowsers(Set<String> supportedBrowsers) {
      if (this.supportedBrowsers == null) {
        this.supportedBrowsers = ImmutableSet.copyOf(supportedBrowsers);
      }
    }
  
    public Set<String> getSupportedPlatforms() {
      return supportedPlatforms;
    }
  
    public void setSupportedPlatforms(Set<String> supportedPlatforms) {
      if (this.supportedPlatforms == null) {
        this.supportedPlatforms = ImmutableSet.copyOf(supportedPlatforms);
      }
    }
  
    public String getDefaultPageLoadStrategy() {
      return defaultPageLoadStrategy;
    }
  
    public void setDefaultPageLoadStrategy(String defaultPageLoadStrategy) {
      if (this.defaultPageLoadStrategy == null) {
        this.defaultPageLoadStrategy = defaultPageLoadStrategy;
      }
    }
  
    public Integer getDefaultTimeoutElementAccess() {
      return defaultTimeoutElementAccess;
    }
  
    public void setDefaultTimeoutElementAccess(Integer defaultTimeoutElementAccess) {
      if (this.defaultTimeoutElementAccess == null) {
        this.defaultTimeoutElementAccess = defaultTimeoutElementAccess;
      }
    }
  
    public Integer getDefaultTimeoutPageLoad() {
      return defaultTimeoutPageLoad;
    }
  
    public void setDefaultTimeoutPageLoad(Integer defaultTimeoutPageLoad) {
      if (this.defaultTimeoutPageLoad == null) {
        this.defaultTimeoutPageLoad = defaultTimeoutPageLoad;
      }
    }
  
    public Integer getDefaultTimeoutScript() {
      return defaultTimeoutScript;
    }
  
    public void setDefaultTimeoutScript(Integer defaultTimeoutScript) {
      if (this.defaultTimeoutScript == null) {
        this.defaultTimeoutScript = defaultTimeoutScript;
      }
    }
  
    public Integer getDefaultTimeoutNewWindow() {
      return defaultTimeoutNewWindow;
    }
  
    public void setDefaultTimeoutNewWindow(Integer defaultTimeoutNewWindow) {
      if (this.defaultTimeoutNewWindow == null) {
        this.defaultTimeoutNewWindow = defaultTimeoutNewWindow;
      }
    }
  
    public Integer getIeDefaultBrowserAttachTimeout() {
      return ieDefaultBrowserAttachTimeout;
    }
  
    public void setIeDefaultBrowserAttachTimeout(Integer ieDefaultBrowserAttachTimeout) {
      if (this.ieDefaultBrowserAttachTimeout == null) {
        this.ieDefaultBrowserAttachTimeout = ieDefaultBrowserAttachTimeout;
      }
    }
  
    public Integer getIeDefaultFileUploadDialogTimeout() {
      return ieDefaultFileUploadDialogTimeout;
    }
  
    public void setIeDefaultFileUploadDialogTimeout(Integer ieDefaultFileUploadDialogTimeout) {
      if (this.ieDefaultFileUploadDialogTimeout == null) {
        this.ieDefaultFileUploadDialogTimeout = ieDefaultFileUploadDialogTimeout;
      }
    }
  
    public String getEnableProfilerLogsProp() {
      return enableProfilerLogsProp;
    }
  
    public void setEnableProfilerLogsProp(String enableProfilerLogsProp) {
      if (this.enableProfilerLogsProp == null) {
        this.enableProfilerLogsProp = enableProfilerLogsProp;
      }
    }
  
    public String getVerboseClientLogsProp() {
      return verboseClientLogsProp;
    }
  
    public void setVerboseClientLogsProp(String verboseClientLogsProp) {
      if (this.verboseClientLogsProp == null) {
        this.verboseClientLogsProp = verboseClientLogsProp;
      }
    }
  
    public String getBrowserPerfLogsDir() {
      return browserPerfLogsDir;
    }
  
    public void setBrowserPerfLogsDir(String browserPerfLogsDir) {
      if (this.browserPerfLogsDir == null) {
        this.browserPerfLogsDir = browserPerfLogsDir;
      }
    }
  
    public String getInternalLogsDir() {
      return internalLogsDir;
    }
  
    public void setInternalLogsDir(String internalLogsDir) {
      if (this.internalLogsDir == null) {
        this.internalLogsDir = internalLogsDir;
      }
    }
  
    public String getDriverLogsDir() {
      return driverLogsDir;
    }
  
    public void setDriverLogsDir(String driverLogsDir) {
      if (this.driverLogsDir == null) {
        this.driverLogsDir = driverLogsDir;
      }
    }
  
    public String getBrowserPerfLogsFile() {
      return browserPerfLogsFile;
    }
  
    public void setBrowserPerfLogsFile(String browserPerfLogsFile) {
      if (this.browserPerfLogsFile == null) {
        this.browserPerfLogsFile = browserPerfLogsFile;
      }
    }
  
    public String getClientLogsFile() {
      return clientLogsFile;
    }
  
    public void setClientLogsFile(String clientLogsFile) {
      if (this.clientLogsFile == null) {
        this.clientLogsFile = clientLogsFile;
      }
    }
  
    public String getProfilerLogsFile() {
      return profilerLogsFile;
    }
  
    public void setProfilerLogsFile(String profilerLogsFile) {
      if (this.profilerLogsFile == null) {
        this.profilerLogsFile = profilerLogsFile;
      }
    }
  
    public String getDriverLogsFile() {
      return driverLogsFile;
    }
  
    public void setDriverLogsFile(String driverLogsFile) {
      if (this.driverLogsFile == null) {
        this.driverLogsFile = driverLogsFile;
      }
    }
  
    public Integer getWaitBetweenLogsCapture() {
      return waitBetweenLogsCapture;
    }
  
    public void setWaitBetweenLogsCapture(Integer waitBetweenLogsCapture) {
      if (this.waitBetweenLogsCapture == null) {
        this.waitBetweenLogsCapture = waitBetweenLogsCapture;
      }
    }
  
    public String getElementShotDir() {
      return elementShotDir;
    }
  
    public void setElementShotDir(String elementShotDir) {
      if (this.elementShotDir == null) {
        this.elementShotDir = elementShotDir;
      }
    }
  }
  
  public static class Services {
  
    @NotBlank
    private String sendgridApiKeySecretCloudFile;
  
    public String getSendgridApiKeySecretCloudFile() {
      return sendgridApiKeySecretCloudFile;
    }
  
    public void setSendgridApiKeySecretCloudFile(String sendgridApiKeySecretCloudFile) {
      if (this.sendgridApiKeySecretCloudFile == null) {
        this.sendgridApiKeySecretCloudFile = sendgridApiKeySecretCloudFile;
      }
    }
  }
  
  public static class Email {
  
    @NotBlank
    private String supportEmail;
  
    @NotBlank
    private String emailSenderName;
  
    @NotBlank
    private String emailBuildSuccessTmpId;
  
    @NotBlank
    private String emailBuildFailedTmpId;
  
    @NotBlank
    private String emailPrefPage;
  
    @NotBlank
    private String buildsPage;
  
    public String getSupportEmail() {
      return supportEmail;
    }
  
    public void setSupportEmail(String supportEmail) {
      if (this.supportEmail == null) {
        this.supportEmail = supportEmail;
      }
    }
  
    public String getEmailSenderName() {
      return emailSenderName;
    }
  
    public void setEmailSenderName(String emailSenderName) {
      if (this.emailSenderName == null) {
        this.emailSenderName = emailSenderName;
      }
    }
  
    public String getEmailBuildSuccessTmpId() {
      return emailBuildSuccessTmpId;
    }
  
    public void setEmailBuildSuccessTmpId(String emailBuildSuccessTmpId) {
      if (this.emailBuildSuccessTmpId == null) {
        this.emailBuildSuccessTmpId = emailBuildSuccessTmpId;
      }
    }
  
    public String getEmailBuildFailedTmpId() {
      return emailBuildFailedTmpId;
    }
  
    public void setEmailBuildFailedTmpId(String emailBuildFailedTmpId) {
      if (this.emailBuildFailedTmpId == null) {
        this.emailBuildFailedTmpId = emailBuildFailedTmpId;
      }
    }
  
    public String getEmailPrefPage() {
      return emailPrefPage;
    }
  
    public void setEmailPrefPage(String emailPrefPage) {
      if (this.emailPrefPage == null) {
        this.emailPrefPage = emailPrefPage;
      }
    }
  
    public String getBuildsPage() {
      return buildsPage;
    }
  
    public void setBuildsPage(String buildsPage) {
      if (this.buildsPage == null) {
        this.buildsPage = buildsPage;
      }
    }
  }
}
