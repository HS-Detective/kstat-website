package net.dima.project.controller;

import java.io.FileInputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dima.project.dto.BoardDTO;
import net.dima.project.dto.LoginUserDetails;
import net.dima.project.service.BoardService;
import net.dima.project.service.ReplyService;
import net.dima.project.util.Masking;
import net.dima.project.util.PageNavigator;
import org.springframework.data.domain.Sort;

@Controller
@RequestMapping("/board")
@Slf4j
@RequiredArgsConstructor
public class BoardController {
    private final BoardService service;
    private final ReplyService replyService;

    @Value("${user.board.pageLimit}") // 한 페이지 글 개수(예: 10)
    int pageLimit;

    // 파일 저장 경로
    @Value("${spring.servlet.multipart.location}")
    String uploadPath;

    // 글 목록
    @GetMapping("/boardList")
    public String boardList(
            @AuthenticationPrincipal LoginUserDetails loginUser,
            @PageableDefault(page = 1) Pageable pageable, // 뷰/쿼리스트링은 1부터
            @RequestParam(name="searchCategory", defaultValue = "boardTitle") String searchCategory,
            @RequestParam(name="searchKeyword", defaultValue = "") String searchKeyword,
            Model model) {

        // 뷰(1-based) → 내부(0-based)
        int requested = pageable.getPageNumber();          // 1,2,3...
        int pageIndex = Math.max(0, requested - 1);        // 0,1,2...
        int size = pageLimit;                              // 고정 사이즈(설정값)
        Pageable pr = PageRequest.of(pageIndex, size, Sort.by(Sort.Direction.DESC, "boardSeq"));

        Page<BoardDTO> list = service.selectAll(pr, searchCategory, searchKeyword);

        int totalPages  = list.getTotalPages();
        int currentPage = pageIndex + 1;                   // 다시 1-based로 뷰에 노출

        if (currentPage > totalPages && totalPages > 0) currentPage = totalPages;

        // PageNavigator는 1-based currentPage를 받도록 수정됨
        PageNavigator navi = new PageNavigator(currentPage, totalPages);

        model.addAttribute("boardList", list);
        model.addAttribute("totalCount", list.getTotalElements());
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("pageSize", list.getSize());
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("searchCategory", searchCategory);
        model.addAttribute("searchKeyword", searchKeyword);
        model.addAttribute("navi", navi);

        if (loginUser != null) {
            model.addAttribute("loginName", loginUser.getUserName());
        }

        return "board/boardList";
    }

    // 글 쓰기
    @GetMapping("/boardWrite")
    public String boardWrite(@AuthenticationPrincipal LoginUserDetails loginUser, Model model) {
        model.addAttribute("isEdit", false);
        model.addAttribute("board", new BoardDTO()); // 빈 객체
        if (loginUser != null) {
            model.addAttribute("loginName", loginUser.getUserName());        // 실명
            model.addAttribute("loginId", loginUser.getUsername());          // 아이디
            model.addAttribute("maskedWriterName", Masking.maskUserName(loginUser.getUserName()));
            model.addAttribute("boardWriter", loginUser.getUsername());
        }
        return "board/boardWrite";
    }

    // 글 등록
    @PostMapping("/boardWrite")
    public String boardWrite(@ModelAttribute BoardDTO boardDTO, @AuthenticationPrincipal LoginUserDetails loginUser) {
        boardDTO.setBoardWriter(loginUser.getUsername());
        service.insertBoard(boardDTO);
        return "redirect:/board/boardList";
    }

    // 글 상세 + 조회수 증가
    @GetMapping("/boardDetail")
    public String boardDetail(@AuthenticationPrincipal LoginUserDetails loginUser,
                              @RequestParam(name = "boardSeq") Long boardSeq,
                              @RequestParam(name = "searchCategory", defaultValue = "boardTitle") String searchCategory,
                              @RequestParam(name = "searchKeyword", defaultValue = "") String searchKeyword,
                              Model model) {

        BoardDTO boardDTO = service.selectOne(boardSeq);
        service.incrementHitcount(boardSeq);

        model.addAttribute("board", boardDTO);
        model.addAttribute("searchCategory", searchCategory);
        model.addAttribute("searchKeyword", searchKeyword);

        if (loginUser != null) {
            model.addAttribute("loginName", loginUser.getUserName());
            model.addAttribute("currentUser", loginUser.getUsername());

            if (loginUser.getRole().equals("ROLE_MANAGER")) {
                model.addAttribute("reply", replyService.selectReply(boardSeq));
                return "board/boardDetail_manager";
            }
        }

        model.addAttribute("reply", replyService.selectReply(boardSeq));
        return "board/boardDetail_user";
    }

    // 글 삭제
    @GetMapping("/boardDelete")
    public String boardDelete(@AuthenticationPrincipal LoginUserDetails loginUser,
                              @RequestParam(name="boardSeq") Long boardSeq,
                              @RequestParam(name="searchCategory", defaultValue = "boardTitle") String searchCategory,
                              @RequestParam(name="searchKeyword", defaultValue = "") String searchKeyword,
                              RedirectAttributes rttr) {

        if (loginUser == null) return "redirect:/user/login";

        BoardDTO board = service.selectOne(boardSeq);

        boolean isAuthor  = loginUser.getUsername().equals(board.getBoardWriter());
        boolean isManager = "ROLE_MANAGER".equals(loginUser.getRole());

        if (!(isAuthor || isManager)) {
            return "redirect:/accessDenied";
        }

        service.deleteOne(boardSeq);
        rttr.addAttribute("searchCategory", searchCategory);
        rttr.addAttribute("searchKeyword", searchKeyword);
        return "redirect:/board/boardList";
    }

    // 글 수정 조회
    @GetMapping("/boardUpdate")
    public String boardUpdate(@AuthenticationPrincipal LoginUserDetails loginUser,
                              @RequestParam(name="boardSeq") Long boardSeq,
                              @RequestParam(name="searchCategory", defaultValue = "boardTitle") String searchCategory,
                              @RequestParam(name="searchKeyword", defaultValue = "") String searchKeyword,
                              Model model) {

        BoardDTO boardDTO = service.selectOne(boardSeq);
        if (loginUser != null) {
            model.addAttribute("loginName", loginUser.getUserName());
        }

        model.addAttribute("isEdit", true);
        model.addAttribute("board", boardDTO);
        
        model.addAttribute("maskedWriterName", boardDTO.getMaskedWriterName()); // 마스킹 이름
        model.addAttribute("boardWriter", boardDTO.getBoardWriter());           // 아이디(히든필드 등)
        
        model.addAttribute("searchCategory", searchCategory);
        model.addAttribute("searchKeyword", searchKeyword);

        return "board/boardWrite";
    }

    // 글 수정
    @PostMapping("/boardUpdate")
    public String boardUpdate(@ModelAttribute BoardDTO boardDTO,
                              @RequestParam(name="searchCategory", defaultValue = "boardTitle") String searchCategory,
                              @RequestParam(name="searchKeyword", defaultValue = "") String searchKeyword,
                              RedirectAttributes rttr) {

        service.updateBoard(boardDTO);
        rttr.addAttribute("searchCategory", searchCategory);
        rttr.addAttribute("searchKeyword", searchKeyword);
        return "redirect:/board/boardList";
    }

    // 첨부파일 다운로드
    @GetMapping("/download")
    public String download(@RequestParam(name="boardSeq") Long boardSeq,
                           HttpServletResponse response) {

        BoardDTO boardDTO = service.selectOne(boardSeq);
        String originalFilename = boardDTO.getOriginalFilename();
        String savedFileName = boardDTO.getSavedFilename();

        try {
            String tempName = URLEncoder.encode(originalFilename, StandardCharsets.UTF_8.toString());
            response.setHeader("Content-Disposition", "attachment;filename=" + tempName);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        String fullPath = uploadPath + "/" + savedFileName;
        try (FileInputStream filein = new FileInputStream(fullPath);
             ServletOutputStream fileout = response.getOutputStream()) {
            FileCopyUtils.copy(filein, fileout);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
