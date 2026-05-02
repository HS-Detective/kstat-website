package net.dima.project.util;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PageNavigator {
    private final int pagePerGroup = 10; // 한 그룹에 보여줄 페이지 수 (예: [1..10], [11..20] ...)
    private int totalPages;              // 전체 페이지 수

    public int startPageGroup;           // 현재 그룹의 시작 페이지(1-based)
    public int endPageGroup;             // 현재 그룹의 끝 페이지(1-based)
    public int currentGroup;             // 현재 그룹 번호(1-based)

    /**
     * @param currentPage 1-based 현재 페이지(뷰에서 쓰는 기준)
     * @param totalPages  전체 페이지 수(0일 수도 있음)
     */
    public PageNavigator(int currentPage, int totalPages) {
        this.totalPages = Math.max(totalPages, 0);

        // 최소 1 페이지로 가드
        int cp = Math.max(currentPage, 1);

        // 전체 페이지가 0이면(데이터 없음) 1페이지로 표시되도록 보정
        int safeTotal = Math.max(this.totalPages, 1);

        // 현재 그룹(1-based): (cp-1)/pagePerGroup + 1
        this.currentGroup = (cp - 1) / pagePerGroup + 1;

        // 시작/끝 페이지 계산
        this.startPageGroup = (currentGroup - 1) * pagePerGroup + 1;
        this.endPageGroup   = Math.min(startPageGroup + pagePerGroup - 1, safeTotal);
    }
}
