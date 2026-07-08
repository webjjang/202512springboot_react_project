-- << 샘플데이터 입력 >>--

-- ### 회원 관리 데이터
-- 회원 데이터
INSERT INTO member(id, pw, NAME, gender, birth, tel,
	post_no, address, email)
VALUES('test', PASSWORD(id), '홍길동', '남자', '1990-03-01', '010-1111-2222',
   '04780', '서울시 성수동 1가', 'hong@naver.com');
-- 회원권한
INSERT INTO member_roles VALUES('test', 'ROLE_USER');
-- 회원 데이터
INSERT INTO member(id, pw, NAME, gender, birth, tel,
	post_no, address, email)
VALUES('admin', PASSWORD(id), '관리자', '여자', '2000-04-11', '010-3333-4444',
   '04780', '서울시 성수동 1가', 'admin@naver.com');
-- 회원권한
INSERT INTO member_roles VALUES('admin', 'ROLE_USER');
INSERT INTO member_roles VALUES('admin', 'ROLE_ADMIN');

COMMIT;