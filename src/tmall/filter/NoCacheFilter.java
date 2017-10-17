package tmall.filter;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

//@WebFilter(filterName = "NoCacheFilter")
public class NoCacheFilter implements Filter {
    public void destroy() {
    }

    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws ServletException, IOException {
        HttpServletResponse response=(HttpServletResponse)resp;

        /*response.setHeader("Cache-Control", "no-cache");
        response.setHeader("Cache-Control", "no-store");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);*/

        response.setDateHeader("Expires", -1);
        response.setHeader("Cache_Control", "no-cache");
        response.setHeader("Pragma", "no-cache");

        chain.doFilter(req,response);
    }

    public void init(FilterConfig config) throws ServletException {

    }

}
