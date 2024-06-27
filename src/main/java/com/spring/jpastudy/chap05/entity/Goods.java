package com.spring.jpastudy.chap05.entity;

import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Setter @Getter
@ToString(exclude = "purchaseList")
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "tbl_mtm_goods")
public class Goods {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "goods_id")
    private Long id;
    private String name;

    // 상대편에선 날 뭐라고 부르나 -> mappedBy
    @OneToMany(mappedBy = "goods", orphanRemoval = true, cascade = CascadeType.ALL)
    @Builder.Default
    private List<Purchase> purchaseList = new ArrayList<>();
}
