# XAMPP 최신버전 필요

# 최초

## root 접속 후 sbsst 계정 생성 및 권한 부여
## root 계정 접속정보 : root/패스워드없음
## GRANT ALL PRIVILEGES ON *.* sbsst@`%` IDENTIFIED BY 'sbs123414';
## 이후 부터는 [sbsst/sbs123414]

# 데이터 베이스 생성
DROP DATABASE IF EXISTS springBootService;
CREATE DATABASE springBootService;
USE springBootService;

# 게시물 테이블 생성
CREATE TABLE article(
    id INT(10) UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
    regDate DATETIME NOT NULL,
    updateDate DATETIME NOT NULL,
    title CHAR(100) NOT NULL,
    `body` TEXT NOT NULL
);

# 게시물, 테스트 데이터 생성
INSERT INTO article
SET regDate=NOW(),
    updateDate=NOW(),
    title="제목1 입니다.",
    `body`="내용1 입니다.";
    
INSERT INTO article
SET regDate=NOW(),
    updateDate=NOW(),
    title="제목2 입니다.",
    `body`="내용2 입니다.";
    
INSERT INTO article
SET regDate=NOW(),
    updateDate=NOW(),
    title="제목3 입니다.",
    `body`="내용3 입니다.";
    
# 회원 테이블 생성
CREATE TABLE `member` (
    id INT(10) UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
    regDate DATETIME NOT NULL,
    updateDate DATETIME NOT NULL,
    loginId CHAR(30) NOT NULL,
    loginPw VARCHAR(100) NOT NULL,
    `name` CHAR(30) NOT NULL,
    `nickname` CHAR(30) NOT NULL,
    `email` CHAR(100) NOT NULL,
    `cellphoneNo` CHAR(20) NOT NULL
);

# 로그인 ID로 검색했을 때
ALTER TABLE `member` ADD UNIQUE INDEX (`loginId`);

# 회원, 테스트 데이터 생성
INSERT INTO `member`
SET regDate = NOW(),
updateDate = NOW(),
loginId = "user1",
loginPw = "user1",
`name` = "강혁수",
nickname = "강바람",
cellphoneNo = "01012341234",
email = "dbrudrjf21@gmail.com";

INSERT INTO `member`
SET regDate = NOW(),
updateDate = NOW(),
loginId = "user2",
loginPw = "user2",
`name` = "신라면",
nickname = "신바람",
cellphoneNo = "01012341234",
email = "dbrudrjf21@gmail.com";

# 게시물 테이블에 회원번호 칼럼 추가
ALTER TABLE article ADD COLUMN memberId INT(10) UNSIGNED NOT NULL AFTER updateDate;

# 기존 게시물의 작성자를 회원1로 지정
UPDATE article
SET memberId = 1
WHERE memberId=0;

/*
insert into article
(regDate, updateDate, memberId, title, `body`)
SELECT NOW(), NOW(), FLOOR(RAND() * 2) + 1, CONCAT('제목_', FLOOR(RAND() * 1000) + 1), CONCAT('내용_', FLOOR(RAND() * 1000) + 1)
from article;

SELECT COUNT(*) FROM article; 
*/

# 게시판 테이블 생성
CREATE TABLE board(
    id INT(10) UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
    regDate DATETIME NOT NULL,
    updateDate DATETIME NOT NULL,
    `code` CHAR(20) UNIQUE NOT NULL,
    `name` CHAR(20) UNIQUE NOT NULL
);

# 게시판 테스트 데이터 생성 (공지사항게시판, 자유게시판)
INSERT INTO board
SET regDate=NOW(),
    updateDate = NOW(),
    `code`= 'notice',
    `name`="공지사항";
    
INSERT INTO board
SET regDate=NOW(),
    updateDate = NOW(),
    `code`= 'free',
    `name`="자유";
    

# 게시물 테이블에 게시판번호 칼럼 추가, updateDate 칼럼 뒤에
ALTER TABLE article ADD COLUMN boardId INT(10) UNSIGNED NOT NULL AFTER updateDate;

# 기존 게시물의 게시판번호를 랜덤으로 지정 ( 1, 2 )
UPDATE article
SET boardId = FLOOR(RAND()*2)+1
WHERE boardId = 0;