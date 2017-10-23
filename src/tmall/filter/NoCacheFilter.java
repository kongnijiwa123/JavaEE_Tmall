package tmall.filter;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

//@WebFilter(filterName = "NoCacheFilter")
public class NoCacheFilter implements Filter {
    @Override
    public void destroy() {
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws ServletException, IOException {
        HttpServletRequest request=(HttpServletRequest)req;
        HttpServletResponse response=(HttpServletResponse)resp;

        /*response.setHeader("Cache-Control", "no-cache");
        response.setHeader("Cache-Control", "no-store");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);*/

        String uri=request.getRequestURI();
        /*//如果发现是css，js或jpg图片文件，直接放行
        if(!(uri.contains(".css")||uri.contains(".js")||uri.contains(".jpg") ||uri.contains(".png"))) {*/

        String contextPath = request.getServletContext().getContextPath();

        uri = uri.replace(contextPath, "");

        //排除css img js
        if (!(uri.startsWith("/css")||uri.startsWith("/js")||uri.startsWith("/img"))) {

            response.setDateHeader("Expires", -1);
            response.setHeader("Cache_Control", "no-cache");
            response.setHeader("Pragma", "no-cache");
        }

        chain.doFilter(req,response);
    }

    @Override
    public void init(FilterConfig config) throws ServletException {

    }

}
