package tmall.dao;

import tmall.bean.Product;
import tmall.bean.ProductImage;
import tmall.util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProductImageDAO {
    public static final String type_single = "type_single";
    public static final String type_detail = "type_detail";

    public int getTotal() {
        int total=0;
        String sql = "select count(*) from productimage";
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

    public void add(ProductImage productImage) {
        String sql = "insert into productimage values(null,?,?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, productImage.getProduct().getId());
            ps.setString(2, productImage.getTyep());

            ps.execute();
            ResultSet rs=ps.getGeneratedKeys();
            if (rs.next()) {
                productImage.setId(rs.getInt(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void update(ProductImage productImage) {
        String sql = "update productimage set pid=?,type=? where id=?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, productImage.getProduct().getId());
            ps.setString(2, productImage.getTyep());
            ps.setInt(3, productImage.getId());

            ps.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void delete(int id) {
        String sql="delete from productimage where id="+id;
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ProductImage get(int id) {
        ProductImage productImage=null;
        String sql = "select * from productImage where id=" + id;

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ResultSet rs=ps.executeQuery();
            if (rs.next()) {
                productImage=new ProductImage();

                productImage.setId(rs.getInt("id"));
                productImage.setProduct(new ProductDAO().get(rs.getInt("pid")));
                productImage.setTyep(rs.getString("type"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return productImage;
    }

    public List<ProductImage> list(Product product, String type) {
        return list(product, type, 0, Short.MAX_VALUE);
    }

    private List<ProductImage> list(Product product, String type, int start, int count) {
        List<ProductImage> productImageList = new ArrayList<>();

        String sql = "select * from productImage where pid=? and type=? order by id desc limit ?,?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, product.getId());
            ps.setString(2, type);
            ps.setInt(3, start);
            ps.setInt(4, count);

            ResultSet rs=ps.executeQuery();
            while (rs.next()) {
                ProductImage productImage=new ProductImage();

                productImage.setId(rs.getInt("id"));
                productImage.setTyep(rs.getString("type"));
                productImage.setProduct(product);

                productImageList.add(productImage);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return productImageList;
    }

    public List<ProductImage> list(int pid) {
        List<ProductImage> productImageList = new ArrayList<>();

        String sql = "select * from productImage where pid=? ";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, pid);


            ResultSet rs=ps.executeQuery();
            while (rs.next()) {
                ProductImage productImage=new ProductImage();

                productImage.setId(rs.getInt("id"));
                productImage.setTyep(rs.getString("type"));
                productImage.setProduct(new ProductDAO().get(pid));

                productImageList.add(productImage);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return productImageList;
    }
}
