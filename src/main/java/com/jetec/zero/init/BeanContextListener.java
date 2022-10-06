package com.jetec.zero.init;

import com.jetec.zero.repository.StockRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * @author jetec
 */
@Component
public class BeanContextListener implements ServletContextListener {

    @Autowired
    StockRepository sr;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        System.out.println("bean context 初始化");
        System.out.println("上傳library");
        ServletContext app = sce.getServletContext();
        Sort sort = Sort.by(Sort.Direction.DESC, "libraryoption");
        app.setAttribute("stockName", sr.getStorkName());
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        System.out.println("bean context 销毁");
    }
}
