我来帮你整理所有接口的文档。

# 点餐系统 API 文档

## 1. 用户管理接口
### 1.1 微信登录
```http
POST /api/user/wx-login
```
请求参数：
```json
{
  "code": "wx_login_code"
}
```
响应数据：
```json
{
  "user": {
    "id": 1,
    "openId": "wx_open_id",
    "nickname": "用户昵称",
    "avatarUrl": "头像URL",
    "gender": 1,
    "country": "国家",
    "province": "省份",
    "city": "城市"
  },
  "token": "登录令牌",
  "openId": "wx_open_id",
  "sessionKey": "会话密钥"
}
```

### 1.2 更新用户信息
```http
PUT /api/user/info
```
请求参数：
```json
{
  "openId": "wx_open_id",
  "nickname": "新昵称",
  "avatarUrl": "新头像URL",
  "gender": 1,
  "country": "国家",
  "province": "省份",
  "city": "城市"
}
```
响应数据：
```json
{
  "id": 1,
  "openId": "wx_open_id",
  "nickname": "新昵称",
  "avatarUrl": "新头像URL",
  "gender": 1,
  "country": "国家",
  "province": "省份",
  "city": "城市"
}
```

## 2. 菜品管理接口
### 2.1 获取所有菜品
```http
GET /api/menu-items
```
响应数据：
```json
[
  {
    "id": 1,
    "name": "红烧肉",
    "description": "美味的红烧肉",
    "price": 28.00,
    "imageUrl": "图片URL",
    "available": true
  }
]
```

### 2.2 获取单个菜品详情
```http
GET /api/menu-items/{id}
```
响应数据：
```json
{
  "id": 1,
  "name": "红烧肉",
  "description": "美味的红烧肉",
  "price": 28.00,
  "imageUrl": "图片URL",
  "available": true
}
```

## 3. 购物车接口
### 3.1 获取购物车
```http
GET /api/cart/{userId}
```
响应数据：
```json
[
  {
    "id": 1,
    "menuItemId": 1,
    "name": "红烧肉",
    "price": 28.00,
    "quantity": 2
  }
]
```

### 3.2 添加到购物车
```http
POST /api/cart/add
```
请求参数：
```json
{
  "userId": 1,
  "menuItemId": 1,
  "quantity": 1
}
```
响应数据：
```json
{
  "id": 1,
  "menuItemId": 1,
  "name": "红烧肉",
  "price": 28.00,
  "quantity": 1
}
```

### 3.3 更新购物车数量
```http
PUT /api/cart/update
```
请求参数：
```json
{
  "userId": 1,
  "menuItemId": 1,
  "quantity": 2
}
```
响应数据：
```json
{
  "id": 1,
  "menuItemId": 1,
  "name": "红烧肉",
  "price": 28.00,
  "quantity": 2
}
```

### 3.4 删除购物车项
```http
DELETE /api/cart/{userId}/{menuItemId}
```

## 4. 订单管理接口
### 4.1 创建订单
```http
POST /api/orders/create
```
请求参数：
```json
{
  "userId": 1
}
```
响应数据：
```json
{
  "id": 1,
  "orderNumber": "ORDER1234567890",
  "userId": 1,
  "totalAmount": 56.00,
  "status": "PENDING",
  "remark": "备注信息",
  "orderItems": [
    {
      "menuItemId": 1,
      "itemName": "红烧肉",
      "price": 28.00,
      "quantity": 2
    }
  ],
  "createTime": "2024-03-20 14:30:00"
}
```

### 4.2 支付订单
```http
POST /api/orders/{orderId}/pay
```
响应数据：
```json
{
  "id": 1,
  "orderNumber": "ORDER1234567890",
  "status": "PAID",
  "totalAmount": 56.00
}
```

### 4.3 获取用户订单列表
```http
GET /api/orders/user/{userId}
```
响应数据：
```json
[
  {
    "id": 1,
    "orderNumber": "ORDER1234567890",
    "totalAmount": 56.00,
    "status": "PAID",
    "orderItems": [
      {
        "menuItemId": 1,
        "itemName": "红烧肉",
        "price": 28.00,
        "quantity": 2
      }
    ],
    "createTime": "2024-03-20 14:30:00"
  }
]
```

## 5. 商家订单管理接口
### 5.1 获取待处理订单
```http
GET /api/manage/orders/pending
```
响应数据：
```json
[
  {
    "id": 1,
    "orderNumber": "ORDER1234567890",
    "totalAmount": 56.00,
    "status": "PAID",
    "orderItems": [
      {
        "menuItemId": 1,
        "itemName": "红烧肉",
        "price": 28.00,
        "quantity": 2
      }
    ],
    "createTime": "2024-03-20 14:30:00"
  }
]
```

### 5.2 开始处理订单
```http
POST /api/manage/orders/{orderId}/process
```
响应数据：
```json
{
  "id": 1,
  "orderNumber": "ORDER1234567890",
  "status": "PREPARING",
  "totalAmount": 56.00
}
```

### 5.3 完成订单
```http
POST /api/manage/orders/{orderId}/complete
```
响应数据：
```json
{
  "id": 1,
  "orderNumber": "ORDER1234567890",
  "status": "COMPLETED",
  "totalAmount": 56.00
}
```

### 5.4 获取今日订单
```http
GET /api/manage/orders/today
```
响应数据：
```json
[
  {
    "id": 1,
    "orderNumber": "ORDER1234567890",
    "totalAmount": 56.00,
    "status": "COMPLETED",
    "orderItems": [
      {
        "menuItemId": 1,
        "itemName": "红烧肉",
        "price": 28.00,
        "quantity": 2
      }
    ],
    "createTime": "2024-03-20 14:30:00"
  }
]
```

所有接口都需要在请求头中添加认证信息：
```http
Authorization: Bearer {token}
```

错误响应格式：
```json
{
  "code": 400,
  "message": "错误信息",
  "timestamp": "2024-03-20 14:30:00"
}
```

需要我详细解释某个接口吗？
