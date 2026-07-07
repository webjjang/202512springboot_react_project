package com.webjjang.api.board.repository;

import com.webjjang.api.board.entity.Board;
import com.webjjang.api.data.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface QBoardRepository
extends JpaRepository<Board, Long>, QuerydslPredicateExecutor<Board> {
    void deleteByNoPw(Long no, String pw);
}
