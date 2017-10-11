package tmall.dao;

import tmall.bean.Product;
import tmall.bean.Review;
import tmall.util.DBUtil;
import tmall.util.DateUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ReviewDAO {
    public int getTotal() {
        int total=0;
        String sql = "select count(*) from review";

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

    public int getTotal(int pid) {
        int total=0;
        String sql="select count(*) from review where pid="+pid;

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ResultSet rs=ps.executeQuery();
            if (rs.next()) {
                total=rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return total;
    }

    public void add(Review review) {
        String sql = "insert into review values(null,?,?,?,?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, review.getContent());
            ps.setInt(2, review.getUser().getId());
            ps.setInt(3, review.getProduct().getId());
            ps.setTimestamp(4, DateUtil.dateToTimestamp(review.getCreateDate()));

            ps.execute();
            ResultSet rs=ps.getGeneratedKeys();
            if (rs.next()) {
                review.setId(rs.getInt(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void update(Review review) {
        String sql = "update review set content=?,uid=?,pid=?,createDate=? where id =?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, review.getContent());
            ps.setInt(2, review.getUser().getId());
            ps.setInt(3, review.getProduct().getId());
            ps.setTimestamp(4, DateUtil.dateToTimestamp(review.getCreateDate()));
            ps.setInt(5, review.getId());

            ps.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void delete(int id) {
        String sql = "delete from review where id=" + id;
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Review get(int id) {
        Review review=null;
        String sql = "select * from review where id=" + id;
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ResultSet rs=ps.executeQuery();
            if (rs.next()) {
                review=new Review();
                review.setId(rs.getInt("id"));
                review.setContent(rs.getString("content"));
                review.setUser(new UserDAO().get(rs.getInt("uid")));
                review.setProduct(new ProductDAO().get(rs.getInt("pid")));
                review.setCreateDate(DateUtil.timestampToDate(rs.getTimestamp("createDate")));

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return review;
    }

    public List<Review> list(int pid) {
        return list(pid, 0, Short.MAX_VALUE);
    }

/*    public int getCount(int pid) {
        int count=0;
        String sql="select * from review where pid="+pid;

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ResultSet rs=ps.executeQuery();
            if (rs.next()) {
                count = rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return  count;
    }*/

    public List<Review> list(int pid, int start, int count) {
        List<Review> reviewList = new ArrayList<>();
        String sql = "select * from review where pid=? order by id desc limit ?,?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, pid);
            ps.setInt(2, start);
            ps.setInt(3, count);

            ResultSet rs=ps.executeQuery();
            while (rs.next()) {
                Review review=new Review();

                review.setId(rs.getInt("id"));
                review.setContent(rs.getString("content"));
                review.setUser(new UserDAO().get(rs.getInt("uid")));
                review.setProduct(new ProductDAO().get(pid));
                review.setCreateDate(DateUtil.timestampToDate(rs.getTimestamp("createDate")));

                reviewList.add(review);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reviewList;
    }

    public boolean isExist(String content, int pid) {
        String sql = "select * from review where content=? and pid=?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, content);
            ps.setInt(2, pid);

            ResultSet rs=ps.executeQuery();

            if (rs.next()) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return  false;
    }
}
