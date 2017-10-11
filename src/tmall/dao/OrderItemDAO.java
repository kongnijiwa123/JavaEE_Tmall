package tmall.dao;

import tmall.bean.Order;
import tmall.bean.OrderItem;
import tmall.bean.Product;
import tmall.bean.User;
import tmall.util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class OrderItemDAO {
    public int getTotal() {
        int total=0;
        String sql = "select count(*) from orderItem";

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

    public void add(OrderItem orderItem) {
        String sql = "insert into orderItem values(null,?,?,?,?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, orderItem.getProduct().getId());

            //订单项在创建的时候，是没有订单信息的
            if (null == orderItem.getOrder()) {
                ps.setInt(2, -1);
            }else {
                ps.setInt(2, orderItem.getOrder().getId());
            }

            ps.setInt(3, orderItem.getUser().getId());
            ps.setInt(4, orderItem.getNumber());

            ps.execute();
            ResultSet rs=ps.getGeneratedKeys();
            if (rs.next()) {
                orderItem.setId(rs.getInt("id"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void update(OrderItem orderItem) {
        String sql = "update OrderItem set pid= ?, oid=?, uid=?,number=?  where id = ?";
        try (Connection c = DBUtil.getConnection(); PreparedStatement ps = c.prepareStatement(sql);) {

            ps.setInt(1, orderItem.getProduct().getId());
            if(null==orderItem.getOrder())
                ps.setInt(2, -1);
            else
                ps.setInt(2, orderItem.getOrder().getId());
            ps.setInt(3, orderItem.getUser().getId());
            ps.setInt(4, orderItem.getNumber());

            ps.setInt(5, orderItem.getId());
            ps.execute();

        } catch (SQLException e) {

            e.printStackTrace();
        }

    }

    public void delete(int id) {
        String sql="delete from orderItem where id="+id;
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public OrderItem get(int id) {
        OrderItem orderItem=null;
        String sql = "select * from orderItem where id=" + id;

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ResultSet rs=ps.executeQuery();
            if (rs.next()) {
                orderItem=new OrderItem();

                int pid = rs.getInt("pid");
                int oid = rs.getInt("oid");
                int uid = rs.getInt("uid");
                int number = rs.getInt("number");
                Product product = new ProductDAO().get(pid);
                User user = new UserDAO().get(uid);
                orderItem.setProduct(product);
                orderItem.setUser(user);
                orderItem.setNumber(number);

                if(-1!=oid){
                    Order order= new OrderDAO().get(oid);
                    orderItem.setOrder(order);
                }

                orderItem.setId(id);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orderItem;
    }

    //查询某个用户的未生成的订单项（既购物车中的订单项）
    public List<OrderItem> listByUser(int uid) {
        return listByUser(uid, 0, Short.MAX_VALUE);
    }

    private List<OrderItem> listByUser(int uid, int start, int count) {
        List<OrderItem> orderItemList = new ArrayList<>();

        //查询某个用户的未生成的订单项（既购物车中的订单项）
        String sql = "select * from orderItem where uid=? and oid=-1 order by id desc limit ?,?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, uid);
            ps.setInt(2, start);
            ps.setInt(3, count);

            ResultSet rs=ps.executeQuery();

            while (rs.next()) {
                OrderItem orderItem=new OrderItem();

                orderItem.setId(rs.getInt("id"));
                orderItem.setNumber(rs.getInt("number"));
                orderItem.setUser(new UserDAO().get(uid));
                orderItem.setProduct(new ProductDAO().get(rs.getInt("pid")));
                //orderItem.setOrder(new OrderDAO().get(rs.getInt("oid")));
                int oid = rs.getInt("oid");
                if (-1 != oid) {
                    Order order = new OrderDAO().get(oid);
                    orderItem.setOrder(order);
                }

                orderItemList.add(orderItem);

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orderItemList;
    }

    public List<OrderItem> listByOrder(int oid) {
        return listByOrder(oid, 0, Short.MAX_VALUE);
    }

    private List<OrderItem> listByOrder(int oid, int start, int count) {
        List<OrderItem> orderItemList = new ArrayList<>();

        String sql = "select * from orderItem where oid=? order by id desc limit ?,?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, oid);
            ps.setInt(2, start);
            ps.setInt(3, count);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                OrderItem orderItem=new OrderItem();

                orderItem.setId(rs.getInt("id"));
                orderItem.setNumber(rs.getInt("number"));
                orderItem.setUser(new UserDAO().get(rs.getInt("uid")));
                orderItem.setProduct(new ProductDAO().get(rs.getInt("pid")));
                //orderItem.setOrder(new OrderDAO().get(rs.getInt("oid")));
                if (-1 != oid) {
                    Order order = new OrderDAO().get(oid);
                    orderItem.setOrder(order);
                }

                orderItemList.add(orderItem);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orderItemList;
    }

    public void fill(List<Order> orderList) {
        for (Order order : orderList) {
            List<OrderItem> orderItemList = listByOrder(order.getId());
            float total=0;
            int totalNumber=0;
            for (OrderItem orderItem : orderItemList) {
                total += orderItem.getNumber() * orderItem.getProduct().getPromotePrice();
                totalNumber+=orderItem.getNumber();
            }
            order.setTotal(total);
            order.setTotalNumber(totalNumber);
            order.setOrderItems(orderItemList);
        }
    }

    public void fill(Order order) {
        List<OrderItem> orderItemList = listByOrder(order.getId());

        float total=0;
        for (OrderItem orderItem : orderItemList) {
            total += orderItem.getNumber() * orderItem.getProduct().getPromotePrice();
        }
        order.setTotal(total);
        order.setOrderItems(orderItemList);
    }

    public List<OrderItem> listByProduct(int pid) {
        return listByProduct(pid, 0, Short.MAX_VALUE);
    }

    private List<OrderItem> listByProduct(int pid, int start, int count) {
        List<OrderItem> orderItemList = new ArrayList<>();
        String sql = "select * from orderItem where pid =? order by id desc limit ?,?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, pid);
            ps.setInt(2, start);
            ps.setInt(3, count);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                OrderItem orderItem = new OrderItem();

                orderItem.setId(rs.getInt("id"));
                orderItem.setNumber(rs.getInt("number"));
                orderItem.setUser(new UserDAO().get(rs.getInt("uid")));
                orderItem.setProduct(new ProductDAO().get(pid));

                int oid = rs.getInt("oid");
                if (-1 != oid) {
                    Order order = new OrderDAO().get(oid);
                    orderItem.setOrder(order);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orderItemList;
    }

    public int getSaleCount(int pid) {
        int total=0;
        String sql = "select sum(number) from orderItem where pid=?" + pid;

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
}
