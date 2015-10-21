package com.xwg.mlyq.core.config;

import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.web.filter.RequestContextFilter;

import com.xwg.mlyq.datatrans.path.JingDianListPath;
import com.xwg.mlyq.datatrans.path.JingDianPath;

/**
 * JERSEY 数据 注册接口
 * @author Andy_Liu 2015-6-1
 *
 */
public class ConfigApplication extends ResourceConfig {

    public ConfigApplication() {
        
        register(RequestContextFilter.class);
        //登录界面
        register(JingDianListPath.class);
        register(JingDianPath.class);
    }
}
