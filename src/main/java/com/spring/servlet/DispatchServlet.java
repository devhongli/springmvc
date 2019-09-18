package com.spring.servlet;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

public class DispatchServlet extends HttpServlet {

    private Properties contextConfig = new Properties();

    private Map<String,Object> ioc = new ConcurrentHashMap<String, Object>();

    private List<String> beanNames = new ArrayList<String>();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doPost(req,resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("---------------- 调用doPost ---------------------");
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        //开始初始化进程

        //定位
        doLoadConfig();

        //加载
        doScanner();

        //注册
        doRegistry();

        //自动依赖注入
        doAutowired();

        //Mvc 会有HnandlerMapping


    }

    private void doAutowired() {
    }

    private void doRegistry() {
    }

    private void doScanner() {
    }

    private void doLoadConfig() {
    }
}
