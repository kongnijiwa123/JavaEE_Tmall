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
import java.util.*;

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
                int id = rs.getInt("id");
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
        fill(categoryList, 0, Short.MAX_VALUE);
        /*for (Category category : categoryList) {
            fill(category);
        }*/
    }

    /**
     * 填充部分产品
     * @param categoryList
     * @param start
     * @param count
     */
    public void fill(List<Category> categoryList,int start,int count) {
        //String sql = "select distinct Product.* from Category,Product WHERE Product.cid=? limit ?,? ";
        String sql = "select p.*,max(productimage.id) from productimage" +
                ",(select distinct Product.* from Category,Product WHERE Product.cid=? limit ?,?)AS p" +
                " GROUP BY p.id,productimage.type,productimage.pid " +
                "HAVING productimage.type='type_single' and productimage.pid=p.id;";

        /*List<ProductImage> productImageList = new ProductImageDAO().list(product, ProductImageDAO.type_single,0,1);
        if (null != productImageList) {
            product.setFirstProductImage(productImageList.get(0));
        }*/
        try (Connection c = DBUtil.getConnection(); PreparedStatement ps = c.prepareStatement(sql);) {
            ps.setInt(2, start);
            ps.setInt(3, count);
            for (Category category : categoryList) {
                ps.setInt(1,category.getId());

                ResultSet rs = ps.executeQuery();

                List<Product> productList = new ArrayList<>();
                while (rs.next()) {
                    Product product = new Product();
                    int id = rs.getInt("id");
                    //int cid = rs.getInt("cid");
                    String name = rs.getString("name");
                    String subTitle = rs.getString("subTitle");
                    float orignalPrice = rs.getFloat("orignalPrice");
                    float promotePrice = rs.getFloat("promotePrice");
                    int stock = rs.getInt("stock");
                    Date createDate = DateUtil.timestampToDate( rs.getTimestamp("createDate"));
                    int productImageId=rs.getInt("max(productimage.id)");

                    product.setName(name);
                    product.setSubTitle(subTitle);
                    product.setOrignalPrice(orignalPrice);
                    product.setPromotePrice(promotePrice);
                    product.setStock(stock);
                    product.setCreateDate(createDate);
                    product.setId(id);
                    product.setCategory(category);
                    //setFirstProductImage(product);

                    ProductImage productImage=new ProductImage();
                    productImage.setId(productImageId);
                    productImage.setTyep(ProductImageDAO.type_single);
                    productImage.setProduct(product);
                    product.setFirstProductImage(productImage);
                    productList.add(product);
                }
                category.setProducts(productList);
            }
        } catch (SQLException e) {

            e.printStackTrace();
        }
    }

    private void fill(Category category) {
       /* List<Product> productList = this.list(category.getId());
        category.setProducts(productList);*/
       fill(category,0,Short.MAX_VALUE);
    }

    /**
     *将所有产品填充的代价太大，设定合适的范围填充
     * @param category
     * @param start
     * @param count
     */
    private void fill(Category category,int start,int count) {
        List<Product> productList = this.list(category.getId(),start,count);
        category.setProducts(productList);
    }

    /**
     * 填充每行每列的产品
     * @param categoryList 类别
     */
    public void fillByRow(List<Category> categoryList) {
        int productNumberEachRow=8;
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
        List<ProductImage> productImageList = new ProductImageDAO().list(product, ProductImageDAO.type_single,0,1);
        if (null != productImageList) {
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


   /* //因为删除操作会影响其他表，所以删除之前找出关联的其他表，
    // product表有productImage，propertyValue，orderItem,review的外键
    public Map<String,List<Integer>> mapForeignKeyIds(Product product) {

        Map<String, List<Integer>> resultMap = new HashMap<>();
        List<Integer> piIds = new ArrayList<>();
        List<Integer> ptvIds = new ArrayList<>();
        List<Integer> oiIds = new ArrayList<>();
        List<Integer> rvIds = new ArrayList<>();

        resultMap.put("piIds", piIds);
        resultMap.put("ptvIds", ptvIds);
        resultMap.put("oiIds", oiIds);
        resultMap.put("rvIds", rvIds);

        String sql_pi=" select id from productImage where pid="+product.getId();
        String sql_ptv=" select id from propertyValue where pid="+product.getId();
        String sql_oi=" select id from propertyValue where pid="+product.getId();
        String sql_rv=" select id from propertyValue where pid="+product.getId();


        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps_pi = conn.prepareStatement(sql_pi)) {

            ResultSet rs = ps_pi.executeQuery();
            while (rs.next()) {
                Integer piId = rs.getInt("id");
                piIds.add(piId);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps_ptv = conn.prepareStatement(sql_ptv)) {

            ResultSet rs=ps_ptv.executeQuery();
            while (rs.next()) {
                Integer ptvId = rs.getInt("id");
                ptvIds.add(ptvId);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return resultMap;
    }

    public static void main(String[] args) {
        ProductDAO productDAO=new ProductDAO();
        System.out.println(productDAO.mapForeignKeyIds(productDAO.get(12)));
    }
*/
}
