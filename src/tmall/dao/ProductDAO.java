package tmall.dao;

import tmall.bean.Category;
import tmall.bean.Product;
import tmall.bean.ProductImage;
import tmall.util.DBUtil;
import tmall.util.DateUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ProductDAO {
    public int getTotal(int cid) {
        int total=0;
        String sql = "select count(*) from product where cid=" + cid;

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

    public void add(Product product) {
        String sql = "insert into product values(null,?,?,?,?,?,?,?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, product.getName());
            ps.setString(2, product.getSubTitle());
            ps.setFloat(3, product.getOrignalPrice());
            ps.setFloat(4, product.getPromotePrice());
            ps.setInt(5, product.getStock());
            ps.setInt(6, product.getCategory().getId());
            ps.setTimestamp(7, DateUtil.dateToTimestamp(product.getCreateDate()));
            ps.execute();

            ResultSet rs=ps.getGeneratedKeys();
            if (rs.next()) {
                product.setId(rs.getInt(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void update(Product product) {
        String sql = "update product set name=?,subTitle=?,orignalPrice=?,promotePrice=?,stock=?,cid=?,createDate=? where id=?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, product.getName());
            ps.setString(2, product.getSubTitle());
            ps.setFloat(3, product.getOrignalPrice());
            ps.setFloat(4, product.getPromotePrice());
            ps.setInt(5, product.getStock());
            ps.setInt(6, product.getCategory().getId());
            ps.setTimestamp(7, DateUtil.dateToTimestamp(product.getCreateDate()));
            ps.setInt(8, product.getId());
            ps.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public void delete(int id) {
        String sql = "delete from product where id=" + id;
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Product get(int id) {
        Product product=null;
        String sql="select * from product where id="+id;

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ResultSet rs=ps.executeQuery();
            if (rs.next()) {
                product=new Product();

                String name = rs.getString("name");
                String subTitle = rs.getString("subTitle");
                float orignalPrice = rs.getFloat("orignalPrice");
                float promotePrice = rs.getFloat("promotePrice");
                int stock = rs.getInt("stock");
                int cid = rs.getInt("cid");
                Date createDate = DateUtil.timestampToDate( rs.getTimestamp("createDate"));

                product.setName(name);
                product.setSubTitle(subTitle);
                product.setOrignalPrice(orignalPrice);
                product.setPromotePrice(promotePrice);
                product.setStock(stock);
                Category category = new CategoryDAO().get(cid);
                product.setCategory(category);
                product.setCreateDate(createDate);
                product.setId(id);
                setFirstProductImage(product);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return product;
    }

    public List<Product> list(int cid) {
        return list(cid, 0, Short.MAX_VALUE);
    }

    public List<Product> list(int cid, int start, int count) {
        List<Product> productList = new ArrayList<>();
        Category category = new CategoryDAO().get(cid);
        String sql = "select * from product where cid=? order by id desc limit ?,?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, cid);
            ps.setInt(2, start);
            ps.setInt(3, count);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Product product = new Product();
                int id = rs.getInt(1);
                String name = rs.getString("name");
                String subTitle = rs.getString("subTitle");
                float orignalPrice = rs.getFloat("orignalPrice");
                float promotePrice = rs.getFloat("promotePrice");
                int stock = rs.getInt("stock");
                Date createDate = DateUtil.timestampToDate( rs.getTimestamp("createDate"));

                product.setName(name);
                product.setSubTitle(subTitle);
                product.setOrignalPrice(orignalPrice);
                product.setPromotePrice(promotePrice);
                product.setStock(stock);
                product.setCreateDate(createDate);
                product.setId(id);
                product.setCategory(category);
                setFirstProductImage(product);
                productList.add(product);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return productList;
    }

    public List<Product> list() {
        return list(0,Short.MAX_VALUE);
    }
    public List<Product> list(int start, int count) {
        List<Product> productList = new ArrayList<Product>();

        String sql = "select * from Product limit ?,? ";

        try (Connection c = DBUtil.getConnection(); PreparedStatement ps = c.prepareStatement(sql);) {

            ps.setInt(1, start);
            ps.setInt(2, count);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Product product = new Product();
                int id = rs.getInt(1);
                int cid = rs.getInt("cid");
                String name = rs.getString("name");
                String subTitle = rs.getString("subTitle");
                float orignalPrice = rs.getFloat("orignalPrice");
                float promotePrice = rs.getFloat("promotePrice");
                int stock = rs.getInt("stock");
                Date createDate = DateUtil.timestampToDate( rs.getTimestamp("createDate"));

                product.setName(name);
                product.setSubTitle(subTitle);
                product.setOrignalPrice(orignalPrice);
                product.setPromotePrice(promotePrice);
                product.setStock(stock);
                product.setCreateDate(createDate);
                product.setId(id);

                Category category = new CategoryDAO().get(cid);
                product.setCategory(category);
                productList.add(product);
            }
        } catch (SQLException e) {

            e.printStackTrace();
        }
        return productList;
    }

    public void fill(List<Category> categoryList) {
        for (Category category : categoryList) {
            fill(category);
        }
    }

    private void fill(Category category) {
        List<Product> productList = this.list(category.getId());
        category.setProducts(productList);
    }

    public void fillByRow(List<Category> categoryList) {
        int productNumberEachRow=0;
        for (Category category : categoryList) {
            List<Product> productList = category.getProducts();
            List<List<Product>> productsByRow = new ArrayList<>();
            for(int i=0;i<productList.size();i+=productNumberEachRow) {
                int size=i+productNumberEachRow;
                size=size>productList.size()?productList.size():size;
                List<Product> productListOfEachRow = productList.subList(i, size);
                productsByRow.add(productListOfEachRow);
            }
            category.setProductsByRow(productsByRow);
        }
    }

    public void setFirstProductImage(Product product) {
        List<ProductImage> productImageList = new ProductImageDAO().list(product, ProductImageDAO.type_single);
        if (!productImageList.isEmpty()) {
            product.setFirstProductImage(productImageList.get(0));
        }
    }

    public void setSaleAndReviewNumber(Product product) {
        int saleCount = new OrderItemDAO().getSaleCount(product.getId());
        product.setSaleCount(saleCount);

        int reviewCount = new ReviewDAO().getTotal(product.getId());
        product.setReviewCount(reviewCount);
    }

    public void setSaleAndReviewNumber(List<Product> productList) {
        for (Product product : productList) {
            setSaleAndReviewNumber(product);
        }
    }

    public List<Product> search(String keyword, int start, int count) {
        List<Product> productList = new ArrayList<>();
        String sql = "select * from product where name like ? limit ?,?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, "%" + keyword.trim() + "%");
            ps.setInt(2, start);
            ps.setInt(3, count);

            ResultSet rs=ps.executeQuery();
            while (rs.next()) {
                Product product=new Product();
                int id = rs.getInt(1);
                int cid = rs.getInt("cid");
                String name = rs.getString("name");
                String subTitle = rs.getString("subTitle");
                float orignalPrice = rs.getFloat("orignalPrice");
                float promotePrice = rs.getFloat("promotePrice");
                int stock = rs.getInt("stock");
                Date createDate = DateUtil.timestampToDate( rs.getTimestamp("createDate"));

                product.setName(name);
                product.setSubTitle(subTitle);
                product.setOrignalPrice(orignalPrice);
                product.setPromotePrice(promotePrice);
                product.setStock(stock);
                product.setCreateDate(createDate);
                product.setId(id);

                Category category = new CategoryDAO().get(cid);
                product.setCategory(category);
                setFirstProductImage(product);
                productList.add(product);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return productList;
    }

}
