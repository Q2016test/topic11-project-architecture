package ru.homework.config;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public record TestData(ProductData startingProduct, UiData ui, ApiData api) {
    public record ProductData(String name, BigDecimal price) {
    }

    public record CartEntry(ProductData product, int quantity) {
    }

    public record UiData(int purchaseQuantity, BigDecimal orderLimit, int startingCartQuantity,
                         List<CartEntry> cartProducts, ProductData createdProduct, ProductData editedProduct) {
    }

    public record PaginationCase(int page, int size, int expectedCount) {
    }

    public record InvalidProduct(Map<String, Object> body, String field) {
    }

    public record AuthCase(String method, String pathKey, boolean hasBody) {
    }

    public record ApiData(int firstPage, int pageSize, int lookupPageSize, long missingId,
                          int paginationProductCount, BigDecimal paginationBasePrice,
                          BigDecimal paginationPriceStep, List<PaginationCase> paginationCases,
                          Map<String, BigDecimal> prices, List<InvalidProduct> invalidProducts,
                          List<AuthCase> authCases) {
        public BigDecimal price(String key) {
            BigDecimal value = prices.get(key);
            if (value == null) {
                throw new IllegalArgumentException("Не задана цена: " + key);
            }
            return value;
        }
    }
}
