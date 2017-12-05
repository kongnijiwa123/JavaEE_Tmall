package tmall.servlet;

import org.springframework.web.util.HtmlUtils;
import tmall.bean.*;
import tmall.dao.CategoryDAO;
import tmall.dao.OrderDAO;
import tmall.dao.ProductImageDAO;
import tmall.dao.UserDAO;
import tmall.util.Page;
import tmall.util.ProductComparator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.DriverManager;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
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

    public String loginAjax(HttpServletRequest request, HttpServletResponse response, Page page) {
        String name = request.getParameter("name");
        String password = request.getParameter("password");
        User user = userDAO.get(name, password);

        if (null == user) {
            return "%fail";
        } else {
            request.getSession().setAttribute("user", user);
            return "%success";
        }
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
        String keyword = request.getParameter("keyword").trim();
        List<Product> ps = null;
        //如果关键字为空，直接查询前100条product,不使用模糊查询以加快速度
        if (0 == keyword.length()) {
            ps = productDAO.list(0, 100);
        } else {
            ps = productDAO.search(keyword, 0, 100);
        }
        productDAO.setSaleAndReviewNumber(ps);
        request.setAttribute("ps", ps);
        return "searchResult.jsp";
    }

    public String buyone(HttpServletRequest request, HttpServletResponse response, Page page) {
        int pid = Integer.parseInt(request.getParameter("pid"));
        int num = Integer.parseInt(request.getParameter("num"));

        Product p = productDAO.get(pid);
        int oiId = 0;

        User user = (User) request.getSession().getAttribute("user");
        if (null == user) {
            return "login.jsp";
        }

        //从购物车OrderItem中查找是否有此商品，如果有就进行数量追加
        boolean found = false;
        List<OrderItem> ois = orderItemDAO.listByUser(user.getId());
        for (OrderItem orderItem : ois) {
            if (orderItem.getProduct().getId() == pid) {
                orderItem.setNumber(orderItem.getNumber() + num);
                orderItemDAO.update(orderItem);
                found = true;
                oiId = orderItem.getId();
                break;
            }
        }
        //如果不存在对应的OrderItem,那么就新增一个订单项OrderItem
        if (!found) {
            OrderItem orderItem = new OrderItem();
            orderItem.setUser(user);
            orderItem.setNumber(num);
            orderItem.setProduct(p);

            orderItemDAO.add(orderItem);
            oiId = orderItem.getId();
        }

        return "@forebuy?oiid=" + oiId;
    }

    public String buy(HttpServletRequest request, HttpServletResponse response, Page page) {
        if (null == request.getSession().getAttribute("user")) {
            return "login.jsp";
        }
        String[] oiIds = request.getParameterValues("oiid");
        List<OrderItem> ois = new ArrayList<>();
        float total = 0;
        for (String strId : oiIds) {
            int oiId = Integer.parseInt(strId);
            OrderItem oi = orderItemDAO.get(oiId);
            //计算价格
            total += oi.getProduct().getPromotePrice() * oi.getNumber();
            ois.add(oi);
        }

        request.setAttribute("ois", ois);
        request.setAttribute("total", total);
        return "buy.jsp";
    }

    public String addCart(HttpServletRequest request, HttpServletResponse response, Page page) {
        int pid = Integer.parseInt(request.getParameter("pid"));
        Product p = productDAO.get(pid);
        int num = Integer.parseInt(request.getParameter("num"));

        User user = (User) request.getSession().getAttribute("user");
        if (null == user) {
            return "login.jsp";
        }

        boolean found = false;

        List<OrderItem> ois = orderItemDAO.listByUser(user.getId());
        for (OrderItem orderItem : ois) {
            if (orderItem.getProduct().getId() == p.getId()) {
                orderItem.setNumber(orderItem.getNumber() + num);
                orderItemDAO.update(orderItem);
                found = true;
                break;
            }
        }

        if (!found) {
            OrderItem orderItem = new OrderItem();
            orderItem.setUser(user);
            orderItem.setNumber(num);
            orderItem.setProduct(p);
            orderItemDAO.add(orderItem);
        }
        return "%success";
    }

    public String cart(HttpServletRequest request, HttpServletResponse response, Page page) {
        User user = (User) request.getSession().getAttribute("user");
        if (null == user) {
            return "login.jsp";
        }
        List<OrderItem> ois = orderItemDAO.listByUser(user.getId());
        request.setAttribute("ois", ois);
        return "cart.jsp";
    }

    public String changeOrderItem(HttpServletRequest request, HttpServletResponse response, Page page) {
        User user = (User) request.getSession().getAttribute("user");
        if (null == user) {
            return "%fail";
        }

        int pid = Integer.parseInt(request.getParameter("pid"));
        int number = Integer.parseInt(request.getParameter("number"));
        List<OrderItem> ois = orderItemDAO.listByUser(user.getId());
        for (OrderItem orderItem : ois) {
            if (orderItem.getProduct().getId() == pid) {
                orderItem.setNumber(number);
                orderItemDAO.update(orderItem);
                break;
            }
        }
        return "%success";
    }

    public String deleteOrderItem(HttpServletRequest request, HttpServletResponse response, Page page) {
        User user = (User) request.getSession().getAttribute("user");
        if (null == user) {
            return "%fail";
        }
        int oiid = Integer.parseInt(request.getParameter("oiid"));
        orderItemDAO.delete(oiid);
        return "%success";
    }

    public String createOrder(HttpServletRequest request, HttpServletResponse response, Page page) {
        User user = (User) request.getSession().getAttribute("user");
        if (null == user) {
            return "@login.jsp";
        }

        String address = request.getParameter("address");
        String post = request.getParameter("post");
        String receiver = request.getParameter("receiver");
        String mobile = request.getParameter("mobile");
        String userMessage = request.getParameter("userMessage");
        String[] oiIdStrs = request.getParameterValues("oiid");


        Date createDate = new Date();
        //生成流水号为时间加随机4位数字
        String orderCode = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(createDate) + (int) ((Math.random() * 9 + 1) * 1000);

        Order order = new Order();

        order.setOrderCode(orderCode);
        order.setAddress(address);
        order.setPost(post);
        order.setReceiver(receiver);
        order.setMobile(mobile);
        order.setUserMessage(userMessage);
        order.setCreateDate(createDate);
        order.setUser(user);
        order.setStatus(OrderDAO.waitPay);

        orderDAO.add(order);

        //float total=0;
        //找出购物车中的订单项
        List<OrderItem> ois = orderItemDAO.listByUser(user.getId());
        for (OrderItem oi : ois) {
            for (String oiIdStr : oiIdStrs) {
                int oiId = Integer.parseInt(oiIdStr);
                if (oiId == oi.getId()) {
                    oi.setOrder(order);
                    orderItemDAO.update(oi);
                    //total += oi.getProduct().getPromotePrice() * oi.getNumber();
                    break;
                }
            }
        }


        return "@forealipay?oid=" + order.getId();
    }

    public String alipay(HttpServletRequest request, HttpServletResponse response, Page page) {
        User user = (User) request.getSession().getAttribute("user");
        int oid = Integer.parseInt(request.getParameter("oid"));

        Order order = orderDAO.get(oid);
        if (null == user || !(order.getUser().getId() == user.getId())) {
            return "login.jsp";
        }

        float total = orderDAO.getPriceTotal(order.getId());
        request.setAttribute("total", total);
        return "alipay.jsp";
    }

    public String payed(HttpServletRequest request, HttpServletResponse response, Page page) {
        int oid = Integer.parseInt(request.getParameter("oid"));
        Order order = orderDAO.get(oid);
        order.setStatus(OrderDAO.waitDelivery);
        order.setPayDate(new Date());
        new OrderDAO().update(order);
        request.setAttribute("o", order);
        request.setAttribute("total", orderDAO.getPriceTotal(oid));
        return "payed.jsp";
    }

    public String bought(HttpServletRequest request, HttpServletResponse response, Page page) {
        User user = (User) request.getSession().getAttribute("user");
        List<Order> os = orderDAO.list(user.getId(), OrderDAO.delete);

        orderItemDAO.fill(os);

        request.setAttribute("os", os);

        return "bought.jsp";
    }

    public String confirmPay(HttpServletRequest request, HttpServletResponse response, Page page) {
        int oid = Integer.parseInt(request.getParameter("oid"));
        Order o = orderDAO.get(oid);
        orderItemDAO.fill(o);
        request.setAttribute("o", o);
        return "confirmPay.jsp";
    }

    public String orderConfirmed(HttpServletRequest request, HttpServletResponse response, Page page) {
        int oid = Integer.parseInt(request.getParameter("oid"));
        User user = (User) request.getSession().getAttribute("user");
        Order order = orderDAO.get(oid);
        if (null == user || order.getUser().getId() != user.getId()) {
            return "login.jsp";
        }
        order.setStatus(OrderDAO.waitReview);
        order.setConfirmDate(new Date());
        orderDAO.update(order);
        return "orderConfirmed.jsp";
    }

    public String deleteOrder(HttpServletRequest request, HttpServletResponse response, Page page) {
        int oid = Integer.parseInt(request.getParameter("oid"));
        User user = (User) request.getSession().getAttribute("user");
        Order order = orderDAO.get(oid);
        if (null == user || order.getUser().getId() != user.getId()) {
            return "%fail";
        }
        order.setStatus(OrderDAO.delete);
        orderDAO.update(order);
        return "%success";
    }

    public String review(HttpServletRequest request, HttpServletResponse response, Page page) {
        int oid = Integer.parseInt(request.getParameter("oid"));
        User user = (User) request.getSession().getAttribute("user");
        Order order = orderDAO.get(oid);
        if (null == user || order.getUser().getId() != user.getId()) {
            return "login.jsp";
        }
        orderItemDAO.fill(order);
        //默认评价此订单的第一个订单项产品
        Product product = order.getOrderItems().get(0).getProduct();
        List<Review> reviews = reviewDAO.list(product.getId());
        productDAO.setSaleAndReviewNumber(product);
        request.setAttribute("p", product);
        request.setAttribute("o", order);
        request.setAttribute("reviews", reviews);
        return "review.jsp";
    }

    public String doReview(HttpServletRequest request, HttpServletResponse response, Page page) {
        int oid = Integer.parseInt(request.getParameter("oid"));
        User user = (User) request.getSession().getAttribute("user");
        Order order = orderDAO.get(oid);
        if (null == user || order.getUser().getId() != user.getId()) {
            return "login.jsp";
        }
        order.setStatus(OrderDAO.finish);
        orderDAO.update(order);

        int pid = Integer.parseInt(request.getParameter("pid"));
        Product product = productDAO.get(pid);

        String content = request.getParameter("content");

        content = HtmlUtils.htmlEscape(content);

        Review review = new Review();
        review.setContent(content);
        review.setProduct(product);
        review.setCreateDate(new Date());
        review.setUser(user);
        reviewDAO.add(review);

        return "@forereview?oid=" + oid + "&showOnly=true";
    }
}
