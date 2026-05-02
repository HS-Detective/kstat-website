package net.dima.project.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.dima.project.entity.ReplyEntity;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReplyDTO {

    private Long boardSeq;            // 게시글 번호 (PK & FK)
    private String replyContent;      // 답변 내용
    private LocalDateTime createDate; // 답변 작성일

    public static ReplyDTO toDTO(ReplyEntity replyEntity) {
        return ReplyDTO.builder()
                .boardSeq(replyEntity.getBoard().getBoardSeq())
                .replyContent(replyEntity.getReplyContent())
                .createDate(replyEntity.getCreateDate())
                .build();
    }
}
