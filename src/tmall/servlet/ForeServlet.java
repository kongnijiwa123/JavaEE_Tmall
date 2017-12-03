package tmall.servlet;

import org.springframework.web.util.HtmlUtils;
import tmall.bean.*;
import tmall.dao.CategoryDAO;
import tmall.dao.ProductImageDAO;
import tmall.util.Page;
import tmall.util.ProductComparator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.DriverManager;
import java.util.Collections;
import java.util.List;

public class ForeServlet extends BaseForeServlet {
    public String home(HttpServletRequest request, HttpServletResponse response, Page page) {
        //填充所有分类
        List<Category> cs = categoryDAO.list();
        //将所有产品填充的代价过大，应该重构
        //首页分类菜单中，每个分类产品是8*8个
        productDAO.fill(cs, 0, 8 * 8);
        productDAO.fillByRow(cs);
        request.setAttribute("cs", cs);
        return "home.jsp";
    }

    public String register(HttpServletRequest request, HttpServletResponse response, Page page) {
        String name = request.getParameter("name");
        String password = request.getParameter("password");
        //对用户名进行转义，防止恶意注册
        name = HtmlUtils.htmlEscape(name);
        boolean exist = userDAO.isExist(name);

        if (exist) {
            request.setAttribute("msg", "用户名已经被使用，请更换");
            return "foreregister.jsp";
        }

        User user = new User();
        user.setName(name);
        user.setPassword(password);
        userDAO.add(user);

        return "@registerSuccess.jsp";
    }

    public String login(HttpServletRequest request, HttpServletResponse response, Page page) {
        String name = request.getParameter("name");
        name = HtmlUtils.htmlEscape(name);
        String password = request.getParameter("password");

        User user = userDAO.get(name, password);

        if (null == user) {
            request.setAttribute("msg", "账号密码错误");
            return "login.jsp";
        }
        request.getSession().setAttribute("user", user);
        return "@forehome";
    }

    public String logout(HttpServletRequest request, HttpServletResponse response, Page page) {
        request.getSession().removeAttribute("user");
        return "@forehome";
    }

    public String product(HttpServletRequest request, HttpServletResponse response, Page page) {
        int pid = Integer.parseInt(request.getParameter("pid"));
        Product p = productDAO.get(pid);

        List<ProductImage> productSingleImages = productImageDAO.list(p, ProductImageDAO.type_single);
        List<ProductImage> productDetailImages = productImageDAO.list(p, ProductImageDAO.type_detail);
        p.setProductSingleImages(productSingleImages);
        p.setProductDetailImages(productDetailImages);

        List<PropertyValue> pvs = propertyValueDAO.list(p.getId());
        List<Review> reviews = reviewDAO.list(p.getId());

        productDAO.setSaleAndReviewNumber(p);
        request.setAttribute("reviews", reviews);

        request.setAttribute("p", p);
        request.setAttribute("pvs", pvs);
        return "product.jsp";
    }

    public String checkLogin(HttpServletRequest request, HttpServletResponse response, Page page) {
        User user = (User) request.getSession().getAttribute("user");
        if (null != user) {
            return "%success";
        }
        return "%fail";
    }

    public String category(HttpServletRequest request, HttpServletResponse response, Page page) {
        int cid = Integer.parseInt(request.getParameter("cid"));

        Category c = new CategoryDAO().get(cid);

        productDAO.fill(c);
        productDAO.setSaleAndReviewNumber(c.getProducts());

        String sort = request.getParameter("sort");
        if (null != sort) {
            switch (sort) {
                case "review":
                    Collections.sort(c.getProducts(), ProductComparator.review);
                    break;
                case "date":
                    Collections.sort(c.getProducts(), ProductComparator.date);
                    break;
                case "saleCount":
                    Collections.sort(c.getProducts(), ProductComparator.saleCount);
                    break;
                case "price":
                    Collections.sort(c.getProducts(), ProductComparator.price);
                    break;
                case "all":
                    Collections.sort(c.getProducts(), ProductComparator.all);
                    break;
                default:
                    break;
            }
        }

        request.setAttribute("c", c);
        return "category.jsp";
    }

    public String search(HttpServletRequest request, HttpServletResponse response, Page page) {
        String keyword = request.getParameter("keyword");
        List<Product> ps = productDAO.search(keyword, 0, 100);
        productDAO.setSaleAndReviewNumber(ps);
        request.setAttribute("ps", ps);
        return "searchResult.jsp";
    }
}
