package com.spring.jpastudy.event.repository;

import com.spring.jpastudy.event.entity.Event;

import java.util.List;

public interface EventRepositoryCustom {

    List<Event> findEvents(String sort);

    // ... 필요한 거 작성하면 됨 -> JPA 리포지토리에 상속

}
