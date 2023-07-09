package com.happyballoon.crm.web.listener;

import com.happyballoon.crm.settings.domain.DicValue;
import com.happyballoon.crm.settings.service.DicService;
import com.happyballoon.crm.settings.service.impl.DicServiceImpl;
import com.happyballoon.crm.utils.ServiceFactory;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.util.*;

public class SysInintListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        /*
            该方法是用来监听上下文域对象，当服务器启动，上下文域对象创建
            对象创建完毕后，马上执行该方法

            event：该参数能够监听的对象
                    监听的是什么对象，就可以通过该参数能取得什么对象
                    例如我们现在监听的是上下文对象，通过该参数就可以取得上下文域对象
         */
        System.out.println("【上下文域对象创建了....】");

        ServletContext application = sce.getServletContext();

        //取数据字典
        /*
            监听器/过滤器/控制器----都可以调业务层
            问业务层要7个list
            打包成map
                map.put("appllationList",dvList);
                map.put("clueStateList",dvList);
                map.put("stageList",dvList);
         */
        DicService ds = (DicService) ServiceFactory.getService(new DicServiceImpl());
        Map<String, List<DicValue>> map = ds.getDictionary();

        Set<String> keys = map.keySet();
        for(String key:keys){
            application.setAttribute(key,map.get(key));
        }

        //*******************************************************************************************
        /*
            处理Stage2Possibility.properties文件步骤：
                解析该文件，将该属性文件中的键值对关系处理成为java中键值对关系（map）

                Map<String(阶段stage),String(可能性possiblity)> pMap = ....
                pMap.put("01资质审查",10)
                pMap.put("02需求分析",25)
                pMap.put("07成交",100)

                pMap保存值之后，放在服务器缓存中
                application.setAttribute("pMap",pMap);
         */
        //解析properties文件
        ResourceBundle rb = ResourceBundle.getBundle("Stage2Possibility");
        Enumeration<String> stages = rb.getKeys();

        Map<String,String> pMap = new HashMap<String, String>();
        while (stages.hasMoreElements()) {
            String stage = stages.nextElement();
            String possiblity = rb.getString(stage);
            pMap.put(stage,possiblity);
        }
        application.setAttribute("pMap",pMap);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {

        System.out.println("【上下文对象被销毁了....】");
    }
}
