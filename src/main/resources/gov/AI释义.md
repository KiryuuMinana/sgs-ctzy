# AI释义文档

## 迭代记录

### v1.0.0 - 初始版本 (2026-06-08)

- 初始化Spring Boot项目结构（由Maven WAR改为JAR）
- 实现4个预组数据配置（魏/蜀/吴/群）
- 实现模拟器核心抽卡逻辑
- 实现REST API接口
- 实现前端三国主题UI页面

---

## 功能模块释义

### M1: 数据模型层 (`org.example.sgs.model`)

| 类 | 职责 | 关键说明 |
|---|---|---|
| `GeneralCard` | 武将卡实体 | id格式: `{faction}_{name}_{index}`，faction为魏/蜀/吴/群 |
| `PresetDeck` | 预组实体 | 包含卡牌列表，totalCount=卡牌数 |
| `GameSession` | 对局会话状态 | 存储于HttpSession，含双方军营/已上阵/回合状态 |
| `DrawResult` | 抽卡结果 | 含drawnCards、finished标志、双方剩余/已上阵数据 |

**GameSession状态流转**:
- `nextIsFirstPlayer`: 标识下一步行动方
- `isFirstTurn`: 仅先手首回合为true，控制首回合抽1张
- 回合数递增时机：后手行动完毕后+1（一轮=先手+后手各行动一次）

### M2: 配置层 (`org.example.sgs.config`)

| 类 | 职责 | 关键说明 |
|---|---|---|
| `DeckConfig` | 预组数据工厂 | @PostConstruct初始化4个预组，每个预组39张卡 |
| `ImageConfig` | 图片路径配置 | **换图口子**：修改CARD_IMAGE_BASE_PATH或CARD_IMAGE_MAP即可更换图片 |

**预组卡牌数量**:
- 魏国(曹操): 13种×3=39
- 蜀国(刘备): 刘备×2+孙乾×3+马云禄×1+其余10种×3=39
- 吴国(孙尚香): 13种×3=39
- 群雄(貂蝉): 13种×3=39

**ImageConfig换图方式**:
1. 全局: 修改`CARD_IMAGE_BASE_PATH`（默认`/images/cards/`）
2. 单卡: 在`CARD_IMAGE_MAP`中添加`武将名→路径`映射
3. 支持本地路径（static下）和外部URL
4. 图片加载失败时前端自动显示首字占位

### M3: 服务层 (`org.example.sgs.service`)

| 类 | 职责 | 关键说明 |
|---|---|---|
| `SimulationService` | 模拟器核心逻辑 | 随机抽卡、回合管理、结束判定 |

**抽卡逻辑要点**:
- `randomDraw()`: 从军营浅拷贝中随机取牌，返回的卡牌对象与军营中同一引用
- `camp.removeAll(drawn)`: 依赖引用相等性移除（GeneralCard未重写equals）
- 先手首回合: `getDrawCount()`返回1，其余回合返回2
- 武将不足判定: `actualDraw < drawCount`时标记finished并提示

### M4: 控制器层 (`org.example.sgs.controller`)

| 类 | 端点 | 说明 |
|---|---|---|
| `ApiController` | `GET /api/decks` | 获取4个预组列表 |
| | `POST /api/simulation/start` | 开启模拟（需firstPlayerDeckId+secondPlayerDeckId） |
| | `POST /api/simulation/next` | 执行下一步抽卡 |
| | `GET /api/simulation/state` | 查询当前状态 |
| | `POST /api/simulation/reset` | 重置模拟 |

**会话管理**: 游戏状态存储在`HttpSession`的`gameSession`属性中，无数据库。

### M5: 前端页面 (`static/index.html`)

单文件HTML+CSS+JS，无外部框架依赖。

**UI结构**:
- 上方: 先手玩家区（金色主题）
- 下方: 后手玩家区（紫色主题）
- 中间: 控制按钮 + 状态栏 + 战斗日志
- 背景: Canvas粒子动画 + CSS径向渐变

**势力颜色映射**: 魏=#4a90d9(蓝), 蜀=#d94a4a(红), 吴=#4ad97a(绿), 群=#9b59b6(紫)

**前端交互流程**:
1. 下拉框选预组 → 启用"开启模拟"按钮
2. 点击"开启模拟" → 禁用下拉框，启用"下一步"
3. 点击"下一步" → POST /next → 渲染卡牌动画+更新状态+添加日志
4. 武将不足 → 禁用"下一步"，Toast提示
5. "重置" → 清空所有状态

**动画效果**: 卡牌翻转出现(cardAppear) + 新卡发光(cardGlow) + 全屏闪光(drawFlash) + 日志滑入(logAppear)

---

## 功能间关联影响描述

### 修改预组数据时
- 影响文件: `DeckConfig.java`
- 关联影响: `ImageConfig`(图片路径)、前端下拉框选项（通过`/api/decks`接口自动同步）、`GameSession`(军营初始化)
- 注意: 卡牌总数必须=39，否则军营进度条显示异常

### 修改抽卡逻辑时
- 影响文件: `SimulationService.java`
- 关联影响: `GameSession`(状态流转)、`DrawResult`(返回数据)、前端nextStep()函数、战斗日志
- 注意: `randomDraw`依赖引用相等，不可改为深拷贝

### 修改图片路径时
- 影响文件: `ImageConfig.java`
- 关联影响: `DeckConfig`(初始化时调用)、前端`createCardElement()`(img.src使用imagePath)
- 注意: 修改后需重新编译，前端图片加载失败有容错处理

### 修改前端UI时
- 影响文件: `static/index.html`（CSS/JS均在此文件）
- 关联影响: 无后端影响，但需同步维护势力颜色映射（CSS变量与JS factionMap需一致）
