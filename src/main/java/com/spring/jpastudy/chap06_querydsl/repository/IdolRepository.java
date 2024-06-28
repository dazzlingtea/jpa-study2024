package com.spring.jpastudy.chap06_querydsl.repository;

import com.spring.jpastudy.chap06_querydsl.entity.Idol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface IdolRepository
        extends JpaRepository<Idol, Long>, IdolCustomRepository {

    @Query("SELECT i FROM Idol i ORDER BY i.age DESC ")
    List<Idol> foundAllSortedByName();

    // 커스텀을 상속하면 jpa 기본 메서드들 + 커스텀 메서드 모두 사용 가능
}
