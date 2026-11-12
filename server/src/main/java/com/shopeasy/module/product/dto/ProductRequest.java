package com.shopeasy.module.product.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductRequest {

    @NotBlank(message = "商品编码不能为空")
    private String sku;

    private String barcode;

    @NotBlank(message = "商品名称不能为空")
    private String name;

    @NotNull(message = "请选择商品分类")
    private Long categoryId;

    @NotBlank(message = "计量单位不能为空")
    private String unit;

    @NotNull(message = "进货价不能为空")
    @DecimalMin(value = "0", message = "进货价不能小于 0")
    private BigDecimal purchasePrice;

    @NotNull(message = "售价不能为空")
    @DecimalMin(value = "0", message = "售价不能小于 0")
    private BigDecimal salePrice;

    private Integer warningStock;

    private Integer status;

    private String remark;
}
