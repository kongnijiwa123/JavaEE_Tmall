package tmall.filter;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class EncodingFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        /*String uri=request.getRequestURI();
        //如果发现是css，js或jpg图片文件，直接放行
        if(!(uri.contains(".css")||uri.contains(".js")||uri.contains(".jpg")|| uri.contains(".png"))) {
        */

        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        //}

        filterChain.doFilter(request, response);
    }

    @Override
    public void destroy() {

    }
}
