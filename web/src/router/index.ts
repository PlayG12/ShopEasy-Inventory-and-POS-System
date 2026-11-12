import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/',
      component: () => import('../layout/MainLayout.vue'),
      redirect: '/dashboard',
      children: [
        {
          path: 'dashboard',
          name: 'dashboard',
          component: () => import('../views/DashboardView.vue'),
          meta: { title: '经营概览' },
        },
        {
          path: 'pos',
          name: 'pos',
          component: () => import('../views/PosView.vue'),
          meta: { title: '收银台' },
        },
        {
          path: 'products',
          name: 'products',
          component: () => import('../views/ProductView.vue'),
          meta: { title: '商品管理' },
        },
        {
          path: 'purchases',
          name: 'purchases',
          component: () => import('../views/PlaceholderView.vue'),
          props: { title: '采购入库', features: ['采购单创建与审核', '到货入库并生成库存流水', '供应商往来与采购价记录'] },
          meta: { title: '采购入库' },
        },
        {
          path: 'inventory',
          name: 'inventory',
          component: () => import('../views/PlaceholderView.vue'),
          props: { title: '库存流水', features: ['全部出入库流水查询', '库存盘点与调整', '库存预警与效期提醒'] },
          meta: { title: '库存流水' },
        },
        {
          path: 'members',
          name: 'members',
          component: () => import('../views/PlaceholderView.vue'),
          props: { title: '会员管理', features: ['会员建档与手机号检索', '积分累计与消费记录', '会员价与储值'] },
          meta: { title: '会员管理' },
        },
        {
          path: 'reports',
          name: 'reports',
          component: () => import('../views/PlaceholderView.vue'),
          props: { title: '经营报表', features: ['日 / 周 / 月销售统计', '商品销量与毛利排行', '收银员交班对账'] },
          meta: { title: '经营报表' },
        },
        {
          path: 'settings',
          name: 'settings',
          component: () => import('../views/PlaceholderView.vue'),
          props: { title: '系统设置', features: ['门店信息与小票样式', '员工账号与角色权限', '数据备份与恢复'] },
          meta: { title: '系统设置' },
        },
      ],
    },
    { path: '/:pathMatch(.*)*', redirect: '/' },
  ],
})

export default router
