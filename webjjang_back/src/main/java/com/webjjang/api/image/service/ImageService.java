package com.webjjang.api.image.service;

import com.webjjang.api.image.vo.ImageVO;
import com.webjjang.api.util.page.PageObject;

import java.util.List;

public interface ImageService {

    // 1. list
    List<ImageVO> list(PageObject pageObject);
    // 2. view
    ImageVO view(Long no, Integer inc);
    // 3. write
    ImageVO write(ImageVO vo);
    // 4. update
    Long update(ImageVO vo);
    // 5. delete - 삭제 후 삭제할 파일명을 리턴해 준다.
    String delete(ImageVO vo);
    // 6. changeImage
    Long changeImage(ImageVO vo);

}
