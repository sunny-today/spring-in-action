//package com.ssookie.tacos.data;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.ssookie.tacos.Order;
//import com.ssookie.tacos.Taco;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.jdbc.core.JdbcTemplate;
//import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
//import org.springframework.stereotype.Repository;
//
//import java.util.Date;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//@Repository
//@Deprecated
//public class JdbcOrderRepository implements OrderRepository {
//
//    private SimpleJdbcInsert orderInserter;
//    private SimpleJdbcInsert orderTacoInserter;
//    private ObjectMapper objectMapper;  // 객체를 Map 으로 변환하기위해 사용함.
//
//    /**
//     * 인스턴스 변수에  JdbcTemplate을 직접 지정하지 않고, JdbcTemplate을 사용하여 2개의 SimpleJdbcInsert 인스턴스를 생성
//     * @param jdbc
//     */
//    @Autowired
//    public JdbcOrderRepository(JdbcTemplate jdbc) {
//        this.orderInserter = new SimpleJdbcInsert(jdbc)
//                .withTableName("Taco_Order")
//                .usingGeneratedKeyColumns("id");    // 데이터베이스가 생성해 주는 것을 사용하기 위함.
//
//        this.orderTacoInserter = new SimpleJdbcInsert(jdbc)
//                .withTableName("Taco_Order_Tacos"); // DB가 생성해주는 id를 사용하지 않고, 직접 지정하여 사용할 것임.
//
//        this.objectMapper = new ObjectMapper();
//    }
//
//    @Override
//    public Order save(Order order) {
//        order.setPlacedAt(new Date());
//        long orderId = saveOrderDetails(order);
//        order.setId(orderId);
//        List<Taco> tacos = order.getTacos();
//        for (Taco taco : tacos) {
//            saveTacoToOrder(taco, orderId);
//        }
//
//        return order;
//    }
//
//    private long saveOrderDetails(Order order) {
//        @SuppressWarnings("unchecked")
//        Map<String, Object> values =  objectMapper.convertValue(order, Map.class); // Order 객체를 Map 으로 변환
//        values.put("placedAt", order.getPlacedAt()); // ObjectMapper 는 Date 타입의 값을 long 으로 변환하므로, 컬럼과 타입 호환되지 않기 때문에 변경해줌.
//        long orderId =
//                orderInserter
//                .executeAndReturnKey(values)    // 해당 테이블에 저장한 후, DB에 생성된 id가 Number객체로 반환
//                .longValue();
//        return orderId;
//    }
//
//    private void saveTacoToOrder(Taco taco, long orderId) {
//        Map<String, Object> values = new HashMap<>();
//        values.put("tacoOrder", orderId);
//        values.put("taco", taco.getId());
//        orderTacoInserter.execute(values);
//    }
//}
