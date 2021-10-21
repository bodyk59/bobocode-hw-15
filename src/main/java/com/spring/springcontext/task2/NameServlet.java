package com.spring.springcontext.task2;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import javax.servlet.ServletConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/name")
public class NameServlet extends HttpServlet {
    private static final String SPRING_APP_CONTEXT = "SPRING_APP_CONTEXT";
    @Override
    public void init(ServletConfig config) {
        var servletContext = config.getServletContext();
        var springContext = new AnnotationConfigApplicationContext(NameProvider.class);
        servletContext.setAttribute(SPRING_APP_CONTEXT, springContext);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        var servletContext = req.getServletContext();
        var springContext = (ApplicationContext) servletContext.getAttribute(SPRING_APP_CONTEXT);
        var nameProviderBean = springContext.getBean(NameProvider.class);
        var name = nameProviderBean.getName();
        var writer = resp.getWriter();
        writer.println(name);
        writer.flush();
        writer.close();
    }
}
