package org.karthick.socialpostagent;

import org.karthick.socialpostagent.dto.PostListDTO;
import org.karthick.socialpostagent.entity.Post;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface PostRepository extends MongoRepository<Post, String> {
  @Query(value = "{}", fields = "{ _id: 1, brief: 1, status: 1, schedule: 1, platforms: 1 }")
  List<PostListDTO> findPostList();
}
