package com.libraryseat;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
//TODO:改掉controller里的setContentType
public class GenericFilter implements Filter {
    private boolean isAjax(HttpServletRequest request){
        String requestedWith = request.getHeader("X-Requested-With");
        return "POST".equalsIgnoreCase(request.getMethod()) &&
                "XMLHttpRequest".equalsIgnoreCase(requestedWith);
    }
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        //如果是ajax
        if (isAjax((HttpServletRequest) request))
            response.setContentType("application/json");
        chain.doFilter(request, response);
    }
}
