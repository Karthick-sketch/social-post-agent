package org.karthick.socialpostagent.llm.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class MessageModel {
  private String role;
  private String content;
}
