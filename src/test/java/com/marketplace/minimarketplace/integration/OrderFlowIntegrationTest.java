package com.marketplace.minimarketplace.integration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.marketplace.minimarketplace.entity.User;
import com.marketplace.minimarketplace.repository.OrderItemRepository;
import com.marketplace.minimarketplace.repository.OrderRepository;
import com.marketplace.minimarketplace.repository.ProductRepository;
import com.marketplace.minimarketplace.repository.UserProfileRepository;
import com.marketplace.minimarketplace.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.util.Map;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@ExtendWith(OutputCaptureExtension.class)
class OrderFlowIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserProfileRepository userProfileRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        orderItemRepository.deleteAll();
        orderRepository.deleteAll();
        productRepository.deleteAll();
        userProfileRepository.deleteAll();
        userRepository.deleteAll();

        User admin = User.builder()
                .name("Admin")
                .email("admin@test.com")
                .password(passwordEncoder.encode("admin123"))
                .role("ROLE_ADMIN")
                .build();
        userRepository.save(admin);
    }

    @Test
    void orderFlow_customerProductOrderStatus_shouldCompleteAndNotifyObservers(CapturedOutput output) throws Exception {
        String adminToken = loginAndGetToken("admin@test.com", "admin123");

        String customerEmail = "customer1@test.com";
        String customerPassword = "pass123";
        createCustomerAsAdmin(adminToken, customerEmail, customerPassword);

        Long productId = createProductAsAdmin(adminToken, "Laptop Sleeve", new BigDecimal("100.00"), 10);

        String customerToken = loginAndGetToken(customerEmail, customerPassword);
        Long orderId = placeOrderAndAssert(customerToken, productId, 5, "bulkDiscount", new BigDecimal("425.00"), "Bulk Discount (15% for 5+ items)");

        mockMvc.perform(get("/api/orders/{id}", orderId)
                        .header("Authorization", bearer(customerToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(orderId))
                .andExpect(jsonPath("$.items", hasSize(1)))
                .andExpect(jsonPath("$.items[0].productId").value(productId.intValue()))
                .andExpect(jsonPath("$.items[0].quantity").value(5));

        mockMvc.perform(patch("/api/orders/{id}/status", orderId)
                        .header("Authorization", bearer(customerToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("status", "SHIPPED"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SHIPPED"));

        mockMvc.perform(get("/api/products/{id}", productId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stock").value(5));

        String logs = output.getOut();
        org.junit.jupiter.api.Assertions.assertTrue(
                logs.contains("[LOG]") && logs.contains("status: SHIPPED") && logs.contains("Order #" + orderId),
                "Expected logging observer entry for SHIPPED status change");
        org.junit.jupiter.api.Assertions.assertTrue(
                logs.contains("[EMAIL]") && logs.contains("status: SHIPPED") && logs.contains("order #" + orderId),
                "Expected email observer entry for SHIPPED status change");
    }

    @Test
    void placeOrder_percentageDiscountStrategy_shouldReturnExpectedTotal() throws Exception {
        String adminToken = loginAndGetToken("admin@test.com", "admin123");

        String customerEmail = "customer2@test.com";
        String customerPassword = "pass123";
        createCustomerAsAdmin(adminToken, customerEmail, customerPassword);

        Long productId = createProductAsAdmin(adminToken, "Phone Case", new BigDecimal("100.00"), 10);

        String customerToken = loginAndGetToken(customerEmail, customerPassword);
        placeOrderAndAssert(customerToken, productId, 2, "percentageDiscount", new BigDecimal("180.00"), "10% Discount");
    }

    private void createCustomerAsAdmin(String adminToken, String email, String password) throws Exception {
        mockMvc.perform(post("/api/customers")
                        .header("Authorization", bearer(adminToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "name", "Customer",
                                "email", email,
                                "password", password,
                                "phone", "01700000000",
                                "address", "Dhaka"
                        ))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value(email));
    }

    private Long createProductAsAdmin(String adminToken, String name, BigDecimal price, int stock) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/products")
                        .header("Authorization", bearer(adminToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "name", name,
                                "price", price,
                                "stock", stock,
                                "description", "integration-test"
                        ))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value(name))
                .andReturn();

        JsonNode json = objectMapper.readTree(result.getResponse().getContentAsString());
        return json.get("id").asLong();
    }

    private Long placeOrderAndAssert(String customerToken,
                                     Long productId,
                                     int quantity,
                                     String strategy,
                                     BigDecimal expectedTotal,
                                     String expectedStrategyName) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/orders")
                        .header("Authorization", bearer(customerToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "pricingStrategy", strategy,
                                "items", new Object[]{Map.of(
                                        "productId", productId,
                                        "quantity", quantity,
                                        "giftWrap", false,
                                        "expressShipping", false
                                )}
                        ))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("PLACED"))
                .andExpect(jsonPath("$.appliedStrategy").value(expectedStrategyName))
                .andReturn();

        JsonNode json = objectMapper.readTree(result.getResponse().getContentAsString());
        org.junit.jupiter.api.Assertions.assertEquals(
                0,
                json.get("totalAmount").decimalValue().compareTo(expectedTotal),
                "Unexpected strategy total amount");
        return json.get("id").asLong();
    }

    private String loginAndGetToken(String email, String password) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "email", email,
                                "password", password
                        ))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andReturn();

        JsonNode json = objectMapper.readTree(result.getResponse().getContentAsString());
        return json.get("token").asText();
    }

    private String bearer(String token) {
        return "Bearer " + token;
    }
}



