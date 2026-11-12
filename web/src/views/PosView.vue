<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { Delete, Minus, Plus, Search } from '@element-plus/icons-vue'
import { fetchCategories, fetchProducts } from '../api/product'
import type { Category, Product } from '../api/types'
import { useCartStore } from '../stores/cart'

const cart = useCartStore()

const categories = ref<Category[]>([])
const products = ref<Product[]>([])
const activeCategoryId = ref<number | undefined>(undefined)
const keyword = ref('')
const loading = ref(false)

const totalAmountText = computed(() => cart.totalAmount.toFixed(2))

async function loadCategories() {
  try {
    categories.value = await fetchCategories()
  } catch {
    categories.value = []
  }
}

async function loadProducts() {
  loading.value = true
  try {
    const result = await fetchProducts({
      page: 1,
      size: 200,
      keyword: keyword.value.trim() || undefined,
      categoryId: activeCategoryId.value,
    })
    products.value = result.records.filter((item) => item.status === 1)
  } catch {
    products.value = []
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  void loadCategories()
  void loadProducts()
})

function selectCategory(id?: number) {
  activeCategoryId.value = id
  void loadProducts()
}

function handleSearch() {
  void loadProducts()
}

function addToCart(product: Product) {
  if (product.stock <= 0) {
    ElMessage.warning('该商品库存不足')
    return
  }
  cart.addProduct(product)
}

function handleCheckout() {
  if (cart.items.length === 0) {
    ElMessage.warning('购物车为空')
    return
  }
  ElMessage.info('销售结算接口待接入，当前已完成选品与购物车')
}
</script>

<template>
  <div class="pos-layout">
    <section class="panel category-panel">
      <div class="panel-title">商品分类</div>
      <button
        class="category-item"
        :class="{ active: activeCategoryId === undefined }"
        @click="selectCategory(undefined)"
      >
        全部商品
      </button>
      <button
        v-for="item in categories"
        :key="item.id"
        class="category-item"
        :class="{ active: activeCategoryId === item.id }"
        @click="selectCategory(item.id)"
      >
        {{ item.name }}
      </button>
    </section>

    <section class="panel product-panel">
      <div class="product-toolbar">
        <el-input
          v-model="keyword"
          placeholder="输入商品名称 / 编码搜索"
          clearable
          :prefix-icon="Search"
          @keyup.enter="handleSearch"
        />
        <el-button type="primary" @click="handleSearch">搜索</el-button>
      </div>

      <div v-loading="loading" class="product-grid">
        <button
          v-for="product in products"
          :key="product.id"
          class="product-card"
          :disabled="product.stock <= 0"
          @click="addToCart(product)"
        >
          <div class="product-name">{{ product.name }}</div>
          <div class="product-meta">
            <span class="product-price">¥{{ Number(product.salePrice).toFixed(2) }}</span>
            <el-tag size="small" :type="product.stock <= product.warningStock ? 'danger' : 'info'" effect="plain">
              库存 {{ product.stock }}
            </el-tag>
          </div>
          <div class="product-sku">{{ product.sku }}</div>
        </button>

        <el-empty v-if="!loading && products.length === 0" description="暂无商品，请先在商品管理中维护" class="empty" />
      </div>
    </section>

    <section class="panel cart-panel">
      <div class="cart-header">
        <div class="panel-title">购物车</div>
        <div class="cart-actions">
          <span class="cart-count">{{ cart.totalQuantity }} 件</span>
          <el-button link type="danger" @click="cart.clear()">清空</el-button>
        </div>
      </div>

      <div class="cart-list">
        <el-empty v-if="cart.items.length === 0" description="点击左侧商品加入购物车" :image-size="80" />
        <div v-for="item in cart.items" :key="item.productId" class="cart-item">
          <div class="cart-item-main">
            <div class="cart-item-name">{{ item.name }}</div>
            <div class="cart-item-price">¥{{ item.unitPrice.toFixed(2) }}</div>
          </div>
          <div class="cart-item-controls">
            <el-button circle size="small" :icon="Minus" @click="cart.decrease(item.productId)" />
            <span class="quantity">{{ item.quantity }}</span>
            <el-button circle size="small" :icon="Plus" @click="cart.increase(item.productId)" />
            <el-button circle size="small" type="danger" plain :icon="Delete" @click="cart.remove(item.productId)" />
          </div>
        </div>
      </div>

      <div class="cart-footer">
        <div class="total-row">
          <span>合计</span>
          <span class="total-amount money">¥{{ totalAmountText }}</span>
        </div>
        <el-button type="primary" size="large" class="checkout" @click="handleCheckout">结算</el-button>
        <div class="checkout-hint">结算与支付接口待接入</div>
      </div>
    </section>
  </div>
</template>

<style scoped>
.pos-layout {
  display: grid;
  grid-template-columns: 170px minmax(0, 1fr) 360px;
  gap: 16px;
  min-height: calc(100vh - 140px);
}

.panel {
  display: flex;
  flex-direction: column;
  min-height: 0;
  padding: 16px;
  background: #fff;
  border: 1px solid #e5e7eb;
  border-radius: 12px;
}

.panel-title {
  font-size: 14px;
  font-weight: 600;
  color: #111827;
}

.category-panel {
  gap: 8px;
}

.category-item {
  width: 100%;
  padding: 10px 12px;
  text-align: left;
  font-size: 14px;
  color: #374151;
  background: #f9fafb;
  border: 1px solid transparent;
  border-radius: 8px;
  cursor: pointer;
}

.category-item:hover {
  background: #f0fdf4;
}

.category-item.active {
  color: #15803d;
  background: #f0fdf4;
  border-color: #86efac;
  font-weight: 600;
}

.product-toolbar {
  display: flex;
  gap: 10px;
  margin-bottom: 16px;
}

.product-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(150px, 1fr));
  gap: 12px;
  align-content: start;
  overflow: auto;
}

.product-card {
  display: flex;
  flex-direction: column;
  gap: 8px;
  padding: 14px;
  text-align: left;
  background: #fff;
  border: 1px solid #e5e7eb;
  border-radius: 10px;
  cursor: pointer;
  transition: border-color 0.15s, box-shadow 0.15s;
}

.product-card:hover:not(:disabled) {
  border-color: #86efac;
  box-shadow: 0 4px 12px rgba(22, 163, 74, 0.08);
}

.product-card:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.product-name {
  font-size: 14px;
  font-weight: 600;
  color: #111827;
  min-height: 38px;
}

.product-meta {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 6px;
}

.product-price {
  font-size: 16px;
  font-weight: 700;
  color: #dc2626;
}

.product-sku {
  font-size: 12px;
  color: #9ca3af;
}

.empty {
  grid-column: 1 / -1;
}

.cart-panel {
  gap: 12px;
}

.cart-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.cart-actions {
  display: flex;
  align-items: center;
  gap: 8px;
}

.cart-count {
  font-size: 12px;
  color: #6b7280;
}

.cart-list {
  flex: 1;
  min-height: 0;
  overflow: auto;
  border-top: 1px solid #f3f4f6;
  border-bottom: 1px solid #f3f4f6;
}

.cart-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 10px;
  padding: 10px 0;
  border-bottom: 1px dashed #f3f4f6;
}

.cart-item:last-child {
  border-bottom: none;
}

.cart-item-main {
  min-width: 0;
}

.cart-item-name {
  font-size: 13px;
  color: #111827;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.cart-item-price {
  margin-top: 4px;
  font-size: 12px;
  color: #6b7280;
}

.cart-item-controls {
  display: flex;
  align-items: center;
  gap: 4px;
}

.quantity {
  min-width: 24px;
  text-align: center;
  font-size: 13px;
  font-variant-numeric: tabular-nums;
}

.cart-footer {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.total-row {
  display: flex;
  align-items: baseline;
  justify-content: space-between;
  font-size: 14px;
  color: #374151;
}

.total-amount {
  font-size: 26px;
  font-weight: 700;
  color: #dc2626;
}

.checkout {
  width: 100%;
}

.checkout-hint {
  text-align: center;
  font-size: 12px;
  color: #9ca3af;
}

@media (max-width: 1200px) {
  .pos-layout {
    grid-template-columns: 150px minmax(0, 1fr) 320px;
  }
}
</style>
