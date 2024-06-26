package com.spring.jpastudy.chap03_page;

import com.spring.jpastudy.chap02.entity.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface StudentPageRepository extends JpaRepository<Student, String> {

    // 전체조회 상황에서 페이징 처리하기
    Page<Student> findAll(Pageable pageable);

    // 검색 + 페이징
    Page<Student> findByNameContaining(String name, Pageable pageable);

//    @Query("") // limit, orderby는 Page가 해주니까 제외하고 작성
//    Page<Student> getList();

}
