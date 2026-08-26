package com.example.demo;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.zkoss.zk.au.http.DHtmlUpdateServlet;
import org.zkoss.zk.ui.http.DHtmlLayoutServlet;

@Configuration
public class ZkWebConfig {

	@Bean
	ServletRegistrationBean<DHtmlLayoutServlet> zkPageServlet() {
		ServletRegistrationBean<DHtmlLayoutServlet> registration =
				new ServletRegistrationBean<>(new DHtmlLayoutServlet(), "*.zul");
		registration.setName("zkPageServlet");
		registration.addInitParameter("update-uri", "/zkau");
		registration.setLoadOnStartup(1);
		return registration;
	}

	@Bean
	ServletRegistrationBean<DHtmlUpdateServlet> zkUpdateServlet() {
		ServletRegistrationBean<DHtmlUpdateServlet> registration =
				new ServletRegistrationBean<>(new DHtmlUpdateServlet(), "/zkau/*");
		registration.setName("zkUpdateServlet");
		registration.setLoadOnStartup(2);
		return registration;
	}
}