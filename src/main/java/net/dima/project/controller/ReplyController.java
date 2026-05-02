package net.dima.project.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dima.project.dto.LoginUserDetails;
import net.dima.project.dto.ReplyDTO;
import net.dima.project.service.ReplyService;

@Controller
@RequestMapping("/reply")
@RequiredArgsConstructor
@Slf4j
public class ReplyController {

    private final ReplyService replyService;

    /** 답변 등록 (폼 POST) */
    @PostMapping("/insertReply")
    public String writeReply(@ModelAttribute ReplyDTO replyDTO,
                             @AuthenticationPrincipal LoginUserDetails loginUser,
                             @RequestParam(name = "searchCategory", defaultValue = "boardTitle") String searchCategory,
                             @RequestParam(name = "searchKeyword", defaultValue = "") String searchKeyword) {

        log.info("답변 등록 요청: {}", replyDTO);

        // 로그인/권한 체크 + 빈 내용 방어
        if (loginUser == null || !"ROLE_MANAGER".equals(loginUser.getUser().getRoles())) {
            return "redirect:/accessDenied";
        }
        String content = replyDTO.getReplyContent() == null ? "" : replyDTO.getReplyContent().trim();
        if (content.isEmpty()) {
            return "redirect:/board/boardDetail?boardSeq=" + replyDTO.getBoardSeq();
        }

        replyDTO.setReplyContent(content);
        replyService.insertReply(replyDTO);

        return "redirect:/board/boardDetail?boardSeq=" + replyDTO.getBoardSeq()
                + "&searchCategory=" + searchCategory
                + "&searchKeyword=" + searchKeyword;
    }

    /** 답변 수정 (PUT JSON) */
    @PutMapping("/update")
    public ResponseEntity<String> updateReply(@AuthenticationPrincipal LoginUserDetails loginUser,
                                              @RequestBody Map<String, Object> payload) {

        if (loginUser == null || !"ROLE_MANAGER".equals(loginUser.getUser().getRoles())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("접근 권한이 없습니다.");
        }

        Long boardSeq = Long.valueOf(String.valueOf(payload.get("boardSeq")));
        String content = String.valueOf(payload.getOrDefault("replyContent","")).trim();
        log.info("답변 수정 요청: boardSeq={}, replyContent='{}'", boardSeq, content);

        if (content.isEmpty()) {
            return ResponseEntity.badRequest().body("내용이 비었습니다.");
        }

        replyService.updateReply(boardSeq, content);
        return ResponseEntity.ok("수정 성공");
    }

    /** 답변 삭제 (DELETE ?boardSeq=) */
    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteReply(@AuthenticationPrincipal LoginUserDetails loginUser,
                                            @RequestParam("boardSeq") Long boardSeq) {

        log.info("답변 삭제 요청: boardSeq={}", boardSeq);

        if (loginUser == null || !"ROLE_MANAGER".equals(loginUser.getUser().getRoles())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        replyService.deleteReply(boardSeq);
        return ResponseEntity.noContent().build(); // 204
    }
}
