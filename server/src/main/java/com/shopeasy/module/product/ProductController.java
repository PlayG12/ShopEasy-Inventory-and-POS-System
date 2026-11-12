package com.shopeasy.module.product;

import com.shopeasy.common.ApiResponse;
import com.shopeasy.common.PageResult;
import com.shopeasy.module.product.dto.ProductRequest;
import com.shopeasy.module.product.dto.ProductView;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public ApiResponse<PageResult<ProductView>> page(
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "20") long size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId) {
        return ApiResponse.ok(productService.page(page, size, keyword, categoryId));
    }

    @GetMapping("/{id}")
    public ApiResponse<ProductView> get(@PathVariable Long id) {
        return ApiResponse.ok(productService.get(id));
    }

    @PostMapping
    public ApiResponse<Long> create(@Valid @RequestBody ProductRequest request) {
        return ApiResponse.ok(productService.create(request));
    }

    @PutMapping("/{id}")
    public ApiResponse<Void> update(@PathVariable Long id, @Valid @RequestBody ProductRequest request) {
        productService.update(id, request);
        return ApiResponse.ok();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        productService.delete(id);
        return ApiResponse.ok();
    }
}
