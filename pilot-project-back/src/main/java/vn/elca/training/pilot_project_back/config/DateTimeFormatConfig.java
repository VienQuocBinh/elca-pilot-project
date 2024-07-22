package vn.elca.training.pilot_project_back.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.text.SimpleDateFormat;

@Configuration
public class DateTimeFormatConfig {
    @Value("${date.format}")
    private String dateFormat;

    @Bean
    public SimpleDateFormat simpleDateFormat() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);
        simpleDateFormat.setLenient(false);
        return simpleDateFormat;
    }
}
