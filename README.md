# 车辆管理系统（Hadoop + HBase + Redis + JWT + Vue）

基于 Spring Boot 的车辆管理示例系统，核心数据落在 HBase，统计结果写入 HDFS，登录态使用 Redis。

## 1. 项目功能介绍

### 1.1 核心能力

- 用户注册、登录、登出（JWT + Redis 登录态）
- RBAC 权限控制（`ADMIN` / `USER`）
- 车辆信息增删改查
- 车辆统计信息查询
- 统计结果上传到 HDFS
- 审计日志记录与查询
- 前端页面联调（静态页面）

### 1.2 角色权限

- `USER`：新增车辆、修改车辆、查询车辆、查看统计、查看审计日志
- `ADMIN`：包含 `USER` 全部权限，额外支持删除车辆、上传统计到 HDFS、创建管理员用户

### 1.3 技术栈

- 后端：Spring Boot 3.x、Java 17+
- 存储：HBase
- 分布式文件系统：HDFS
- 缓存/会话：Redis
- 认证：JWT
- 前端：Vue（静态资源）

## 2. 运行环境

本文档按以下环境编写：

- 虚拟机 IP：`10.211.55.3`
- ZooKeeper：`10.211.55.3:2181`
- HBase RPC：`10.211.55.3:16020`
- HDFS：`hdfs://10.211.55.3:9000`

## 3. 启动前检查

### 3.1 本机依赖

```bash
java -version
mvn -version
redis-server --version
```

### 3.2 虚拟机大数据组件状态

```bash
jps
ss -lntp | egrep '2181|9000|16000|16010|16020|16030|9870'
```

期望至少包含以下进程：

- `NameNode`
- `DataNode`
- `SecondaryNameNode`
- `HMaster`
- `HRegionServer`
- `HQuorumPeer`

期望关键端口可见：

- `2181`
- `9000`
- `16000`
- `16010`
- `16020`

### 3.3 本机到虚拟机连通性验证

```bash
nc -vz 10.211.55.3 2181
nc -vz 10.211.55.3 16020
nc -vz 10.211.55.3 9000
```

## 4. 启动步骤

### 4.1 启动 Redis（本机）

```bash
redis-server
```

或后台启动：

```bash
redis-server --daemonize yes
```

### 4.2 启动 Hadoop/HBase（虚拟机）

```bash
start-dfs.sh
start-hbase.sh
```

### 4.3 启动后端（本机）

```bash
cd /Users/yzh/Documents/vehicle-management

VM_HOST=10.211.55.3 \
HDFS_PORT=9000 \
ZK_PORT=2181 \
REDIS_HOST=127.0.0.1 \
JWT_SECRET='YourStrongSecretAtLeast32Chars_123456' \
mvn spring-boot:run
```

## 5. 启动后访问

- 健康检查：`http://localhost:8080/api/health`
- 前端首页：`http://localhost:8080/`
- HBase Master UI：`http://10.211.55.3:16010`
- HDFS NameNode UI：`http://10.211.55.3:9870`

## 6. 终端 HBase 操作示例（替代 curl）

以下操作全部在虚拟机中执行 `hbase shell` 完成。

### 6.1 启动 HBase Shell

```bash
hbase shell
```

### 6.2 用户数据（模拟注册后的存储）

```ruby
put 'users', 'admin', 'info:password', '123456'
put 'users', 'admin', 'info:role', 'ADMIN'
put 'users', 'user01', 'info:password', '123456'
put 'users', 'user01', 'info:role', 'USER'

get 'users', 'admin'
scan 'users'
```

### 6.3 车辆数据（模拟新增车辆）

```ruby
put 'vehicles', 'vehicle_001', 'info:plateNumber', '沪A12345'
put 'vehicles', 'vehicle_001', 'info:brand', 'BYD'
put 'vehicles', 'vehicle_001', 'info:model', 'Han'
put 'vehicles', 'vehicle_001', 'info:ownerName', 'Zhang San'
put 'vehicles', 'vehicle_001', 'info:phone', '13800000000'
put 'vehicles', 'vehicle_001', 'info:status', 'ACTIVE'

put 'vehicles', 'vehicle_002', 'info:plateNumber', '沪B67890'
put 'vehicles', 'vehicle_002', 'info:brand', 'Tesla'
put 'vehicles', 'vehicle_002', 'info:model', 'Model 3'
put 'vehicles', 'vehicle_002', 'info:ownerName', 'Li Si'
put 'vehicles', 'vehicle_002', 'info:phone', '13900000000'
put 'vehicles', 'vehicle_002', 'info:status', 'MAINTENANCE'

get 'vehicles', 'vehicle_001'
scan 'vehicles'
```

### 6.4 审计日志数据（模拟系统审计）

```ruby
put 'vehicle_audit', 'vehicle_001_20260420190000', 'info:vehicleId', 'vehicle_001'
put 'vehicle_audit', 'vehicle_001_20260420190000', 'info:action', 'CREATE'
put 'vehicle_audit', 'vehicle_001_20260420190000', 'info:operator', 'admin'
put 'vehicle_audit', 'vehicle_001_20260420190000', 'info:timestamp', '2026-04-20T19:00:00+08:00'

put 'vehicle_audit', 'vehicle_002_20260420190500', 'info:vehicleId', 'vehicle_002'
put 'vehicle_audit', 'vehicle_002_20260420190500', 'info:action', 'UPDATE_STATUS'
put 'vehicle_audit', 'vehicle_002_20260420190500', 'info:operator', 'admin'
put 'vehicle_audit', 'vehicle_002_20260420190500', 'info:timestamp', '2026-04-20T19:05:00+08:00'

scan 'vehicle_audit'
```

### 6.5 常用查询/修改/删除

```ruby
# 查询单行
get 'vehicles', 'vehicle_002'

# 修改状态
put 'vehicles', 'vehicle_002', 'info:status', 'ACTIVE'

# 删除单行
deleteall 'vehicles', 'vehicle_002'
```

### 6.6 一键导入测试数据

仓库提供了测试数据脚本 [hbase-seed.hql](/Users/yzh/Documents/vehicle-management/hbase-seed.hql)。

在虚拟机中执行：

```bash
hbase shell /path/to/hbase-seed.hql
```

## 7. 数据查看

### 7.1 HBase

```bash
hbase shell
scan 'users'
scan 'vehicles'
scan 'vehicle_audit'
```

### 7.2 HDFS

```bash
hdfs dfs -ls /vehicle-management/stats
hdfs dfs -cat /vehicle-management/stats/文件名.json
```

## 8. 库表设计说明

本项目核心数据表为 `users`、`vehicles`、`vehicle_audit`，统一使用列族 `info`。

### 8.1 users（用户表）

- 表名：`users`
- RowKey：`username`
- 列族：`info`
- 主要字段：
- `info:password`：密码（建议生产中存哈希值）
- `info:role`：角色（`ADMIN` / `USER`）

设计说明：

- 以 `username` 作为 RowKey，便于登录时按用户名直接 `get`，查询路径最短。
- 用户数据字段相对固定，单列族即可满足需求。

### 8.2 vehicles（车辆表）

- 表名：`vehicles`
- RowKey：`vehicleId`
- 列族：`info`
- 主要字段：
- `info:plateNumber`：车牌号
- `info:brand`：品牌
- `info:model`：型号
- `info:ownerName`：车主姓名
- `info:phone`：联系电话
- `info:status`：车辆状态（如 `ACTIVE`、`MAINTENANCE`）

设计说明：

- 以 `vehicleId` 作为唯一主键，适合高频单车查询与更新。
- HBase 天然更适合按 RowKey 访问；若要高效按品牌/状态检索，建议后续增加二级索引表。

### 8.3 vehicle_audit（审计日志表）

- 表名：`vehicle_audit`
- RowKey：`{vehicleId}_{yyyyMMddHHmmss}`
- 列族：`info`
- 主要字段：
- `info:vehicleId`：车辆 ID
- `info:action`：操作类型（`CREATE`、`UPDATE_STATUS`、`DELETE` 等）
- `info:operator`：操作人
- `info:timestamp`：操作时间（ISO8601）

设计说明：

- 审计是典型时序数据，RowKey 采用“业务主键 + 时间”方便按车辆维度追踪变更历史。
- 可通过 `PrefixFilter(vehicleId_)` 快速获取某车辆全部操作记录。
- 如日志规模增大，可考虑 RowKey 加盐（salt）避免写热点。

### 8.4 统计文件（HDFS）

- 路径：`/vehicle-management/stats`
- 内容：统计结果 JSON 文件
- 用途：归档统计快照，支持离线分析与追溯

设计说明：

- HBase 负责在线读写，HDFS 负责低成本持久化统计产物，分工清晰。
- 建议文件名包含时间戳（如 `stats-20260420-190500.json`）以避免覆盖。

## 9. 常见问题

### 8.1 报错 `No meta znode available`

说明 HBase 元数据未就绪，排查：

- `HMaster` 是否启动
- `HRegionServer` 是否启动
- 客户端配置的 `hbase.zookeeper.znode.parent` 是否与集群一致

### 8.2 宿主机连不上 `16020` 或 `9000`

一般是虚拟机服务绑定在回环地址。确认配置后重启：

- HBase 的 `hbase.regionserver.ipc.address`
- HBase 的 `hbase.regionserver.hostname`
- HDFS 的 `dfs.namenode.rpc-bind-host`
- HDFS 的 `dfs.namenode.rpc-address`

### 8.3 端口已监听但应用仍连接失败

优先检查：

- 宿主机 `nc -vz` 到对应端口是否成功
- 虚拟机 `jps` 是否有完整进程（尤其 `HMaster` 与 `NameNode`）
- 启动命令中的 `VM_HOST`、`HDFS_PORT`、`ZK_PORT` 是否正确

## 10. 建议

- 生产环境请替换高强度 `JWT_SECRET`
- 不建议在生产中使用默认账号或弱密码
- 启动后优先访问 `/api/health` 再进行业务联调
