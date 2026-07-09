package com.webjjang.api.image.service;

import com.querydsl.core.Tuple;
import com.webjjang.api.board.entity.Board;
import com.webjjang.api.board.repository.BoardRepositoryCustom;
import com.webjjang.api.board.repository.QBoardRepository;
import com.webjjang.api.board.vo.BoardVO;
import com.webjjang.api.image.entity.Image;
import com.webjjang.api.image.repository.ImageRepositoryCustom;
import com.webjjang.api.image.repository.QImageRepository;
import com.webjjang.api.image.vo.ImageVO;
import com.webjjang.api.member.entity.Member;
import com.webjjang.api.util.page.PageObject;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Log4j2
public class ImageServiceImpl implements ImageService {

    @Autowired
    private ImageRepositoryCustom imageRepositoryCustom;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private QImageRepository qImageRepository;

    @Override
    public List<ImageVO> list(PageObject pageObject) {

        // 화면에 페이지 내이션을 표시하기 위한 페이지
        pageObject.setTotalRow(imageRepositoryCustom.getCount(pageObject.getKey(), pageObject.getWord()));

        // Tuple-> [no, title, fileName, id, name, writedDate, hit] :: index 번호를 이용해서 데이터를 가져온다.
        List<Tuple> tupleList = imageRepositoryCustom.getList(
                pageObject.getPage(),
                pageObject.getPerPageNum(),
                pageObject.getKey(),
                pageObject.getWord()
        );

        // List<Tuple> to List<BoardVO>
        List<ImageVO> list = new ArrayList<>();
        for(Tuple tuple : tupleList){
            ImageVO vo = new ImageVO();
            vo.setNo(tuple.get(0, Long.class));
            vo.setTitle(tuple.get(1, String.class));
            vo.setFileName(tuple.get(2, String.class));
            vo.setId(tuple.get(3, String.class));
            vo.setName(tuple.get(4, String.class));
            vo.setWritedDate(tuple.get(5, LocalDateTime.class));
            vo.setHit(tuple.get(6, Long.class));
            list.add(vo);
        }

        return list;
    }

    @Override
    @Transactional
    public ImageVO view(Long no, Integer inc) {
        if(inc == 1) imageRepositoryCustom.increaseHit(no);
        // Tuple -> [no, title, content, id, name, writedDate, hit]
        Tuple tuple = imageRepositoryCustom.getImage(no);
        ImageVO vo = new ImageVO();
        vo.setNo(tuple.get(0, Long.class));
        vo.setTitle(tuple.get(1, String.class));
        vo.setContent(tuple.get(2, String.class));
        vo.setFileName(tuple.get(3, String.class));
        vo.setId(tuple.get(4, String.class));
        vo.setName(tuple.get(5, String.class));
        vo.setWritedDate(tuple.get(6, LocalDateTime.class));
        vo.setHit(tuple.get(7, Long.class));
        return vo;
    }

    // Image Entity -> ImageVO
    ImageVO imageToImageVO(Image image){
        ImageVO vo = new ImageVO();
        vo.setNo(image.getNo());
        vo.setTitle(image.getTitle());
        vo.setContent(image.getContent());
        vo.setFileName(image.getFileName());
        vo.setId(image.getMember().getId());
        vo.setName(image.getMember().getName());
        vo.setWritedDate(image.getWritedDate());
        vo.setUpdatedDate(image.getUpdatedDate());
        vo.setHit(image.getHit());
        return vo;
    }

    // ImageVO -> Image Entity
    Image imageVOToImage(ImageVO vo){
        Image image = new Image();
        image.setNo(vo.getNo());
        image.setTitle(vo.getTitle());
        image.setContent(vo.getContent());
        image.setFileName(vo.getFileName());
        // image id 세팅하기 : member 생성 -> member에 id 세팅 -> image에 member 세팅
        Member member = new Member();
        member.setId(vo.getId());
        image.setMember(member);
        image.setWritedDate(vo.getWritedDate());
        image.setUpdatedDate(vo.getUpdatedDate());
        image.setHit(vo.getHit()==null?0:vo.getHit());
        return image;
    }

    @Override
    @Transactional
    public ImageVO write(ImageVO vo) {
        Image image = imageRepositoryCustom.writeImage(
                imageVOToImage(vo) // BoardVO -> Board
        );

        return imageToImageVO(image); // Board -> BoardVO
    }

    @Override
    @Transactional
    public Long update(ImageVO vo) {
       log.info("[update] imageVOToImage(vo) = {}", imageVOToImage(vo));

       Long result = imageRepositoryCustom.updateImage(vo.getTitle(), vo.getContent(), vo.getNo(), vo.getId());

       if(result == 0)
           throw new RuntimeException("이미지 게시판 수정 안됨 - 없는 이미지 게시글이거나 본인이 작성한 게시물이 아님.");

       return result;
    }

    @Override
    @Transactional
    // 삭제 하면 뒤에 삭제한 데이터의 파일명을 리턴해주면 파일시스템에서 파일을 삭제하셔야 합니다.
    public String delete(ImageVO vo) {

        // pw를 포함 데이터를 먼저 가져온다.
       Optional<Image> optional = qImageRepository.findById(vo.getNo());
       if(optional.isEmpty())
           throw new RuntimeException("이미지 게시판 삭제 오류 - 글번호 확인");
       Image image = optional.get();
       imageRepositoryCustom.deleteImage(vo.getNo());

        return image.getFileName();
    }

    @Override
    public Long changeImage(ImageVO vo) {
        return imageRepositoryCustom.changeImage(vo.getNo(), vo.getId(), vo.getFileName());
    }
}
