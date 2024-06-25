package com.spring.jpastudy.chap01.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="tbl_product")
public class Product {

    @Id
    @Column(name="prod_id")
    private Long id; // PK

    @Column(name="prod_nm")
    private String name; // 상품명

    @Column(name="prod_price")
    private int price; // 상품 가격
}
