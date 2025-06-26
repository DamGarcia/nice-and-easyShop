package org.yearup.data.mysql;

import org.springframework.stereotype.Component;
import org.yearup.data.OrderLineItemDao;
import org.yearup.models.OrderLineItem;
import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class MySqlOrderLineItemDao extends MySqlDaoBase implements OrderLineItemDao {
    
    public MySqlOrderLineItemDao(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public OrderLineItem getByOrderId(int orderId) {
        OrderLineItem orderLineItem = new OrderLineItem();
        String query = """
                select * from order_line_items
                where order_id = ?
                """;
        
        try(Connection c = getConnection();
            PreparedStatement s = c.prepareStatement(query))
        {
            s.setInt(1, orderId);

            ResultSet row = s.executeQuery();
            
            while(row.next()){
                orderLineItem = mapRow(row);
            }
            
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        
        return orderLineItem;
    }

    @Override
    public void create(OrderLineItem orderLineItem){
        String insertQuery = """
                insert into order_line_items (order_id, product_id, sales_price, quantity, discount)
                values (?, ?, ?, ?, ?)
                """;
        
        try(Connection c = getConnection();
        PreparedStatement s = c.prepareStatement(insertQuery, PreparedStatement.RETURN_GENERATED_KEYS))
        {
            s.setInt(1, orderLineItem.getOrderId());
            s.setInt(2, orderLineItem.getProductId());
            s.setBigDecimal(3, orderLineItem.getSalesPrice());
            s.setInt(4, orderLineItem.getQuantity());
            s.setBigDecimal(5, orderLineItem.getDiscount());

            int rowsAffected = s.executeUpdate();
            
            if(rowsAffected > 0){
                try(ResultSet key = s.getGeneratedKeys()){
                    if(key.next()){
                        orderLineItem.setOrderLineItemId(key.getInt(1));
                    }
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void update(OrderLineItem orderLineItem) {

    }

    @Override
    public OrderLineItem delete(int orderId) {
        return null;
    }

    // helper method
    protected static OrderLineItem mapRow(ResultSet row){
        try{
            int orderLineItemId = row.getInt("order_line_item_id");
            int orderId = row.getInt("order_id");
            int productId = row.getInt("product_id");
            BigDecimal salesPrice = row.getBigDecimal("sales_price");
            int quantity = row.getInt("quantity");
            BigDecimal discount = row.getBigDecimal("discount");
            
            return new OrderLineItem(orderLineItemId, orderId, productId, salesPrice, quantity, discount);
            
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
