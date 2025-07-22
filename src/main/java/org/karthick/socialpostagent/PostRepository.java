package org.karthick.socialpostagent;

import org.karthick.socialpostagent.entity.Post;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PostRepository extends MongoRepository<Post, String> {}
