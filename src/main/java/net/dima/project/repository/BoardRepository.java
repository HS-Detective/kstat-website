package net.dima.project.repository;

import net.dima.project.entity.BoardEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BoardRepository extends JpaRepository<BoardEntity, Long> {

    // 제목/내용/작성자(아이디) 부분검색
    Page<BoardEntity> findByBoardTitleContains(String searchKeyword, Pageable pageable);
    Page<BoardEntity> findByBoardContentContains(String searchKeyword, Pageable pageable);
    Page<BoardEntity> findByBoardWriterContains(String searchKeyword, Pageable pageable); // (유지)

    //  추가: 작성자 아이디 목록으로 IN 검색 (이름 → 아이디 변환 후 사용)
    Page<BoardEntity> findByBoardWriterIn(List<String> writerIds, Pageable pageable);
}
