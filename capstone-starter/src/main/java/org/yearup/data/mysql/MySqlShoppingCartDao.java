package org.yearup.data.mysql;

import org.springframework.stereotype.Component;
import org.yearup.models.Product;
import org.yearup.models.ShoppingCart;
import org.yearup.models.ShoppingCartItem;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class MySqlShoppingCartDao extends MySqlDaoBase implements org.yearup.data.ShoppingCartDao {

    public MySqlShoppingCartDao(DataSource dataSource) {
        super(dataSource);
    }
    
    @Override
    public void create(int userId, int productId) {
        
        // this query works so that when a user adds a new item to their cart
        // it will auto update its value by 1 if a product is already in the user's cart
        String insertQuery = """
                insert into shopping_cart (user_id, product_id)
                values (?, ?)
                on duplicate key update quantity = quantity + 1;
                """;
        
        try(Connection c = getConnection();
        PreparedStatement s = c.prepareStatement(insertQuery))
        {
            s.setInt(1, userId);
            s.setInt(2, productId);
            
            s.executeUpdate();
            
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ShoppingCart getByUserId(int userId) {
        ShoppingCart shoppingCart = new ShoppingCart();

        String query = """
                select p.*, sc.*
                from products p
                left join shopping_cart sc on p.product_id  = sc.product_id
                where user_id = ?;
                """;

        try(Connection c = getConnection();
            PreparedStatement s = c.prepareStatement(query))
        {
            s.setInt(1, userId);

            ResultSet row = s.executeQuery();

            while(row.next()){
                // we want to return a user's cart with the shopping cart item
                // since we dont receieve a quantity from the user, we have to get the quantity
                // from the database column
                Product product = mapRow(row);
                ShoppingCartItem item = new ShoppingCartItem();
                item.setProduct(product);
                item.setQuantity(row.getInt("quantity"));

                shoppingCart.add(item);
            }

            return shoppingCart;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void update(int userId, ShoppingCartItem item) {
        String updateQuery = """
                update shopping_cart
                set quantity = ?
                where user_id = ? and product_id = ?;
                """;
        
        try(Connection c = getConnection();
        PreparedStatement s = c.prepareStatement(updateQuery))
        {
            s.setInt(1, item.getQuantity());
            s.setInt(2, userId);
            s.setInt(3, item.getProductId());
            
            s.executeUpdate();
            
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ShoppingCart delete(int userId) {
        // this method returns a shopping cart object so the user still receieves a shopping cart after the clear
        // the cart will simply be empty
        ShoppingCart shoppingCart = new ShoppingCart();
        
        String deleteQuery = """
                delete from shopping_cart
                where user_id = ?;
                """;
        
        try(Connection c = getConnection();
        PreparedStatement s = c.prepareStatement(deleteQuery))
        {
            s.setInt(1, userId);
            
            s.executeUpdate();

            
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        
        return shoppingCart;
    }

    // helper method
    protected static Product mapRow(ResultSet row)
    {
        try{
            int productId = row.getInt("product_id");
            String productName = row.getString("name");
            BigDecimal productPrice = row.getBigDecimal("price");
            int categoryId = row.getInt("category_id");
            String description = row.getString("description");
            String color = row.getString("color");
            int stock = row.getInt("stock");
            boolean isFeatured = row.getBoolean("featured");
            String imageUrl = row.getString("image_url");
    
            return new Product(productId, productName, productPrice, categoryId, description, color, stock, isFeatured, imageUrl);
            
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
