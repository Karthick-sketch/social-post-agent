package org.karthick.socialpostagent.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.karthick.socialpostagent.enums.Platform;
import org.karthick.socialpostagent.model.ImageModel;
import org.karthick.socialpostagent.model.PostModel;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class PostPreviewDTO {
  String id;
  PostModel post;
  List<ImageModel> images;
  String schedule;
  List<Platform> platforms;
}
