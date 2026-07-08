package com.webjjang.api.image.repository;

import com.querydsl.core.Tuple;
import com.webjjang.api.image.entity.Image;

import java.util.List;

public interface ImageRepositoryCustom {

    // list
    List<Tuple> getList(Long page, Long perPageNum, String key, String word);

    // count
    Long getCount(String key, String word);

    // view
    Tuple getImage(Long no);

    // hit + 1
    Long increaseHit(Long no);

    // write
    Image writeImage(Image imageData);

    // update
    Image updateImage(Image imageData);

    // delete
    void deleteImage(Long no);

}
