# API Documentation - ABT-Bio Backend

## Tổng quan

Backend API ABT-Bio được xây dựng với Spring Boot, sử dụng JWT authentication và OAuth2.

**Base URL:** `http://localhost:8080` (hoặc domain của bạn)

**Authentication:** Bearer Token (JWT) - đặt trong header `Authorization: Bearer <token>`

---

## 1. Authentication API

### 1.1 Đăng nhập
- **Endpoint:** `POST /api/auth/login`
- **Quyền hạn:** Public (không cần đăng nhập)

**Request:**
```json
{
  "email": "user@example.com",
  "password": "password123"
}
```

**Response:**
```json
{
  "code": 0,
  "result": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "refreshToken": "refresh_token_here",
    "isAuthenticated": true
  },
  "message": "OK"
}
```

### 1.2 Đăng ký
- **Endpoint:** `POST /api/auth/register`
- **Quyền hạn:** Public

**Request:**
```json
{
  "email": "user@example.com",
  "password": "password123",
  "fullName": "Nguyen Van A"
}
```

**Response:**
```json
{
  "code": 0,
  "message": "OK"
}
```

### 1.3 Lấy thông tin user hiện tại
- **Endpoint:** `GET /api/auth/me`
- **Quyền hạn:** Yêu cầu đăng nhập

**Response:**
```json
{
  "code": 0,
  "result": {
    "id": "uuid-here",
    "contactEmail": "user@example.com",
    "contactPhone": "0123456789",
    "fullName": "Nguyen Van A",
    "avatarUrl": "https://example.com/avatar.jpg",
    "lastTimeChange": "2024-01-01T00:00:00Z",
    "verified": true,
    "status": "ACTIVE",
    "createdAt": "2024-01-01T00:00:00Z",
    "updatedAt": "2024-01-01T00:00:00Z",
    "role": "USER"
  }
}
```

---

## 2. Product API

### 2.1 Danh sách sản phẩm (có tìm kiếm & lọc)
- **Endpoint:** `GET /api/products`
- **Quyền hạn:** Public

**Query Params:**
- `keyword` (optional) - Từ khóa tìm kiếm
- `categoryId` (optional) - ID danh mục
- `page` (default: 0) - Trang hiện tại
- `size` (default: 20) - Số items mỗi trang

**Response:**
```json
{
  "code": 0,
  "result": [
    {
      "id": 1,
      "name": "Product Name",
      "category": "Electronics",
      "price": 1000000,
      "unit": "cái",
      "image": "https://example.com/product.jpg",
      "description": "Product description",
      "specs": ["spec1", "spec2"],
      "stock": 100,
      "featured": true,
      "slug": "product-name"
    }
  ]
}
```

### 2.2 Chi tiết sản phẩm
- **Endpoint:** `GET /api/products/{id}`
- **Quyền hạn:** Public

**Response:**
```json
{
  "code": 0,
  "result": {
    "id": 1,
    "name": "Product Name",
    "category": "Electronics",
    "price": 1000000,
    "unit": "cái",
    "image": "https://example.com/product.jpg",
    "description": "Product description",
    "specs": ["spec1", "spec2"],
    "stock": 100,
    "featured": true,
    "slug": "product-name"
  }
}
```

### 2.3 Sản phẩm bán chạy
- **Endpoint:** `GET /api/products/best-selling`
- **Quyền hạn:** Public

**Response:** Giống như 2.1

### 2.4 Tạo sản phẩm (Admin)
- **Endpoint:** `POST /admin/products`
- **Quyền hạn:** Admin

**Request:**
```json
{
  "name": "Product Name",
  "slug": "product-slug",
  "detailedDescription": "Detailed description",
  "thumbnailUrl": "https://example.com/thumb.jpg",
  "galleryUrls": ["url1", "url2"],
  "categoryId": 1,
  "inventoryCount": 100,
  "amount": 1000000,
  "originalAmount": 1200000,
  "currency": "VND",
  "supportEmail": "support@example.com",
  "supportTelegram": "telegram_username"
}
```

**Response:** Giống như 2.2

### 2.5 Cập nhật sản phẩm (Admin)
- **Endpoint:** `PUT /admin/products/{id}`
- **Quyền hạn:** Admin

**Request:** Giống như 2.4

### 2.6 Xóa sản phẩm (Admin)
- **Endpoint:** `DELETE /admin/products/{id}`
- **Quyền hạn:** Admin

**Response:**
```json
{
  "code": 0,
  "message": "Deleted"
}
```

---

## 3. Category API

### 3.1 Danh sách danh mục
- **Endpoint:** `GET /api/categories`
- **Quyền hạn:** Public

**Query Params:**
- `page` (default: 0)
- `size` (default: 20)

**Response:**
```json
{
  "code": 0,
  "result": [
    {
      "id": 1,
      "name": "Electronics",
      "slug": "electronics",
      "image": "https://example.com/cat.jpg",
      "description": "Category description",
      "status": "ACTIVE",
      "productCount": 50
    }
  ]
}
```

### 3.2 Tạo danh mục (Admin)
- **Endpoint:** `POST /admin/categories`
- **Quyền hạn:** Admin

**Request:**
```json
{
  "name": "Electronics",
  "slug": "electronics",
  "image": "https://example.com/cat.jpg",
  "description": "Category description",
  "status": "ACTIVE",
  "productCount": 0
}
```

### 3.3 Cập nhật danh mục (Admin)
- **Endpoint:** `PUT /admin/categories/{id}`
- **Quyền hạn:** Admin

### 3.4 Xóa danh mục (Admin)
- **Endpoint:** `DELETE /admin/categories/{id}`
- **Quyền hạn:** Admin

---

## 4. Cart API

### 4.1 Xem giỏ hàng
- **Endpoint:** `GET /api/cart`
- **Quyền hạn:** Yêu cầu đăng nhập

**Response:**
```json
{
  "code": 0,
  "result": [
    {
      "product": {
        "id": 1,
        "name": "Product Name",
        "price": 1000000,
        "image": "https://example.com/product.jpg",
        "stock": 100
      },
      "quantity": 2
    }
  ]
}
```

### 4.2 Thêm vào giỏ hàng
- **Endpoint:** `POST /api/cart`
- **Quyền hạn:** Yêu cầu đăng nhập

**Request:**
```json
{
  "productId": 1,
  "quantity": 2
}
```

**Response:** Giống như 4.1 (trả về giỏ hàng sau khi thêm)

### 4.3 Xóa item theo index
- **Endpoint:** `DELETE /api/cart/{itemIndex}`
- **Quyền hạn:** Yêu cầu đăng nhập

**Response:** Giống như 4.1

### 4.4 Xóa item theo productId
- **Endpoint:** `DELETE /api/cart/product/{productId}`
- **Quyền hạn:** Yêu cầu đăng nhập

**Response:** Giống như 4.1

---

## 5. Order API

### 5.1 Checkout (Tạo đơn hàng)
- **Endpoint:** `POST /api/cart/checkout`
- **Quyền hạn:** Yêu cầu đăng nhập

**Request:**
```json
{
  "customerName": "Nguyen Van A",
  "email": "user@example.com",
  "phone": "0123456789",
  "address": "123 Street, City",
  "organization": "Company Name",
  "paymentMethod": "VIETQR",
  "items": [
    {
      "productId": 1,
      "quantity": 2,
      "priceAtOrder": 1000000
    }
  ],
  "total": 2000000,
  "notes": "Note here"
}
```

**Response:**
```json
{
  "code": 0,
  "result": {
    "id": "order-uuid",
    "orderCode": "ORD-123456",
    "date": "2024-01-01T00:00:00Z",
    "customerName": "Nguyen Van A",
    "email": "user@example.com",
    "phone": "0123456789",
    "address": "123 Street, City",
    "organization": "Company Name",
    "paymentMethod": "VIETQR",
    "items": [
      {
        "productId": 1,
        "productName": "Product Name",
        "quantity": 2,
        "price": 1000000
      }
    ],
    "status": "PENDING",
    "total": 2000000,
    "paymentStatus": "UNPAID",
    "notes": "Note here"
  },
  "message": "Checkout created"
}
```

### 5.2 Danh sách đơn hàng của tôi
- **Endpoint:** `GET /api/orders`
- **Quyền hạn:** Yêu cầu đăng nhập

**Query Params:**
- `page` (default: 0)
- `size` (default: 20)

**Response:** Array of OrderResponse

### 5.3 Đơn hàng đã hoàn thành của tôi
- **Endpoint:** `GET /api/my-completed`
- **Quyền hạn:** Yêu cầu đăng nhập (User)

**Query Params:**
- `page` (default: 0)
- `size` (default: 20)

**Response:** Array of OrderResponse (chỉ các đơn PAID hoặc COMPLETED)

### 5.4 Tất cả đơn hàng đã hoàn thành (Admin)
- **Endpoint:** `GET /api/admin/completed`
- **Quyền hạn:** Admin only

**Query Params:**
- `page` (default: 0)
- `size` (default: 20)

**Response:** Array of OrderResponse (tất cả các đơn PAID hoặc COMPLETED của mọi user)

---

## 6. Payment API

### 6.1 Tạo giao dịch thanh toán (PayOS)
- **Endpoint:** `POST /api/payments/payos/create`
- **Quyền hạn:** Yêu cầu đăng nhập

**Request:**
```json
{
  "orderId": "order-uuid",
  "amount": 2000000,
  "currency": "VND",
  "provider": "PAYOS",
  "paymentMethod": "VIETQR"
}
```

**Response:**
```json
{
  "code": 200,
  "message": "Tạo giao dịch PayOS thành công",
  "result": {
    "transactionId": "transaction-uuid",
    "transactionCode": "TX-123456",
    "amount": 2000000,
    "currency": "VND",
    "provider": "PAYOS",
    "checkoutUrl": "https://payos.vn/checkout/...",
    "expiresAt": "2024-01-01T01:00:00Z",
    "qrCode": "data:image/png;base64,..."
  }
}
```

### 6.2 Kiểm tra trạng thái thanh toán
- **Endpoint:** `GET /api/payments/status/{transactionId}`
- **Quyền hạn:** Yêu cầu đăng nhập

**Response:**
```json
{
  "code": 200,
  "message": "Lấy trạng thái thành công",
  "result": "PAID"
}
```

### 6.3 Webhook PayOS
- **Endpoint:** `POST /api/payments/webhook/payos`
- **Quyền hạn:** Public (PayOS gọi vào)

**Request:** Raw body từ PayOS

**Response:**
```json
{
  "code": 200,
  "message": "OK"
}
```

---

## 7. User Management API

### 7.1 Danh sách users (Admin)
- **Endpoint:** `GET /api/admin/users`
- **Quyền hạn:** Admin

**Query Params:**
- `page` (default: 0)
- `size` (default: 20)

**Response:**
```json
{
  "code": 0,
  "result": [
    {
      "id": "user-uuid",
      "contactEmail": "user@example.com",
      "contactPhone": "0123456789",
      "fullName": "Nguyen Van A",
      "avatarUrl": "https://example.com/avatar.jpg",
      "lastTimeChange": "2024-01-01T00:00:00Z",
      "verified": true,
      "status": "ACTIVE",
      "createdAt": "2024-01-01T00:00:00Z",
      "updatedAt": "2024-01-01T00:00:00Z",
      "role": "USER"
    }
  ]
}
```

### 7.2 Chi tiết user (Admin)
- **Endpoint:** `GET /api/admin/users/{id}`
- **Quyền hạn:** Admin

**Response:** Giống như 7.1 (single object)

---

## Bảng Quyền Hạn

| API Endpoint | GET (Public) | POST (Public) | PUT/PATCH/DELETE | User | Admin |
|--------------|--------------|---------------|-------------------|------|-------|
| `/api/auth/login` | ✅ Public | ✅ Public | - | - | - |
| `/api/auth/register` | - | ✅ Public | - | - | - |
| `/api/auth/me` | ✅ | - | - | ✅ | ✅ |
| `/api/products` | ✅ Public | - | - | - | - |
| `/api/products/{id}` | ✅ Public | - | - | - | - |
| `/api/products/best-selling` | ✅ Public | - | - | - | - |
| `/admin/products` | - | ✅ Admin | ✅ Admin | - | ✅ |
| `/admin/products/{id}` | - | - | ✅ Admin (PUT/DELETE) | - | ✅ |
| `/api/categories` | ✅ Public | - | - | - | - |
| `/admin/categories` | - | ✅ Admin | ✅ Admin (PUT/DELETE) | - | ✅ |
| `/api/cart` | ✅ | ✅ | ✅ (DELETE) | ✅ | ✅ |
| `/api/cart/checkout` | - | ✅ | - | ✅ | ✅ |
| `/api/orders` | ✅ | - | - | ✅ | ✅ |
| `/api/my-completed` | ✅ | - | - | ✅ | - |
| `/api/admin/completed` | ✅ Admin only | - | - | - | ✅ |
| `/api/payments/payos/create` | - | ✅ | - | ✅ | ✅ |
| `/api/payments/status/{id}` | ✅ | - | - | ✅ | ✅ |
| `/api/payments/webhook/**` | - | ✅ Public (Webhook) | - | - | - |
| `/api/admin/users` | ✅ Admin | - | - | - | ✅ |
| `/api/admin/users/{id}` | ✅ Admin | - | - | - | ✅ |

## Roles

- **USER**: Người dùng thường, có thể mua hàng, xem đơn hàng của mình
- **ADMIN**: Quản trị viên, có full quyền hạn including quản lý users, products, categories, orders

## Common Error Codes

- `0` - Success
- `1` - Not found
- `401` - Unauthorized (Token không hợp lệ hoặc hết hạn)
- `403` - Forbidden (Không có quyền truy cập)
- `503` - Service Unavailable (Hệ thống bảo trì)

## Notes

1. Tất cả API response đều có format:
   ```json
   {
     "code": 0,
     "result": {...},
     "message": "OK"
   }
   ```

2. Pagination:
   - `page`: bắt đầu từ 0
   - `size`: mặc định 20 items/trang

3. Authentication:
   - Sử dụng JWT Bearer Token
   - Token trong header: `Authorization: Bearer <token>`
   - Token có thể bị blacklist (logout)

4. Date Format:
   - Sử dụng ISO 8601: `2024-01-01T00:00:00Z`

5. Currency:
   - `VND` (Vietnam Dong)
   - `USD` (US Dollar)
