package org.karthick.socialpostagent.scheduler;

import org.karthick.socialpostagent.entity.Post;
import org.karthick.socialpostagent.enums.Platform;

import java.util.List;
import java.util.Map;

public interface Scheduler {
  void post(Map<String, String> post, List<Platform> platforms);

  void schedule(Post post, String when, List<Platform> platforms);
}
