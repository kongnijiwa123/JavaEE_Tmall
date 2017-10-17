package tmall.dao;

import tmall.bean.Category;
import tmall.bean.Property;
import tmall.util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PropertyDAO {
    public int getTotal(int cid){
        int total=0;
        String sql = "select count(*) from Property where cid =" + cid;

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ResultSet rs=ps.executeQuery();
            while (rs.next()) {
                total = rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return total;
    }

    public void add(Property property) {
        String sql = "insert into property values(null,?,?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, property.getCategory().getId());
            ps.setString(2, property.getName());

            ps.execute();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                property.setId(rs.getInt(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void update(Property property) {
        String sql = "update property set cid=?,name=? where id=?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, property.getCategory().getId());
            ps.setString(2, property.getName());
            ps.setInt(3, property.getId());

            ps.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void delete(int id) {
        String sql = "delete from property where id=" + id;
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Property get(String name, int cid) {
        Property property=null;
        String sql="select * from property where name="+name+"and cid="+cid;
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ResultSet rs=ps.executeQuery();
            if (rs.next()) {
                property=new Property();

                property.setId(rs.getInt("id"));
                property.setName(name);
                property.setCategory(new CategoryDAO().get(cid));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return property;
    }

    public Property get(int id) {
        Property property=null;
        String sql="select * from property where id="+id;
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ResultSet rs=ps.executeQuery();
            if (rs.next()) {
                property=new Property();

                property.setId(id);
                property.setName(rs.getString("name"));
                property.setCategory(new CategoryDAO().get(rs.getInt("cid")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return property;
    }

    public List<Property> list(int cid) {
        return list(cid, 0, Short.MAX_VALUE);
    }

    public List<Property> list(int cid, int start, int count) {
        List<Property> propertyList = new ArrayList<>();
        String sql = "select * from property where cid=? order by id desc limit ?,?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, cid);
            ps.setInt(2, start);
            ps.setInt(3, count);

            ResultSet rs=ps.executeQuery();

            while (rs.next()) {
                Property property=new Property();

                property.setId(rs.getInt("id"));
                property.setName(rs.getString("name"));
                property.setCategory(new CategoryDAO().get(rs.getInt("cid")));

                propertyList.add(property);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return propertyList;
    }
}
