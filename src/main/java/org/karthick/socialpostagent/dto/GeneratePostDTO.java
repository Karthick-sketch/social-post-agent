package org.karthick.socialpostagent.dto;

import lombok.Getter;
import lombok.Setter;
import org.karthick.socialpostagent.enums.Platform;

import java.util.List;

@Getter
@Setter
public class GeneratePostDTO {
  String brief;
  String brand;
  String tone;
  List<Platform> platforms;
}
