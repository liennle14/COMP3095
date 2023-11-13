package ca.gbc.service;

import ca.gbc.repository.InventoryRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

public interface InventoryService {

    public boolean isInStock(String skuCode);
}
