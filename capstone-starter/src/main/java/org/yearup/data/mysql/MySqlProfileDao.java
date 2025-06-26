package org.yearup.data.mysql;

import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Component;
import org.yearup.models.Profile;
import org.yearup.data.ProfileDao;

import javax.sql.DataSource;
import java.sql.*;

@Component
public class MySqlProfileDao extends MySqlDaoBase implements ProfileDao
{
    // the creation of the MySqlDaoBase as a parent class is to allow the child classes
    // to inherit the datasource object which allows the class to have access to the same database
    public MySqlProfileDao(DataSource dataSource)
    {
        super(dataSource);
    }

    @Override
    public Profile create(Profile profile)
    {
        String sql = "INSERT INTO profiles (user_id, first_name, last_name, phone, email, address, city, state, zip) " +
                " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try(Connection connection = getConnection())
        {
            PreparedStatement ps = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            ps.setInt(1, profile.getUserId());
            ps.setString(2, profile.getFirstName());
            ps.setString(3, profile.getLastName());
            ps.setString(4, profile.getPhone());
            ps.setString(5, profile.getEmail());
            ps.setString(6, profile.getAddress());
            ps.setString(7, profile.getCity());
            ps.setString(8, profile.getState());
            ps.setString(9, profile.getZip());

            ps.executeUpdate();

            return profile;
        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Profile getByUserId(int userId) {
        Profile profile = new Profile();
        String query = """
                select * from profiles
                """;
        
        try(Connection c = getConnection();
        PreparedStatement s = c.prepareStatement(query);
        ResultSet result = s.executeQuery())
        {
            while(result.next()){
                profile = mapRow(result);
                
                }
                
            } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return profile;
    }
    
    @Override
    public void update(Profile profile) {
        String query = """
                update profiles
                set first_name = ?,
                last_name = ?,
                phone = ?,
                email = ?,
                address = ?,
                city = ?,
                state = ?,
                zip = ?
                where user_id = ?
                """;
        try(Connection c = getConnection();
        PreparedStatement s = c.prepareStatement(query))
        {
            s.setString(1, profile.getFirstName());
            s.setString(2, profile.getLastName());
            s.setString(3, profile.getPhone());
            s.setString(4, profile.getEmail());
            s.setString(5, profile.getAddress());
            s.setString(6, profile.getCity());
            s.setString(7, profile.getState());
            s.setString(8, profile.getZip());
            s.setInt(9, profile.getUserId());
            
            int rowsAffected = s.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    
    private Profile mapRow(ResultSet row){
        try{
            int userId = row.getInt("user_id");
            String firstName = row.getString("first_name");
            String lastname = row.getString(3);
            String phoneNumber = row.getString(4);
            String email = row.getString(5);
            String address = row.getString(6);
            String city = row.getString(7);
            String state = row.getString(8);
            String zip = row.getString(9);
            
            return new Profile(userId, firstName, lastname, phoneNumber, email, address, city, state, zip);
            
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
