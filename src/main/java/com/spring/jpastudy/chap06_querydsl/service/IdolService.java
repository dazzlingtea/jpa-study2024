package com.spring.jpastudy.chap06_querydsl.service;


import com.spring.jpastudy.chap06_querydsl.entity.Idol;
import com.spring.jpastudy.chap06_querydsl.repository.IdolRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional // JPA, QueryDsl 쓸 때 잊지말 것!
public class IdolService {

    private final IdolRepository idolRepository;

    // 아이돌을 나이순으로 내림차 정렬해서 조회
    public List<Idol> getIdols() {

        // JPA @Query 에서 정렬
        List<Idol> idolList = idolRepository.foundAllSortedByName();
        
        // 그룹명으로 아이돌을 조회
        idolRepository.foundByGroupName();

        // 스트림
//        List<Idol> idolList = idolRepository.findAll();
//        return idolList.stream()
//                .sorted(Comparator.comparing(Idol::getAge).reversed())
//                .collect(Collectors.toList());

        return idolList;
    }


}
