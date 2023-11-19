package ca.gbc;

import ca.gbc.inventoryservice.InventoryServiceApplication;
import ca.gbc.inventoryservice.controller.InventoryController;
import ca.gbc.inventoryservice.dto.InventoryRequest;
import ca.gbc.inventoryservice.dto.InventoryResponse;
import ca.gbc.inventoryservice.model.Inventory;
import ca.gbc.inventoryservice.repository.InventoryRepository;
import ca.gbc.inventoryservice.service.InventoryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Collections;
import java.util.List;

@SpringBootTest
@AutoConfigureMockMvc
class InventoryServiceApplicationTests extends AbstractContainerBaseTest{

	private InventoryRepository inventoryRepository;
	private InventoryService inventoryService;

	@Autowired
	private MockMvc mockMvc;

	private ObjectMapper objectMapper;

	@Test
	void contextLoads() {
	}

	@Test
	public void isInStock(){
		InventoryRequest request = new InventoryRequest();
		request.setSkuCode("sku_12345");
		request.setQuantity(1);
		boolean allProductsInStock = inventoryService.isInStock(Collections.singletonList(request))
				.stream()
				.allMatch(InventoryResponse::getSufficientStock);
		List<InventoryResponse> inventoryResponses = inventoryService.isInStock(Collections.singletonList(request));

		if (Boolean.TRUE.equals(allProductsInStock)){
			assert inventoryResponses.get(0).getSkuCode().equals(request.getSkuCode());
			assert inventoryResponses.get(0).getSufficientStock();
		} else {
			throw new RuntimeException("Not all products are in stock, order cannot be placed");
		}
	}
}
