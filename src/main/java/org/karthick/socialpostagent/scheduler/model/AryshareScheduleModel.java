package org.karthick.socialpostagent.scheduler.model;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
public class AryshareScheduleModel extends AryshareModel {
  private String scheduleDate;
}
