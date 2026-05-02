package net.dima.project.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "reply")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReplyEntity {

    @Id
    @Column(name = "board_seq")
    private Long boardSeq;  // PK이자 FK

    @OneToOne
    @MapsId  // board_seq를 PK로 사용
    @JoinColumn(name = "board_seq")
    private BoardEntity board;  // BoardEntity에서 mappedBy로 연결됨

    @Column(name = "reply_content", nullable = false, length = 1000)
    private String replyContent;

    @Column(name = "create_date")
    @CreationTimestamp
    private LocalDateTime createDate;

    public static ReplyEntity toEntity(BoardEntity board, String replyContent) {
        return ReplyEntity.builder()
                .board(board)
                .replyContent(replyContent)
                .build();
    }
}
