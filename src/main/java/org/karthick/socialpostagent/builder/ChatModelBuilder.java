package org.karthick.socialpostagent.builder;

import org.karthick.socialpostagent.llm.model.ChatModel;
import org.karthick.socialpostagent.llm.model.MessageModel;

import java.util.List;

public final class ChatModelBuilder {
  public static ChatModel buildChatModel(List<MessageModel> messages, double temperature) {
    ChatModel chatModel = new ChatModel();
    chatModel.setMessages(messages);
    chatModel.setTemperature(temperature);
    return chatModel;
  }
}
