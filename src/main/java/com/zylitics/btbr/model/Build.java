package com.zylitics.btbr.model;

public class Build {

  private int buildId;
  
  private String buildKey;
  
  private BuildCapability buildCapability;
  
  private int buildVMId;
  
  private Boolean isSuccess;
  
  private String shotBucketSessionStorage;
  
  private boolean abortOnFailure;
  
  private boolean aetKeepSingleWindow;
  
  private boolean aetUpdateUrlBlank;
  
  private boolean aetResetTimeouts;
  
  private boolean aetDeleteAllCookies;
  
  
  private int userId;
  
  public int getBuildId() {
    return buildId;
  }
  
  public Build setBuildId(int buildId) {
    this.buildId = buildId;
    return this;
  }
  
  public String getBuildKey() {
    return buildKey;
  }
  
  public Build setBuildKey(String buildKey) {
    this.buildKey = buildKey;
    return this;
  }
  
  public BuildCapability getBuildCapability() {
    return buildCapability;
  }
  
  public Build setBuildCapability(BuildCapability buildCapability) {
    this.buildCapability = buildCapability;
    return this;
  }
  
  public int getBuildVMId() {
    return buildVMId;
  }
  
  public Build setBuildVMId(int buildVMId) {
    this.buildVMId = buildVMId;
    return this;
  }
  
  public Boolean isSuccess() {
    return isSuccess;
  }
  
  public Build setSuccess(Boolean success) {
    isSuccess = success;
    return this;
  }
  
  public String getShotBucketSessionStorage() {
    return shotBucketSessionStorage;
  }
  
  public Build setShotBucketSessionStorage(String shotBucketSessionStorage) {
    this.shotBucketSessionStorage = shotBucketSessionStorage;
    return this;
  }
  
  public boolean isAbortOnFailure() {
    return abortOnFailure;
  }
  
  public Build setAbortOnFailure(boolean abortOnFailure) {
    this.abortOnFailure = abortOnFailure;
    return this;
  }
  
  public boolean isAetKeepSingleWindow() {
    return aetKeepSingleWindow;
  }
  
  public Build setAetKeepSingleWindow(boolean aetKeepSingleWindow) {
    this.aetKeepSingleWindow = aetKeepSingleWindow;
    return this;
  }
  
  public boolean isAetUpdateUrlBlank() {
    return aetUpdateUrlBlank;
  }
  
  public Build setAetUpdateUrlBlank(boolean aetUpdateUrlBlank) {
    this.aetUpdateUrlBlank = aetUpdateUrlBlank;
    return this;
  }
  
  public boolean isAetResetTimeouts() {
    return aetResetTimeouts;
  }
  
  public Build setAetResetTimeouts(boolean aetResetTimeouts) {
    this.aetResetTimeouts = aetResetTimeouts;
    return this;
  }
  
  public boolean isAetDeleteAllCookies() {
    return aetDeleteAllCookies;
  }
  
  public Build setAetDeleteAllCookies(boolean aetDeleteAllCookies) {
    this.aetDeleteAllCookies = aetDeleteAllCookies;
    return this;
  }
  
  public int getUserId() {
    return userId;
  }
  
  public Build setUserId(int userId) {
    this.userId = userId;
    return this;
  }
  
  @Override
  public String toString() {
    return "Build{" +
        "buildId=" + buildId +
        ", buildKey='" + buildKey + '\'' +
        ", buildCapability=" + buildCapability +
        ", buildVMId=" + buildVMId +
        ", isSuccess=" + isSuccess +
        ", shotBucketSessionStorage='" + shotBucketSessionStorage + '\'' +
        ", abortOnFailure=" + abortOnFailure +
        ", aetKeepSingleWindow=" + aetKeepSingleWindow +
        ", aetUpdateUrlBlank=" + aetUpdateUrlBlank +
        ", aetResetTimeouts=" + aetResetTimeouts +
        ", aetDeleteAllCookies=" + aetDeleteAllCookies +
        ", userId=" + userId +
        '}';
  }
}
