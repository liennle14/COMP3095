package ca.gbc.orderservice.service;

import ca.gbc.orderservice.dto.InventoryRequest;
import ca.gbc.orderservice.dto.InventoryResponse;
import ca.gbc.orderservice.dto.OrderLineItemDTO;
import ca.gbc.orderservice.dto.OrderRequest;
import ca.gbc.orderservice.model.OrderLineItem;
import ca.gbc.orderservice.repository.OrderRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import ca.gbc.orderservice.model.Order;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService{
    private final OrderRepository orderRepository;
    private final WebClient webClient;
    private final WebClient.Builder webClientBuilder;
    @Value("${inventory.service.url}")
    private String inventoryApiUrl;

    @Override
    public String placeOrder(OrderRequest orderRequest) {
        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());
        List<OrderLineItem> orderLineItems = orderRequest
                .getOrderLineItemDTOList()
                .stream()
                .map(this::mapToDto)
                .toList();
        order.setOrderLineItemList(orderLineItems);

        List<InventoryRequest> inventoryRequests = order.getOrderLineItemList()
                .stream()
                .map(orderLineItem -> InventoryRequest
                        .builder()
                        .skuCode(orderLineItem.getSkuCode())
                        .quantity(orderLineItem.getQuantity())
                        .build())
                .toList();

        List<InventoryResponse> inventoryResponseList = webClient
                .post()
                .uri(inventoryApiUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(inventoryRequests)
                .retrieve()
                .bodyToFlux(InventoryResponse.class)
                .collectList()
                .block(); // block to make it synchronous

        assert inventoryResponseList != null;
        boolean allProductsInStock = inventoryResponseList
                .stream()
                .allMatch(InventoryResponse::getSufficientStock);

        if (Boolean.TRUE.equals(allProductsInStock)){
            orderRepository.save(order);
            log.info("Items in stock.");
        } else {
            throw new RuntimeException("Not all products are in stock, order cannot be placed");
        }


//        orderRepository.save(order);
        webClientBuilder.build();
        log.info("Order placed successfully");
        return "Order placed successfully";
    }

    private OrderLineItem mapToDto(OrderLineItemDTO orderLineItemDto) {
        OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setPrice(orderLineItemDto.getPrice());
        orderLineItem.setQuantity(orderLineItemDto.getQuantity());
        orderLineItem.setSkuCode(orderLineItemDto.getSkuCode());
        return orderLineItem;
    }
}
