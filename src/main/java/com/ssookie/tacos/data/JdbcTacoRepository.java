//package com.ssookie.tacos.data;
//
//import com.ssookie.tacos.Ingredient;
//import com.ssookie.tacos.Taco;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.jdbc.core.JdbcTemplate;
//import org.springframework.jdbc.core.PreparedStatementCreator;
//import org.springframework.jdbc.core.PreparedStatementCreatorFactory;
//import org.springframework.jdbc.support.GeneratedKeyHolder;
//import org.springframework.jdbc.support.KeyHolder;
//import org.springframework.stereotype.Repository;
//
//import java.sql.*;
//import java.util.Arrays;
//import java.util.Date;
//
//@Repository
//public class JdbcTacoRepository implements TacoRepository{
//
//    private JdbcTemplate jdbc;
//
//    @Autowired
//    public JdbcTacoRepository(JdbcTemplate jdbc) {
//        this.jdbc = jdbc;
//    }
//
//    @Override
//    public Taco save(Taco taco) {
//        long tacoId = saveTacoInfo(taco);   // Taco 테이블에 각 식자재 저장
//        taco.setId(tacoId);
//        for (Ingredient ingredient : taco.getIngredients()) {
//            saveIngredientToTaco(ingredient, tacoId);   // 타코와 식자재 연관 정보 저장
//        }
//        return taco;
//    }
//
//    private long saveTacoInfo(Taco taco) {
//        taco.setCreatedAt(new Date());
//
//        // 단계 1) SQL과 매개변수의 타입을 인자로 전달하여 PreparedStatementCreatorFactory 생성
//        PreparedStatementCreatorFactory preparedStatementCreatorFactory = new PreparedStatementCreatorFactory(
//                "insert into Taco (name, createdAt) values (?, ?)",
//                Types.VARCHAR, Types.TIMESTAMP);
//        preparedStatementCreatorFactory.setReturnGeneratedKeys(true); // 자동 생성 된 키를 반환할 수 있어야 하는지 여부 설정 (default false)
//        preparedStatementCreatorFactory.setGeneratedKeysColumnNames("id"); // 자동 생성 된 키의 열 이름을 설정
//
//        // 단계 2) PreparedStatementCreator 를 생성하기 위해 쿼리 매개변수 값을 인자로 전달함
//        PreparedStatementCreator psc = preparedStatementCreatorFactory.newPreparedStatementCreator(
//                Arrays.asList(
//                        taco.getName(),
//                        new Timestamp(taco.getCreatedAt().getTime())
//                )
//        );
//
//        // 단계3) PreparedStatementCreator 객체와 KeyHolder객체를 인자로 전달
//        KeyHolder keyHolder = new GeneratedKeyHolder();
//        jdbc.update(psc, keyHolder);    // KeyHolder 객체: 생성된 타코 ID를 제공함.
//        return keyHolder.getKey().longValue();
//    }
//
//    private void saveIngredientToTaco(Ingredient ingredient, long tacoId) {
//        jdbc.update("insert into Taco_Ingredients (taco, ingredient) values (?, ?)",
//                tacoId, ingredient.getId());
//    }
//}
