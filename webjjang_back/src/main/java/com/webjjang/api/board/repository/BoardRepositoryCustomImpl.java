package com.webjjang.api.board.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.webjjang.api.board.entity.Board;
import com.webjjang.api.board.entity.QBoard;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor // private 이면서 final 붙인 변수를 자동 DI 해준다. 생성자를 이용한다.
public class BoardRepositoryCustomImpl implements BoardRepositoryCustom{

    private final JPAQueryFactory queryFactory;
    private final QBoardRepository qBoardRepository;

    // dsl 작성을 위해 사용한다.
    QBoard board = QBoard.board;

    @Override
    // Tuple = [data1, data2, ... ]
    public List<Tuple> getList(Long page, Long perPageNum, String key, String word) {
        return queryFactory
                .select(
                        board.no,
                        board.title,
                        board.writer,
                        board.hit,
                        board.writedDate
                )
                .from(board)
                .where(search(key, word)) // BooleanBuilder - true || false
                .orderBy(board.no.desc())
                .limit(perPageNum)// 보여질 데이터의 개수
                .offset((page-1)*perPageNum) // 이전페이지까지 데이터 개수
                .fetch();
    }

    // 검색에 필요한 메서드 작성
    private BooleanBuilder search(String key, String word){
        BooleanBuilder builder = new BooleanBuilder();

        // word에 데이터가 없는 경우(비어있음 - empty) 검색을 하지 않는다.
        if(word == null || word.trim().isEmpty())
            return builder; // 검색 조건이 없는 builder를 넘기면 조건처리를 하지 않는다.

        // key에 t가 포함이 되어있으면 제목에서 검색
        if(key.contains("t"))
            builder.or(board.title.contains(word));

        // key에 c가 포함이 되어있으면 내용에서 검색
        if(key.contains("c"))
            builder.or(board.content.contains(word));

        // key에 w가 포함이 되어있으면 작성자에서 검색
        if(key.contains("w"))
            builder.or(board.writer.contains(word));

        return builder;
    }

    @Override
    public Long getCount(String key, String word) {
        return queryFactory
                .select(board.count())
                .from(board)
                .where(search(key, word))
                .fetchOne();
    }

    @Override
    // 기본 쿼리 가능 - findById(Long no) -> Factory 보안 때문에
    public Tuple getBoard(Long no) {
        return queryFactory
                .select(
                        board.no,
                        board.title,
                        board.content,
                        board.writer,
                        board.writedDate,
                        board.hit
                )
                .from(board)
                .where(board.no.eq(no))
                .fetchOne();
    }

    @Override
    // 1 방법. : 기본 CRUD의 수정은 먼저 데이터를 꺼내온다(findById()) -> 꺼내온 데이터 변경(JAVA에서)
    // -> 수정된 내용을 DB에 저장(save()) : @LastModifedDate 수정날짜 자동 변경 됨
    // 2. QueryFactory 사용 : 수정 쿼리 실행 - @LastModifedDate 수정날짜 자동 변경 안됨
    public Long increaseHit(Long no) {
        return queryFactory
                .update(board)
                .set(board.hit, board.hit.add(1))
                .where(board.no.eq(no))
                .execute();
    }

    @Override
    public Board writeBoard(Board boardData) {
        return qBoardRepository.save(boardData);
    }

    @Override
    // 1 방법. : 기본 CRUD의 수정은 먼저 데이터를 꺼내온다(findById()) -> 꺼내온 데이터 변경(JAVA에서)
    // -> 수정된 내용을 DB에 저장(save()) : @LastModifedDate 수정날짜 자동 변경 됨
    // 2. QueryFactory 사용 : 수정 쿼리 실행 - @LastModifedDate 수정날짜 자동 변경 안됨
    public Long updateBoard(Board boardData) {
        return queryFactory
                .update(board)
                .set(board.title, boardData.getTitle())
                .set(board.content, boardData.getContent())
                .set(board.writer, boardData.getWriter())
                // queryFactory를 이용하면 자동 수정일 안됨.
                .set(board.updatedDate, LocalDateTime.now())
                .where(
                        board.no.eq(boardData.getNo()),
                        board.pw.eq(boardData.getPw())
                )
                .execute();
    }

    @Override
    public Long deleteBoard(Board boardData) {
        return queryFactory
                .delete(board)
                .where(
                        board.no.eq(boardData.getNo()),
                        board.pw.eq(boardData.getPw())
                )
                .execute();
    }
}
