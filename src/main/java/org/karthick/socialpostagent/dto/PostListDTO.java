package org.karthick.socialpostagent.dto;

import lombok.Getter;
import lombok.Setter;
import org.karthick.socialpostagent.enums.Status;

import java.util.List;

@Getter
@Setter
public class PostListDTO {
  String id;
  String brief;
  Status status;
  String schedule;
  List<String> platforms;
}
