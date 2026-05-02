package net.dima.project.dev;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import net.dima.project.entity.BoardEntity;
import net.dima.project.entity.ReplyEntity;
import net.dima.project.entity.UserEntity;
import net.dima.project.repository.BoardRepository;
import net.dima.project.repository.ReplyRepository;
import net.dima.project.repository.UserRepository;

@Profile("local")
@Component
@RequiredArgsConstructor
public class DummyDataSeeder implements CommandLineRunner {

	private final BoardRepository boardRepository;
	private final ReplyRepository replyRepository;
	private final UserRepository userRepository;
	private final BCryptPasswordEncoder passwordEncoder;

	// ===== 시더 옵션 =====
	private static final boolean RESET_AND_SEED = false; // true면 기존 데이터 싹 지우고 다시 생성
	private static final int ARTICLE_COUNT = 180; // 생성할 게시글 수
	private static final int MAX_PAST_DAYS = 180; // 과거 N일 안의 랜덤 날짜
	private static final double ANSWER_RATE = 0.55; // 답변완료 비율
	private static final double ATTACH_RATE = 0.15; // 첨부 포함 비율

	// 실제 가입된 7명 아이디(작성자)
	private static final String[] WRITERS = { "mgr", "jyh", "jmk", "mhk", "dik", "hjj", "jjmin" };
	private static final String DEFAULT_SEEDED_PASSWORD = "1234";

	// 제목/본문 재료
	private static final String[] PRODUCTS = { "폴리카보네이트", "커피 원두", "전기자전거", "LED 조명", "태양광 모듈", "냉동만두", "블루베리",
			"리튬이온배터리", "PE 파이프", "친환경 세제" };
	private static final String[] METRICS = { "수출금액", "수입금액", "물량(kg)", "단가", "거래건수" };
	private static final String[] FEATURES = { "통계 업데이트 알림", "최근검색 저장", "다크 모드", "모바일 최적화", "내보내기 개선" };
	private static final String[] MENUS = { "국내통계", "해외통계", "IMF 세계통계", "맞춤분석", "자사통계" };

	private final ThreadLocalRandom R = ThreadLocalRandom.current();

	@Override
	@Transactional
	public void run(String... args) {
		if (RESET_AND_SEED) {
			replyRepository.deleteAllInBatch();
			boardRepository.deleteAllInBatch();
			userRepository.deleteAllInBatch();
		} else {
			// 이미 게시글 데이터 있으면 중복 생성 방지
			// (다만, 작성자 계정이 없을 수도 있으니 유저는 먼저 보정 생성)
			if (boardRepository.count() > 0) {
				seedUsersIfMissing();
				return;
			}
		}

		// 0) 회원(작성자) 먼저 생성/저장 → board_writer FK 보장
		seedUsersIfMissing();

		// 1) 게시글 생성

		List<BoardEntity> boards = new ArrayList<>(ARTICLE_COUNT);
		for (int i = 1; i <= ARTICLE_COUNT; i++) {
			String writer = pick(WRITERS);
			LocalDateTime created = LocalDateTime.now().minusDays(R.nextInt(0, MAX_PAST_DAYS + 1));

			String title = makeTitle(i);
			String content = makeBody(i);

			BoardEntity boardEntity = new BoardEntity();
			boardEntity.setBoardWriter(writer);
			boardEntity.setBoardStatus(R.nextDouble() < ANSWER_RATE ? "답변완료" : "진행중");
			boardEntity.setBoardTitle(title);
			boardEntity.setBoardContent(content);
			boardEntity.setHitCount(skewedHits()); // 자연스러운 조회수 분포
			boardEntity.setCreateDate(created);
			boardEntity.setUpdateDate(created.plusDays(R.nextInt(0, 6)));

			// 첨부 확률적으로 추가
			if (R.nextDouble() < ATTACH_RATE) {
				String[] att = makeAttachment(i);
				boardEntity.setOriginalFilename(att[0]);
				boardEntity.setSavedFilename(att[1]);
			}
			boards.add(boardEntity);
		}

		// 2) 저장(IDs 부여 & 영속화)
		boardRepository.saveAll(boards);

		// 3) 답변: 상태가 '답변완료'인 글에는 반드시 답변을 달아 일관성 유지
		int replyCount = 0;
		for (BoardEntity boardEntity : boards) {
			if (!"답변완료".equals(boardEntity.getBoardStatus()))
				continue;

			ReplyEntity replyEntity = ReplyEntity.toEntity(boardEntity, makeReply(boardEntity.getBoardTitle()));
			replyEntity.setCreateDate(boardEntity.getCreateDate().plusDays(R.nextInt(0, 10)));
			replyRepository.save(replyEntity);
			replyCount++;
		}

		System.out.printf("[DummyDataSeeder] boards=%d, replies=%d%n", boards.size(), replyCount);
	}

	// ====== 유틸 ======
	// 제목 생성
	private String makeTitle(int idx) {
		String[] patterns = { "HS코드 문의: %s 관련", "무역통계 조회 오류 — %s 그래프 확인 요청", "사이트 개선 제안: %s", "경로 안내 오류 제보: '%s' 메뉴 이동",
				"챗봇 답변 보완 요청(HS코드/통계)" };
		String p = patterns[R.nextInt(patterns.length)];
		return switch (p) {
			case "HS코드 문의: %s 관련" -> p.formatted(pick(PRODUCTS));
			case "무역통계 조회 오류 — %s 그래프 확인 요청" -> p.formatted(pick(METRICS));
			case "사이트 개선 제안: %s" -> p.formatted(pick(FEATURES));
			case "경로 안내 오류 제보: '%s' 메뉴 이동" -> p.formatted(pick(MENUS));
			default -> "챗봇 답변 보완 요청(HS코드/통계)";
		};
	}

	// 본문 생성
	private String makeBody(int idx) {
		String product = pick(PRODUCTS);
		String metric = pick(METRICS);
		String menu = pick(MENUS);
		String feature = pick(FEATURES);

		return """
				안녕하세요. 무역통계포털(K-stat) 이용자입니다.

				1) HS코드/상품: %s
				2) 조회 항목: %s
				3) 주로 이용하는 메뉴: %s

				챗봇(HS코드 검색/무역통계 조회/경로 안내/FAQ)을 자주 이용하고 있습니다.
				다만 일부 화면에서 데이터 로딩이 느리거나, '%s' 기능이 제공되면 더 편리할 것 같습니다.

				관련 스크린샷(또는 파일)이 있는 경우 첨부하겠습니다.
				감사합니다.
				""".formatted(product, metric, menu, feature);
	}

	private String makeReply(String title) {
		if (title.contains("HS코드")) {
			return """
					안녕하세요, K-stat 운영팀입니다.
					문의하신 HS코드는 품목명 확인 후 유사어/키워드로 재검색하면 정확도가 높아집니다.
					챗봇의 'HS코드 검색'으로도 즉시 확인 가능하니 활용해 주세요.
					""";
		} else if (title.contains("무역통계")) {
			return """
					안녕하세요, K-stat 운영팀입니다.
					통계 조회 오류는 캐시 초기화 및 조회기간 축소로 우선 확인을 권장드립니다.
					지속될 경우 화면/조건을 알려주시면 로그로 원인을 추적하여 반영하겠습니다.
					""";
		} else if (title.contains("경로") || title.contains("메뉴")) {
			return """
					안녕하세요, K-stat 운영팀입니다.
					경로 안내는 상단 메뉴 > %s > 세부분석 순으로 접근 가능합니다.
					챗봇의 '경로 안내' 기능도 즉시 경로를 안내하니 참고 부탁드립니다.
					""".formatted(pick(MENUS));
		} else if (title.contains("개선") || title.contains("보완")) {
			return """
					안녕하세요, K-stat 운영팀입니다.
					제안 주신 사항은 내부 검토 후 로드맵에 반영하겠습니다.
					FAQ와 챗봇 답변도 함께 개선하여 더 편리하게 이용하시도록 하겠습니다.
					""";
		}
		return """
				안녕하세요, K-stat 운영팀입니다.
				문의 주셔서 감사합니다. 관련 내용은 검토 후 반영하겠습니다.
				추가 이슈는 챗봇(HS코드 검색/무역통계 조회/경로 안내/FAQ)로도 빠르게 확인 가능합니다.
				""";
	}

	private int skewedHits() {
		// 0~30이 가장 많이, 가끔 100+도 나오게
		int base = R.nextInt(0, 35);
		if (R.nextDouble() < 0.15)
			base += R.nextInt(30, 120);
		return base;
	}

	private String[] makeAttachment(int i) {
		String[] exts = { "png", "jpg", "webp", "xlsx", "pdf" };
		String ext = pick(exts);
		String original = (R.nextBoolean() ? "스크린샷_" : "자료요청_") + i + "." + ext;
		String saved = UUID.randomUUID() + "." + ext;
		return new String[] { original, saved };
	}

	private void seedUsersIfMissing() {
		List<UserEntity> toSave = new ArrayList<>();

		for (String userId : WRITERS) {
			if (userRepository.existsById(userId)) {
				continue;
			}

			String userName = "테스트_" + userId;
			String email = userId + "@dummy.local";
			String roles = userId.equals("mgr") ? "ROLE_MANAGER" : "ROLE_USER";

			UserEntity user = UserEntity.builder()
					.userId(userId)
					.userName(userName)
					.userPwd(passwordEncoder.encode(DEFAULT_SEEDED_PASSWORD))
					.email(email)
					.roles(roles)
					.enabled(true)
					.build();

			toSave.add(user);
		}

		if (!toSave.isEmpty()) {
			userRepository.saveAll(toSave);
		}
	}

	private <T> T pick(T[] arr) {
		return arr[R.nextInt(arr.length)];
	}

}