//package com.ssookie.tacos.data;
//
//import com.ssookie.tacos.Ingredient;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.jdbc.core.JdbcTemplate;
//import org.springframework.jdbc.core.RowMapper;
//import org.springframework.stereotype.Repository;
//
//import java.sql.ResultSet;
//import java.sql.SQLException;
//
//@Repository // 스테레오 타입 애노테이션, 스프링 컴포넌트 검색에서 이 클래스를 자동으로 찾아서 스프링 애플리케이션 컨텍스트의 빈으로 생성
//public class JdbcIngredientRepository implements IngredientRepository{
//
//    private JdbcTemplate jdbc;
//
//    @Autowired // JdbcIngredientRepository 빈이 생성되면 → 스프링이 이걸 JdbcTempalte에 주입(연결)함.
//    public JdbcIngredientRepository(JdbcTemplate jdbc) {
//        this.jdbc = jdbc; // JdbcIngredientRepository 의 생성자에서 JdbcTemplate 참조를 인스턴스 변수에 저장 → 쿼리를 위해 메서드에서 사용
//    }
//
//    @Override
//    public Iterable<Ingredient> findAll() {
//        // query(Sql 명령, RowMapper 인터페이스 구현체)
//        return jdbc.query("select id, name, type from Ingredient",
//                this::mapRowToIngredient); // mapRowToIngredient 메서드는 ResultSet의 row 개수만큼 호출
//    }
//
//    @Override
//    public Ingredient findById(String id) {
//        // queryForObject(Sql 명령, RowMapper 인터페이스 구현체, 인자 전달) → 물음표 대신 교체되어 쿼리에 사용
//        return jdbc.queryForObject(
//                "select id, name, type from Ingredient where id=?",
//                this::mapRowToIngredient, id);
//    }
//
//    @Override
//    public Ingredient findByIdMap(String id) {
//        // RowM
//        return jdbc.queryForObject(
//                "select id, name, type from Ingredient where id=?",
//                // RowMapper 인터페이스의 mapRow() 메서드 직접 구현
//                // findByIdMap 이 호출될 때마다 RowMapper를 구현한 익명 클래스 인스턴스가 생성되어 인자로 전달된 후, mapRow() 메서드 실행됨
//                new RowMapper<Ingredient>() {
//                    @Override
//                    public Ingredient mapRow(ResultSet rs, int rowNum) throws SQLException {
//                        return new Ingredient(
//                                rs.getString("id"),
//                                rs.getString("name"),
//                                Ingredient.Type.valueOf(rs.getString("type")));
//                    };
//                }
//                , id);
//    }
//
//    private Ingredient mapRowToIngredient(ResultSet rs, int rowNum)
//            throws SQLException {
//        return new Ingredient(
//                rs.getString("id"),
//                rs.getString("name"),
//                Ingredient.Type.valueOf(rs.getString("type")));
//    }
//
//    @Override
//    public Ingredient save(Ingredient ingredient) {
//        // update(Sql 명령, 인자 전달)
//        jdbc.update(
//                "insert into Ingredient (id, name, type) values (?, ?, ?)",
//                ingredient.getId(),
//                ingredient.getName(),
//                ingredient.getType().toString());
//        return ingredient;
//    }
//}
