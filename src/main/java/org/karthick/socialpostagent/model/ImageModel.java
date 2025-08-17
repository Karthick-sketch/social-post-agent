package org.karthick.socialpostagent.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ImageModel {
  private String id;
  private String url;
  private boolean selected;

  public ImageModel(String id, String url) {
    this.id = id;
    this.url = url;
    this.selected = false;
  }
}
