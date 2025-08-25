package org.karthick.socialpostagent.builder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.karthick.socialpostagent.entity.Post;
import org.karthick.socialpostagent.enums.Status;
import org.karthick.socialpostagent.model.PostContentModel;
import org.karthick.socialpostagent.model.PostModel;

import java.util.List;

public final class PostBuilder {
  public static Post buildPost(String brief, String content, List<String> platforms)
      throws JsonProcessingException {
    Post post = new Post();
    post.setBrief(brief);
    post.setContent(mergeHashtags(buildPostContentModel(content)));
    post.setPlatforms(platforms);
    post.setStatus(Status.DRAFT);
    return post;
  }

  public static String postModelToJsonString(PostModel postModel) {
    String post = "{ ";
    if (postModel.getLinkedin() != null) {
      post += "\"linkedin\" : \"" + postModel.getLinkedin() + "\",";
    }
    if (postModel.getInstagram() != null) {
      post += "\"instagram\" : \"" + postModel.getInstagram() + "\",";
    }
    if (postModel.getX() != null) {
      post += "\"x\" : \"" + postModel.getX() + "\",";
    }
    return post + " }";
  }

  private static PostContentModel buildPostContentModel(String content)
      throws JsonProcessingException {
    ObjectMapper mapper = new ObjectMapper();
    return mapper.readValue(content, PostContentModel.class);
  }

  private static PostModel mergeHashtags(PostContentModel postContent) {
    PostModel postModel = new PostModel();
    String hashtags = "\n\n" + String.join(" ", postContent.getHashtags());
    if (postContent.getLinkedin() != null) {
      postModel.setLinkedin(postContent.getLinkedin() + hashtags);
    }
    if (postContent.getInstagram() != null) {
      postModel.setInstagram(postContent.getInstagram() + hashtags);
    }
    if (postContent.getX() != null) {
      postModel.setX(postContent.getX() + hashtags);
    }
    return postModel;
  }
}
