package com.spring.jpastudy.chap06_querydsl.repository;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.spring.jpastudy.chap06_querydsl.dto.GroupAverageAgeDto;
import com.spring.jpastudy.chap06_querydsl.entity.Group;
import com.spring.jpastudy.chap06_querydsl.entity.Idol;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static com.spring.jpastudy.chap06_querydsl.entity.QIdol.idol;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
//@Rollback(value = false)
class QueryDslGroupingTest {

    @Autowired
    IdolRepository idolRepository;

    @Autowired
    GroupRepository groupRepository;

    @Autowired
    JPAQueryFactory factory;

    @BeforeEach
    void setUp() {
        //given
        Group leSserafim = new Group("르세라핌");
        Group ive = new Group("아이브");
        Group bts = new Group("방탄소년단");
        Group newjeans = new Group("뉴진스");

        groupRepository.save(leSserafim);
        groupRepository.save(ive);
        groupRepository.save(bts);
        groupRepository.save(newjeans);

        Idol idol1 = new Idol("김채원", 24, "여", leSserafim);
        Idol idol2 = new Idol("사쿠라", 26, "여", leSserafim);
        Idol idol3 = new Idol("가을", 22, "여", ive);
        Idol idol4 = new Idol("리즈", 20, "여", ive);
        Idol idol5 = new Idol("장원영", 20, "여", ive);
        Idol idol6 = new Idol("안유진", 21, "여", ive);
        Idol idol7 = new Idol("카즈하", 21, "여", leSserafim);
        Idol idol8 = new Idol("RM", 29, "남", bts);
        Idol idol9 = new Idol("정국", 26, "남", bts);
        Idol idol10 = new Idol("해린", 18, "여", newjeans);
        Idol idol11 = new Idol("혜인", 16, "여", newjeans);

        idolRepository.save(idol1);
        idolRepository.save(idol2);
        idolRepository.save(idol3);
        idolRepository.save(idol4);
        idolRepository.save(idol5);
        idolRepository.save(idol6);
        idolRepository.save(idol7);
        idolRepository.save(idol8);
        idolRepository.save(idol9);
        idolRepository.save(idol10);
        idolRepository.save(idol11);

    }

    @Test
    @DisplayName("성별별로 그룹화하여 아이돌의 숫자가 3명 이하인 그룹만 조회")
    void groupByGenderTest() {
        //given
        // 성별별, 그룹별이면?
        // 디비관점이면 groupId(FK), 객체관점에서는 group 객체 자체를 기준으로 그룹화

        /*
            SELECT G.g*, I.gender, COUNT(I.idol_id)
            FROM tbl_idol I
            JOIN tbl_group G
            ON I.group_id = G.group_id
            GROUP BY G.group_id, I.gender
         */

        //when
        List<Tuple> idolList = factory.select(idol.group, idol.gender, idol.count())
                .from(idol)
                .groupBy(idol.gender, idol.group)
                .having(idol.count().loe(3))
//                .having(idol.age.avg().loe(3))
                .fetch();

        //then
        System.out.println("\n\n");
        for (Tuple tuple : idolList) {
            Group group = tuple.get(idol.group);// 그룹정보만 튜플에서 추출하겠다
            String s = tuple.get(idol.gender);
            Long count = tuple.get(idol.count());
            System.out.println(String.format("\n그룹명: %s, 성별: %s, 인원수: %d\n"
            ,group.getGroupName(), s, count));
        }
//        System.out.println("idolList = " + idolList);
        System.out.println("\n\n");
    }
    @Test
    @DisplayName("연령대별로 그룹화하여 아이돌 수를 조회")
    void ageGroupTest() {
        /*
            SELECT
                CASE age WHEN BETWEEN 10 AND 19 THEN 10
                CASE age WHEN BETWEEN 20 AND 29 THEN 20
                CASE age WHEN BETWEEN 30 AND 39 THEN 30
                END,
                COUNT(idol_id)
            FROM tbl_idol
            GROUP BY
                CASE age WHEN BETWEEN 10 AND 19 THEN 10
                CASE age WHEN BETWEEN 20 AND 29 THEN 20
                CASE age WHEN BETWEEN 30 AND 39 THEN 30
                END

         */

        //given

        // QueryDSL로 CASE WHEN THEN 표현식 만들기
        NumberExpression<Integer> ageGroupExpression = new CaseBuilder()
                .when(idol.age.between(10, 19)).then(10)
                .when(idol.age.between(20, 29)).then(20)
                .when(idol.age.between(30, 39)).then(30)
                .otherwise(0); // .as() 별칭

        //when
        List<Tuple> result = factory
                .select(ageGroupExpression, idol.count())
                .from(idol)
                .groupBy(ageGroupExpression)
                .having(idol.count().gt(5))
                .fetch();

        //then
        assertFalse(result.isEmpty());
        for (Tuple tuple : result) {
            int ageGroupValue = tuple.get(ageGroupExpression);
            long count = tuple.get(idol.count());

            System.out.println("\n\nAge Group: " + ageGroupValue + "대, Count: " + count);
        }
    }
    // 아이돌 그룹별로 아이돌의 그룹명과 평균 나이를 조회하세요.
    // 평균 나이가 20세와 25세 사이인 그룹만 조회합니다.
    @Test
    @DisplayName("그룹별 평균 나이 조회")
    void groupAvgAgeTest() {
        //given
        /*
            SELECT G.group_name, AVG(I.age)
            FROM tbl_idol I
            JOIN tbl_group G
            on I.group_id = G.group_id
            GROUP BY G.group_id
            HAVING AVG(I.age) BETWEEN 20 AND 25
         */
        //when
        List<Tuple> result = factory.select(idol.group.groupName, idol.age.avg())
                .from(idol)   // idol 안에 group이 있어서 idol만 사용해도 조인 가능
                .groupBy(idol.group) // lazyloading 으로 조인
                .having(idol.age.avg().between(20, 25))
                .fetch();

        //then
        assertFalse(result.isEmpty());
        for (Tuple tuple : result) {
            String groupName = tuple.get(idol.group.groupName);
            double averageAge = tuple.get(idol.age.avg());

            System.out.println("\n\nGroup: " + groupName + ", Average Age: " + averageAge);
        }
    }

    @Test
    @DisplayName("그룹별 평균 나이 조회(결과 DTO 처리)")
    void groupAvgAgeDtoTest() {
        //given
        /*
            SELECT G.group_name, AVG(I.age)
            FROM tbl_idol I
            JOIN tbl_group G
            on I.group_id = G.group_id
            GROUP BY G.group_id
            HAVING AVG(I.age) BETWEEN 20 AND 25
         */
        //when  -- 튜플 대신 커스텀 DTO
        // Projections: 커스텀 DTO를 포장해주는 객체
        List<GroupAverageAgeDto> result = factory
                .select(
                        Projections.constructor(
                                GroupAverageAgeDto.class,
                                idol.group.groupName,
                                idol.age.avg()
                        )
                )
                .from(idol)   // idol 안에 group이 있어서 idol만 사용해도 조인 가능
                .groupBy(idol.group) // lazyloading 으로 조인
                .having(idol.age.avg().between(20, 25))
                .fetch();

        //then
        assertFalse(result.isEmpty());
        for (GroupAverageAgeDto dto : result) {
            String groupName = dto.getGroupName();
            double averageAge = dto.getAverageAge();

            System.out.println("\n\nGroup: " + groupName + ", Average Age: " + averageAge);
        }
    }


}