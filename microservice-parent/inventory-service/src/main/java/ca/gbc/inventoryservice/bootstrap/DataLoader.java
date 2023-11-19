package ca.gbc.inventoryservice.bootstrap;

import ca.gbc.inventoryservice.repository.InventoryRepository;
import ca.gbc.inventoryservice.model.Inventory;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {
    private final InventoryRepository inventoryRepository;

    @Override
    public void run(String... args) throws Exception {
        if (inventoryRepository.findBySkuCode("sku_12345").isEmpty()){
            Inventory widgets = Inventory.builder()
                    .skuCode("sku_12345")
                    .quantity(2)
                    .build();

            inventoryRepository.save(widgets);
        }

        if (inventoryRepository.findBySkuCode("sku_22222").isEmpty()){
            Inventory widgets = Inventory.builder()
                    .skuCode("sku_22222")
                    .quantity(1)
                    .build();

            inventoryRepository.save(widgets);
        }
        if (inventoryRepository.findBySkuCode("sku_11111").isEmpty()){
            Inventory widgets = Inventory.builder()
                    .skuCode("sku_11111")
                    .quantity(3)
                    .build();

            inventoryRepository.save(widgets);
        }
    }
}
