package org.karthick.socialpostagent.image;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.karthick.socialpostagent.model.ImageModel;

import java.util.List;

public interface ImageProvider {
  List<ImageModel> search(String query, int page, int per_page) throws JsonProcessingException;
}
