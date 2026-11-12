package com.shopeasy.module.product;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shopeasy.common.BizException;
import com.shopeasy.common.PageResult;
import com.shopeasy.module.category.Category;
import com.shopeasy.module.category.mapper.CategoryMapper;
import com.shopeasy.module.product.dto.ProductRequest;
import com.shopeasy.module.product.dto.ProductView;
import com.shopeasy.module.product.mapper.ProductMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private static final int MAX_PAGE_SIZE = 200;

    private final ProductMapper productMapper;
    private final CategoryMapper categoryMapper;

    public PageResult<ProductView> page(long page, long size, String keyword, Long categoryId) {
        long safePage = Math.max(page, 1);
        long safeSize = Math.min(Math.max(size, 1), MAX_PAGE_SIZE);
        Page<Product> query = new Page<>(safePage, safeSize);

        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            String kw = keyword.trim();
            wrapper.and(w -> w.like(Product::getName, kw)
                    .or().like(Product::getSku, kw)
                    .or().like(Product::getBarcode, kw));
        }
        if (categoryId != null) {
            wrapper.eq(Product::getCategoryId, categoryId);
        }
        wrapper.orderByDesc(Product::getId);

        Page<Product> result = productMapper.selectPage(query, wrapper);

        Map<Long, String> categoryNames = categoryMapper.selectList(null).stream()
                .collect(Collectors.toMap(Category::getId, Category::getName));

        var records = result.getRecords().stream()
                .map(p -> ProductView.of(p, categoryNames.getOrDefault(p.getCategoryId(), "未分类")))
                .toList();
        return new PageResult<>(records, result.getTotal(), safePage, safeSize);
    }

    public ProductView get(Long id) {
        Product product = requireProduct(id);
        Category category = categoryMapper.selectById(product.getCategoryId());
        return ProductView.of(product, category == null ? "未分类" : category.getName());
    }

    @Transactional
    public Long create(ProductRequest request) {
        ensureUnique(request.getSku(), request.getBarcode(), null);

        Product product = new Product();
        applyRequest(product, request);
        product.setStock(0);
        product.setStatus(request.getStatus() == null ? 1 : request.getStatus());
        product.setWarningStock(request.getWarningStock() == null ? 5 : request.getWarningStock());
        productMapper.insert(product);
        return product.getId();
    }

    @Transactional
    public void update(Long id, ProductRequest request) {
        Product product = requireProduct(id);
        ensureUnique(request.getSku(), request.getBarcode(), id);

        applyRequest(product, request);
        if (request.getStatus() != null) {
            product.setStatus(request.getStatus());
        }
        if (request.getWarningStock() != null) {
            product.setWarningStock(request.getWarningStock());
        }
        productMapper.updateById(product);
    }

    @Transactional
    public void delete(Long id) {
        requireProduct(id);
        productMapper.deleteById(id);
    }

    private Product requireProduct(Long id) {
        Product product = productMapper.selectById(id);
        if (product == null) {
            throw new BizException("商品不存在");
        }
        return product;
    }

    private void ensureUnique(String sku, String barcode, Long excludeId) {
        LambdaQueryWrapper<Product> skuWrapper = new LambdaQueryWrapper<Product>()
                .eq(Product::getSku, sku);
        if (excludeId != null) {
            skuWrapper.ne(Product::getId, excludeId);
        }
        if (productMapper.selectCount(skuWrapper) > 0) {
            throw new BizException("商品编码已存在");
        }

        if (StringUtils.hasText(barcode)) {
            LambdaQueryWrapper<Product> barcodeWrapper = new LambdaQueryWrapper<Product>()
                    .eq(Product::getBarcode, barcode);
            if (excludeId != null) {
                barcodeWrapper.ne(Product::getId, excludeId);
            }
            if (productMapper.selectCount(barcodeWrapper) > 0) {
                throw new BizException("商品条码已存在");
            }
        }
    }

    private void applyRequest(Product product, ProductRequest request) {
        product.setSku(request.getSku().trim());
        product.setBarcode(StringUtils.hasText(request.getBarcode()) ? request.getBarcode().trim() : null);
        product.setName(request.getName().trim());
        product.setCategoryId(request.getCategoryId());
        product.setUnit(request.getUnit().trim());
        product.setPurchasePrice(request.getPurchasePrice());
        product.setSalePrice(request.getSalePrice());
        product.setRemark(request.getRemark());
    }
}
