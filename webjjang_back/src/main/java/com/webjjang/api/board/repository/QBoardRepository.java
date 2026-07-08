package com.webjjang.api.board.repository;

import com.webjjang.api.board.entity.Board;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface QBoardRepository
extends JpaRepository<Board, Long>, QuerydslPredicateExecutor<Board> {
    Long deleteByNoAndPw(Long no, String pw);
}
