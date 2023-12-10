package ca.gbc.orderservice.service;

import ca.gbc.orderservice.dto.OrderRequest;
import org.springframework.web.reactive.function.client.WebClient;

public interface OrderService {

    String placeOrder(OrderRequest orderRequest);
}
