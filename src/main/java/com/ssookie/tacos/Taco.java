package com.ssookie.tacos;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;
import java.util.List;

@Data
@Entity
public class Taco {

    // 객체 식별 필드
    // Getter, Setter 및 생성자는 런타임 시에 Lombok이 자동 생성해줄 것임.
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)  // DB가 자동으로 생성해주는 ID값
    private Long id;

    // 이력 저장을 위한 필드
    private Date createdAt;

    @NotNull
    @Size(min = 5, message = "Name must be at least 5 characters long")
    private String name;

    @ManyToMany(targetEntity=Ingredient.class)  // Taco객체와 Ingredient 객체의 관계
    @Size(min = 1, message = "You must choose at least 1 ingredient")
    private List<Ingredient> ingredients;

    @PrePersist
    void createdAt() {
        this.createdAt = new Date();
    }
}
