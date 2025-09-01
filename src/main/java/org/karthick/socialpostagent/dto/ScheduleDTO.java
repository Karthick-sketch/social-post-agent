package org.karthick.socialpostagent.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ScheduleDTO {
  private String date;
  private String time;

  public ScheduleDTO(String when) {
    String[] split = when.split("T");
    this.date = split[0];
    this.time = split[1];
  }

  @Override
  public String toString() {
    return String.format("%sT%s:00Z", this.date, this.time);
  }
}
