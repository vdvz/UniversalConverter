package com.example.UniversalConverter.RequestRepresentation;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ConversionRequest {

  @JsonProperty("from")
  private String from;

  @JsonProperty("to")
  private String to;

  public String getFrom() {
    return from;
  }

  public String getTo() {
    return to;
  }

}
