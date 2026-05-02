package net.dima.project.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import net.dima.project.entity.BoardEntity;
import net.dima.project.entity.ReplyEntity;

public interface ReplyRepository extends JpaRepository<ReplyEntity, Long> {

    // 게시글에 달린 답변 1개 (1:1 매핑)
    Optional<ReplyEntity> findByBoard(BoardEntity boardEntity);
}
