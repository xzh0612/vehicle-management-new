# Frontend

这个目录是独立前端应用，已经重构为 Vue 3 组件式结构，和 Spring Boot 后端通过 `/api/**` 接口对接。

## 目录说明

- `index.html`：前端入口
- `app.css`：整套视觉主题与布局
- `js/main.js`：Vue 应用装配与页面状态
- `js/components/`：页面组件
- `js/api.js`：接口调用层
- `js/store.js`：本地状态初始化
- `vendor/vue.esm-browser.prod.js`：本地化 Vue 运行时

## 本地运行

1. 启动后端：

```bash
mvn spring-boot:run
```

2. 启动前端静态服务：

```bash
cd frontend
python3 -m http.server 5173
```

3. 访问：

- 前端：[http://localhost:5173](http://localhost:5173)
- 后端 API：[http://localhost:8080/api/health](http://localhost:8080/api/health)

如果后端不在 `http://localhost:8080/api`，可以在页面顶部修改 `API 基地址`。
