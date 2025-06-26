package org.yearup.data.mysql;

import org.springframework.stereotype.Component;
import org.yearup.data.OrderDao;
import org.yearup.models.Order;
import org.yearup.models.Profile;
import org.yearup.models.ShoppingCart;


import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.*;

@Component
public class MySqlOrderDao extends MySqlDaoBase implements OrderDao {
    
    public MySqlOrderDao(DataSource dataSource){super(dataSource);}

    @Override
    public Order getByUserId(int userId) {
        Order order = new Order();
        String query = """
                select * from profiles
                where user_id = ?;
                """;
        
        try(Connection c = getConnection();
            PreparedStatement s = c.prepareStatement(query);)
        {
            s.setInt(1, userId);
            ResultSet row = s.executeQuery();
            while(row.next()){
                order =  mapRow(row);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        
        return order;
    }

    @Override
    public void create(Profile profile, ShoppingCart cart) {
        String insertQuery = """
                insert into orders (user_id, date, address, city, state, zip, shipping_amount)
                values (?, CURRENT_TIMESTAMP(), ?, ?, ?, ?, ?)
                """;
        
        try(Connection c = getConnection();
        PreparedStatement s = c.prepareStatement(insertQuery))
        {
            s.setInt(1, profile.getUserId());
            s.setString(2, profile.getAddress());
            s.setString(3, profile.getCity());
            s.setString(4, profile.getState());
            s.setString(5, profile.getZip());
            s.setBigDecimal(6, cart.getTotal());
            
            
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void update(int userId, Order order) {
        String updateQuery = """
                update orders
                set date = ?,
                address = ?,
                city = ?,
                state = ?,
                zip = ?,
                shipping_amount = ?
                where user_id = ?
                """;
        
        try(Connection c = getConnection();
        PreparedStatement s = c.prepareStatement(updateQuery))
        {
            s.setDate(1, order.getDate());
            s.setString(2, order.getAddress());
            s.setString(3, order.getCity());
            s.setString(4, order.getState());
            s.setString(5, order.getZip());
            s.setBigDecimal(6, order.getShippingAmount());
            s.setInt(7, order.getUserId());
            
            s.executeUpdate();
            
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Order delete(int userId) {
        Order order = new Order();
        String deleteQuery = """
                delete from orders
                where user_id = ?
                """;
        
        try(Connection c = getConnection();
        PreparedStatement s = c.prepareStatement(deleteQuery))
        {
            s.setInt(1, userId);
            
            s.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        
        return order;
    }
    
    // helper method
    protected static Order mapRow(ResultSet row){
        try{
            int orderId = row.getInt("order_id");
            int user_id = row.getInt("user_id");
            Date date = row.getDate("date");
            String address = row.getString("address");
            String city = row.getString("city");
            String state = row.getString("state");
            String zip = row.getString("zip");
            BigDecimal shippingAmount = row.getBigDecimal("shipping_amount");
            
            return new Order(orderId, user_id, date, address, city, state, zip , shippingAmount);
                    
            
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
