package org.karthick.socialpostagent.image;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.karthick.socialpostagent.model.ImageModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class UnsplashService implements ImageProvider {
  private final RestTemplate restTemplate;

  @Value("ACCESS_KEY")
  private String ACCESS_KEY;

  public UnsplashService(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  @Override
  public List<ImageModel> search(String query, int page, int perPage)
      throws JsonProcessingException {
    String url = getUrl(query, page, perPage);
    ResponseEntity<String> response =
        restTemplate.exchange(url, HttpMethod.GET, getHttpEntity(), String.class);
    List<ImageModel> images = new ArrayList<>();
    List<Map<String, Object>> results = getResults(response.getBody());
    for (Map<String, Object> result : results) {
      Map<String, Object> imagesUrls = (Map<String, Object>) result.get("urls");
      images.add(
          new ImageModel(
              Integer.parseInt(result.get("id").toString()), imagesUrls.get("raw").toString()));
    }
    return images;
  }

  private String getUrl(String query, int page, int perPage) {
    return String.format(
        "https://api.unsplash.com/search/photos?query={%s}&page={%d}&per_page={%d}",
        query, page, perPage);
  }

  private HttpEntity<Void> getHttpEntity() {
    HttpHeaders headers = new HttpHeaders();
    headers.add("Authorization", "Client-ID " + ACCESS_KEY);
    return new HttpEntity<>(headers);
  }

  private List<Map<String, Object>> getResults(String body) throws JsonProcessingException {
    ObjectMapper mapper = new ObjectMapper();
    JsonNode rootNode = mapper.readTree(body);
    JsonNode contentNode = rootNode.path("results");
    String jsonString = contentNode.toString();
    return mapper.readValue(jsonString, new TypeReference<>() {});
  }
}
