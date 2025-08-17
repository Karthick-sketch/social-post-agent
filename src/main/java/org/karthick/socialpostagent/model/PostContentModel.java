package org.karthick.socialpostagent.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PostContentModel {
  private String linkedin;
  private String instagram;
  private String x;
  private List<String> hashtags;
}
