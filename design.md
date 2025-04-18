设计一个地铁信息数据库需要考虑以下几个核心实体和它们之间的关系，主要包括：

1. 地铁线路（Line）
2. 车站（Station）
3. 线路方向（Direction / Route）
4. 线路停靠站点（Stop / DirectionStation）
5. 列车运行（Train / TrainTrip）
6. 到站时刻表（StopTime / Timetable）

```sql
CREATE TABLE line (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL,         -- 线路名称，例如“1号线”
    code VARCHAR(20) UNIQUE NOT NULL,  -- 唯一标识
    color VARCHAR(10),                 -- 地图上使用的颜色
    operator VARCHAR(50)               -- 运营公司
);

CREATE TABLE station (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL,         -- 站名
    code VARCHAR(20) UNIQUE,           -- 站点代码
    address VARCHAR(200),              -- 地址
);

CREATE TABLE route (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    line_id BIGINT NOT NULL,
    name VARCHAR(50) NOT NULL,              -- 路线名称
    start_station_id BIGINT,                -- 起点站
    end_station_id BIGINT,                  -- 终点站
);

CREATE TABLE stop (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    route_id BIGINT NOT NULL,
    station_id BIGINT NOT NULL,
    seq INT NOT NULL,                      -- 顺序
);

CREATE TABLE train_trip (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    route_id BIGINT NOT NULL,
    train_number VARCHAR(20) NOT NULL,
    run_date DATE NOT NULL,                -- 哪一天运行
);

CREATE TABLE stop_time (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    train_trip_id BIGINT NOT NULL,
    station_id BIGINT NOT NULL,
    arrival_time TIME,                     -- 到达时间
    departure_time TIME,                   -- 离开时间
    stop_seq INT,                          -- 在当前行程中的顺序
);
```

二、表之间的关系图（ER 简述）
• 一条 line 有多条 route（方向）；
• 每条 route 有多个 stop，每个 stop 关联一个 station；
• 每列车运行一次叫做一个 train_trip；
• 每 train_trip 会生成多个 stop_time，记录该车在每站的时间；

三、UI 展示

```
[选择器区]
线路：  [下拉]   方向：  [下拉]   日期： [日历选择]   （固定）
 └─（可选）站点：[下拉]   车次号：[下拉]

[顶部信息卡片]
→ 当前线路名称、方向、首末班时间

[主区展示]
【车次时刻表】 / 【站点列车列表】（el-table）

[可选功能]
🔍 搜索 🔄 刷新
```
