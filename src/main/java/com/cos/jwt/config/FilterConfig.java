package com.cos.jwt.config;

import com.cos.jwt.filter.MyFilter1;
import com.cos.jwt.filter.MyFilter2;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {
    // 여기는 시큐리티 필터에 등록하는게 아닌 그냥 필터를 스프링컨테어너에 등록한거
    // 실행순서는 시큐리티 필터들이 다 실행된뒤에 아래 필터들이 실행된다.
    @Bean
    public FilterRegistrationBean<MyFilter1> filter1() {
        FilterRegistrationBean<MyFilter1> bean = new FilterRegistrationBean<>(new MyFilter1());
        bean.addUrlPatterns("/*"); // 모든요청에 다 필터해라
        bean.setOrder(0);// 낮은번호가 필터중 가장먼저 실행.(우선순위주는거)
        return bean;
    }

    @Bean
    public FilterRegistrationBean<MyFilter2> filter2() {
        FilterRegistrationBean<MyFilter2> bean = new FilterRegistrationBean<>(new MyFilter2());
        bean.addUrlPatterns("/*");
        bean.setOrder(1);
        return bean;
    }
}
