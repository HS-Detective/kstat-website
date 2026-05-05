package net.dima.project.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dima.project.dto.UserDTO;
import net.dima.project.service.UserService;

@Controller
@RequestMapping("/user")
@Slf4j
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * 로그인 화면 요청
     */
    @GetMapping("/login")
    public String login(@RequestParam(name = "error", required = false) boolean error,
            @RequestParam(name = "errMessage", required = false) String errMessage,
            HttpServletRequest request,
            Model model) {

        model.addAttribute("error", error);
        model.addAttribute("errMessage", errMessage);

        String refererUrl = request.getHeader("Referer");
        HttpSession session = request.getSession();

        if (refererUrl != null && !error && !refererUrl.contains("login")) {
            session.setAttribute("refererUrl", refererUrl);
            log.info("refererUrl: {}", refererUrl);
        }

        return "user/login";
    }

    /**
     * 회원가입 화면 요청
     */
    @GetMapping("/join")
    public String join() {
        return "user/join";
    }

    /**
     * 회원가입 처리 요청
     */
    @PostMapping("/joinProc")
    public String joinProc(@ModelAttribute UserDTO userDTO, Model model) {
        log.info("회원가입 요청 도착");
        log.info("입력된 userPwd: {}", userDTO.getUserPwd());
        log.info("입력된 pwdConfirm: {}", userDTO.getPwdConfirm());

        // 1. 비밀번호 확인 불일치 시
        if (!userDTO.getUserPwd().equals(userDTO.getPwdConfirm())) {
            log.warn("비밀번호 불일치");
            model.addAttribute("error", true);
            model.addAttribute("errMessage", "비밀번호와 비밀번호 확인이 일치하지 않습니다.");
            return "user/join";
        }

        // 2. 아이디 중복 검사 및 저장
        boolean result = userService.joinProc(userDTO);
        log.info("회원 저장 결과: {}", result);

        if (!result) {
            model.addAttribute("error", true);
            model.addAttribute("errMessage", "이미 존재하는 아이디입니다.");
            return "user/join";
        }

        log.info("회원가입 성공! 로그인으로 이동");
        return "user/login";
    }

    /**
     * 중복된 아이디가 있는지 확인(ajax 처리)
     * 
     * @return
     */
    @ResponseBody
    @PostMapping("/confirmId")
    public boolean confirmId(@RequestParam(name = "userId") String userId) {
        return userService.selectOne(userId) == null;
    }

    @GetMapping("/first")
    public String showFirstPage() {
        return "user/first"; // templates/user/first.html 을 찾습니다.
    }

    @GetMapping("/first2")
    public String showFirst2Page() { // 메서드 이름은 겹치지 않게 변경
        return "user/first2"; // templates/user/first2.html 을 찾습니다.
    }
}
