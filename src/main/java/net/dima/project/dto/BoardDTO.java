package net.dima.project.dto;

import java.time.LocalDateTime;

import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.dima.project.entity.BoardEntity;
import net.dima.project.util.Masking;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BoardDTO {
	private Long boardSeq;
	private String boardWriter;
	private String boardStatus;
	private String maskedWriterName;
	private String boardTitle;
	private String boardContent;
	private int hitCount;
	private LocalDateTime createDate;
	private LocalDateTime updateDate;
	private String originalFilename;
	private String savedFilename;
	
	// 파일 첨부되었을 때를 위한 추가작업
	private MultipartFile uploadFile;
	
	// 생성자 추가(Paging 을 위해 작성한 코드)
	public BoardDTO(Long boardSeq, String boardWriter, String boardTitle, int hitCount, LocalDateTime createDate, String originalFilename) {
		this.boardSeq = boardSeq;
		this.boardWriter = boardWriter;
		this.boardTitle = boardTitle;
		this.hitCount = hitCount;
		this.createDate = createDate;
		this.originalFilename = originalFilename;
	}

	// 기본 toDTO (userName 마스킹 없이)
	public static BoardDTO toDTO(BoardEntity boardEntity) {
		return BoardDTO.builder()
				.boardSeq(boardEntity.getBoardSeq())
				.boardWriter(boardEntity.getBoardWriter())
				.boardStatus(boardEntity.getBoardStatus())
				.boardTitle(boardEntity.getBoardTitle())
				.boardContent(boardEntity.getBoardContent())
				.hitCount(boardEntity.getHitCount())
				.createDate(boardEntity.getCreateDate())
				.updateDate(boardEntity.getUpdateDate())
				.originalFilename(boardEntity.getOriginalFilename())
				.savedFilename(boardEntity.getSavedFilename())
				.build();
	}

	// 마스킹 이름 포함하는 toDTO 오버로딩
	public static BoardDTO toDTO(BoardEntity boardEntity, String userName) {
		return BoardDTO.builder()
				.boardSeq(boardEntity.getBoardSeq())
				.boardWriter(boardEntity.getBoardWriter())
				.boardStatus(boardEntity.getBoardStatus())
				.boardTitle(boardEntity.getBoardTitle())
				.boardContent(boardEntity.getBoardContent())
				.hitCount(boardEntity.getHitCount())
				.createDate(boardEntity.getCreateDate())
				.updateDate(boardEntity.getUpdateDate())
				.originalFilename(boardEntity.getOriginalFilename())
				.savedFilename(boardEntity.getSavedFilename())
				.maskedWriterName(Masking.maskUserName(userName))
				.build();
	}
}
