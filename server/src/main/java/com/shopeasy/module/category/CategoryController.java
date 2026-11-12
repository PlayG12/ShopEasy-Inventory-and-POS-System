package com.shopeasy.module.category;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.shopeasy.common.ApiResponse;
import com.shopeasy.module.category.mapper.CategoryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryMapper categoryMapper;

    @GetMapping
    public ApiResponse<List<Category>> list() {
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<Category>()
                .orderByAsc(Category::getSort)
                .orderByAsc(Category::getId);
        return ApiResponse.ok(categoryMapper.selectList(wrapper));
    }
}
