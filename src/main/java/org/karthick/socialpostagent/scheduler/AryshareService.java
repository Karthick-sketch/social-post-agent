package org.karthick.socialpostagent.scheduler;

import org.karthick.socialpostagent.entity.Post;
import org.karthick.socialpostagent.enums.Platform;
import org.karthick.socialpostagent.model.ImageModel;
import org.karthick.socialpostagent.model.PostModel;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class AryshareService implements Scheduler {
  private final String URL = "https://api.ayrshare.com/api/post";

  @Override
  public void post(Map<String, String> post, List<Platform> platforms) {
  }

  @Override
  public void schedule(Post post, String when, List<Platform> platforms) {
    PostModel postModel = post.getUserContent();
    List<ImageModel> imageModels = post.getImages();
    System.out.println(postModel);
    System.out.println(imageModels);
    // headers = {
    //     "Authorization": f"Bearer {os.getenv('AYRSHARE_API_KEY')}",
    //     "Content-Type": "application/json",
    // }
    // for platform in platforms:
    //     print(content[platform.value])
    //     payload = {
    //         "post": content[platform.value],
    //         "platforms": [platform.value],
    //         "mediaUrls": images,
    //         "scheduleDate": when,
    //     }
    //     response = requests.post(self.url, json=payload, headers=headers)
    //     print(response.json())
  }
}
