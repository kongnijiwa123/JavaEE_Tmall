package tmall.bean;

public class ProductImage {
    private String tyep;
    private Product product;
    private int id;

    public String getTyep() {
        return tyep;
    }

    public void setTyep(String tyep) {
        this.tyep = tyep;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
