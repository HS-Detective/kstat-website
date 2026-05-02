package net.dima.project.controller;

import lombok.RequiredArgsConstructor;
import net.dima.project.service.NewsService;
import net.dima.project.util.PageNavigator;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class NewsController {
    private final NewsService newsService;

    @GetMapping("/news/newsList")
    public String newsList(
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestParam(name = "searchCategory", required = false) String searchCategory,
            @RequestParam(name = "searchKeyword", required = false) String searchKeyword,
            Model model
    ) {
        var res = newsService.list(searchCategory, searchKeyword, page, size);
        
        // 지역 변수로 뽑아서 넘기기
        int currentPage = page;         
        int totalPages  = Math.max(res.totalPages(), 1); 

        model.addAttribute("newsList", res.content());
        model.addAttribute("totalCount", res.totalCount());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", Math.max(res.totalPages(), 1));
        model.addAttribute("pageSize", size);
        model.addAttribute("searchCategory", searchCategory);
        model.addAttribute("searchKeyword", searchKeyword);

        // 페이지 네비게이터
        model.addAttribute("navi", new PageNavigator(currentPage, totalPages));

        return "news/newsList";
    }
}
