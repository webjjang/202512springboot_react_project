drop table if exists board;
create table board (
	hit bigint,
	no bigint not null auto_increment,
	updated_date datetime(6),
	writed_date datetime(6),
	writer varchar(30) not null,
	title varchar(300) not null,
	content text not null,
	pw varchar(255) not null,
	PRIMARY key (no)
) engine=INNODB;
    
