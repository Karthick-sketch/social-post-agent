package org.karthick.socialpostagent.llm;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.karthick.socialpostagent.llm.model.ChatModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class DeepSeekService implements LLMProvider {
  private final RestTemplate restTemplate;

  @Value("${llm.deepseek.model}")
  private String MODEL;

  @Value("${llm.deepseek.api-key}")
  private String API_KEY;

  public DeepSeekService(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  @Override
  public String chat(ChatModel chatModel) throws JsonProcessingException {
    String url = "https://openrouter.ai/api/v1/chat/completions";
    chatModel.setModel(MODEL);
    HttpEntity<ChatModel> entity = getHttpEntity(chatModel);
    ResponseEntity<String> response =
        restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
    return getContent(response.getBody());
  }

  private HttpEntity<ChatModel> getHttpEntity(ChatModel chatModel) {
    HttpHeaders headers = new HttpHeaders();
    headers.add("Authorization", "Bearer " + API_KEY);
    headers.add("Content-Type", "application/json");
    return new HttpEntity<>(chatModel, headers);
  }

  private String getContent(String body) throws JsonProcessingException {
    ObjectMapper mapper = new ObjectMapper();
    JsonNode rootNode = mapper.readTree(body);
    JsonNode contentNode = rootNode.path("choices").path(0).path("message").path("content");
    return contentNode.asText();
  }
}
