package net.dima.project.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import lombok.RequiredArgsConstructor;
import net.dima.project.dto.LoginUserDetails;
import net.dima.project.service.NewsService;

@RequiredArgsConstructor
@Controller
public class MainController {

    private final NewsService newsService;

    /**
     * 메인 화면 요청
     */
    @GetMapping({"/", ""})
    public String index(@AuthenticationPrincipal LoginUserDetails loginUser,
                        Model model) {

        // 로그인된 사용자라면 이름 전달
        if (loginUser != null) {
            model.addAttribute("loginName", loginUser.getUserName());
        }

        // 최신 뉴스 3건 전달
        model.addAttribute("newsList", newsService.latest3());

        return "main";  // templates/main.html
    }
    

}
