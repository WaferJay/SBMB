package com.wanfajie.microblog;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.bind.support.ConfigurableWebBindingInitializer;

@SpringBootApplication
@ComponentScan
@ServletComponentScan
public class MicroBlogApplication extends SpringBootServletInitializer {

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
	    return builder.sources(MicroBlogApplication.class);
	}

	public static void main(String[] args) {
		SpringApplication.run(MicroBlogApplication.class, args);
	}

}

