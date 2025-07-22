package org.karthick.socialpostagent.config;

import org.springframework.lang.NonNull;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;

import java.time.*;
import java.util.*;

@Configuration
public class MongoConfig extends AbstractMongoClientConfiguration {

  @Override
  protected @NonNull String getDatabaseName() {
    return "social_post_agent";
  }

  @Override
  public @NonNull MongoCustomConversions customConversions() {
    return new MongoCustomConversions(
            List.of(new LocalDateToDateConverter(), new DateToLocalDateConverter()));
  }

  static class LocalDateToDateConverter implements Converter<LocalDate, Date> {
    @Override
    public Date convert(LocalDate source) {
      return Date.from(source.atStartOfDay(ZoneOffset.UTC).toInstant());
    }
  }

  static class DateToLocalDateConverter implements Converter<Date, LocalDate> {
    @Override
    public LocalDate convert(Date source) {
      return source.toInstant().atZone(ZoneOffset.UTC).toLocalDate();
    }
  }
}
