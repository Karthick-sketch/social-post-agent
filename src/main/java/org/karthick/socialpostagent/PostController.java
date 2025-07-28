package org.karthick.socialpostagent;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.AllArgsConstructor;
import org.karthick.socialpostagent.dto.*;
import org.karthick.socialpostagent.entity.Post;
import org.karthick.socialpostagent.enums.Platform;
import org.karthick.socialpostagent.model.ImageModel;
import org.karthick.socialpostagent.model.PostModel;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/post")
@AllArgsConstructor
public class PostController {
  private PostService postService;

  @PostMapping("/generate")
  public Post generatePost(@RequestBody GeneratePostDTO generatePostDTO)
      throws JsonProcessingException {
    return postService.createPost(generatePostDTO);
  }

  @PutMapping("/save")
  public void savePost(@RequestBody PostDTO postDTO) {
    postService.savePost(postDTO);
  }

  @GetMapping("/{postId}")
  public PostModel findPostModelById(@PathVariable String postId) {
    return postService.findPostModelById(postId);
  }

  @GetMapping("/{postId}/suggest-images")
  public List<ImageModel> suggestImages(@PathVariable String postId, int page, int perPage)
      throws JsonProcessingException {
    return postService.suggestImages(postId, page, perPage);
  }

  @PutMapping("/{postId}/save-post-images")
  public void savePostImages(@PathVariable String postId, @RequestBody List<ImageModel> model) {
    postService.savePostImages(postId, model);
  }

  @GetMapping("/{postId}/platforms")
  public List<Platform> selectedPlatforms(@PathVariable String postId) {
    return postService.selectedPlatforms(postId);
  }

  @GetMapping("/{postId}/schedule")
  public ScheduleDTO getSchedule(@PathVariable String postId) {
    return postService.getSchedule(postId);
  }

  @PostMapping("/{postId}/schedule")
  public void schedulePost(@PathVariable String postId, @RequestBody ScheduleDTO scheduleDTO) {
    postService.schedulePost(postId, scheduleDTO);
  }

  @GetMapping("/list")
  public List<PostListDTO> postList() {
    return postService.postList();
  }

  @GetMapping("/{postId}/preview")
  public PostPreviewDTO postPreview(@PathVariable String postId) {
    return postService.postPreview(postId);
  }
}
