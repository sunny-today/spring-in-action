package com.ssookie.tacos;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;

@Data   // 인자가 있는 생성자를 자동으로 추가
@RequiredArgsConstructor    // 인자가 있는 생성자는 제거되지만
@NoArgsConstructor(access= AccessLevel.PRIVATE, force=true) // → private 의 인자 없는 생성자와 더불어 인자가 있는 생성자를 여전히 가질 수 있음.
@Entity // JPA entity로 선언
public class Ingredient {
    @Id // DB entity를 고유하게 식별
    private final String id;

    private final String name;
    private final Type type;

    // 초기화가 필요한 final 속성 → @NoArgsConstructor 의 force 속성을 true로 지정
    public static enum Type {
        WRAP, PROTEIN, VEGGIES, CHEESE, SAUCE
    }
}
