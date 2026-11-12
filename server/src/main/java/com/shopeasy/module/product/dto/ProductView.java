package com.shopeasy.module.product.dto;

import com.shopeasy.module.product.Product;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ProductView(
        Long id,
        String sku,
        String barcode,
        String name,
        Long categoryId,
        String categoryName,
        String unit,
        BigDecimal purchasePrice,
        BigDecimal salePrice,
        Integer stock,
        Integer warningStock,
        Integer status,
        String remark,
        LocalDateTime createdAt
) {

    public static ProductView of(Product product, String categoryName) {
        return new ProductView(
                product.getId(),
                product.getSku(),
                product.getBarcode(),
                product.getName(),
                product.getCategoryId(),
                categoryName,
                product.getUnit(),
                product.getPurchasePrice(),
                product.getSalePrice(),
                product.getStock(),
                product.getWarningStock(),
                product.getStatus(),
                product.getRemark(),
                product.getCreatedAt()
        );
    }
}
