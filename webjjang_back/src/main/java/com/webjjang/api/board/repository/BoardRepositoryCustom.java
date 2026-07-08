package com.webjjang.api.board.repository;

import com.querydsl.core.Tuple;
import com.webjjang.api.board.entity.Board;

import java.util.List;

public interface BoardRepositoryCustom {

    // list
    List<Tuple> getList(Long page, Long perPageNum, String key, String word);

    // count
    Long getCount(String key, String word);

    // view
    Tuple getBoard(Long no);

    // hit + 1
    Long increaseHit(Long no);

    // write
    Board writeBoard(Board boardData);

    // update
    Board updateBoard(Board boardData);

    // delete
    void deleteBoard(Long no);

}
