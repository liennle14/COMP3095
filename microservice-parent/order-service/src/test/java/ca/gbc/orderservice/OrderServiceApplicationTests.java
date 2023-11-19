package ca.gbc.orderservice;

import ca.gbc.orderservice.model.Order;
import ca.gbc.orderservice.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(classes = OrderServiceApplication.class)
@AutoConfigureMockMvc
class OrderServiceApplicationTests extends AbstractContainerBaseTest{
	@Autowired
	private OrderRepository orderRepository;

	@Autowired
	private MockMvc mockMvc;

	@Test
	void contextLoads() {
	}

	@Test
	public void placeOrder(){
		Order order = new Order();
		orderRepository.save(order);
	}
}
