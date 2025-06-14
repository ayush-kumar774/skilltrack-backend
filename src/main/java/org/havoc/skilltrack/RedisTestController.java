package org.havoc.skilltrack;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;

@RestController
@RequestMapping("/api/redis")
@RequiredArgsConstructor
public class RedisTestController {

    private final RedisTemplate<String, Object> redisTemplate;

    @GetMapping("/ping")
    public ResponseEntity<String> pingRedis() {
        redisTemplate.opsForValue().set("testKey", "SkillTrack 🚀", Duration.ofMinutes(5));
        String value = (String) redisTemplate.opsForValue().get("testKey");
        return ResponseEntity.ok("Redis says: " + value);
    }
}
