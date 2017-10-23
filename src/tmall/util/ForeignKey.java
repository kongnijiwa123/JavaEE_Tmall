package tmall.util;

import tmall.bean.*;
import tmall.dao.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ForeignKey {

    private static ProductDAO productDAO=new ProductDAO();
    private static ProductImageDAO productImageDAO=new ProductImageDAO();
    private static PropertyValueDAO propertyValueDAO=new PropertyValueDAO();
    private static OrderItemDAO orderItemDAO = new OrderItemDAO();
    private static ReviewDAO reviewDAO=new ReviewDAO();

    //因为删除操作会影响其他表，所以删除之前找出关联的其他表，
    // product表有productImage，propertyValue，orderItem,review的外键
    public static Map<String, List> mapForeignKeys(Product product) {

        Map<String, List> resultMap = new HashMap<>();

        List<ProductImage> pis = null;
        List<PropertyValue> ptvs = null;
        List<OrderItem> ois = null;
        List<Review> rvs = null;

        pis = productImageDAO.list(product.getId());
        ptvs = propertyValueDAO.list(product.getId());
        ois = orderItemDAO.listByProduct(product.getId());
        rvs = reviewDAO.list(product.getId());

        resultMap.put("pis", pis);
        resultMap.put("ptvs", ptvs);
        resultMap.put("ois", ois);
        resultMap.put("rvs", rvs);

        return resultMap;
    }

    public static void main(String[] args) {
        Map map = mapForeignKeys(productDAO.get(12));
        for (Object o : map.values()) {
            System.out.println(o);
        }
    }
}
