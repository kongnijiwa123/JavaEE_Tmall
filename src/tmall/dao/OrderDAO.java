package tmall.dao;

import tmall.bean.Order;
import tmall.bean.User;
import tmall.util.DBUtil;
import tmall.util.DateUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class OrderDAO {
    public static final String waitPay = "waitPay";
    public static final String waitDelivery = "waitDelivery";
    public static final String waitConfirm = "waitConfirm";
    public static final String waitReview = "waitReview";
    public static final String finish = "finish";
    public static final String delete = "delete";

    public int getTotal() {
        int total=0;
        String sql = "select count(*) from order_";

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

    public int getPriceTotal(int oid) {
        int total=0;
        String sql = "select promotePrice,number from order_,orderitem,product where order_.id=? and orderitem.oid=order_.id and product.id=orderitem.pid;";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1,oid);

            ResultSet rs=ps.executeQuery();
            while (rs.next()) {
                total += rs.getFloat("promotePrice") * rs.getInt("number");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return total;
    }


    public void add(Order order) {
        String sql="insert into order_ values(null,?,?,?,?,?,?,?,?,?,?,?,?)";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, order.getOrderCode());
            ps.setString(2, order.getAddress());
            ps.setString(3, order.getPost());
            ps.setString(4, order.getReceiver());
            ps.setString(5, order.getMobile());
            ps.setString(6, order.getUserMessage());

            ps.setTimestamp(7,  DateUtil.dateToTimestamp(order.getCreateDate()));
            ps.setTimestamp(8,  DateUtil.dateToTimestamp(order.getPayDate()));
            ps.setTimestamp(9,  DateUtil.dateToTimestamp(order.getDeliveryDate()));
            ps.setTimestamp(10,  DateUtil.dateToTimestamp(order.getConfirmDate()));
            ps.setInt(11, order.getUser().getId());
            ps.setString(12, order.getStatus());

            ps.execute();

            ResultSet rs = ps.getGeneratedKeys();

            if (rs.next()) {
                order.setId(rs.getInt(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void update(Order order) {
        String sql="update order_ set address= ?, post=?, receiver=?,mobile=?,userMessage=? ,createDate = ? , payDate =? , deliveryDate =?, confirmDate = ? , orderCode =?, uid=?, status=? where id = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, order.getAddress());
            ps.setString(2, order.getPost());
            ps.setString(3, order.getReceiver());
            ps.setString(4, order.getMobile());
            ps.setString(5, order.getUserMessage());
            ps.setTimestamp(6, DateUtil.dateToTimestamp(order.getCreateDate()));;
            ps.setTimestamp(7, DateUtil.dateToTimestamp(order.getPayDate()));;
            ps.setTimestamp(8, DateUtil.dateToTimestamp(order.getDeliveryDate()));;
            ps.setTimestamp(9, DateUtil.dateToTimestamp(order.getConfirmDate()));;
            ps.setString(10, order.getOrderCode());
            ps.setInt(11, order.getUser().getId());
            ps.setString(12, order.getStatus());
            ps.setInt(13, order.getId());
            ps.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void delete(int id) {
        String sql = "delete from order_ where id=" + id;
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Order get(int id) {
        Order order=null;
        String sql="select * from order_ where id=" + id;

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ResultSet rs=ps.executeQuery();

            if (rs.next()) {
                order=new Order();

                String orderCode =rs.getString("orderCode");
                String address = rs.getString("address");
                String post = rs.getString("post");
                String receiver = rs.getString("receiver");
                String mobile = rs.getString("mobile");
                String userMessage = rs.getString("userMessage");
                String status = rs.getString("status");
                int uid =rs.getInt("uid");
                Date createDate = DateUtil.timestampToDate( rs.getTimestamp("createDate"));
                Date payDate = DateUtil.timestampToDate( rs.getTimestamp("payDate"));
                Date deliveryDate = DateUtil.timestampToDate( rs.getTimestamp("deliveryDate"));
                Date confirmDate = DateUtil.timestampToDate( rs.getTimestamp("confirmDate"));

                order.setOrderCode(orderCode);
                order.setAddress(address);
                order.setPost(post);
                order.setReceiver(receiver);
                order.setMobile(mobile);
                order.setUserMessage(userMessage);
                order.setCreateDate(createDate);
                order.setPayDate(payDate);
                order.setDeliveryDate(deliveryDate);
                order.setConfirmDate(confirmDate);
                User user = new UserDAO().get(uid);
                order.setUser(user);
                order.setStatus(status);

                order.setId(id);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return order;
    }

    public List<Order> list() {
        return list(0, Short.MAX_VALUE);
    }

    public List<Order> list(int start, int count) {
        List<Order> orderList = new ArrayList<Order>();

        String sql = "select * from order_ order by id desc limit ?,? ";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, start);
            ps.setInt(2, count);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Order order = new Order();
                String orderCode =rs.getString("orderCode");
                String address = rs.getString("address");
                String post = rs.getString("post");
                String receiver = rs.getString("receiver");
                String mobile = rs.getString("mobile");
                String userMessage = rs.getString("userMessage");
                String status = rs.getString("status");
                Date createDate = DateUtil.timestampToDate( rs.getTimestamp("createDate"));
                Date payDate = DateUtil.timestampToDate( rs.getTimestamp("payDate"));
                Date deliveryDate = DateUtil.timestampToDate( rs.getTimestamp("deliveryDate"));
                Date confirmDate = DateUtil.timestampToDate( rs.getTimestamp("confirmDate"));
                int uid =rs.getInt("uid");

                int id = rs.getInt("id");
                order.setId(id);
                order.setOrderCode(orderCode);
                order.setAddress(address);
                order.setPost(post);
                order.setReceiver(receiver);
                order.setMobile(mobile);
                order.setUserMessage(userMessage);
                order.setCreateDate(createDate);
                order.setPayDate(payDate);
                order.setDeliveryDate(deliveryDate);
                order.setConfirmDate(confirmDate);
                User user = new UserDAO().get(uid);
                order.setUser(user);
                order.setStatus(status);

                orderList.add(order);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orderList;
    }

    public List<Order> list(int uid,String excludedStatus) {
        return list(uid,excludedStatus,0, Short.MAX_VALUE);
    }

    public List<Order> list(int uid, String excludedStatus, int start, int count) {
        List<Order> orderList = new ArrayList<Order>();

        String sql = "select * from order_ where uid = ? and status != ? order by id desc limit ?,? ";

        try (Connection c = DBUtil.getConnection(); PreparedStatement ps = c.prepareStatement(sql);) {

            ps.setInt(1, uid);
            ps.setString(2, excludedStatus);
            ps.setInt(3, start);
            ps.setInt(4, count);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Order order = new Order();

                String orderCode =rs.getString("orderCode");
                String address = rs.getString("address");
                String post = rs.getString("post");
                String receiver = rs.getString("receiver");
                String mobile = rs.getString("mobile");
                String userMessage = rs.getString("userMessage");
                String status = rs.getString("status");
                Date createDate = DateUtil.timestampToDate( rs.getTimestamp("createDate"));
                Date payDate = DateUtil.timestampToDate( rs.getTimestamp("payDate"));
                Date deliveryDate = DateUtil.timestampToDate( rs.getTimestamp("deliveryDate"));
                Date confirmDate = DateUtil.timestampToDate( rs.getTimestamp("confirmDate"));

                int id = rs.getInt("id");
                order.setId(id);
                order.setOrderCode(orderCode);
                order.setAddress(address);
                order.setPost(post);
                order.setReceiver(receiver);
                order.setMobile(mobile);
                order.setUserMessage(userMessage);
                order.setCreateDate(createDate);
                order.setPayDate(payDate);
                order.setDeliveryDate(deliveryDate);
                order.setConfirmDate(confirmDate);
                User user = new UserDAO().get(uid);
                order.setStatus(status);
                order.setUser(user);

                orderList.add(order);
            }
        } catch (SQLException e) {

            e.printStackTrace();
        }
        return orderList;
    }

}

