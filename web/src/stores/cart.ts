import { computed, ref } from 'vue'
import { defineStore } from 'pinia'
import type { Product } from '../api/types'

export interface CartItem {
  productId: number
  name: string
  unitPrice: number
  quantity: number
  stock: number
}

export const useCartStore = defineStore('cart', () => {
  const items = ref<CartItem[]>([])

  const totalQuantity = computed(() => items.value.reduce((sum, item) => sum + item.quantity, 0))
  const totalAmount = computed(() =>
    items.value.reduce((sum, item) => sum + item.unitPrice * item.quantity, 0),
  )

  function addProduct(product: Product) {
    const existing = items.value.find((item) => item.productId === product.id)
    if (existing) {
      if (existing.quantity < existing.stock) {
        existing.quantity += 1
      }
      return
    }
    if (product.stock <= 0) {
      return
    }
    items.value.push({
      productId: product.id,
      name: product.name,
      unitPrice: Number(product.salePrice),
      quantity: 1,
      stock: product.stock,
    })
  }

  function increase(productId: number) {
    const item = items.value.find((entry) => entry.productId === productId)
    if (item && item.quantity < item.stock) {
      item.quantity += 1
    }
  }

  function decrease(productId: number) {
    const item = items.value.find((entry) => entry.productId === productId)
    if (!item) {
      return
    }
    item.quantity -= 1
    if (item.quantity <= 0) {
      items.value = items.value.filter((entry) => entry.productId !== productId)
    }
  }

  function remove(productId: number) {
    items.value = items.value.filter((entry) => entry.productId !== productId)
  }

  function clear() {
    items.value = []
  }

  return { items, totalQuantity, totalAmount, addProduct, increase, decrease, remove, clear }
})
