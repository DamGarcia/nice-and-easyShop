package org.yearup.data.mysql;

import org.springframework.stereotype.Component;
import org.yearup.data.CategoryDao;
import org.yearup.models.Category;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Component
public class MySqlCategoryDao extends MySqlDaoBase implements CategoryDao
{
    public MySqlCategoryDao(DataSource dataSource)
    {
        super(dataSource);
    }

    @Override
    // get all categories
    public List<Category> getAllCategories()
    {
        // category array list to hold the available categories
        List<Category> categories = new ArrayList<>();
        
        // query to search for the desired data
        String query = """
                select category_id, name, description
                from categories;
                """;
    
        // connect to the database to retrieve the data
        try(Connection c = getConnection();
            PreparedStatement s = c.prepareStatement(query);
            ResultSet row = s.executeQuery())
        {

            // while there are results to iterate through
            // retrieve the column data to create the object and add the object to the list
            while(row.next()){
                Category category = mapRow(row);
                categories.add(category);
            }
            
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


        return categories;
    }

    @Override
    // get category by id
    public Category getById(int categoryId)
    {
        String query = """
                select category_id, name, description
                from categories
                where category_id = ?;
                """;
        
        try(Connection c = getConnection();
        PreparedStatement s = c.prepareStatement(query))
        {
            s.setInt(1, categoryId);
            
            ResultSet row = s.executeQuery();
            
            while(row.next()){
                return mapRow(row);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        
        return null;
    }

    @Override
    // create a new category
    public Category create(Category category)
    {
        String insertQuery = """
                insert into categories (name, description)
                values (?, ?);
                """;
        
        try(Connection c = getConnection();
        PreparedStatement s = c.prepareStatement(insertQuery, PreparedStatement.RETURN_GENERATED_KEYS))
        {
            s.setString(1, category.getName());
            s.setString(2, category.getDescription());
            
            int rowsAffected = s.executeUpdate();
            // get generated keys to set the auto incremented value in db
            if(rowsAffected > 0){
                ResultSet keys = s.getGeneratedKeys();
                
                if(keys.next()){
                    int categoryID = keys.getInt(1);
                    
                    return getById(categoryID);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    @Override
    // update category
    public void update(int categoryId, Category category)
    {
        String updateQuery = """
                update categories
                set name = ?,
                description = ?
                where category_id = ?;
                """;
        
        try(Connection c = getConnection();
        PreparedStatement s = c.prepareStatement(updateQuery))
        {
            s.setString(1, category.getName());
            s.setString(2, category.getDescription());
            s.setInt(3, categoryId);
            
            s.executeUpdate();
            
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    // delete category
    public void delete(int categoryId)
    {
        String deleteQuery = """
                delete from categories
                where category_id = ?;
                """;
        
        try(Connection c = getConnection();
        PreparedStatement s = c.prepareStatement(deleteQuery))
        {
            s.setInt(1, categoryId);
            
            s.executeUpdate();
            
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // helper method
    private Category mapRow(ResultSet row) throws SQLException
    {
        int categoryId = row.getInt("category_id");
        String name = row.getString("name");
        String description = row.getString("description");

        Category category = new Category()
        {{
            setCategoryId(categoryId);
            setName(name);
            setDescription(description);
        }};

        return category;
    }

}
