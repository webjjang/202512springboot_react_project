package com.webjjang.api.board.service;

import com.querydsl.core.Tuple;
import com.webjjang.api.board.entity.Board;
import com.webjjang.api.board.repository.BoardRepositoryCustom;
import com.webjjang.api.board.vo.BoardVO;
import com.webjjang.api.util.page.PageObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class BoardServiceImpl implements BoardService {

    @Autowired
    private  BoardRepositoryCustom boardRepositoryCustom;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public List<BoardVO> list(PageObject pageObject) {

        // 화면에 페이지 내이션을 표시하기 위한 페이지
        pageObject.setTotalRow(boardRepositoryCustom.getCount(pageObject.getKey(), pageObject.getWord()));

        List<Tuple> tupleList = boardRepositoryCustom.getList(
                pageObject.getPage(),
                pageObject.getPerPageNum(),
                pageObject.getKey(),
                pageObject.getWord()
        );

        // List<Tuple> to List<BoardVO>
        List<BoardVO> list = new ArrayList<>();
        for(Tuple tuple : tupleList){
            BoardVO vo = new BoardVO();
            vo.setNo(tuple.get(0, Long.class));
            vo.setTitle(tuple.get(1, String.class));
            vo.setWriter(tuple.get(2, String.class));
            vo.setHit(tuple.get(3, Long.class));
            vo.setWriteDate(tuple.get(4, LocalDateTime.class));
            list.add(vo);
        }

        return list;
    }

    @Override
    @Transactional
    public BoardVO view(Long no, Integer inc) {
        if(inc == 1) boardRepositoryCustom.increaseHit(no);
        Tuple tuple = boardRepositoryCustom.getBoard(no);
        BoardVO vo = new BoardVO();
        vo.setNo(tuple.get(0, Long.class));
        vo.setTitle(tuple.get(1, String.class));
        vo.setContent(tuple.get(2, String.class));
        vo.setWriter(tuple.get(3, String.class));
        vo.setWriteDate(tuple.get(4, LocalDateTime.class));
        vo.setHit(tuple.get(5, Long.class));
        return vo;
    }

    // Board -> BoardVO
    BoardVO boardToBoardVO(Board board){
        BoardVO vo = new BoardVO();
        vo.setNo(board.getNo());
        vo.setTitle(board.getTitle());
        vo.setContent(board.getContent());
        vo.setWriter(board.getWriter());
        vo.setHit(board.getHit());
        vo.setWriteDate(board.getWritedDate());
        vo.setUpdateDate(board.getUpdatedDate());
        vo.setPw(board.getPw());
        return vo;
    }

    // BoardVO -> Board
    Board boardVOToBoard(BoardVO vo){
        Board board = new Board();
        board.setNo(vo.getNo());
        board.setTitle(vo.getTitle());
        board.setContent(vo.getContent());
        board.setWriter(vo.getWriter());
        board.setWritedDate(vo.getWriteDate());
        board.setUpdatedDate(vo.getUpdateDate());
        board.setPw(passwordEncoder.encode(vo.getPw()));
        return board;
    }

    @Override
    @Transactional
    public BoardVO write(BoardVO vo) {
        Board board = boardRepositoryCustom.writeBoard(
                boardVOToBoard(vo) // BoardVO -> Board
        );

        return boardToBoardVO(board); // Board -> BoardVO
    }

    @Override
    @Transactional
    public Long update(BoardVO vo) {
        return boardRepositoryCustom.updateBoard(boardVOToBoard(vo));
    }

    @Override
    @Transactional
    public Long delete(BoardVO vo) {
        return boardRepositoryCustom.deleteBoard(boardVOToBoard(vo));
    }
}
