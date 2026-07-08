package com.webjjang.api.image.repository;

import com.webjjang.api.board.entity.Board;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface QImageRepository
extends JpaRepository<Board, Long>, QuerydslPredicateExecutor<Board> {
}
