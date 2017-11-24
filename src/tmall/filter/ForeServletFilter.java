package tmall.filter;

import tmall.bean.Category;
import tmall.bean.OrderItem;
import tmall.bean.User;
import tmall.dao.CategoryDAO;
import tmall.dao.OrderItemDAO;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public class ForeServletFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        String contextPath = request.getServletContext().getContextPath();
        String uri = request.getRequestURI().replaceFirst(contextPath, "");

        if (!uri.startsWith("/admin_")) {

            User user = (User) request.getSession().getAttribute("user");
            int cartTotalItemNumber = 0;
            if (null != user) {
                List<OrderItem> ois = new OrderItemDAO().listByUser(user.getId());
                for (OrderItem orderItem : ois) {
                    cartTotalItemNumber += orderItem.getNumber();
                }
            }
            request.setAttribute("cartTotalItemNumber", cartTotalItemNumber);

            //cs将用于每个页面搜索框下的推荐，4个就够了
            List<Category> cs = (List<Category>) request.getAttribute("cs");
            if (null == cs) {
                cs = new CategoryDAO().list(0, 4);
                request.setAttribute("cs", cs);
            }
        }
        if (uri.startsWith("/fore") && !uri.startsWith("/foreServlet")) {

            //request.getServletContext().setAttribute("contextPath",contextPath);
            String method = uri.replaceFirst("/fore", "");
            request.setAttribute("method", method);
            request.getRequestDispatcher("/foreServlet").forward(request, response);
            return;
        }

        filterChain.doFilter(request, response);
    }

    @Override
    public void destroy() {

    }
}
