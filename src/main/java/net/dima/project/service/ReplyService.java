package net.dima.project.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import net.dima.project.dto.ReplyDTO;
import net.dima.project.entity.BoardEntity;
import net.dima.project.entity.ReplyEntity;
import net.dima.project.repository.BoardRepository;
import net.dima.project.repository.ReplyRepository;

@Service
@RequiredArgsConstructor
public class ReplyService {
    private final BoardRepository boardRepository;
    private final ReplyRepository replyRepository;

    /** 댓글 저장 (1:1) + 게시글 상태 변경 */
    @Transactional
    public void insertReply(ReplyDTO replyDTO) {
        BoardEntity board = boardRepository.findById(replyDTO.getBoardSeq())
                .orElseThrow(() -> new IllegalArgumentException("게시글이 존재하지 않습니다."));

        // 내용 방어
        String content = replyDTO.getReplyContent() == null ? "" : replyDTO.getReplyContent().trim();
        if (content.isEmpty()) return;

        // 이미 답변 존재하면 무시(공유 PK 구조에서는 existsById가 가장 확실)
        if (replyRepository.existsById(board.getBoardSeq())) return;

        ReplyEntity reply = ReplyEntity.builder()
                .board(board)            // @MapsId로 PK=board_seq
                .replyContent(content)
                .build();

        // 양방향 일관성(BoardEntity에 setReply(reply) 헬퍼 있으면 그걸 사용)
        board.setReply(reply);

        replyRepository.save(reply);

        board.setBoardStatus("답변완료");
        board.setUpdateDate(LocalDateTime.now());
        boardRepository.save(board);
    }

    /** 댓글 조회 (1:1) */
    @Transactional
    public ReplyDTO selectReply(Long boardSeq) {
        return replyRepository.findById(boardSeq)
                .map(ReplyDTO::toDTO)
                .orElse(null);
    }

    /** 댓글 수정 (1:1) */
    @Transactional
    public void updateReply(Long boardSeq, String replyContent) {
        String content = replyContent == null ? "" : replyContent.trim();
        if (content.isEmpty()) return;

        replyRepository.findById(boardSeq).ifPresent(r -> {
            r.setReplyContent(content);
            // 필요시 게시글 업데이트 시간도 갱신
            BoardEntity b = r.getBoard();
            b.setUpdateDate(LocalDateTime.now());
            boardRepository.save(b);
        });
    }

    /** 댓글 삭제 (1:1) + 게시글 상태 복원 */
    @Transactional
    public void deleteReply(Long boardSeq) {
        replyRepository.findById(boardSeq).ifPresent(r -> {
            BoardEntity b = r.getBoard();

            // 양방향 끊기
            b.setReply(null);

            replyRepository.deleteById(boardSeq);

            b.setBoardStatus("진행중");
            b.setUpdateDate(LocalDateTime.now());
            boardRepository.save(b);
        });
    }
}