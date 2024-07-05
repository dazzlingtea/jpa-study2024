package com.spring.jpastudy.event.repository;

import com.spring.jpastudy.event.entity.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface EventRepositoryCustom {

    Page<Event> findEvents(Pageable pageable, String sort);

    // ... 필요한 거 작성하면 됨 -> JPA 리포지토리에 상속

}
