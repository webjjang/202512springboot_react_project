package com.webjjang.api.image.service;

import com.webjjang.api.board.entity.Board;
import com.webjjang.api.board.vo.BoardVO;
import com.webjjang.api.util.page.PageObject;

import java.util.List;

public interface ImageService {

    // 1. list
    List<BoardVO> list(PageObject pageObject);
    // 2. view
    BoardVO view(Long no, Integer inc);
    // 3. write
    BoardVO write(BoardVO vo);
    // 4. update
    Long update(BoardVO vo);
    // 5. delete
    Long delete(BoardVO vo);

}
