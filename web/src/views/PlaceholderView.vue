<script setup lang="ts">
defineProps<{
  title: string
  features: string[]
}>()
</script>

<template>
  <div class="page">
    <div class="page-header">
      <div>
        <h2 class="page-title">{{ title }}</h2>
        <p class="page-subtitle">模块骨架已就绪，业务功能待实现。</p>
      </div>
      <el-tag type="info" effect="plain">待开发</el-tag>
    </div>

    <el-card shadow="never" class="business-card">
      <el-empty :description="`${title}模块尚未实现`">
        <div class="feature-list">
          <div class="feature-title">计划包含的能力</div>
          <div v-for="feature in features" :key="feature" class="feature-item">
            <el-tag size="small" type="success" effect="plain">规划</el-tag>
            <span>{{ feature }}</span>
          </div>
        </div>
      </el-empty>
    </el-card>
  </div>
</template>

<style scoped>
.feature-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
  min-width: 320px;
  text-align: left;
}

.feature-title {
  font-size: 13px;
  color: #9ca3af;
}

.feature-item {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 14px;
  color: #374151;
}
</style>
