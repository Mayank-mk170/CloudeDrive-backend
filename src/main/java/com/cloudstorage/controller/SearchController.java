package com.cloudstorage.controller;

import com.cloudstorage.model.File;
import com.cloudstorage.model.User;
import com.cloudstorage.service.SearchService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/files")
public class SearchController {

    private final SearchService searchService;

    public SearchController(SearchService searchService) {
        this.searchService = searchService;
    }

    // ==========================================
    // SEARCH FILES
    // GET /api/files/search
    // ==========================================

    @GetMapping("/search")
    public ResponseEntity<?> searchFiles(
            @RequestParam(defaultValue = "") String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {

        Object principal =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication()
                        .getPrincipal();

        User user = (User) principal;

        Page<File> result =
                searchService.searchFiles(
                        user.getEmail(),
                        name,
                        page,
                        size
                );

        return ResponseEntity.ok(result);
    }
}