package store.web.rest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan({ "store.model", "store.web.rest" })
@EntityScan(basePackages = "store.model.entity")
public class StoreApplication {
	public static void main(String[] args) {
		SpringApplication.run( StoreApplication.class, args);
	}
}
