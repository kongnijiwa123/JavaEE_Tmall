package tmall.servlet;

import org.springframework.web.util.HtmlUtils;
import tmall.bean.Category;
import tmall.bean.User;
import tmall.util.Page;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

public class ForeServlet extends BaseForeServlet {
    public String home(HttpServletRequest request, HttpServletResponse response, Page page) {
        //填充所有分类
        List<Category> cs = categoryDAO.list();
        //将所有产品填充的代价过大，应该重构
        //首页分类菜单中，每个分类产品是8*8个
        productDAO.fill(cs,0,8*8);
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
            request.setAttribute("msg","用户名已经被使用，请更换");
            return "foreregister.jsp";
        }

        User user = new User();
        user.setName(name);
        user.setPassword(password);
        userDAO.add(user);

        return "@registerSuccess.jsp";
    }
}
