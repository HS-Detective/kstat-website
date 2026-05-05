package net.dima.project.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.dima.project.dto.BoardDTO;

@Entity
@Table(name="board")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BoardEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="board_seq")
	private Long boardSeq;
	
	@Column(name="board_writer")
	private String boardWriter;

	 @Builder.Default
	 @Column(name = "board_status", length = 20)
	 private String boardStatus = "진행중";
	
	@Column(name="board_title")
	private String boardTitle;
	
	@Column(name="board_content")
	private String boardContent;
	
	@Column(name="hit_count")
	private int hitCount;
	
	@Column(name="create_date")
	@CreationTimestamp		// default로 설정된 글등록날짜를 현재날짜로 세팅
	private LocalDateTime createDate;
	
	@Column(name="update_date")
	private LocalDateTime updateDate;
	
	@Column(name="original_filename")
	private String originalFilename;
	
	@Column(name="saved_filename")
	private String savedFilename;

	
	@OneToOne(mappedBy = "board", cascade = CascadeType.ALL, orphanRemoval = true)
	private ReplyEntity reply;

	
	public static BoardEntity toEntity(BoardDTO boardDTO) {
		return BoardEntity.builder()
				.boardSeq(boardDTO.getBoardSeq())
				.boardWriter(boardDTO.getBoardWriter())
				.boardTitle(boardDTO.getBoardTitle())
				.boardContent(boardDTO.getBoardContent())
				.hitCount(boardDTO.getHitCount())
				.boardStatus(boardDTO.getBoardStatus())
				.createDate(boardDTO.getCreateDate())
				.updateDate(boardDTO.getUpdateDate())
				.originalFilename(boardDTO.getOriginalFilename())
				.savedFilename(boardDTO.getSavedFilename())
				.build();
	}
}
