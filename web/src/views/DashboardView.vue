<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { Goods, ShoppingCart } from '@element-plus/icons-vue'
import http from '../api/http'
import type { ApiResponse } from '../api/types'

const router = useRouter()
const backendStatus = ref<'checking' | 'online' | 'offline'>('checking')

const stats = [
  { label: '今日销售额', value: '--', hint: '销售模块接入后展示' },
  { label: '今日订单数', value: '--', hint: '销售模块接入后展示' },
  { label: '库存预警商品', value: '--', hint: '库存模块接入后展示' },
  { label: '会员总数', value: '--', hint: '会员模块接入后展示' },
]

const statusText = ref('正在检测后端服务')

onMounted(async () => {
  try {
    const response = await http.get<ApiResponse<{ status: string }>>('/health')
    backendStatus.value = response.data.data.status === 'UP' ? 'online' : 'offline'
    statusText.value = backendStatus.value === 'online' ? '后端服务已连接' : '后端服务状态异常'
  } catch {
    backendStatus.value = 'offline'
    statusText.value = '无法连接后端服务，请先启动 server'
  }
})
</script>

<template>
  <div class="page">
    <div class="page-header">
      <div>
        <h2 class="page-title">经营概览</h2>
        <p class="page-subtitle">当前为项目骨架，商品管理与收银台已接入后端，其余模块待实现。</p>
      </div>
      <el-tag :type="backendStatus === 'online' ? 'success' : backendStatus === 'offline' ? 'danger' : 'info'">
        {{ statusText }}
      </el-tag>
    </div>

    <div class="stat-grid">
      <div v-for="item in stats" :key="item.label" class="stat-card">
        <div class="stat-label">{{ item.label }}</div>
        <div class="stat-value">{{ item.value }}</div>
        <div class="stat-hint">{{ item.hint }}</div>
      </div>
    </div>

    <div class="content-grid">
      <el-card shadow="never" class="business-card">
        <template #header>
          <span class="card-title">快捷入口</span>
        </template>
        <div class="quick-actions">
          <el-button type="primary" size="large" :icon="ShoppingCart" @click="router.push('/pos')">
            打开收银台
          </el-button>
          <el-button size="large" :icon="Goods" @click="router.push('/products')">
            管理商品
          </el-button>
        </div>
        <el-divider />
        <ul class="todo-list">
          <li>采购入库：采购单 → 到货入库 → 库存流水</li>
          <li>销售结算：购物车 → 支付 → 扣减库存并生成流水</li>
          <li>报表中心：日结、商品排行、毛利分析</li>
        </ul>
      </el-card>

      <el-card shadow="never" class="business-card">
        <template #header>
          <span class="card-title">技术栈</span>
        </template>
        <div class="stack-list">
          <div><span>前端</span>Vue 3 + TypeScript + Vite + Element Plus</div>
          <div><span>状态 / 路由</span>Pinia + Vue Router</div>
          <div><span>后端</span>Spring Boot 3 + MyBatis-Plus</div>
          <div><span>数据库</span>MySQL 8 + Flyway</div>
          <div><span>接口文档</span>Swagger UI（/swagger-ui.html）</div>
        </div>
      </el-card>
    </div>
  </div>
</template>

<style scoped>
.stat-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 16px;
}

.stat-card {
  padding: 18px 20px;
  background: #fff;
  border: 1px solid #e5e7eb;
  border-radius: 12px;
}

.stat-label {
  font-size: 13px;
  color: #6b7280;
}

.stat-value {
  margin-top: 10px;
  font-size: 28px;
  font-weight: 700;
  font-variant-numeric: tabular-nums;
}

.stat-hint {
  margin-top: 6px;
  font-size: 12px;
  color: #9ca3af;
}

.content-grid {
  display: grid;
  grid-template-columns: 1.4fr 1fr;
  gap: 16px;
}

.card-title {
  font-weight: 600;
}

.quick-actions {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
}

.todo-list {
  margin: 0;
  padding-left: 18px;
  color: #4b5563;
  line-height: 2;
  font-size: 13px;
}

.stack-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
  font-size: 13px;
  color: #374151;
}

.stack-list span {
  display: inline-block;
  width: 84px;
  color: #9ca3af;
}

@media (max-width: 1100px) {
  .stat-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }

  .content-grid {
    grid-template-columns: 1fr;
  }
}
</style>
