package org.havoc.skilltrack;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.TimeZone;

@Slf4j
@SpringBootApplication
@EnableScheduling
public class SkillTrackBackendApplication {
    @PostConstruct
    public void init() {
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Kolkata"));
        log.info("✅ Default timezone set to IST");
    }


    public static void main(String[] args) {
        SpringApplication.run(SkillTrackBackendApplication.class, args);
    }

}
