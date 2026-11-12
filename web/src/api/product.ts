import http from './http'
import type { ApiResponse, Category, PageResult, Product, ProductPayload, ProductQuery } from './types'

export async function fetchProducts(query: ProductQuery): Promise<PageResult<Product>> {
  const response = await http.get<ApiResponse<PageResult<Product>>>('/products', { params: query })
  return response.data.data
}

export async function fetchCategories(): Promise<Category[]> {
  const response = await http.get<ApiResponse<Category[]>>('/categories')
  return response.data.data
}

export async function createProduct(payload: ProductPayload): Promise<number> {
  const response = await http.post<ApiResponse<number>>('/products', payload)
  return response.data.data
}

export async function updateProduct(id: number, payload: ProductPayload): Promise<void> {
  await http.put<ApiResponse<void>>(`/products/${id}`, payload)
}

export async function deleteProduct(id: number): Promise<void> {
  await http.delete<ApiResponse<void>>(`/products/${id}`)
}
