package com.shopeasy.module.product;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("product")
public class Product {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String sku;

    private String barcode;

    private String name;

    private Long categoryId;

    private String unit;

    private BigDecimal purchasePrice;

    private BigDecimal salePrice;

    /**
     * 库存快照，真实库存以 stock_movement 流水为准。
     */
    private Integer stock;

    private Integer warningStock;

    /**
     * 1 在售，0 停售。
     */
    private Integer status;

    private String remark;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
