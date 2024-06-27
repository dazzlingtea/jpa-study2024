package com.spring.jpastudy.chap06_querydsl.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.spring.jpastudy.chap06_querydsl.entity.Group;
import com.spring.jpastudy.chap06_querydsl.entity.Idol;
import com.spring.jpastudy.chap06_querydsl.entity.QIdol;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import java.util.List;
import java.util.Optional;

import static com.spring.jpastudy.chap06_querydsl.entity.QIdol.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
//@Rollback(value = false)
class QueryDslBasicTest {

    @Autowired
    IdolRepository idolRepository;

    @Autowired
    GroupRepository groupRepository;

    // JPA의 CRUD를 제어하는 객체
    @Autowired
    EntityManager em;

    @Autowired
    JPAQueryFactory factory;

    @BeforeEach
    void setUp() {
        //given
        Group leSserafim = new Group("르세라핌");
        Group ive = new Group("아이브");

        groupRepository.save(leSserafim);
        groupRepository.save(ive);

        Idol idol1 = new Idol("김채원", 24, leSserafim);
        Idol idol2 = new Idol("사쿠라", 26, leSserafim);
        Idol idol3 = new Idol("가을", 22, ive);
        Idol idol4 = new Idol("리즈", 20, ive);

        idolRepository.save(idol1);
        idolRepository.save(idol2);
        idolRepository.save(idol3);
        idolRepository.save(idol4);

    }
    @Test
    @DisplayName("JPQL로 특정 이름의 아이돌 조회하기")
    void jpqlTest() {
        //given
        String jpqlQuery = "select i from Idol i WHERE i.idolName = ?1"; // safety 한게 QueryDSL

        //when
        Idol foundIdol = em.createQuery(jpqlQuery, Idol.class)
                .setParameter(1, "가을")
                .getSingleResult();

        //then
        assertEquals("아이브", foundIdol.getGroup().getGroupName());

        System.out.println("\n\n\n\n");
        System.out.println("foundIdol = " + foundIdol);
        System.out.println("foundIdol.getGroup() = " + foundIdol.getGroup());
        System.out.println("\n\n\n\n");
    }

    @Test
    @DisplayName("QueryDsl로 특정 이름의 아이돌 조회하기")
    void queryDslTest() {
        //given
        // QueryDsl로 JPQL을 만드는 빌더
//        JPAQueryFactory factory = new JPAQueryFactory(em);

        //when
        Idol foundIdol = factory
                .select(idol) // 모두 조회
//                .select(QIdol.idol.idolName) // 특정 필드 조회
                .from(idol) // QIdol에서 idol 엔터티
                .where(idol.idolName.eq("사쿠라"))
                .fetchOne();

        //then
        assertEquals("르세라핌", foundIdol.getGroup().getGroupName());

        System.out.println("\n\n\n\n");
        System.out.println("foundIdol = " + foundIdol);
        System.out.println("foundIdol.getGroup() = " + foundIdol.getGroup());
        System.out.println("\n\n\n\n");
    }

    @Test
    @DisplayName("이름과 나이로 아이돌 조회하기")
    void searchTest() {
        //given
        String name = "리즈";
        int age = 20;

        //when
        Idol foundIdol = factory.select(idol)
                .from(idol)
                .where(
                        idol.idolName.eq(name)
                                .and(idol.age.eq(age))
                )
                .fetchOne();

        //then
        assertNotNull(foundIdol);
        assertEquals("아이브",  foundIdol.getGroup().getGroupName());

        System.out.println("\n\n\n\n");
        System.out.println("foundIdol = " + foundIdol);
        System.out.println("foundIdol.getGroup() = " + foundIdol.getGroup());
        System.out.println("\n\n\n\n");

//        idol.idolName.eq("리즈") // idolName = '리즈'
//        idol.idolName.ne("리즈") // username != '리즈'
//        idol.idolName.eq("리즈").not() // username != '리즈'
//        idol.idolName.isNotNull() //이름이 is not null
//        idol.age.in(10, 20) // age in (10,20)
//        idol.age.notIn(10, 20) // age not in (10, 20)
//        idol.age.between(10,30) //between 10, 30
//        idol.age.goe(30) // age >= 30   greater or equal
//        idol.age.gt(30) // age > 30     greater
//        idol.age.loe(30) // age <= 30   less or equal
//        idol.age.lt(30) // age < 30     less
//        idol.idolName.like("_김%")  // like _김%
//        idol.idolName.contains("김") // like %김%
//        idol.idolName.startsWith("김") // like 김%
//        idol.idolName.endsWith("김") // like %김


    }

    @Test
    @DisplayName("조회 결과 반환하기")
    void fetchTest() {

        // 리스트 조회     fetch() 결과는 List
        List<Idol> idolList = factory.select(idol)
                .from(idol)
                .fetch();
        System.out.println("============= fetch =============");
        idolList.forEach(System.out::println);

    }
    @Test
    @DisplayName("단일")
    void fetchOneTest() {
        // 단일행 조회 (fetchOne)
        // 단일행 조회시 null safety를 위한 Optional로 받고 싶을 때
        Optional<Idol> foundIdol = Optional.ofNullable(factory
                .select(idol)
                .from(idol)
                .where(idol.age.lt(21))
                .fetchOne()
        );
        Idol foundIdol2 = foundIdol.orElseThrow();

        System.out.println("============= fetch =============");
        System.out.println("foundIdol2 = " + foundIdol2);

    }
    
    @Test
    @DisplayName("나이가 24세 이상인 아이돌을 조회하세요.")
    void findByAgeTest() {
        //given
        List<Idol> idolList = factory.select(idol)
                .from(idol)
                .where(idol.age.goe(24))
                .fetch();
        
        System.out.println("============= fetch =============");
        idolList.forEach(System.out::println);
//        ============= fetch =============
//        Idol(id=1, idolName=김채원, age=24)
//        Idol(id=2, idolName=사쿠라, age=26)
    }
    @Test
    @DisplayName("이름에 '김'이라는 문자열이 포함된 아이돌을 조회하세요.")
    void findByNameTest() {
        //given
        List<Idol> idolList = factory.select(idol)
                .from(idol)
                .where(idol.idolName.contains("김"))
                .fetch();

        System.out.println("============= fetch =============");
        idolList.forEach(System.out::println);

//        ============= fetch =============
//        Idol(id=1, idolName=김채원, age=24)
    }
//    @Test
//    @DisplayName("이름에 '김'이 포함된 아이돌 조회")
//    void testNameContains() {
//        // given
//        String substring = "김";
//
//        // when
//        List<Idol> result = factory
//                .selectFrom(idol)
//                .where(idol.idolName.contains(substring))
//                .fetch();
//
//        // then
//        assertFalse(result.isEmpty());
//        for (Idol idol : result) {
//            System.out.println("Idol: " + idol);
//            assertTrue(idol.getIdolName().contains(substring));
//        }
//    }
    
    @Test
    @DisplayName("나이가 20세에서 25세 사이인 아이돌을 조회하세요.")
    void findByAgeBetweenTest() {
        //given
        List<Idol> idolList = factory.select(idol)
                .from(idol)
                .where(idol.age.between(20, 25))
                .fetch();

        System.out.println("============= fetch =============");
        idolList.forEach(System.out::println);

//        ============= fetch =============
//        Idol(id=1, idolName=김채원, age=24)
//        Idol(id=3, idolName=가을, age=22)
//        Idol(id=4, idolName=리즈, age=20)
    }
    @Test
    @DisplayName("그룹이름이 문자열 '르세라핌'인 아이돌을 조회하세요.")
    void findGroupNameTest() {

        List<Idol> idolList = factory.select(idol)
                .from(idol)
                .where(idol.group.groupName.eq("르세라핌"))
                .fetch();
        System.out.println("============= fetch =============");
        idolList.forEach(System.out::println);

//        ============= fetch =============
//        Idol(id=1, idolName=김채원, age=24)
//        Idol(id=2, idolName=사쿠라, age=26)
    }
    
    



}