package org.karthick.socialpostagent.entity;

import lombok.Getter;
import lombok.Setter;
import org.karthick.socialpostagent.enums.Platform;
import org.karthick.socialpostagent.enums.Status;
import org.karthick.socialpostagent.model.ImageModel;
import org.karthick.socialpostagent.model.PostModel;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Getter
@Setter
@Document(collection = "posts")
public class Post {
  @Id String id;
  String brief;
  String content;
  List<Platform> platforms;
  PostModel userContent;
  List<ImageModel> images;
  String schedule;
  Status status;
  String created_at;
  String updated_at;
}
