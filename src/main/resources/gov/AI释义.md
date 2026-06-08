# AI释义文档

## 迭代记录

### v1.0.0 - 初始版本 (2026-06-08)

- 初始化Spring Boot项目结构（由Maven WAR改为JAR）
- 实现4个预组数据配置（魏/蜀/吴/群）
- 实现模拟器核心抽卡逻辑
- 实现REST API接口
- 实现前端三国主题UI页面

### v1.1.0 - 休整区/乐不思蜀/撤回/主帅卡/技能弹窗 (2026-06-08)

- 新增休整区功能：战场武将可置入休整区，休整区武将可恢复至战场
- 新增乐不思蜀状态：战场武将可标记乐不思蜀，进入休整区时自动消失
- 新增战场武将上限校验：>5时阻止抽卡
- 新增主帅卡自动生成：选预组后主帅卡自动出现在战场
- 新增武将技能弹窗：点击武将卡显示技能描述+图片（预留口子）
- 新增撤回功能：撤回上一步抽卡，保留同一批武将信息
- 新增SkillConfig配置类（技能描述/图片预留口子）

### v1.1.1 - Bug修复 (2026-06-08)

- 修复休整区点击无反应bug（showRestAreaPopup调用了不存在的getRestCardsFromDOM函数）
- 修改战场>5校验：检查双方战场武将均≤5才允许抽卡（若任一方战场存在"徐庶"则豁免校验）
- 修复撤回后重抽bug：撤回后再次点击"下一步"时，抽出同一批武将（非重新随机）
- 按钮文案从"撤回上一步"改为"撤回上一次抽卡"

### v1.2.0 - 放回军营顶+自定义抽卡数量 (2026-06-08)

- 新增"放回军营顶部"功能：武将卡悬浮显示"\u2191"按钮，点击后放回军营顶部（LIFO栈，后进先出），下次抽卡必然先抽出
- 新增自定义抽卡数量："下一步"旁增加输入框(1-4)，默认2；抽卡后自动重置为2
- 后端新增campTop(LIFO栈)机制，抽卡优先从campTop取出再从camp随机抽取
- 新增API端点 POST /api/simulation/card/camp-top
- 修改nextStep()接受customDrawCount参数
- 撤回逻辑增强：undo时同时从campTop移除lastDrawnCards

---

## 功能模块释义

### M1: 数据模型层 (`org.example.sgs.model`)

| 类 | 职责 | 关键说明 |
|---|---|---|
| `GeneralCard` | 武将卡实体 | id格式: `{faction}_{name}_{index}`，faction为魏/蜀/吴/群；新增isCommander(主帅标记)、skillDescription(技能描述口子)、skillImagePath(技能图片口子) |
| `PresetDeck` | 预组实体 | 包含卡牌列表，totalCount=卡牌数 |
| `GameSession` | 对局会话状态 | 存储于HttpSession，含双方军营/军营顶部(LIFO栈)/战场/休整区/乐不思蜀/撤回数据/回合状态 |
| `DrawResult` | 抽卡结果 | 含drawnCards、finished标志、双方剩余/已上阵/休整区/乐不思蜀/军营顶部/撤回标记 |

**GameSession状态流转**:
- `nextIsFirstPlayer`: 标识下一步行动方
- `isFirstTurn`: 仅先手首回合为true，控制首回合抽1张
- 回合数递增时机：后手行动完毕后+1（一轮=先手+后手各行动一次）
- **休整区**: `firstPlayerRestArea/secondPlayerRestArea`，List<GeneralCard>
- **乐不思蜀**: `firstPlayerLeBuSiShu/secondPlayerLeBuSiShu`，Set<String>(卡牌ID集合)，进入休整区时自动移除
- **撤回**: `lastDrawnCards`(上次抽出卡牌引用)+`lastDrawPlayer`+`lastDrawWasFirstTurn`+`lastDrawTurn`+`canUndo`标志
- **战场上限校验**: 双方战场均>5时阻止抽卡，FIELD_MAX_LIMIT=5；若任一方战场存在"徐庶"则豁免校验
- **军营顶部(campTop)**: `firstPlayerCampTop/secondPlayerCampTop`，List<GeneralCard>，LIFO栈（add到末尾、remove从末尾），放回军营顶的武将下次抽卡优先抽出
- **自定义抽卡数量**: nextStep(customDrawCount)支持1-4，默认由getDrawCount()决定（先手首回合1，其余2）

### M2: 配置层 (`org.example.sgs.config`)

| 类 | 职责 | 关键说明 |
|---|---|---|
| `DeckConfig` | 预组数据工厂 | @PostConstruct初始化4个预组，每个预组39张卡；新增createCommanderCard()方法生成主帅卡 |
| `ImageConfig` | 图片路径配置 | **换图口子**：修改CARD_IMAGE_BASE_PATH或CARD_IMAGE_MAP即可更换图片 |
| `SkillConfig` | 技能描述配置 | **技能文案口子**：修改SKILL_DESC_MAP/SKILL_IMAGE_MAP即可添加技能描述和图片 |

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
| `SimulationService` | 模拟器核心逻辑 | 随机抽卡、回合管理、结束判定、双方>5校验(徐庶豁免)、撤回(重抽同批)、休整区操作、乐不思蜀切换、军营顶LIFO操作、主帅卡自动上场、自定义抽卡数量 |

**抽卡逻辑要点**:
- `randomDraw()`: 从军营浅拷贝中随机取牌，返回的卡牌对象与军营中同一引用
- `camp.removeAll(drawn)`: 依赖引用相等性移除（GeneralCard未重写equals）
- 先手首回合: `getDrawCount()`返回1，其余回合返回2
- 武将不足判定: `actualDraw < drawCount`时标记finished并提示
- **双方>5校验**: nextStep()中检查双方field.size()均≤FIELD_MAX_LIMIT，否则返回阻断消息；若hasXuShuOnField()为true则豁免校验
- **撤回重抽同批**: 撤回后lastDrawnCards非null且canUndo=false时，nextStep()使用lastDrawnCards而非drawFromCampAndTop()
- **军营顶部LIFO**: drawFromCampAndTop()先从campTop末尾(LIFO)取出，不足时再randomDraw()从camp随机抽取
- **放回军营顶**: moveToCampTop()将战场武将add到campTop末尾，同时移除乐不思蜀状态
- **自定义抽卡数量**: nextStep(customDrawCount)，1-4有效，否则使用getDrawCount()默认值
- **撤回增强**: undoLastStep()同时从campTop.removeAll(lastDrawn)移除，防止撤回后武将同时在camp和campTop
- **主帅卡**: startSimulation()末尾调用createCommanderCard()自动放入field
- **休整区**: moveToRestArea()/restoreFromRestArea()移动卡牌，进入休整区自动移除乐不思蜀
- **乐不思蜀**: toggleLeBuSiShu()切换Set中的卡牌ID

### M4: 控制器层 (`org.example.sgs.controller`)

| 类 | 端点 | 说明 |
|---|---|---|
| `ApiController` | `GET /api/decks` | 获取4个预组列表 |
| | `POST /api/simulation/start` | 开启模拟（含主帅卡自动上场） |
| | `POST /api/simulation/next` | 执行下一步抽卡（含双方>5校验+徐庶豁免，body: {drawCount:1-4}） |
| | `POST /api/simulation/undo` | 撤回上一步抽卡 |
| | `POST /api/simulation/card/rest` | 置入休整区(player+cardId) |
| | `POST /api/simulation/card/restore` | 从休整区恢复(player+cardId) |
| | `POST /api/simulation/card/lebusishu` | 切换乐不思蜀(player+cardId) |
| | `POST /api/simulation/card/camp-top` | 放回军营顶部(player+cardId) |
| | `GET /api/card/skill` | 获取武将技能描述(name参数) |
| | `GET /api/simulation/state` | 查询当前状态 |
| | `POST /api/simulation/reset` | 重置模拟 |

**会话管理**: 游戏状态存储在`HttpSession`的`gameSession`属性中，无数据库。

### M5: 前端页面 (`static/index.html`)

单文件HTML+CSS+JS，无外部框架依赖。

**UI结构**:
- 上方: 先手玩家区（金色主题）+ 休整区
- 下方: 后手玩家区（紫色主题）+ 休整区
- 中间: 控制按钮（开启模拟/下一步/抽卡数量输入/撤回上一次抽卡/重置）+ 状态栏 + 战斗日志
- 背景: Canvas粒子动画 + CSS径向渐变
- 弹窗: 休整区详情弹窗/武将技能弹窗（共用modal-overlay）

**势力颜色映射**: 魏=#4a90d9(蓝), 蜀=#d94a4a(红), 吴=#4ad97a(绿), 群=#9b59b6(紫)

**前端交互流程**:
1. 下拉框选预组 → 启用"开启模拟"按钮
2. 点击"开启模拟" → 禁用下拉框，启用"下一步"；主帅卡自动出现在战场
3. 悬浮战场武将卡 → 显示"x"(置入休整区)、"乐"(乐不思蜀)、"\u2191"(放回军营顶)按钮
4. 点击"x" → 调用rest API → 武将置入休整区，乐不思蜀状态消失
5. 点击"乐" → 调用lebusishu API → 切换乐不思蜀标记，卡牌显示浮动标签
6. 点击武将卡 → 调用skill API → 弹窗显示技能描述+图片
7. 点击休整区叠卡 → 弹窗显示所有休整区武将，悬浮显示"恢复至战场"按钮
8. 点击"下一步" → 若双方战场任一>5且无徐庶 → 提示"请先将战场上武将卡弃置至5或5以下"
9. 点击"下一步" → POST /next {drawCount:N} → 渲染卡牌动画+更新状态+添加日志+启用撤回；抽卡数量输入框重置为2
10. 点击"\u2191"按钮 → 调用camp-top API → 武将放回军营顶部，下次抽卡优先抽出
11. 点击"撤回上一次抽卡" → POST /undo → 撤回上次抽卡，武将返回军营；再次"下一步"抽出同一批武将
12. 武将不足 → 禁用"下一步"，Toast提示
13. "重置" → 清空所有状态

**动画效果**: 卡牌翻转出现(cardAppear) + 新卡发光(cardGlow) + 全屏闪光(drawFlash) + 日志滑入(logAppear) + 乐不思蜀浮动(lebuFloat)

---

## 功能间关联影响描述

### 修改预组数据时
- 影响文件: `DeckConfig.java`
- 关联影响: `ImageConfig`(图片路径)、前端下拉框选项（通过`/api/decks`接口自动同步）、`GameSession`(军营初始化)
- 注意: 卡牌总数必须=39，否则军营进度条显示异常

### 修改抽卡逻辑时
- 影响文件: `SimulationService.java`
- 关联影响: `GameSession`(状态流转)、`DrawResult`(返回数据)、前端nextStep()/syncFullState()函数、战斗日志、撤回逻辑
- 注意: `randomDraw`依赖引用相等，不可改为深拷贝；撤回时保留lastDrawnCards引用

### 修改休整区/乐不思蜀逻辑时
- 影响文件: `SimulationService.java`(moveToRestArea/restoreFromRestArea/toggleLeBuSiShu)
- 关联影响: `GameSession`(restArea/leBuSiShu字段)、`ApiController`(3个新端点)、前端updateRestAreaDisplay()/applyLeBuSiShuIndicators()/showRestAreaPopup()
- 注意: 进入休整区时乐不思蜀自动消失（leBuSiShu.remove(cardId))

### 修改技能描述时
- 影响文件: `SkillConfig.java`(SKILL_DESC_MAP/SKILL_IMAGE_MAP)
- 关联影响: `DeckConfig`(createCommanderCard调用skillConfig)、前端showSkillPopup()(调用/api/card/skill)
- 注意: SkillConfig是预留口子，当前默认返回"技能描述待补充"

### 修改图片路径时
- 影响文件: `ImageConfig.java`
- 关联影响: `DeckConfig`(初始化时调用)、前端`createCardElement()`(img.src使用imagePath)
- 注意: 修改后需重新编译，前端图片加载失败有容错处理

### 修改前端UI时
- 影响文件: `static/index.html`（CSS/JS均在此文件）
- 关联影响: 无后端影响，但需同步维护势力颜色映射（CSS变量与JS factionMap需一致）
