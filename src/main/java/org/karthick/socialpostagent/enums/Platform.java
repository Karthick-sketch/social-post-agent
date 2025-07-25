package org.karthick.socialpostagent.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum Platform {
  LINKEDIN("LinkedIn"),
  INSTAGRAM("Instagram"),
  TWITTER("Twitter");

  private final String value;

  Platform(String value) {
    this.value = value;
  }

  @JsonValue
  public String getValue() {
    return this.value;
  }
}
