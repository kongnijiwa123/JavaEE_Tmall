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

        String contextPath=request.getServletContext().getContextPath();
        String uri=request.getRequestURI();

        uri = uri.replace(contextPath, "");

        if (uri.startsWith("/admin_")) {
            String[] paths=uri.split("_");

            //这里需要uri设置合理，否则会数组越界
            String servletPath=paths[1]+"Servlet";
            String method = paths[2];

            request.setAttribute("method", method);

            request.getRequestDispatcher("/" + servletPath);
            return;
        }
        filterChain.doFilter(request,response);
    }

    @Override
    public void destroy() {

    }
}
