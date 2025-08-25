package org.karthick.socialpostagent.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class GeneratePostDTO {
  String brief;
  String brand;
  String tone;
  List<String> platforms;
}
