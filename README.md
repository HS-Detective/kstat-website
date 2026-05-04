# Trit - AI 기반 무역 인사이트 및 챗봇 웹 서비스 🌐

![Trit Logo](src/main/resources/static/images/Trit_logo.png)

## 📌 프로젝트 개요
**Trit**는 사용자에게 무역 관련 최신 뉴스, 통계 데이터를 제공하고, AI 챗봇(Trit)을 통해 무역 및 수출입 관련 질문에 실시간으로 답변해 주는 웹 서비스 플랫폼입니다. 
본 레포지토리는 서비스의 백엔드와 프론트엔드를 구성하는 **Java/Spring Boot 웹 애플리케이션** 파트를 담고 있습니다. (AI 챗봇 처리 및 예측 모델링을 담당하는 Python/FastAPI 서버는 별도로 운영됩니다.)

## 🚀 주요 기능

### 1. 🤖 AI 무역 챗봇 'Trit' 연동
- FastAPI 기반의 AI 서버 및 n8n 워크플로우와 연동하여 무역 관련 질의응답 서비스 제공
- 인터랙티브한 채팅 UI 지원 (웹소켓 및 REST API 활용)

### 2. 📰 무역 뉴스 및 인사이트 제공
- 최신 무역/경제 뉴스 리스트 제공
- 메인 화면에 최신 뉴스 하이라이트 노출

### 3. 📝 커뮤니티 게시판 (Board)
- **일반 사용자 & 관리자 분리:** 권한에 따른 조회 및 관리 UI 차별화 (`boardDetail_user.html`, `boardDetail_manager.html`)
- **게시글 CRUD:** 작성, 수정, 삭제, 조회
- **파일 업로드:** 최대 100MB 대용량 첨부파일 지원
- **댓글 기능:** 게시글 내 댓글 작성 및 소통 가능
- **페이징 처리:** 대량의 게시글을 페이지네이션으로 깔끔하게 제공

### 4. 🔐 사용자 인증 및 보안 (User/Auth)
- **Spring Security** 기반의 강력한 보안 및 세션 관리
- 회원가입, 로그인, 로그아웃 기능 지원
- Custom Login Success/Failure Handler를 통한 세밀한 로그인 흐름 제어

## 🛠 기술 스택 (Tech Stack)

### Backend
- **Language:** Java 21
- **Framework:** Spring Boot 3.5.4
- **Security:** Spring Security
- **ORM:** Spring Data JPA, Hibernate
- **Database:** MySQL 8.x
- **Build Tool:** Gradle

### Frontend
- **Template Engine:** Thymeleaf (+ thymeleaf-extras-springsecurity6)
- **Language:** HTML5, CSS3, JavaScript (ES6)
- **Library:** jQuery 3.7.1

### External Integration
- **AI / Predict Server:** Python / FastAPI (별도 구축)
- **Workflow Automation:** n8n
- **Cloud Database:** Supabase

## 📁 프로젝트 폴더 구조

```text
C:\diane\web\
├── build.gradle                # Gradle 빌드 스크립트
├── src/
│   ├── main/
│   │   ├── java/net/dima/project/
│   │   │   ├── config/         # Spring Security 등 설정 클래스
│   │   │   ├── controller/     # 웹 요청을 처리하는 컨트롤러 (Main, Board, Chat, News, User 등)
│   │   │   ├── dto/            # 데이터 전송 객체
│   │   │   ├── entity/         # JPA 엔티티 클래스
│   │   │   ├── handler/        # 로그인 성공/실패 등 커스텀 핸들러
│   │   │   ├── repository/     # DB 접근을 위한 JPA 리포지토리
│   │   │   ├── service/        # 비즈니스 로직 계층
│   │   │   └── util/           # 파일 업로드, 페이징, 마스킹 유틸리티
│   │   └── resources/
│   │       ├── application.properties # 애플리케이션 환경 설정
│   │       ├── static/         # 정적 리소스 (css, images, js, videos, downloads)
│   │       └── templates/      # Thymeleaf HTML 템플릿 (board, chat, fragments, news, user)
└── 기타 설정 파일 (.gitignore, gradle wrapper 등)
```

## ⚙️ 설정 및 실행 방법

### 1. 필수 환경 변수 설정
프로젝트 최상단(root)에 `.env` 파일을 생성하고 아래의 변수들을 환경에 맞게 기입해주세요. (Spring Boot의 `spring.config.import=optional:file:.env` 기능을 통해 로드됩니다.)

```properties
# .env 예시
DB_USERNAME=your_mysql_username
DB_PASSWORD=your_mysql_password
FASTAPI_BASE_URL=http://localhost:8001
SUPABASE_SERVICE_KEY=your_supabase_key
```

### 2. 데이터베이스 설정
- MySQL에 `dimaProject` 데이터베이스를 생성합니다.
- 초기 테이블 구조는 `kita_ddl.sql` 파일을 실행하여 구축할 수 있습니다.

### 3. 파일 업로드 디렉토리 생성
로컬 환경에서 첨부파일 업로드 기능을 테스트하려면 `C:/uploadPath` 디렉토리를 생성해야 합니다.
(경로를 변경하려면 `application.properties`의 `spring.servlet.multipart.location` 값을 수정하세요.)

### 4. 프로젝트 빌드 및 실행
```bash
# Gradle Wrapper를 사용한 실행 (Windows)
./gradlew.bat bootRun

# 또는 빌드 후 jar 파일 실행
./gradlew.bat build
java -jar build/libs/project-0.0.1-SNAPSHOT.jar
```

## 🤝 연동 아키텍처 참고
이 애플리케이션은 메인 백엔드 역할을 담당합니다. 챗봇 기능(`ChatApiController.java` 및 `trit.html`)은 설정된 `FASTAPI_BASE_URL` (포트 8001) 및 `n8n` 서버로 데이터를 비동기 통신하여 AI 응답을 받아와 사용자 화면에 렌더링하도록 설계되어 있습니다. 파이썬 서버가 실행 중이어야 챗봇 기능이 정상 작동합니다.
