package com.webjjang.api.image.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.webjjang.api.board.entity.Board;
import com.webjjang.api.image.entity.Image;
import com.webjjang.api.image.entity.QImage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor // private 이면서 final 붙인 변수를 자동 DI 해준다. 생성자를 이용한다.
public class ImageRepositoryCustomImpl implements ImageRepositoryCustom{

    private final JPAQueryFactory queryFactory;
    private final QImageRepository qImageRepository;

    // dsl 작성을 위해 사용한다.
    QImage image = QImage.image;

    @Override
    // Tuple = [data1, data2, ... ]
    public List<Tuple> getList(Long page, Long perPageNum, String key, String word) {
        return queryFactory
                .select(
                        image.no,
                        image.title,
                        image.member().id,
                        image.member().name,
                        image.hit,
                        image.writedDate
                )
                .from(image)
                .where(search(key, word)) // BooleanBuilder - true || false
                .orderBy(image.no.desc())
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
            builder.or(image.title.contains(word));

        // key에 c가 포함이 되어있으면 내용에서 검색
        if(key.contains("c"))
            builder.or(image.content.contains(word));

        // key에 w가 포함이 되어있으면 작성자에서 검색
        if(key.contains("i"))
            builder.or(image.member().id.contains(word));

        return builder;
    }

    @Override
    public Long getCount(String key, String word) {
        return queryFactory
                .select(image.count())
                .from(image)
                .where(search(key, word))
                .fetchOne();
    }

    @Override
    // 기본 쿼리 가능 - findById(Long no) -> Factory 보안 때문에
    public Tuple getImage(Long no) {
        return queryFactory
                .select(
                        image.no,
                        image.title,
                        image.content,
                        image.member().id,
                        image.member().name,
                        image.writedDate,
                        image.hit
                )
                .from(image)
                .where(image.no.eq(no))
                .fetchOne();
    }

    @Override
    // 1 방법. : 기본 CRUD의 수정은 먼저 데이터를 꺼내온다(findById()) -> 꺼내온 데이터 변경(JAVA에서)
    // -> 수정된 내용을 DB에 저장(save()) : @LastModifedDate 수정날짜 자동 변경 됨
    // 2. QueryFactory 사용 : 수정 쿼리 실행 - @LastModifedDate 수정날짜 자동 변경 안됨
    public Long increaseHit(Long no) {
        return queryFactory
                .update(image)
                .set(image.hit, image.hit.add(1))
                .where(image.no.eq(no))
                .execute();
    }

    @Override
    public Image writeImage(Image imageData) {
        return qImageRepository.save(imageData);
    }

    @Override
    // 1 방법. : 기본 CRUD의 수정은 먼저 데이터를 꺼내온다(findById()) -> 꺼내온 데이터 변경(JAVA에서)
    // -> 수정된 내용을 DB에 저장(save()) : @LastModifedDate 수정날짜 자동 변경 됨
    // 2. QueryFactory 사용 : 수정 쿼리 실행 - @LastModifedDate 수정날짜 자동 변경 안됨
    public Image updateImage(Image imageData) {
        return qImageRepository.save(imageData);
      }

    @Override
    public void deleteImage(Long no) {
        qImageRepository.deleteById(no);
    }
}
