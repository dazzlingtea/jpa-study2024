package com.spring.jpastudy.chap04_relation.entity;

import lombok.*;

import javax.persistence.*;

@Getter @Setter
@EqualsAndHashCode(of="id")
@NoArgsConstructor
@AllArgsConstructor
@Builder

@Entity
@Table(name = "tbl_emp")
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "emp_id")
    private Long id; // 사원번호

    @Column(name = "emp_name", nullable = false)
    private String name;

}
