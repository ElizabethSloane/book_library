package org.example;

import jakarta.servlet.MultipartConfigElement;
import jakarta.servlet.ServletRegistration;
import org.apache.log4j.Logger;
import org.example.app.config.AppContextConfig;
import org.example.web.config.WebContextConfig;
import org.h2.server.web.JakartaWebServlet;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

public class WebAppInitializer implements WebApplicationInitializer {

    Logger logger = Logger.getLogger(WebAppInitializer.class);

    @Override
    public void onStartup(jakarta.servlet.ServletContext servletContext) {
        setAppContext(servletContext);
        setDispatcherServlet(servletContext);
        setDBServlet(servletContext);
    }

    public void setAppContext(jakarta.servlet.ServletContext servletContext) {
        logger.info("loading app config");

        //XmlWebApplicationContext appContext = new XmlWebApplicationContext();
        //appContext.setConfigLocation("classpath:app-config.xml");

        AnnotationConfigWebApplicationContext appContext = new AnnotationConfigWebApplicationContext();
        appContext.register(AppContextConfig.class);
        servletContext.addListener(new ContextLoaderListener(appContext));
    }

    public void setDispatcherServlet(jakarta.servlet.ServletContext servletContext) {
        logger.info("loading web config");

        AnnotationConfigWebApplicationContext webContext = new AnnotationConfigWebApplicationContext();
        webContext.register(WebContextConfig.class);
        DispatcherServlet dispatcherServlet = new DispatcherServlet(webContext);

        ServletRegistration.Dynamic dispatcher = servletContext.addServlet("dispatcher", dispatcherServlet);
        dispatcher.setLoadOnStartup(1);
        dispatcher.addMapping("/");

        String folderPath = "C:\\Users\\Yarolika\\Desktop\\apache-tomcat-10.1.9\\external_uploads";
        int MAX_UPLOAD_SIZE = 5 * 1024 * 1024;
        MultipartConfigElement multipartConfigElement = new MultipartConfigElement(folderPath,
                MAX_UPLOAD_SIZE, MAX_UPLOAD_SIZE * 2, MAX_UPLOAD_SIZE / 2);
        dispatcher.setMultipartConfig(multipartConfigElement);

        logger.info("dispatcher ready");
    }

    public void setDBServlet(jakarta.servlet.ServletContext servletContext) {
        jakarta.servlet.ServletRegistration.Dynamic servlet = servletContext.addServlet("h2-console", new JakartaWebServlet());
        servlet.setLoadOnStartup(2);
        servlet.addMapping("/console/*");
    }
}
