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

        //生成文件
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
                //把文件保存为jpg格式
                BufferedImage img = ImageUtil.changeToJpg(file);
                ImageIO.write(img, "jpg", file);

                if (ProductImageDAO.type_single.equals(type)) {
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
        return null;
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
        return null;
    }
}
