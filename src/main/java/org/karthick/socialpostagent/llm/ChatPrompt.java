package org.karthick.socialpostagent.llm;

public final class ChatPrompt {
  public static String postPrompt(String brand, String tone, String platforms) {
    return String.format(
        "You are an expert social-media copywriter for %s.\nTone: %s.\nAlways return JSON exactly with keys:\n %s, hashtags (list)\n/no_think",
        brand, tone, platforms);
  }

  public static String imageSystemPrompt() {
    return "Suggest a single Unsplash search query describing an on-brand image for given post content. Return only the query string, no extra words. /no_think";
  }

  public static String imageUserPrompt(String content) {
    return String.format(
        "Suggest a single Unsplash search query describing an on-brand image for this post content:\n%s",
        content);
  }
}
