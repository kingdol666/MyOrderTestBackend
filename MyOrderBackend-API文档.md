# 智慧食堂系统 API 文档

## 用户相关接口

### 微信登录
`POST /api/user/wx-login`
```json
请求参数：
{
  "code": "微信登录凭证"
}
响应示例：
{
  "token": "JWT令牌",
  "user": {用户信息},
  "openId": "微信openId",
  "sessionKey": "微信session_key"
}
```

### 更新用户信息
`POST /api/user/wx-update-info`
```json
请求体：
{
  "openId": "用户openId",
  "userInfo": {用户信息对象}
}
```

## 菜品相关接口

### 获取所有菜品
`GET /api/menu-items`

### 获取单个菜品详情
`GET /api/menu-items/{id}`

### 获取推荐菜品
`GET /api/menu-items/recommend`

### 获取热销菜品
`GET /api/menu-items/hot`

### 根据分类获取菜品
`GET /api/menu-items/category/{categoryId}`

### 搜索菜品
`GET /api/menu-items/search?keyword={关键词}`

## 购物车相关接口

### 获取购物车
`GET /api/cart/{userId}`

### 保存购物车项
`POST /api/cart/save`
```json
请求体：
{
  "userId": 用户ID,
  "menuItemId": 菜品ID,
  "quantity": 数量
}
```

### 删除购物车项
`DELETE /api/cart/{userId}/{menuItemId}`

### 清空购物车
`DELETE /api/cart/{userId}`

## 订单相关接口

### 创建订单
`POST /api/orders/create?userId={用户ID}`

### 获取订单详情
`GET /api/orders/{orderId}`

### 支付订单
`POST /api/orders/{orderId}/pay`

### 获取用户订单列表
`GET /api/orders/user/{userId}`

### 获取微信支付参数
`GET /api/orders/{orderId}/wx-pay-params`

## 分类管理接口

### 获取所有分类
`GET /api/categories?page=0&size=10`

### 创建分类
`POST /api/categories`

oss:
endpoint: oss-cn-hangzhou.aliyuncs.com
accessKeyId: LTAI5tKYi2j4girAc1hEuAqL
accessKeySecret: BZFTiKHkzN4VArVpaxHvb18efnBmtY
bucketName: web-tlias-kingdol