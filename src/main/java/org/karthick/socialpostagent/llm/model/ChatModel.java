package org.karthick.socialpostagent.llm.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ChatModel {
  private String model;
  private List<MessageModel> messages;
  private double temperature;
  private int max_tokens;
  private boolean stream;
}
