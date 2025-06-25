package org.yearup.data.mysql;

import org.springframework.stereotype.Component;
import org.yearup.data.ShoppingCartDao;
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
public class MySqlShoppingCartDao extends MySqlDaoBase implements ShoppingCartDao {

    public MySqlShoppingCartDao(DataSource dataSource) {
        super(dataSource);
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
                
                Product product = mapRow(row);
                ShoppingCartItem item = new ShoppingCartItem();
                item.setProduct(product);
                item.setQuantity(1);
                
                shoppingCart.add(item);
            }
            
            return shoppingCart;
            
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void addProduct(int userId, int productId) {
        
        String insertQuery = """
                insert into shopping_cart (user_id, product_id, quantity)
                values (?, ?, ?)
                on duplicate key update quantity = quantity + 1;
                """;
        
        try(Connection c = getConnection();
        PreparedStatement s = c.prepareStatement(insertQuery))
        {
            s.setInt(1, userId);
            s.setInt(2, productId);
            s.setInt(3, 1);
            s.executeUpdate();
            
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    
    @Override
    public void updateProduct(int userId, ShoppingCartItem item) {
        String updateQuery = """
                update shopping_cart
                set quantity = ?
                where user_id = ? and product_id = ?
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
    public void clearCart(int userId) {
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
    }

    protected static Product mapRow(ResultSet row) throws SQLException
    {
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
    }
}
