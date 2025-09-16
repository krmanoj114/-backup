package com.tpex.admin;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.remoting.httpinvoker.HttpInvokerProxyFactoryBean;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.tpex.admin.service.AddressMasterService;

/**
 * The Class AdminApplication.
 */
@SpringBootApplication
@ComponentScan(basePackages = "com.tpex.*")
@EntityScan(basePackages = "com.tpex.*")
@EnableJpaRepositories(basePackages = "com.tpex.*")
@EnableAsync
@EnableRetry
public class AdminApplication {

	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args) {
		SpringApplication.run(AdminApplication.class, args);
	}

	/**
	 * Configurer.
	 *
	 * @return the web mvc configurer
	 */
	@Bean
	public WebMvcConfigurer configurer() {
		return new WebMvcConfigurer() {
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				registry.addMapping("/**");

			}
		};
	}

	@Bean
	public RestTemplate getRestTemplate() {
		return new RestTemplate();
	}

	@Value("${invoice.batch.service.host}")
	String invoiceBatchServiceHost;

	@SuppressWarnings("deprecation")
	@Bean
	public HttpInvokerProxyFactoryBean exporter() {

		HttpInvokerProxyFactoryBean b = new HttpInvokerProxyFactoryBean();
		b.setServiceUrl(invoiceBatchServiceHost + "/AddressService");
		b.setServiceInterface(AddressMasterService.class);
		return b;
	}

}
