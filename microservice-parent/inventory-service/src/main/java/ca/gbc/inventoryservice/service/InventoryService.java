package ca.gbc.inventoryservice.service;

import ca.gbc.inventoryservice.dto.InventoryRequest;
import ca.gbc.inventoryservice.dto.InventoryResponse;

import java.util.List;
import java.util.stream.Collectors;

public interface InventoryService {

    public List<InventoryResponse> isInStock(List<InventoryRequest> requests);
}
