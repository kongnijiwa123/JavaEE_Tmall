package tmall.dao;

import tmall.bean.Product;
import tmall.bean.Property;
import tmall.bean.PropertyValue;
import tmall.util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PropertyValueDAO {
    public int getTotal() {
        int total=0;
        String sql = "select count(*) from propertyValue";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ResultSet rs=ps.executeQuery();

            if (rs.next()) {
                total = rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return total;
    }

    public void add(PropertyValue propertyValue) {
        String sql = "insert into propertyValue values(null,?,?,?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, propertyValue.getProduct().getId());
            ps.setInt(2, propertyValue.getProperty().getId());
            ps.setString(3, propertyValue.getValue());

            ps.execute();
            ResultSet rs=ps.getGeneratedKeys();
            if (rs.next()) {
                propertyValue.setId(rs.getInt(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void update(PropertyValue propertyValue) {
        String sql = "update propertyValue set pid=?,ptid=?,value=? where id=?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, propertyValue.getProduct().getId());
            ps.setInt(2, propertyValue.getProperty().getId());
            ps.setString(3, propertyValue.getValue());
            ps.setInt(4, propertyValue.getId());

            ps.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void delete(int id) {
        String sql = "delete from propertyValue where id=" + id;
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public PropertyValue get(int id) {
        PropertyValue propertyValue=null;
        String sql = "select * from propertyValue where id=" + id;

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ResultSet rs=ps.executeQuery();

            if (rs.next()) {
                propertyValue=new PropertyValue();
                propertyValue.setId(id);
                propertyValue.setValue(rs.getString("value"));
                propertyValue.setProduct(new ProductDAO().get(rs.getInt("pid")));
                propertyValue.setProperty(new PropertyDAO().get(rs.getInt("ptid")));

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return propertyValue;
    }

    public PropertyValue get(int pid, int ptid) {
        PropertyValue propertyValue=null;
        String sql = "select * from propertyValue where pid=? and ptid =?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, pid);
            ps.setInt(2, ptid);

            ResultSet rs=ps.executeQuery();

            if (rs.next()) {
                propertyValue=new PropertyValue();
                propertyValue.setId(rs.getInt("id"));
                propertyValue.setProduct(new ProductDAO().get(pid));
                propertyValue.setProperty(new PropertyDAO().get(ptid));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return propertyValue;
    }

    public List<PropertyValue> list() {
        return list(0, Short.MAX_VALUE);
    }

    private List<PropertyValue> list(int start, int  count) {
        List<PropertyValue> propertyValueList = new ArrayList<>();
        String sql = "select * from propertyValue order by id desc limit ?,?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, start);
            ps.setInt(2, count);

            ResultSet rs=ps.executeQuery();
            while (rs.next()) {
                PropertyValue propertyValue=new PropertyValue();

                propertyValue.setId(rs.getInt("id"));
                propertyValue.setValue(rs.getString("value"));
                propertyValue.setProduct(new ProductDAO().get(rs.getInt("pid")));
                propertyValue.setProperty(new PropertyDAO().get(rs.getInt("ptid")));

                propertyValueList.add(propertyValue);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return propertyValueList;
    }

    public void init(Product product) {
        List<Property> propertyList=new PropertyDAO().list(product.getCategory().getId());

        for (Property pt : propertyList) {
            PropertyValue propertyValue=get(product.getId(),pt.getId());
            if (null == propertyValue) {
                propertyValue=new PropertyValue();

                propertyValue.setProduct(product);
                propertyValue.setProperty(pt);

                this.add(propertyValue);
            }
        }
    }

    public List<PropertyValue> list(int pid) {
        List<PropertyValue> propertyValueList = new ArrayList<>();
        String sql = "select * from propertyValue where pid=" + pid + "order by ptid desc";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ResultSet rs=ps.executeQuery();
            while (rs.next()) {
                PropertyValue propertyValue=new PropertyValue();

                propertyValue.setId(rs.getInt("id"));
                propertyValue.setValue(rs.getString("value"));
                propertyValue.setProduct(new ProductDAO().get(pid));
                propertyValue.setProperty(new PropertyDAO().get(rs.getInt("ptid")));

                propertyValueList.add(propertyValue);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return propertyValueList;
    }
}
