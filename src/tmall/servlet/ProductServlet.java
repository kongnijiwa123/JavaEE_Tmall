package tmall.servlet;

import tmall.bean.*;
import tmall.dao.ProductImageDAO;
import tmall.util.ForeignKey;
import tmall.util.Page;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class ProductServlet extends BaseBackServlet {

    @Override
    public String add(HttpServletRequest request, HttpServletResponse response, Page page) {
        int cid = Integer.parseInt(request.getParameter("cid"));
        Category category = categoryDAO.get(cid);

        String name = request.getParameter("name");
        String subTitle = request.getParameter("subTitle");
        float orignalPrice = Float.parseFloat(request.getParameter("orignalPrice"));
        float promotePrice = Float.parseFloat(request.getParameter("promotePrice"));
        int stock = Integer.parseInt(request.getParameter("stock"));

        Product product=new Product();

        product.setCategory(category);
        product.setName(name);
        product.setSubTitle(subTitle);
        product.setOrignalPrice(orignalPrice);
        product.setPromotePrice(promotePrice);
        product.setStock(stock);
        //createDate没有用到

        productDAO.add(product);
        return "@admin_product_list?cid="+cid;
    }

    @Override
    public String delete(HttpServletRequest request, HttpServletResponse response, Page page) {
        int id = Integer.parseInt(request.getParameter("id"));
        Product product = productDAO.get(id);

        /*
        //因为存在外间约束，所以根据product,找出所有对应的图片删除
        List<ProductImage> pisSingle = productImageDAO.list(product, ProductImageDAO.type_single);
        List<ProductImage> pisDetail=productImageDAO.list(product, ProductImageDAO.type_detail);
        //删除数据库中图片的信息
        for (ProductImage piSingle : pisSingle) {
            productImageDAO.delete(piSingle.getId());
        }
        for (ProductImage piDetail : pisDetail) {
            productImageDAO.delete(piDetail.getId());
        }
        //删除对应的图片文件
        String imageFolder_detail = request.getSession().getServletContext().getRealPath("img/Detail");
        String imageFolder_single = request.getSession().getServletContext().getRealPath("img/productSingle");
        String imageFolder_small = request.getSession().getServletContext().getRealPath("img/productSingle_small");
        String imageFolder_middle = request.getSession().getServletContext().getRealPath("img/productSingle_middle");


        File imageFile_detail = new File(imageFolder_detail, id + ".jpg");
        File imageFile_single = new File(imageFolder_single, id + ".jpg");
        File imageFile_small = new File(imageFolder_small, id + ".jpg");
        File imageFile_middle = new File(imageFolder_middle, id + ".jpg");

        imageFile_detail.delete();
        imageFile_single.delete();
        imageFile_small.delete();
        imageFile_middle.delete();
        */

        Map<String,List> foreignKey = ForeignKey.mapForeignKeys(product);

        List<ProductImage> pis = foreignKey.get("pis");
        List<PropertyValue> ptvs = foreignKey.get("ptvs");
        List<OrderItem> ois = foreignKey.get("ois");
        List<Review> rvs = foreignKey.get("rvs");

        //如果没有关联的外键就可以删除产品
        if (0 == pis.size() && 0 == ois.size() && 0 == rvs.size()) {
            //因为属性值是自动初始化的，且始终存在即使值为null，所以删除产品时应自动关联删除其属性值
            if (0 < ptvs.size()) {
                for (PropertyValue ptv : ptvs) {
                    propertyValueDAO.delete(ptv.getId());//删除对应的属性值
                }
            }
            productDAO.delete(id);
            return "@admin_product_list?cid="+product.getCategory().getId();
        }

        //外键信息

        String fkInfo = "产品(name)：" + product.getName() + "无法删除，原因：外键约束\n\n";
        if(0<pis.size()){
            String piInfo="产品图片(id)：";
            for (ProductImage pi : pis) {
                piInfo+=(pi.getId()+" ");
            }
            fkInfo += piInfo + "\n";
        }

        if (0 < ois.size()) {
            String oiInfo="订单项(id)：";
            for (OrderItem oi : ois) {
                oiInfo = oi.getId() + " ";
            }
            fkInfo += oiInfo + "\n";
        }
        if (0 < rvs.size()) {
            String rvInfo="产品评论(id)：";
            for (Review rv : rvs) {
                rvInfo = rv.getId()+" ";
            }
            fkInfo += rvInfo + "\n";
        }

        return "%"+fkInfo;
    }

    @Override
    public String edit(HttpServletRequest request, HttpServletResponse response, Page page) {
        int id = Integer.parseInt(request.getParameter("id"));
        Product product = productDAO.get(id);
        request.setAttribute("p", product);

        return "admin/editProduct.jsp";
    }

    public String editPropertyValue(HttpServletRequest request, HttpServletResponse response, Page page){
        int id = Integer.parseInt(request.getParameter("id"));
        Product product = productDAO.get(id);
        request.setAttribute("p", product);

        //List<Property> pts = propertyDAO.list(product.getCategory().getId());
        propertyValueDAO.init(product);

        List<PropertyValue> ptvs = propertyValueDAO.list(product.getId());

        request.setAttribute("ptvs", ptvs);

        return "admin/editProductValue.jsp";
    }

    public String updatePropertyValue(HttpServletRequest request, HttpServletResponse response,Page page) {
        int ptvid = Integer.parseInt(request.getParameter("ptvid"));
        String value = request.getParameter("value");

        PropertyValue ptv = propertyValueDAO.get(ptvid);
        ptv.setValue(value);
        propertyValueDAO.update(ptv);
        return "%success";
    }


    @Override
    public String update(HttpServletRequest request, HttpServletResponse response, Page page) {
        int cid = Integer.parseInt(request.getParameter("cid"));
        Category category = categoryDAO.get(cid);

        int id = Integer.parseInt(request.getParameter("id"));
        int stock = Integer.parseInt(request.getParameter("stock"));
        float orignalPrice = Float.parseFloat(request.getParameter("orignalPrice"));
        float promotePrice = Float.parseFloat(request.getParameter("promotePrice"));
        String subTitle = request.getParameter("subTitle");
        String name = request.getParameter("name");

        Product product=new Product();

        product.setCategory(category);
        product.setId(id);
        product.setName(name);
        product.setSubTitle(subTitle);
        product.setOrignalPrice(orignalPrice);
        product.setPromotePrice(promotePrice);
        product.setStock(stock);

        productDAO.update(product);
        return "@admin_product_list?cid=" + cid;
    }

    @Override
    public String list(HttpServletRequest request, HttpServletResponse response, Page page) {
        int cid = Integer.parseInt(request.getParameter("cid"));
        Category category = categoryDAO.get(cid);

        List<Product> ps = productDAO.list(cid, page.getStart(), page.getCount());

        int total = productDAO.getTotal(cid);
        page.setTotal(total);
        page.setParam("&cid=" + cid);

        request.setAttribute("c", category);
        request.setAttribute("ps", ps);
        request.setAttribute("page", page);

        return "admin/listProduct.jsp";
    }
}
