-- 1) 회원가입 테이블
CREATE DATABASE trit;
USE trit;

DROP TABLE IF EXISTS kitauser;

CREATE TABLE kitauser (
    user_id VARCHAR(50) NOT NULL,
    user_name VARCHAR(50) NOT NULL,
    user_pwd VARCHAR(100) NOT NULL,
    email VARCHAR(50) NOT NULL,
    roles VARCHAR(20) DEFAULT 'ROLE_USER' CHECK (roles IN ('ROLE_USER', 'ROLE_MANAGER')),
    enabled CHAR(1) DEFAULT '1' CHECK (enabled IN ('1', '0')),
    CONSTRAINT kitauser_pk PRIMARY KEY (user_id),
    CONSTRAINT kitauser_email_uk UNIQUE (email)
);
COMMIT;
SELECT * FROM kitauser;

-- 2) 게시판 테이블(kitauser.user_id 와 연동)
-- DROP TABLE IF EXISTS reply;
-- DROP TABLE IF EXISTS board;

CREATE TABLE board (
    board_seq         BIGINT AUTO_INCREMENT PRIMARY KEY,     
    board_title       VARCHAR(200) NOT NULL,                 
    board_writer      VARCHAR(50) NOT NULL,                  -- kitauser.user_id 참조
    board_content     TEXT,                                  
    hit_count         INT DEFAULT 0,                         
    board_status      VARCHAR(20) DEFAULT '진행중',           
    create_date       DATETIME DEFAULT CURRENT_TIMESTAMP,    
    update_date       DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    original_filename VARCHAR(255),                          
    saved_filename    VARCHAR(255),
    CONSTRAINT fk_board_writer FOREIGN KEY (board_writer)
        REFERENCES kitauser(user_id)
        ON DELETE CASCADE
);
COMMIT;
SELECT * FROM board;

-- 3) 관리자 댓글 테이블
CREATE TABLE reply (
    board_seq      BIGINT PRIMARY KEY,                     -- 게시글 ID (1:1 매핑 & PK)
    reply_content  VARCHAR(1000) NOT NULL,                 -- 답변 내용
    create_date    DATETIME DEFAULT CURRENT_TIMESTAMP,     -- 답변 작성일
    CONSTRAINT fk_reply_board FOREIGN KEY (board_seq)
        REFERENCES board(board_seq) ON DELETE CASCADE
);
COMMIT;
SELECT * FROM reply;

UPDATE kitauser
SET roles = 'ROLE_MANAGER'
WHERE user_id = 'mgr';


-- 4) 뉴스 테이블
CREATE TABLE trade_news (
    news_seq BIGINT AUTO_INCREMENT PRIMARY KEY,
    news_title VARCHAR(300) NOT NULL,
    news_link VARCHAR(500) NOT NULL,
    news_thumbnail VARCHAR(500),
    news_pubdate DATETIME,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
ALTER TABLE trade_news
    ADD UNIQUE KEY uk_news_link (news_link(191));
    
COMMIT;
SELECT * FROM trade_news;