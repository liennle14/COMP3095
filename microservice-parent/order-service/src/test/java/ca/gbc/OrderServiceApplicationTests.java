package ca.gbc;

import ca.gbc.orderservice.dto.OrderRequest;
import ca.gbc.orderservice.model.Order;
import ca.gbc.orderservice.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.bind.annotation.RequestBody;

@SpringBootTest
class OrderServiceApplicationTests {
	private OrderRepository orderRepository;

	@Test
	void contextLoads() {
	}

	@Test
	void createOrder(@RequestBody OrderRequest orderRequest){
		Order order = new Order();
		orderRepository.save(order);
	}
}
