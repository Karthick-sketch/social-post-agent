package org.karthick.socialpostagent.entity;

import lombok.Getter;
import lombok.Setter;
import org.karthick.socialpostagent.enums.ChatType;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Document(collection = "llm_chat_logs")
public class LLMChatLog {
  @Id private String id;
  private String response;
  private ChatType chatType;

  public LLMChatLog(String response, ChatType chatType) {
    this.response = response;
    this.chatType = chatType;
  }
}
