package com.NMCNPM.ABT_bio;

import com.NMCNPM.ABT_bio.dto.response.CartItemResponse;
import com.NMCNPM.ABT_bio.dto.response.OrderResponse;
import com.NMCNPM.ABT_bio.dto.response.ProductResponse;
import com.NMCNPM.ABT_bio.entity.Cart;
import com.NMCNPM.ABT_bio.entity.Category;
import com.NMCNPM.ABT_bio.entity.Product;
import com.NMCNPM.ABT_bio.entity.Orders;
import com.NMCNPM.ABT_bio.enums.OrderStatusEnum;
import com.NMCNPM.ABT_bio.repository.ProductRepository;
import com.NMCNPM.ABT_bio.service.ApiContractMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

class ApiContractMapperTest {

    private final ProductRepository productRepository = Mockito.mock(ProductRepository.class);
    private final ApiContractMapper mapper = new ApiContractMapper(productRepository, new ObjectMapper());

    @Test
    void toProductResponse_mapsCoreFields() {
        Category category = Category.builder().name("Molecular Tools").build();
        Product product = Product.builder()
                .id(10L)
                .name("PCR Thermocycler")
                .slug("pcr-thermocycler")
                .detailedDescription("High precision")
                .thumbnailUrl("/img.png")
                .galleryUrls(List.of("/img1.png"))
                .inventoryCount(12)
                .isPopular(true)
                .category(category)
                .prices(List.of(Product.ProductPrice.builder().amount(new BigDecimal("1200000")).build()))
                .build();

        ProductResponse response = mapper.toProductResponse(product);

        assertThat(response.getId()).isEqualTo(10L);
        assertThat(response.getCategory()).isEqualTo("Molecular Tools");
        assertThat(response.getPrice()).isEqualByComparingTo("1200000");
        assertThat(response.getStock()).isEqualTo(12);
        assertThat(response.getFeatured()).isTrue();
    }

    @Test
    void toOrderResponse_readsSnapshotFromBuyerContent() {
        Product product = Product.builder()
                .id(99L)
                .name("Microscope")
                .slug("microscope")
                .thumbnailUrl("/microscope.png")
                .inventoryCount(6)
                .build();

        when(productRepository.findById(99L)).thenReturn(Optional.of(product));

        Orders order = Orders.builder()
                .orderCode("ORD-1001")
                .productName("Microscope")
                .totalAmount(new BigDecimal("2400000"))
                .currency("VND")
                .status(OrderStatusEnum.PAID)
                .createdAt(Instant.parse("2025-01-01T00:00:00Z"))
                .buyerContent("{\"customerName\":\"Nguyen Van A\",\"email\":\"a@abt.vn\",\"phone\":\"0900000000\",\"address\":\"Room 12\",\"organization\":\"Bệnh viện A\",\"paymentMethod\":\"payos\",\"notes\":\"Urgent\",\"items\":[{\"productId\":99,\"quantity\":2,\"priceAtOrder\":1200000}]}")
                .build();

        OrderResponse response = mapper.toOrderResponse(order);

        assertThat(response.getCustomerName()).isEqualTo("Nguyen Van A");
        assertThat(response.getEmail()).isEqualTo("a@abt.vn");
        assertThat(response.getItems()).hasSize(1);
        assertThat(response.getPaymentStatus()).isEqualTo("paid");
        assertThat(response.getStatus()).isEqualTo("paid");
    }

    @Test
    void toCartItemResponse_mapsCartItemToDto() {
        Product product = Product.builder()
                .id(7L)
                .name("DNA Extractor")
                .slug("dna-extractor")
                .thumbnailUrl("/dna.png")
                .inventoryCount(10)
                .category(Category.builder().name("Lab Tools").build())
                .build();

        when(productRepository.findById(7L)).thenReturn(Optional.of(product));

        Cart.CartItem item = Cart.CartItem.builder()
                .productId(7L)
                .productName("DNA Extractor")
                .quantity(3)
                .unitPrice(new BigDecimal("500000"))
                .currency("VND")
                .build();

        CartItemResponse response = mapper.toCartItemResponse(item);

        assertThat(response.getProduct()).isNotNull();
        assertThat(response.getProduct().getId()).isEqualTo(7L);
        assertThat(response.getProduct().getName()).isEqualTo("DNA Extractor");
        assertThat(response.getQuantity()).isEqualTo(3);
    }
}
