package org.karthick.socialpostagent.scheduler;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.karthick.socialpostagent.entity.Post;

import java.util.List;

public interface Scheduler {
  List<String> getSocialAccounts() throws JsonProcessingException;

  void post(Post post);

  void schedule(Post post);
}
