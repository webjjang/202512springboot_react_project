package com.webjjang.api.member.repository;

import com.webjjang.api.image.entity.Image;
import com.webjjang.api.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface QMemberRepository
        extends JpaRepository<Member, String>, QuerydslPredicateExecutor<Member> {
}
