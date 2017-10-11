package tmall.dao;

import tmall.bean.Category;
import tmall.util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategoryDAO {
    public int getTotal() {
        int total=0;
        String sql = "select count(*) from Category";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery(sql);
            while (rs.next()) {
                total = rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return total;
    }

    public void add(Category category) {
        String sql = "insert into category value(null,?)";
        try(Connection conn=DBUtil.getConnection();
        PreparedStatement ps=conn.prepareStatement(sql)) {
            ps.setString(1, category.getName());

            ps.execute();

            ResultSet rs = ps.getGeneratedKeys();

            if (rs.next()) {
                category.setId(rs.getInt(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void update(Category category) {
        String sql = "update category set name = ? where id=?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, category.getName());
            ps.setInt(2, category.getId());

            ps.execute();

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public void delete(int id) {
        String sql="delete from Category where id="+id;
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Category get(int id) {
        String sql="select * from Category where id = "+id;
        Category category=null;

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ResultSet rs=ps.executeQuery();

            if (rs.next()) {
                category=new Category();
                category.setId(id);
                category.setName(rs.getString("name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return category;
    }

    public List<Category> list() {
        return list(0, Short.MAX_VALUE);
    }

    public List<Category> list(int start, int count) {
        List<Category> categoryList = new ArrayList<>();
        String sql="select * form Category order by id desc ?,? ";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, start);
            ps.setInt(2, count);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Category category=new Category();

                category.setId(rs.getInt("id"));
                category.setName(rs.getString("name"));

                categoryList.add(category);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return categoryList;
    }
}
