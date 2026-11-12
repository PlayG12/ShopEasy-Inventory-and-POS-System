<script setup lang="ts">
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import {
  DataAnalysis,
  Goods,
  Setting,
  ShoppingCart,
  Tickets,
  TrendCharts,
  User,
  Van,
} from '@element-plus/icons-vue'

const route = useRoute()

const menuItems = [
  { path: '/dashboard', label: '经营概览', icon: DataAnalysis },
  { path: '/pos', label: '收银台', icon: ShoppingCart },
  { path: '/products', label: '商品管理', icon: Goods },
  { path: '/purchases', label: '采购入库', icon: Van },
  { path: '/inventory', label: '库存流水', icon: Tickets },
  { path: '/members', label: '会员管理', icon: User },
  { path: '/reports', label: '经营报表', icon: TrendCharts },
  { path: '/settings', label: '系统设置', icon: Setting },
]

const pageTitle = computed(() => (route.meta.title as string | undefined) ?? 'ShopEasy')
const today = new Intl.DateTimeFormat('zh-CN', { dateStyle: 'full' }).format(new Date())
</script>

<template>
  <el-container class="layout">
    <el-aside width="220px" class="sidebar">
      <div class="brand">
        <div class="brand-logo">SE</div>
        <div>
          <div class="brand-name">ShopEasy</div>
          <div class="brand-sub">进销存 · 收银</div>
        </div>
      </div>

      <el-menu
        :default-active="route.path"
        router
        class="sidebar-menu"
        background-color="transparent"
        text-color="#cbd5e1"
        active-text-color="#ffffff"
      >
        <el-menu-item v-for="item in menuItems" :key="item.path" :index="item.path">
          <el-icon><component :is="item.icon" /></el-icon>
          <span>{{ item.label }}</span>
        </el-menu-item>
      </el-menu>

      <div class="sidebar-footer">v0.1.0 · 开发中</div>
    </el-aside>

    <el-container>
      <el-header class="topbar">
        <div class="topbar-title">{{ pageTitle }}</div>
        <div class="topbar-right">
          <span class="topbar-date">{{ today }}</span>
          <el-tag type="success" effect="plain">演示门店</el-tag>
        </div>
      </el-header>

      <el-main class="main-content">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<style scoped>
.layout {
  height: 100%;
}

.sidebar {
  display: flex;
  flex-direction: column;
  background: var(--app-sidebar);
  color: #e2e8f0;
}

.brand {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 20px 18px;
  border-bottom: 1px solid rgba(148, 163, 184, 0.15);
}

.brand-logo {
  display: grid;
  place-items: center;
  width: 38px;
  height: 38px;
  border-radius: 10px;
  background: var(--app-sidebar-active);
  font-weight: 700;
  letter-spacing: 0.5px;
}

.brand-name {
  font-size: 16px;
  font-weight: 600;
  color: #f8fafc;
}

.brand-sub {
  font-size: 12px;
  color: #94a3b8;
  margin-top: 2px;
}

.sidebar-menu {
  flex: 1;
  border-right: none;
  padding: 10px 8px;
}

.sidebar-menu :deep(.el-menu-item) {
  height: 44px;
  margin-bottom: 4px;
  border-radius: 8px;
}

.sidebar-menu :deep(.el-menu-item:hover) {
  background: rgba(148, 163, 184, 0.12);
}

.sidebar-menu :deep(.el-menu-item.is-active) {
  background: var(--app-sidebar-active);
}

.sidebar-footer {
  padding: 14px 18px;
  font-size: 12px;
  color: #64748b;
  border-top: 1px solid rgba(148, 163, 184, 0.15);
}

.topbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 60px;
  padding: 0 24px;
  background: #fff;
  border-bottom: 1px solid #e5e7eb;
}

.topbar-title {
  font-size: 16px;
  font-weight: 600;
}

.topbar-right {
  display: flex;
  align-items: center;
  gap: 12px;
}

.topbar-date {
  font-size: 13px;
  color: #6b7280;
}

.main-content {
  padding: 20px;
  background: var(--app-bg);
  overflow: auto;
}
</style>
