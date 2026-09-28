package com.cwp.shorturl.service;

import com.cwp.shorturl.controller.UrlResponse;
import com.cwp.shorturl.entity.UrlEntity;
import com.cwp.shorturl.repository.UrlRepository;
import com.cwp.shorturl.utility.Base62Encoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class UrlService {

    @Autowired
    StringRedisTemplate redisTemplate;

    @Value("${cache.expiration.time}")
    private long redisExpirationTime;

    @Autowired
    UrlRepository urlRepository;

    public ResponseEntity<UrlResponse> generateShortUrl(String originalUrl) {

        // Validate URL
        if (originalUrl == null || originalUrl.trim().isEmpty()) {
            return ResponseEntity
                    .badRequest()
                    .body(new UrlResponse("Invalid URL"));
        }

        // Create entity
        UrlEntity urlEntity = new UrlEntity();

        // IMPORTANT: set original URL
        urlEntity.setOriginalUrl(originalUrl);

        // First save to generate ID
        UrlEntity urlEntityDB = urlRepository.save(urlEntity);

        // Generate Base62 short code from database ID
        String shorterUrl =
                "http://short.url/" + Base62Encoder.encode(urlEntityDB.getId());

        // Set generated short URL
        urlEntityDB.setShortUrl(shorterUrl);

        // Save again
        urlRepository.save(urlEntityDB);

        // Return response
        UrlResponse response = new UrlResponse(shorterUrl);

        return ResponseEntity.ok(response);
    }

    public String redirectToOriginalUrl(String id) {

        String shortUrl = "http://localhost:8080/" + id;
        String cachedValue = redisTemplate.opsForValue().get(shortUrl);
        if(cachedValue != null){
            return cachedValue;
        }
        UrlEntity urlEntity = urlRepository.findByShortUrl(shortUrl);
        if(urlEntity != null){
            redisTemplate.opsForValue().set(shortUrl, urlEntity.getOriginalUrl(), redisExpirationTime);
            return urlEntity.getOriginalUrl();
        }
        else{
            return null;
        }
    }
}