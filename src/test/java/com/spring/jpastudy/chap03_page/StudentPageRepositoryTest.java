package com.spring.jpastudy.chap03_page;

import com.spring.jpastudy.chap02.entity.Student;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Rollback
class StudentPageRepositoryTest {

    @Autowired
    StudentPageRepository repository;

    @BeforeEach
    void bulkInsert() {
        for (int i = 0; i < 147; i++) {
            Student s = Student.builder()
                    .name("김시골_"+i)
                    .city("도시_"+i)
                    .major("숨쉬기_"+i)
                    .build();
            repository.save(s);
        }
    }
    @Test
    @DisplayName("기본적인 페이지 조회 테스트")
    void basicPageTest() {
        //given
        int pageNo = 6;
        int amount = 10;

        // 페이지 정보 객체를 생성 (Pageable)
        // 여기서는 페이지번호가 zero-based : 1page 0으로 취급
        // Pageable은 인터페이스라 그 구현체인 PageRequest.of 사용
        Pageable pageInfo = PageRequest.of(pageNo-1, amount);

        //when
        Page<Student> students = repository.findAll(pageInfo);

        // 실질적인 데이터 꺼내기
        List<Student> studentList = students.getContent();

        // 총 페이지 수
        int totalPages = students.getTotalPages();
        // 총 학생 수
        long count = students.getTotalElements();

        //then
        System.out.println("\n\n\n");
        System.out.println("totalPages = " + totalPages);
        System.out.println("count = " + count);
        studentList.forEach(System.out::println);
        System.out.println("\n\n\n");
    }
    @Test
    @DisplayName("페이징 + 정렬")
    void pagingAndSortTest() {
        //given
        Pageable pageInfo = PageRequest.of(
                0, // 1페이지
                10,
                // 매개값으로는 엔터티 필드명, stu_name(x, 컬럼명)
//                Sort.by("name").descending() // 내림차순
//                // 여러 조건으로 정렬
                Sort.by(
                        Sort.Order.desc("name"),
                        Sort.Order.asc("city")
                        //... 다른 조건 추가 가능
                )
        );
        //when
        Page<Student> studentPage = repository.findAll(pageInfo);

        //then
        System.out.println("\n\n\n");
        studentPage.forEach(System.out::println);
        System.out.println("\n\n\n");
    }


}