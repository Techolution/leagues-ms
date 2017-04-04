package com.makeurpicks;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@EnableEurekaClient
@SpringBootApplication
public class EuroclientApplication {

	public static void main(String[] args) {
		SpringApplication.run(EuroclientApplication.class, args);
	}
	
}

@RestController
class ServiceInstanceRestController	 {
	
	@Autowired
	private DiscoveryClient discoveryClient;
	
	@RequestMapping("/service-instance/{applicationName}")
	public List<ServiceInstance> serviceInstanceByApplicatoinName(@PathVariable String applicationName) {
		return this.discoveryClient.getInstances(applicationName);
	}
}
