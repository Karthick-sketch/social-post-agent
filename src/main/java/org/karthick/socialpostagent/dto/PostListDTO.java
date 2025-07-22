package org.karthick.socialpostagent.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.karthick.socialpostagent.enums.Platform;
import org.karthick.socialpostagent.enums.Status;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class PostListDTO {
  String id;
  String brief;
  Status status;
  String schedule;
  List<Platform> platforms;
}
