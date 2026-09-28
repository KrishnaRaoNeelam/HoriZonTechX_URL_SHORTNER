package com.cwp.shorturl.repository;

import com.cwp.shorturl.entity.UrlEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UrlRepository extends JpaRepository<UrlEntity, Long> {
    UrlEntity findByShortUrl(String shortUrl);
}
