package tmall.servlet;

import tmall.bean.Product;
import tmall.bean.ProductImage;
import tmall.dao.ProductImageDAO;
import tmall.util.ImageUtil;
import tmall.util.Page;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductImageServlet extends BaseBackServlet {

    @Override
    public String add(HttpServletRequest request, HttpServletResponse response, Page page) {
        //上传文件的输入流
        InputStream is = null;
        //提交文件时的其他参数
        Map<String, String> params = new HashMap<>();

        //解析上传
        is = parseUpload(request, params);

        //根据上传的参数生成product对象
        int pid = Integer.parseInt(params.get("pid"));
        Product p = productDAO.get(pid);

        //生成productImage对象
        String type = params.get("type");
        ProductImage pi = new ProductImage();
        pi.setTyep(type);
        pi.setProduct(p);

        productImageDAO.add(pi);

        //生成文件(文件名根据数据库生成的图片id+.jpg)
        String fileName = pi.getId() + ".jpg";
        String imageFolder = null;
        String imageFolder_small = null;
        String imageFolder_middle = null;
        if (ProductImageDAO.type_single.equals(type)) {
            imageFolder = request.getSession().getServletContext().getRealPath("img/productSingle");
            imageFolder_small = request.getSession().getServletContext().getRealPath("img/productSingle_small");
            imageFolder_middle = request.getSession().getServletContext().getRealPath("img/productSingle_middle");
        } else {
            imageFolder = request.getSession().getServletContext().getRealPath("img/productDetail");
        }
        File file = new File(imageFolder, fileName);
        file.getParentFile().mkdirs();

        //复制文件
        try {
            if (null != is && 0 != is.available()) {

                FileOutputStream fos = new FileOutputStream(file);

                byte[] b=new byte[1024*1024];
                int length=0;
                while (-1 != (length = is.read(b))) {
                    fos.write(b,0,length);
                }

                fos.flush();
                fos.close();
                //把文件保存为jpg格式
                BufferedImage img = ImageUtil.changeToJpg(file);
                ImageIO.write(img, "jpg", file);

                if (ProductImageDAO.type_single.equals(type)) {
                    //另存缩略图
                    File file_small = new File(imageFolder_small, fileName);
                    File file_middle = new File(imageFolder_middle, fileName);

                    ImageUtil.resizeImage(file, 56, 56, file_small);
                    ImageUtil.resizeImage(file, 217, 190, file_middle);
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "@admin_productImage_list?pid="+pid;
    }

    @Override
    public String delete(HttpServletRequest request, HttpServletResponse response, Page page) {
        int id = Integer.parseInt(request.getParameter("id"));
        ProductImage pi = productImageDAO.get(id);
        productImageDAO.delete(id);

        //如果是单图片类型（single类型）则找出另存的缩略图，一并删除
        if (ProductImageDAO.type_single.equals(pi.getTyep())) {
            String imageFolder_single = request.getSession().getServletContext().getRealPath("img/productSingle");
            String imageFolder_small = request.getSession().getServletContext().getRealPath("img/productSingle_small");
            String imageFolder_middle = request.getSession().getServletContext().getRealPath("img/productSingle_middle");

            File file_single = new File(imageFolder_single, id + ".jpg");
            file_single.delete();
            File file_small = new File(imageFolder_small, id + ".jpg");
            file_small.delete();
            File file_middle = new File(imageFolder_middle, id + ".jpg");
            file_middle.delete();
        } else {//如果是详情图片detail 不存在缩略图只删除本身就可以了
            String imageFolder_detail = request.getSession().getServletContext().getRealPath("img/productDetail");
            File file_detail = new File(imageFolder_detail, id + ".jpg");
            file_detail.delete();
        }
        return "@admin_productImage_list?pid="+pi.getProduct().getId();
    }

    @Override
    public String edit(HttpServletRequest request, HttpServletResponse response, Page page) {
        return null;
    }

    @Override
    public String update(HttpServletRequest request, HttpServletResponse response, Page page) {
        return null;
    }

    @Override
    public String list(HttpServletRequest request, HttpServletResponse response, Page page) {
        int pid = Integer.parseInt(request.getParameter("pid"));
        Product product = productDAO.get(pid);
        List<ProductImage> pisSingle = productImageDAO.list(product, ProductImageDAO.type_single);
        List<ProductImage> pisDetail = productImageDAO.list(product, ProductImageDAO.type_detail);

        request.setAttribute("p", product);
        request.setAttribute("pisSingle", pisSingle);
        request.setAttribute("pisDetail", pisDetail);

        return "admin/listProductImage.jsp";
    }
}
