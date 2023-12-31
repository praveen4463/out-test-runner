package com.zylitics.btbr.test.model;

import com.zylitics.btbr.runner.TestStatus;

import java.time.LocalDateTime;

public class Build {
  
  // The reason why we store date in LocalDateTime rather than OffsetDateTime is, we send a
  // timestamp to postgres in UTC and it stores it as is. While retrieving, the timestamp is
  // converted to a particular time zone (for example the zone user is currently in or has selected)
  // After conversion the timestamp doesn't technically represent a timezone and is converted to a
  // local date time.
  private LocalDateTime startDate;
  
  private LocalDateTime endDate;
  
  private TestStatus finalStatus;
  
  private String error;
  
  public LocalDateTime getStartDate() {
    return startDate;
  }
  
  public Build setStartDate(LocalDateTime startDate) {
    this.startDate = startDate;
    return this;
  }
  
  public LocalDateTime getEndDate() {
    return endDate;
  }
  
  public Build setEndDate(LocalDateTime endDate) {
    this.endDate = endDate;
    return this;
  }
  
  public TestStatus getFinalStatus() {
    return finalStatus;
  }
  
  public Build setFinalStatus(TestStatus finalStatus) {
    this.finalStatus = finalStatus;
    return this;
  }
  
  public String getError() {
    return error;
  }
  
  public Build setError(String error) {
    this.error = error;
    return this;
  }
  
  @Override
  public String toString() {
    return "Build{" +
        "startDate=" + startDate +
        ", endDate=" + endDate +
        ", finalStatus=" + finalStatus +
        ", error='" + error + '\'' +
        '}';
  }
}
