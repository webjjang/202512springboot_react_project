-- --------------------------------------------------------
-- 호스트:                          localhost
-- 서버 버전:                        10.6.27-MariaDB - MariaDB Server
-- 서버 OS:                        Win64
-- HeidiSQL 버전:                  12.11.0.7065
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

-- 테이블 데이터 webjjang.member:~2 rows (대략적) 내보내기
INSERT INTO `member` (`birth`, `con_date`, `write_date`, `name`, `address`, `email`, `gender`, `id`, `post_no`, `pw`, `tel`) VALUES
	('2006-03-01 00:00:00.000000', '2026-07-10 15:54:39.116419', '2026-07-10 15:54:39.116419', '관리자', '서울시', 'admin@naver.com', '여자', 'admin', '11111', '{bcrypt}$2a$10$qJ.rjfwjbG1nS8lTDd1A3uFDy5lDTwndynq2L0jYlxx3i9NaXHY0m', '010-3333-4444'),
	('2000-03-01 00:00:00.000000', '2026-07-10 15:52:28.095570', '2026-07-10 15:52:28.095570', '홍길동', '서울시', 'hong@naver.com', '남자', 'test', '11111', '{bcrypt}$2a$10$K4RNVSYH4qwgr0kUHTzb6.WgVl.3kv342pvl3jWtmNhcrsESTq.VO', '010-1111-2222');

-- 테이블 데이터 webjjang.member_roles:~3 rows (대략적) 내보내기
INSERT INTO `member_roles` (`member_id`, `roles`) VALUES
	('test', 'ROLE_USER'),
	('admin', 'ROLE_USER'),
	('admin', 'ROLE_ADMIN');

/*!40103 SET TIME_ZONE=IFNULL(@OLD_TIME_ZONE, 'system') */;
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IFNULL(@OLD_FOREIGN_KEY_CHECKS, 1) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40111 SET SQL_NOTES=IFNULL(@OLD_SQL_NOTES, 1) */;
