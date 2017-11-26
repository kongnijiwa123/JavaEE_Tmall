package tmall.util;

import tmall.bean.Product;

import java.util.Comparator;

public class ProductComparator {
    //all排序，根据评论数+销量，从高到低排序
    public final static Comparator<Product> all;
    //date排序，根据上新时间排序
    public final static Comparator<Product> date;
    //price排序，根据价格，从低到高排序
    public final static Comparator<Product> price;
    //review排序，根据评论数量，从高到低排序
    public final static Comparator<Product> review;
    //saleCount排序，根据销量，从高到低排序
    public final static Comparator<Product> saleCount;

    static {
        all = new Comparator<Product>() {
            @Override
            public int compare(Product p1, Product p2) {
                return (p2.getReviewCount() + p2.getSaleCount()) - (p1.getReviewCount() + p1.getSaleCount());
            }
        };

        date = new Comparator<Product>() {
            @Override
            public int compare(Product p1, Product p2) {
                return p1.getCreateDate().compareTo(p2.getCreateDate());
            }
        };

        price = new Comparator<Product>() {
            @Override
            public int compare(Product p1, Product p2) {
                return (int) (p1.getPromotePrice() - p2.getPromotePrice());
            }
        };

        review = new Comparator<Product>() {
            @Override
            public int compare(Product p1, Product p2) {
                return p2.getReviewCount() - p1.getReviewCount();
            }
        };

        saleCount = new Comparator<Product>() {
            @Override
            public int compare(Product p1, Product p2) {
                return p2.getSaleCount() - p1.getSaleCount();
            }
        };
    }
}
