package org.karthick.socialpostagent;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.AllArgsConstructor;
import org.karthick.socialpostagent.dto.*;
import org.karthick.socialpostagent.entity.Post;
import org.karthick.socialpostagent.enums.Platform;
import org.karthick.socialpostagent.enums.Status;
import org.karthick.socialpostagent.image.ImageProvider;
import org.karthick.socialpostagent.llm.LLMProvider;
import org.karthick.socialpostagent.llm.model.ChatModel;
import org.karthick.socialpostagent.llm.model.MessageModel;
import org.karthick.socialpostagent.model.ImageModel;
import org.karthick.socialpostagent.model.PostModel;
import org.karthick.socialpostagent.scheduler.Scheduler;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class PostService {
  private final PostRepository postRepository;

  private final LLMProvider llmProvider;
  private final ImageProvider imageProvider;
  private final Scheduler scheduler;

  /** Chat prompts */
  private String getPostPrompt(String brand, String tone, String platforms) {
    return String.format(
        "You are an expert social-media copywriter for %s.\nTone: %s.\nAlways return JSON exactly with keys:\n %s, hashtags (list)\n/no_think",
        brand, tone, platforms);
  }

  private String getImagePrompt(String content) {
    return String.format(
        "Suggest a single Unsplash search query describing an on-brand image for this post:\n%s\nReturn only the query string, no extra words. /no_think",
        content);
  }

  /** Chat prompts */
  public Post findPostById(String postId) {
    Optional<Post> postOptional = postRepository.findById(postId);
    if (postOptional.isEmpty()) {
      throw new RuntimeException("Post not found");
    }
    return postOptional.get();
  }

  public PostModel findPostModelById(String postId) {
    return findPostById(postId).getUserContent();
  }

  // 1. Generate captions + hashtags
  public Post createPost(GeneratePostDTO generatePostDTO) throws JsonProcessingException {
    String platformsStr =
        generatePostDTO.getPlatforms().stream()
            .map(Platform::getValue)
            .collect(Collectors.joining(", "));
    String prompt =
        getPostPrompt(generatePostDTO.getBrand(), generatePostDTO.getTone(), platformsStr);
    MessageModel system = new MessageModel("system", prompt);
    MessageModel user = new MessageModel("user", generatePostDTO.getBrief());
    System.out.println("Generating Post text...");
    ChatModel chatModel = new ChatModel();
    chatModel.setMessages(List.of(system, user));
    chatModel.setTemperature(0.7);
    String content = removeObstacles(llmProvider.chat(chatModel));
    Post post = new Post();
    post.setBrief(generatePostDTO.getBrief());
    post.setContent(content);
    post.setPlatforms(generatePostDTO.getPlatforms());
    post.setStatus(Status.DRAFT);
    return postRepository.save(post);
  }

  private String removeObstacles(String content) {
    return content.replace("```json\n", "").replace("\n```", "");
  }

  public void savePost(PostDTO postDTO) {
    Post post = findPostById(postDTO.getId());
    String userContent =
        ("{\"linkedin\" : \""
            + postDTO.getLinkedin()
            + "\", \"instagram\" : \""
            + postDTO.getInstagram()
            + "\", \"twitter\" : \""
            + postDTO.getTwitter()
            + "}");
    userContent = userContent.replace("\n", "\\n");
    post.setContent(userContent);
    postRepository.save(post);
  }

  public void savePostImages(String postId, List<ImageModel> imageModels) {
    Post post = findPostById(postId);
    post.setImages(imageModels);
    postRepository.save(post);
  }

  // 2. Suggest Unsplash image
  public List<ImageModel> suggestImages(String postId, int page, int perPage)
      throws JsonProcessingException {
    System.out.println("Generating Post images...");
    Post post = findPostById(postId);
    MessageModel messageModel = new MessageModel("user", getImagePrompt(post.getContent()));
    ChatModel chatModel = new ChatModel();
    chatModel.setMessages(List.of(messageModel));
    chatModel.setTemperature(0.5);
    chatModel.setMax_tokens(20);
    String query = llmProvider.chat(chatModel);
    return imageProvider.search(query, page, perPage);
  }

  public List<Platform> selectedPlatforms(String postId) {
    Post post = findPostById(postId);
    return post.getPlatforms();
  }

  // 3. Schedule after human approval
  public void schedulePost(String postId, ScheduleDTO scheduleDTO) {
    Post post = findPostById(postId);
    scheduler.schedule(post, scheduleDTO.toString(), post.getPlatforms());
    post.setSchedule(scheduleDTO.toString());
    post.setStatus(Status.SCHEDULED);
    postRepository.save(post);
  }

  public ScheduleDTO getSchedule(String postId) {
    Post post = findPostById(postId);
    return new ScheduleDTO(post.getSchedule());
  }

  public List<PostListDTO> postList() {
    List<Post> posts = postRepository.findAll();
    List<PostListDTO> postList1 = new ArrayList<>();
    for (Post post : posts) {
      postList1.add(
          new PostListDTO(
              post.getId(),
              post.getBrief(),
              post.getStatus(),
              post.getSchedule(),
              post.getPlatforms()));
    }
    return postList1;
  }

  public PostPreviewDTO postPreview(String postId) {
    Post post = findPostById(postId);
    PostModel userContent = post.getUserContent();
    List<ImageModel> images = post.getImages();
    return new PostPreviewDTO(
        post.getId(), userContent, images, post.getSchedule(), post.getPlatforms());
  }
}
