package org.yearup.data.mysql;

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
    public void create(Profile profile)
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

        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Profile getByUserId(int userId) {
        // we return a profile so the user can do something to the object (update/delete)
        // we find the Profile through the users' id
        Profile profile = new Profile();
        String query = """
                select * from profiles
                where user_id = ?
                """;
        
        try(Connection c = getConnection();
        PreparedStatement s = c.prepareStatement(query);)
        {
            s.setInt(1, userId);
            
            ResultSet row = s.executeQuery();
            while(row.next()){
                
                profile = mapRow(row);
                
                }
                
            } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return profile;
    }
    
    @Override
    public void update(Profile profile) {
        // the user must pass in a profile to be updated
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
            
            s.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    
    private Profile mapRow(ResultSet row){
        try{
            int userId = row.getInt("user_id");
            String firstName = row.getString("first_name");
            String lastname = row.getString("last_name");
            String phoneNumber = row.getString("phone");
            String email = row.getString("email");
            String address = row.getString("address");
            String city = row.getString("city");
            String state = row.getString("state");
            String zip = row.getString("zip");
            
            return new Profile(userId, firstName, lastname, phoneNumber, email, address, city, state, zip);
            
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
