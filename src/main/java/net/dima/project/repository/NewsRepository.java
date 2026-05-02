// src/main/java/net/dima/project/repository/NewsRepository.java
package net.dima.project.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import net.dima.project.entity.NewsEntity;

public interface NewsRepository extends JpaRepository<NewsEntity, Long> {

    //최신순: pubdate 없으면 created_at로 대체해 정렬
    @Query("""
        select n
        from NewsEntity n
        order by coalesce(n.newsPubdate, n.createdAt) desc
    """)
    List<NewsEntity> findLatest(Pageable pageable);

    // 전체 최신순
    @Query("""
        select n
        from NewsEntity n
        order by coalesce(n.newsPubdate, n.createdAt) desc
    """)
    List<NewsEntity> findAllOrderByDate();

    // ===== 검색 + 페이징 =====
    List<NewsEntity> findByNewsTitleContainingIgnoreCase(String keyword, Pageable pageable);


    // 날짜(하루/기간) 검색은 범위로
    List<NewsEntity> findByNewsPubdateBetween(
            LocalDateTime start, LocalDateTime end, Pageable pageable);

    // ===== 총 개수 (페이지네이션 계산용) =====
    long countByNewsTitleContainingIgnoreCase(String keyword);
    long countByNewsPubdateBetween(LocalDateTime start, LocalDateTime end);
}
