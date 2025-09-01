package org.karthick.socialpostagent.scheduler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.karthick.socialpostagent.entity.Post;
import org.karthick.socialpostagent.model.ImageModel;
import org.karthick.socialpostagent.model.PostModel;
import org.karthick.socialpostagent.scheduler.model.AryshareModel;
import org.karthick.socialpostagent.scheduler.model.AryshareScheduleModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class AryshareService implements Scheduler {
  private final String BASE_URL = "https://api.ayrshare.com/api";

  private final RestTemplate restTemplate;

  @Value("${scheduler.aryshare.api-key}")
  private String AYRSHARE_API_KEY;

  public AryshareService(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  @Override
  public List<String> getSocialAccounts() throws JsonProcessingException {
    HttpEntity<String> entity = new HttpEntity<>(getHttpHeaders());
    String userDetails = getUserDetails(entity);
    return getActiveSocialAccounts(userDetails);
  }

  @Override
  public void post(Post post) {
    PostModel postModel = post.getContent();
    List<ImageModel> imageModels = post.getImages();
    for (String platform : post.getPlatforms()) {
      String content = getContent(postModel, platform);
      AryshareModel payload = getPayload(content, platform, imageModels);
      HttpEntity<AryshareModel> entity = new HttpEntity<>(payload, getHttpHeaders());
      String response = publishPost(entity);
      System.out.println(response);
    }
  }

  @Override
  public void schedule(Post post) {
    PostModel postModel = post.getContent();
    List<ImageModel> imageModels = post.getImages();
    for (String platform : post.getPlatforms()) {
      String content = getContent(postModel, platform);
      AryshareModel payload = getPayload(content, platform, imageModels, post.getSchedule());
      HttpEntity<AryshareModel> entity = new HttpEntity<>(payload, getHttpHeaders());
      String response = publishPost(entity);
      System.out.println(response);
    }
  }

  private String getUserDetails(HttpEntity<String> entity) {
    String url = BASE_URL + "/user";
    ResponseEntity<String> response =
        restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
    return response.getBody();
  }

  private List<String> getActiveSocialAccounts(String body) throws JsonProcessingException {
    ObjectMapper mapper = new ObjectMapper();
    JsonNode rootNode = mapper.readTree(body);
    JsonNode contentNode = rootNode.path("activeSocialAccounts");
    String jsonString = contentNode.toString();
    return mapper.readValue(jsonString, new TypeReference<>() {});
  }

  private String publishPost(HttpEntity<AryshareModel> entity) {
    String url = BASE_URL + "/post";
    ResponseEntity<String> response =
        restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
    return response.getBody();
  }

  private HttpHeaders getHttpHeaders() {
    HttpHeaders headers = new HttpHeaders();
    headers.add("Authorization", "Bearer " + AYRSHARE_API_KEY);
    headers.add("Content-Type", "application/json");
    return headers;
  }

  private String getContent(PostModel postModel, String platform) {
    return switch (platform) {
      case "linkedin" -> postModel.getLinkedin();
      case "instagram" -> postModel.getInstagram();
      default -> postModel.getX();
    };
  }

  private AryshareModel getPayload(String content, String platform, List<ImageModel> images) {
    String platforms = getPlatforms(platform);
    String imageUrls = getImageUrls(images);
    return AryshareModel.builder().post(content).platforms(platforms).mediaUrls(imageUrls).build();
  }

  private AryshareScheduleModel getPayload(
      String content, String platform, List<ImageModel> images, String when) {
    String platforms = getPlatforms(platform);
    String imageUrls = getImageUrls(images);
    return AryshareScheduleModel.builder()
        .post(content)
        .platforms(platforms)
        .mediaUrls(imageUrls)
        .scheduleDate(when)
        .build();
  }

  private String getPlatforms(String platform) {
    return "[\"" + platform + "\"]";
  }

  private String getImageUrls(List<ImageModel> imageModels) {
    String imageUrlsString =
        imageModels.stream()
            .filter(ImageModel::isSelected)
            .map(ImageModel::getUrl)
            .collect(Collectors.joining("\", \""));
    return "[" + (imageUrlsString.isEmpty() ? "" : "\"" + imageUrlsString + "\"") + "]";
  }
}
