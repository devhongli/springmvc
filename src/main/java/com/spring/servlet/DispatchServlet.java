package com.spring.servlet;

import com.spring.action.DemoAction;
import com.spring.annotation.Autowried;
import com.spring.annotation.Controller;
import com.spring.annotation.Service;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

public class DispatchServlet extends HttpServlet {

    private Properties contextConfig = new Properties();

    private Map<String,Object> beanMap = new ConcurrentHashMap<String, Object>();

    private List<String> classNames = new ArrayList<String>();

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
        doLoadConfig(config.getInitParameter("contextConfigLocation"));

        //加载
        doScanner(contextConfig.getProperty("scanPackage"));

        //注册
        doRegistry();

        //自动依赖注入
        doAutowired();

        DemoAction action = (DemoAction)beanMap.get("demoAction");
        action.query(null,null,"Tom");

        //Mvc 会有HnandlerMapping
        initHnandlerMapping();

    }

    private void initHnandlerMapping() {
    }

    private void doAutowired() {
        if(beanMap.isEmpty()){return;}

        for (Map.Entry<String, Object> entry : beanMap.entrySet()) {
            Field [] fields = entry.getValue().getClass().getDeclaredFields();

            for(Field field : fields){
                if(!field.isAnnotationPresent(Autowried.class)){return;}

                Autowried autowried = field.getAnnotation(Autowried.class);
                String beanName = autowried.value().trim();

                if("".equals(beanName)){
                    beanName = field.getType().getName();
                }
                field.setAccessible(true);

                try {
                    field.set(entry.getValue(), beanMap.get(beanName));
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        }

    }

    private void doRegistry() {

        if (classNames.isEmpty()){return;}

        try {

            for (String className : classNames) {

                Class<?> clazz = Class.forName(className);

                if(clazz.isAnnotationPresent(Controller.class)){
                    String beanName = lowerFirstCase(clazz.getSimpleName());
                    beanMap.put(beanName, clazz.newInstance());
                }else if(clazz.isAnnotationPresent(Service.class)){
                    Service service = clazz.getAnnotation(Service.class);
                    String beanName = service.value();

                    if("".equals(beanName.trim())){
                        beanName = lowerFirstCase(clazz.getSimpleName());
                    }

                    Object instance = clazz.newInstance();

                    beanMap.put(beanName, instance);

                    Class<?>[] interfaces = clazz.getInterfaces();

                    for (Class<?> i : interfaces){
                        beanMap.put(beanName, instance);
                    }

                }else{
                    continue;
                }

            }

        } catch (Exception e){

        }
    }

    private void doScanner(String packageName) {

        URL url = this.getClass().getClassLoader().getResource("/" + packageName.replaceAll("\\.","/"));

        File classDir = new File(url.getFile());

        for (File file : classDir.listFiles()) {
            if(file.isDirectory()){
                doScanner(packageName + "." + file.getName());
            } else {
                classNames.add(packageName + "." + file.getName().replace(".class",""));
            }
        }

    }

    private void doLoadConfig(String location) {

        InputStream is = this.getClass().getClassLoader().getResourceAsStream(location.replace("classpath:",""));
        try {
            contextConfig.load(is);
        } catch (IOException e){
            e.printStackTrace();
        } finally {
            try {
                if(null != is){is.close();}
            } catch (Exception e){
                e.printStackTrace();
            }
        }

    }

    private String lowerFirstCase(String str){
        char [] chars = str.toCharArray();
        chars[0] += 32;
        return String.valueOf(chars);
    }
}
