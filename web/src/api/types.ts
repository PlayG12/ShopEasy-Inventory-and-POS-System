export interface ApiResponse<T> {
  code: number
  message: string
  data: T
}

export interface PageResult<T> {
  records: T[]
  total: number
  page: number
  size: number
}

export interface Category {
  id: number
  name: string
  sort: number
}

export interface Product {
  id: number
  sku: string
  barcode: string | null
  name: string
  categoryId: number
  categoryName: string
  unit: string
  purchasePrice: number
  salePrice: number
  stock: number
  warningStock: number
  status: number
  remark: string | null
  createdAt: string
}

export interface ProductPayload {
  sku: string
  barcode?: string
  name: string
  categoryId: number | null
  unit: string
  purchasePrice: number
  salePrice: number
  warningStock: number
  status: number
  remark?: string
}

export interface ProductQuery {
  page: number
  size: number
  keyword?: string
  categoryId?: number
}
