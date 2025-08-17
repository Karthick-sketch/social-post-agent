package org.karthick.socialpostagent;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.AllArgsConstructor;
import org.karthick.socialpostagent.builder.PostBuilder;
import org.karthick.socialpostagent.dto.*;
import org.karthick.socialpostagent.entity.LLMChatLog;
import org.karthick.socialpostagent.entity.Post;
import org.karthick.socialpostagent.enums.ChatType;
import org.karthick.socialpostagent.enums.Platform;
import org.karthick.socialpostagent.enums.Status;
import org.karthick.socialpostagent.image.ImageProvider;
import org.karthick.socialpostagent.llm.ChatPrompt;
import org.karthick.socialpostagent.llm.LLMProvider;
import org.karthick.socialpostagent.llm.model.ChatModel;
import org.karthick.socialpostagent.llm.model.MessageModel;
import org.karthick.socialpostagent.builder.ChatModelBuilder;
import org.karthick.socialpostagent.model.ImageModel;
import org.karthick.socialpostagent.model.PostModel;
import org.karthick.socialpostagent.scheduler.Scheduler;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class PostService {
  private final PostRepository postRepository;
  private final LLMChatLogRepository llmChatLogRepository;

  private final LLMProvider llmProvider;
  private final ImageProvider imageProvider;
  private final Scheduler scheduler;

  public void logChatResponse(String response, ChatType chatType) {
    llmChatLogRepository.save(new LLMChatLog(response, chatType));
  }

  public Post findPostById(String postId) {
    Optional<Post> postOptional = postRepository.findById(postId);
    if (postOptional.isEmpty()) {
      throw new RuntimeException("Post not found");
    }
    return postOptional.get();
  }

  public PostModel findPostModelById(String postId) {
    return findPostById(postId).getContent();
  }

  /** 1. Generate captions + hashtags */
  public Post createPost(GeneratePostDTO generatePostDTO) throws JsonProcessingException {
    String platformsStr = generatePostDTO.getPlatforms().toString().toLowerCase();
    String prompt =
        ChatPrompt.postPrompt(generatePostDTO.getBrand(), generatePostDTO.getTone(), platformsStr);
    MessageModel system = new MessageModel("system", prompt);
    MessageModel user = new MessageModel("user", generatePostDTO.getBrief());
    ChatModel chatModel = ChatModelBuilder.buildChatModel(List.of(system, user), 0.7);
    System.out.println("Generating Post text...");
    String content = llmProvider.chat(chatModel);
    logChatResponse(content, ChatType.CONTENT);
    return postRepository.save(
        PostBuilder.buildPost(
            generatePostDTO.getBrief(), removeObstacles(content), generatePostDTO.getPlatforms()));
  }

  /** delete the leading ```json\n (8 characters) & delete the trailing \n``` (4 characters) */
  private String removeObstacles(String content) {
    return content.substring(8, content.length() - 4);
  }

  public void savePost(PostDTO postDTO) {
    Post post = findPostById(postDTO.getId());
    post.setContent(postDTOToModel(postDTO));
    postRepository.save(post);
  }

  private PostModel postDTOToModel(PostDTO postDTO) {
    return new PostModel(postDTO.getLinkedin(), postDTO.getInstagram(), postDTO.getX());
  }

  public void savePostImages(String postId, List<ImageModel> imageModels) {
    Post post = findPostById(postId);
    post.setImages(imageModels);
    postRepository.save(post);
  }

  /** 2. Suggest Unsplash image */
  public List<ImageModel> suggestImages(String postId, int page, int perPage)
      throws JsonProcessingException {
    Post post = findPostById(postId);
    MessageModel system = new MessageModel("system", ChatPrompt.imageSystemPrompt());
    MessageModel user =
        new MessageModel(
            "user",
            ChatPrompt.imageUserPrompt(PostBuilder.postModelToJsonString(post.getContent())));
    ChatModel chatModel = ChatModelBuilder.buildChatModel(List.of(system, user), 0.5);
    System.out.println("Generating Post images...");
    String query = llmProvider.chat(chatModel);
    logChatResponse(query, ChatType.IMAGE);
    post.setImages(imageProvider.search(query, page, perPage));
    return postRepository.save(post).getImages();
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
    return postRepository.findPostList();
  }

  public PostPreviewDTO postPreview(String postId) {
    Post post = findPostById(postId);
    PostModel userContent = post.getContent();
    List<ImageModel> images = post.getImages();
    return new PostPreviewDTO(
        post.getId(), userContent, images, post.getSchedule(), post.getPlatforms());
  }
}
