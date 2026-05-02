// src/main/java/net/dima/project/service/NewsService.java
package net.dima.project.service;

import lombok.RequiredArgsConstructor;
import net.dima.project.entity.NewsEntity;
import net.dima.project.repository.NewsRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NewsService {
    private final NewsRepository repo;
    
    // 첫 페이지에서 3개만 가져오기
    public List<NewsEntity> latest3() {
        return repo.findLatest(PageRequest.of(0, 3)); 
    }
    
    // 뉴스 목록 페이지용 (검색 + 페이징)
    public NewsListResult list(String category, String keyword, int page1, int size) {
        int page0 = Math.max(page1 - 1, 0);
        var pageable = PageRequest.of(page0, size);

        boolean hasKw = keyword != null && !keyword.isBlank();
        String kw = hasKw ? keyword.trim() : null;

        List<NewsEntity> content;
        long total;

        if (!hasKw || category == null || category.isBlank()) {
            content = repo.findLatest(pageable);
            total = repo.count();
        } else {
            switch (category) {
                case "newsTitle" -> {
                    content = repo.findByNewsTitleContainingIgnoreCase(kw, pageable);
                    total = repo.countByNewsTitleContainingIgnoreCase(kw);
                }
                case "newsPubdate" -> {
                	try {
                        // keyword → 날짜 변환 (예: "2025-08-10")
                        var date = java.time.LocalDate.parse(kw);
                        var start = date.atStartOfDay();
                        var end = date.atTime(23, 59, 59);

                        content = repo.findByNewsPubdateBetween(start, end, pageable);
                        total = repo.countByNewsPubdateBetween(start, end);
                    } catch (Exception e) {
                        // 날짜 파싱 실패 시 빈 결과
                        content = List.of();
                        total = 0;
                    }
                }
                case "nSeq" -> {
                    try {
                        var one = repo.findById(Long.valueOf(kw)).orElse(null);
                        content = (one == null) ? List.of() : List.of(one);
                        total = (one == null) ? 0 : 1;
                    } catch (NumberFormatException e) {
                        content = List.of();
                        total = 0;
                    }
                }
                default -> {
                    content = repo.findLatest(pageable);
                    total = repo.count();
                }
            }
        }

        int totalPages = (int) Math.ceil((double) total / size);
        return new NewsListResult(content, total, totalPages);
    }

    // 목록 응답용 DTO
    public record NewsListResult(List<NewsEntity> content, long totalCount, int totalPages) {}
}
