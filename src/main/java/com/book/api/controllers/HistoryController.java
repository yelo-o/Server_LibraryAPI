package com.book.api.controllers;

import com.book.api.dto.BookDto;
import com.book.api.dto.HistoryDto;
import com.book.api.dto.PageResponse;
import com.book.api.service.HisotryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/history")
@RequiredArgsConstructor
public class HistoryController {
    private final HisotryService hisotryService;

    @GetMapping
//    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    public ResponseEntity<PageResponse<?>> getHistories(
            @RequestParam(value = "pageNo", defaultValue = "0", required = false) int pageNo,
            @RequestParam(value = "pageSize", defaultValue = "5", required = false) int pageSize) {
        return new ResponseEntity<>(hisotryService.getAllHistory(pageNo, pageSize), HttpStatus.OK);
    }

    @GetMapping("/{id}")
//    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    public ResponseEntity<HistoryDto> historyDetail(@PathVariable Long id) {
        return ResponseEntity.ok(hisotryService.getHistoryById(id));
    }

}
