package tmall.filter;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class BackServletFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response=(HttpServletResponse)servletResponse;

        String uri=request.getRequestURI();
       /* //如果发现是css，js或图片文件，直接放行
        if(!(uri.contains(".css")||uri.contains(".js")||uri.contains(".jpg")|| uri.contains(".png"))) {*/
            String contextPath = request.getServletContext().getContextPath();

            uri = uri.replaceFirst(contextPath, "");

            //除了admin目录其他都排除
            if (uri.startsWith("/admin_")) {
                String[] paths = uri.split("_");

                //这里需要uri设置合理，否则会数组越界
                String servletPath = paths[1] + "Servlet";
                String method = paths[2];

                request.setAttribute("method", method);

                request.getRequestDispatcher("/" + servletPath).forward(request, response);
                return;
            }
       // }
        filterChain.doFilter(request,response);
    }

    @Override
    public void destroy() {

    }
}
