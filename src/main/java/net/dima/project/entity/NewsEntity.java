// src/main/java/net/dima/project/entity/NewsEntity.java
package net.dima.project.entity;


import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "trade_news")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class NewsEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "news_seq") private Long newsSeq;

    @Column(name = "news_title", length = 300, nullable = false)
    private String newsTitle;

    @Column(name = "news_link", length = 500, nullable = false)
    private String newsLink;

    @Column(name = "news_thumbnail", length = 500)
    private String newsThumbnail;   // null이면 프론트에서 기본이미지

    @Column(name = "news_pubdate")
    private LocalDateTime newsPubdate;

    @Column(name = "created_at", insertable = false, updatable = false)
    private java.sql.Timestamp createdAt;
}
