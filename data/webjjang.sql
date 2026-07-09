-- 1. 객체 제거 (FK -> PK)

   alter table if exists image 
	   DROP 
	   foreign key if exists FKnnvd0itj2hhoyuua7g3ive7vo;
   alter table if exists member_roles 
      drop 
      foreign key if exists FKet63dfllh4o5qa9qwm7f5kx9x;

	 drop table if exists board;
    drop table if exists image;
    drop table if exists member_roles;
    drop table if exists member;
    
-- 2. 객체 생성
    create table board (
        hit bigint,
        no bigint not null auto_increment,
        updated_date datetime(6),
        writed_date datetime(6),
        writer varchar(30) not null,
        title varchar(300) not null,
        content text not null,
        pw varchar(255) not null,
        primary key (no)
    ) engine=INNODB;
    
    create table member (
        birth datetime(6) not null,
        con_date datetime(6),
        write_date datetime(6),
        name varchar(30) not null,
        address varchar(255),
        email varchar(255) not null,
        gender varchar(255) not null,
        id varchar(255) not null,
        post_no varchar(255),
        pw varchar(255) not null,
        tel varchar(255),
        primary key (id),
        check (gender in ('남자','여자'))
    ) engine=INNODB;

    create table image (
        hit bigint,
        no bigint not null auto_increment,
        updated_date datetime(6),
        writed_date datetime(6),
        title varchar(300) not null,
        content text not null,
        member_id varchar(255),
        primary key (no)
    ) engine=INNODB;

    create table member_roles (
        member_id varchar(255) not null,
        roles varchar(255)
    ) engine=InnoDB

-- 3. 제약 조건 설정
    alter table if exists image 
       add constraint FKnnvd0itj2hhoyuua7g3ive7vo 
       foreign key (member_id) 
       references member (id);

    alter table if exists member_roles 
       add constraint FKet63dfllh4o5qa9qwm7f5kx9x 
       foreign key (member_id) 
       references member (id);
