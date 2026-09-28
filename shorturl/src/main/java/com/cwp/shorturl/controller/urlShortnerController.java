package com.cwp.shorturl.controller;

import com.cwp.shorturl.service.UrlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;



@RestController
public class urlShortnerController {

    @Autowired
    private UrlService urlService;

    @PostMapping("v1/shortner")
    public ResponseEntity<UrlResponse> shortenUrl(@RequestBody UrlRequest request) {
        return urlService.generateShortUrl(request.getOriginalUrl());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Void> redirectToOriginalUrl(@PathVariable String id){
        String originalUrl = urlService.redirectToOriginalUrl(id);
        if(originalUrl != null){
            String headerName;
            return ResponseEntity.status(302).header( "Location", originalUrl).build();
        }
        else{
            return ResponseEntity.notFound().build();
        }
    }

}
