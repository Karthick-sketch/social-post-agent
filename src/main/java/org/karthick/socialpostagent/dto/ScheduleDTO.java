package org.karthick.socialpostagent.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ScheduleDTO {
  String date;
  String time;

  public ScheduleDTO(String when) {
    String[] split = when.split("T");
    this.date = split[0];
    this.time = split[1];
  }

  @Override
  public String toString() {
    return String.format("%sT%s", this.date, this.time);
  }
}
