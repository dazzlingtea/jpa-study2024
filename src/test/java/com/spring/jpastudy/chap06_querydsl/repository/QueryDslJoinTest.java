package com.spring.jpastudy.chap06_querydsl.repository;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.spring.jpastudy.chap06_querydsl.dto.GroupAverageAgeDto;
import com.spring.jpastudy.chap06_querydsl.entity.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.spring.jpastudy.chap06_querydsl.entity.QAlbum.*;
import static com.spring.jpastudy.chap06_querydsl.entity.QGroup.group;
import static com.spring.jpastudy.chap06_querydsl.entity.QIdol.idol;
import static org.junit.jupiter.api.Assertions.assertFalse;

@SpringBootTest
@Transactional
//@Rollback(value = false)
class QueryDslJoinTest {

    @Autowired
    IdolRepository idolRepository;

    @Autowired
    GroupRepository groupRepository;

    @Autowired
    AlbumRepository albumRepository;

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
        Idol idol12 = new Idol("김종국", 48, "남", null);
        Idol idol13 = new Idol("아이유", 31, "여", null);

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
        idolRepository.save(idol12);
        idolRepository.save(idol13);

        Album album1 = new Album("MAP OF THE SOUL 7", 2020, bts);
        Album album2 = new Album("FEARLESS", 2022, leSserafim);
        Album album3 = new Album("UNFORGIVEN", 2023, bts);
        Album album4 = new Album("ELEVEN", 2021, ive);
        Album album5 = new Album("LOVE DIVE", 2022, ive);
        Album album6 = new Album("OMG", 2023, newjeans);

        albumRepository.save(album1);
        albumRepository.save(album2);
        albumRepository.save(album3);
        albumRepository.save(album4);
        albumRepository.save(album5);
        albumRepository.save(album6);
    }

    @Test
    @DisplayName("내부조인 예제")
    void innerJoinTest() {
        //given

        //when
        List<Tuple> idolList = factory
                .select(idol, group)
                .from(idol)
                // 첫번째 파라미터는 from절에 있는 엔터티의 연관 객체
                // 두번째 파라미터는 실제로 조인할 엔터티
                .innerJoin(idol.group, group)
                .fetch();
        //then
        System.out.println("\n\n");
        for (Tuple tuple : idolList) {
            Idol foundIdol = tuple.get(idol);
            Group foundGroup = tuple.get(group);
            System.out.println(foundIdol);
            System.out.println(foundGroup);
        }
        System.out.println("\n\n");
    }
    @Test
    @DisplayName("Left Outer Join")
    void outerJoinTest() {
        //given

        //when
        List<Tuple> result = factory.select(idol, group)
                .from(idol)
                .leftJoin(idol.group, group)
                .fetch();

        //then
        assertFalse(result.isEmpty());
        for (Tuple tuple : result) {
            Idol i = tuple.get(idol);
            Group g = tuple.get(group);

            System.out.println("\nIdol: " + i.getIdolName()
                    + ", Group: "
                    + (g != null ? g.getGroupName() : "솔로가수"));
        }
    }
    // 연습문제
    @Test
    @DisplayName("문제 1.  ‘아이브’ 그룹에 속한 아이돌의 이름과 그룹명 조회")
    void practice1Test() {
        //given
        String groupName = "아이브";
        //when
        List<Tuple> tuples = factory.select(idol, group)
                .from(idol)
                .innerJoin(idol.group, group)
                .where(group.groupName.eq("아이브"))
                .fetch();

        //then
        assertFalse(tuples.isEmpty());
        for (Tuple tuple : tuples) {
            System.out.printf("\n#이름: %s, 그룹명: %s\n", tuple.get(idol).getIdolName(), tuple.get(group).getGroupName());
        }
    }
    @Test
    @DisplayName("문제2. 그룹별 평균 나이 계산하여 평균 나이가 22세 이상인 그룹의 그룹명과 평균나이 조회")
    void practice2Test() {
        //when
        List<Tuple> tuples = factory.select(group.groupName, idol.age.avg())
                .from(idol)
                .innerJoin(idol.group, group)
                .groupBy(group.id)
                .having(idol.age.avg().goe(22)).fetch();

        //then
        assertFalse(tuples.isEmpty());
        for (Tuple tuple : tuples) {
            String groupName = tuple.get(group.groupName);
            double averageAge = tuple.get(idol.age.avg());
            System.out.printf("\n#그룹명: %s, 평균나이: %.2f\n", groupName, averageAge);
        }
    }
    @Test
    @DisplayName("문제3. 2022년에 발매된 앨범이 있는 아이돌의 이름과 그룹명과 앨범명과 발매년도 조회")
    void innerJoinAlbumTest() {
        //given
        int year = 2022;
        //when
        List<Tuple> tuples = factory.select(idol, album)
                .from(idol)
                .innerJoin(idol.group, group)
                .innerJoin(group.albums, album)
                .where(album.releaseYear.eq(year))
                .fetch();
        //then
        assertFalse(tuples.isEmpty());
        for (Tuple tuple : tuples) {
            Idol i = tuple.get(idol);
            Album a = tuple.get(album);
            System.out.printf("\n#아이돌명: %s, 그룹명: %s, 앨범명: %s, 발매연도: %d년\n",
                    i.getIdolName(), i.getGroup().getGroupName(), a.getAlbumName(), a.getReleaseYear()
                    );
        }
    }
    




}