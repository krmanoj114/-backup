package com.tpex;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class TpexInvoiceBatchApplication {

	public static void main(String[] args) {
		SpringApplication.run(TpexInvoiceBatchApplication.class, args);
	}

}
