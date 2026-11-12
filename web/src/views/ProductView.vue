<script setup lang="ts">
import { nextTick, onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import { Plus, Refresh, Search } from '@element-plus/icons-vue'
import { createProduct, deleteProduct, fetchCategories, fetchProducts, updateProduct } from '../api/product'
import type { Category, Product, ProductPayload } from '../api/types'

const loading = ref(false)
const submitting = ref(false)
const products = ref<Product[]>([])
const categories = ref<Category[]>([])
const total = ref(0)

const keyword = ref('')
const categoryId = ref<number | undefined>(undefined)
const page = ref(1)
const pageSize = ref(10)

const dialogVisible = ref(false)
const editingId = ref<number | null>(null)
const formRef = ref<FormInstance>()
const form = reactive<ProductPayload>(emptyForm())

const rules: FormRules = {
  sku: [{ required: true, message: '请输入商品编码', trigger: 'blur' }],
  name: [{ required: true, message: '请输入商品名称', trigger: 'blur' }],
  categoryId: [{ required: true, message: '请选择商品分类', trigger: 'change' }],
  unit: [{ required: true, message: '请输入计量单位', trigger: 'blur' }],
  purchasePrice: [{ required: true, message: '请输入进货价', trigger: 'change' }],
  salePrice: [{ required: true, message: '请输入售价', trigger: 'change' }],
}

function emptyForm(): ProductPayload {
  return {
    sku: '',
    barcode: '',
    name: '',
    categoryId: null,
    unit: '件',
    purchasePrice: 0,
    salePrice: 0,
    warningStock: 5,
    status: 1,
    remark: '',
  }
}

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
      page: page.value,
      size: pageSize.value,
      keyword: keyword.value.trim() || undefined,
      categoryId: categoryId.value,
    })
    products.value = result.records
    total.value = result.total
  } catch {
    products.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  void loadCategories()
  void loadProducts()
})

function handleSearch() {
  page.value = 1
  void loadProducts()
}

function handleReset() {
  keyword.value = ''
  categoryId.value = undefined
  page.value = 1
  void loadProducts()
}

function handlePageChange(value: number) {
  page.value = value
  void loadProducts()
}

function handleSizeChange(value: number) {
  pageSize.value = value
  page.value = 1
  void loadProducts()
}

function openCreate() {
  editingId.value = null
  Object.assign(form, emptyForm())
  dialogVisible.value = true
  void nextTick(() => formRef.value?.clearValidate())
}

function openEdit(product: Product) {
  editingId.value = product.id
  Object.assign(form, {
    sku: product.sku,
    barcode: product.barcode ?? '',
    name: product.name,
    categoryId: product.categoryId,
    unit: product.unit,
    purchasePrice: Number(product.purchasePrice),
    salePrice: Number(product.salePrice),
    warningStock: product.warningStock,
    status: product.status,
    remark: product.remark ?? '',
  })
  dialogVisible.value = true
  void nextTick(() => formRef.value?.clearValidate())
}

async function submitForm() {
  if (!formRef.value) {
    return
  }
  try {
    await formRef.value.validate()
  } catch {
    return
  }
  if (form.categoryId === null) {
    return
  }

  submitting.value = true
  try {
    const payload: ProductPayload = { ...form, categoryId: form.categoryId }
    if (editingId.value === null) {
      await createProduct(payload)
      ElMessage.success('商品已创建')
    } else {
      await updateProduct(editingId.value, payload)
      ElMessage.success('商品已更新')
    }
    dialogVisible.value = false
    await loadProducts()
  } catch {
    // 错误提示由请求拦截器统一处理
  } finally {
    submitting.value = false
  }
}

async function handleDelete(product: Product) {
  try {
    await ElMessageBox.confirm(`确定删除商品「${product.name}」吗？该操作不可恢复。`, '删除确认', {
      type: 'warning',
      confirmButtonText: '删除',
      cancelButtonText: '取消',
    })
  } catch {
    return
  }

  try {
    await deleteProduct(product.id)
    ElMessage.success('商品已删除')
    if (products.value.length === 1 && page.value > 1) {
      page.value -= 1
    }
    await loadProducts()
  } catch {
    // 错误提示由请求拦截器统一处理
  }
}
</script>

<template>
  <div class="page">
    <div class="page-header">
      <div>
        <h2 class="page-title">商品管理</h2>
        <p class="page-subtitle">维护商品主数据；库存变动请通过采购、销售或盘点产生。</p>
      </div>
      <el-button type="primary" :icon="Plus" @click="openCreate">新增商品</el-button>
    </div>

    <el-card shadow="never" class="business-card">
      <el-form inline @submit.prevent>
        <el-form-item label="关键字">
          <el-input
            v-model="keyword"
            placeholder="编码 / 条码 / 名称"
            clearable
            :prefix-icon="Search"
            style="width: 220px"
            @keyup.enter="handleSearch"
          />
        </el-form-item>
        <el-form-item label="分类">
          <el-select v-model="categoryId" placeholder="全部分类" clearable style="width: 160px">
            <el-option v-for="item in categories" :key="item.id" :label="item.name" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="Search" @click="handleSearch">查询</el-button>
          <el-button :icon="Refresh" @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card shadow="never" class="business-card">
      <el-table v-loading="loading" :data="products" stripe>
        <el-table-column prop="sku" label="商品编码" width="130" />
        <el-table-column prop="name" label="商品名称" min-width="180" show-overflow-tooltip />
        <el-table-column prop="categoryName" label="分类" width="110" />
        <el-table-column prop="unit" label="单位" width="70" />
        <el-table-column label="进货价" width="100" align="right">
          <template #default="{ row }">¥{{ Number(row.purchasePrice).toFixed(2) }}</template>
        </el-table-column>
        <el-table-column label="售价" width="100" align="right">
          <template #default="{ row }">¥{{ Number(row.salePrice).toFixed(2) }}</template>
        </el-table-column>
        <el-table-column label="库存" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.stock <= row.warningStock ? 'danger' : 'success'" effect="plain">
              {{ row.stock }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="90" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'" effect="plain">
              {{ row.status === 1 ? '在售' : '停售' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="140" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="openEdit(row)">编辑</el-button>
            <el-button link type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination">
        <el-pagination
          background
          layout="total, sizes, prev, pager, next"
          :total="total"
          :current-page="page"
          :page-size="pageSize"
          :page-sizes="[10, 20, 50]"
          @current-change="handlePageChange"
          @size-change="handleSizeChange"
        />
      </div>
    </el-card>

    <el-dialog
      v-model="dialogVisible"
      :title="editingId === null ? '新增商品' : '编辑商品'"
      width="620px"
      destroy-on-close
    >
      <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="商品编码" prop="sku">
              <el-input v-model="form.sku" placeholder="如 SKU-0001" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="条码" prop="barcode">
              <el-input v-model="form.barcode" placeholder="可选" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="商品名称" prop="name">
          <el-input v-model="form.name" placeholder="商品名称" />
        </el-form-item>

        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="商品分类" prop="categoryId">
              <el-select v-model="form.categoryId" placeholder="请选择分类" style="width: 100%">
                <el-option v-for="item in categories" :key="item.id" :label="item.name" :value="item.id" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="计量单位" prop="unit">
              <el-input v-model="form.unit" placeholder="件 / 瓶 / 斤" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="进货价" prop="purchasePrice">
              <el-input-number v-model="form.purchasePrice" :min="0" :precision="2" :step="0.5" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="售价" prop="salePrice">
              <el-input-number v-model="form.salePrice" :min="0" :precision="2" :step="0.5" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="预警库存">
              <el-input-number v-model="form.warningStock" :min="0" :precision="0" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态">
              <el-switch v-model="form.status" :active-value="1" :inactive-value="0" active-text="在售" inactive-text="停售" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="备注">
          <el-input v-model="form.remark" type="textarea" :rows="2" placeholder="可选" />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="submitForm">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.pagination {
  display: flex;
  justify-content: flex-end;
  margin-top: 16px;
}
</style>
