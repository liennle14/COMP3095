package ca.gbc.productservice;

import ca.gbc.productservice.dto.ProductRequest;
import ca.gbc.productservice.dto.ProductResponse;
import ca.gbc.productservice.model.Product;
import ca.gbc.productservice.repository.ProductRepository;
import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import com.fasterxml.jackson.core.JsonProcessingException;      //**
import com.fasterxml.jackson.core.type.TypeReference;           //**
import com.fasterxml.jackson.databind.ObjectMapper;             //**

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.assertEquals;


@SpringBootTest
@AutoConfigureMockMvc
class ProductServiceApplicationTests extends AbstractContainerBaseTest {  //naming convention: tests or test always appended



    @Autowired
    private MockMvc mockMvc; //pretending to do a request

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    MongoTemplate mongoTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    private ProductRequest getProductRequest() {
        return ProductRequest.builder()
                .name("Apple iPad 2023")
                .description("Apple iPad version 2023")
                .price(BigDecimal.valueOf(1200))
                .build();
    }

    private List<Product> getProductList() {
        List<Product> productList = new ArrayList<>();
        UUID uuid=UUID.randomUUID();

        Product product = Product.builder()
                .id(uuid.toString())
                .name("Apple iPad 2023")
                .description("Apple iPad version 2023")
                .price(BigDecimal.valueOf(1200))
                .build();

        productList.add(product);
        return productList;
    }

    private String convertObjectToJson(List<ProductResponse> productList) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(productList);
    }

    private List<ProductResponse> convertJsonStringToObject(String jsonString)
            throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(jsonString, new TypeReference<List<ProductResponse>>() {

        });

    }



    @Test //special annotation meaning this is one test
    void createProduct() throws Exception {
        ProductRequest product = getProductRequest();
        String productRequestString = objectMapper.writeValueAsString(product);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/product") //to end point /api/product  || post request - not quite returning anything
                        .contentType("application/json")
                        .content(productRequestString))
                .andExpect(MockMvcResultMatchers.status().isCreated());

        Assertions.assertTrue(productRepository.findAll().size() > 0);
        Query query = new Query();
        query.addCriteria(Criteria.where("name").is("Apple iPad 2023"));
        List<Product> products=mongoTemplate.find(query, Product.class);
        Assertions.assertTrue(products.size() > 0);

    }

    //BDD - behaviour driven development
    //Given - setup
    //When - action
    //Then - verify
    @Test
    void getAllProducts() throws Exception {

        //GIVEN
        productRepository.saveAll(getProductList()); // call repo, passing in list of products

        //WHEN
        ResultActions response = mockMvc.perform(MockMvcRequestBuilders // hold get request in "response"
                .get("/api/product")
                .accept(MediaType.APPLICATION_JSON));

        //THEN
        response.andExpect(MockMvcResultMatchers.status().isOk()); // should return (200) status code
        response.andDo(MockMvcResultHandlers.print());

        MvcResult result = response.andReturn();
        String jsonResponse = result.getResponse().getContentAsString(); //get json payload
        JsonNode jsonNodes = new ObjectMapper().readTree(jsonResponse); // objectmapper reads string and produce/parse json nodes

        int actualSize = jsonNodes.size();
        int expectedSize = getProductList().size();

        Assertions.assertEquals(expectedSize, actualSize);

    }

    @Test
    void updateProduct() throws Exception{

        //GIVEN
        Product savedProduct = Product.builder()
                .id(UUID.randomUUID().toString())
                .name("Widget")
                .description("Widget Original Price")
                .price(BigDecimal.valueOf(100))
                .build();
        //saved product with original price
        productRepository.save(savedProduct);

        //prepare updated product and productRequest
        savedProduct.setPrice(new BigDecimal(BigInteger.valueOf(200)));
        String productRequestString = objectMapper.writeValueAsString(savedProduct);

        //WHEN
        ResultActions response = mockMvc.perform(MockMvcRequestBuilders
                .put("/api/product/" + savedProduct.getId().toString())
                .contentType(MediaType.APPLICATION_JSON)
                .content(productRequestString));

        //THEN
        response.andExpect(MockMvcResultMatchers.status().isNoContent()); // should return (204) status code
        response.andDo(MockMvcResultHandlers.print()); // optional

        Query query = new Query();
        query.addCriteria(Criteria.where("id").is(savedProduct.getId()));
        Product storedProduct = mongoTemplate.findOne(query, Product.class);

        assertEquals(savedProduct.getPrice(), storedProduct.getPrice());
    }

    @Test
    void deleteProduct() throws Exception {
        //GIVEN
        Product savedProduct = Product.builder()
                .id(UUID.randomUUID().toString())
                .name("Java Microservices Programming")
                .description("Course Textbook - Java Microservices Programming")
                .price(new BigDecimal(200))
                .build();

        //WHEN
        ResultActions response = mockMvc.perform(MockMvcRequestBuilders
                .delete("/api/product/" + savedProduct.getId().toString())
                .contentType(MediaType.APPLICATION_JSON));

        //THEN
        response.andExpect(MockMvcResultMatchers.status().isNoContent()); // should return (204) status code
        response.andDo(MockMvcResultHandlers.print());

        Query query = new Query();
        query.addCriteria(Criteria.where("id").is(savedProduct.getId()));
        Long productCount = mongoTemplate.count(query, Product.class);

        assertEquals(0, productCount);

    }
}


