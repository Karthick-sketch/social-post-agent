package org.karthick.socialpostagent.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ImageModel {
  long id;
  String url;
  boolean selected;

  public ImageModel(long id, String url) {
    this.id = id;
    this.url = url;
    this.selected = false;
  }
}
