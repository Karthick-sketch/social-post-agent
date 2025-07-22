package org.karthick.socialpostagent.llm;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.karthick.socialpostagent.llm.model.ChatModel;

public interface LLMProvider {
  String chat(ChatModel chatModel) throws JsonProcessingException;
}
