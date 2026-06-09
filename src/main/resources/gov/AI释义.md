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

- 新增"放回军营顶部"功能：武将卡悬浮显示"↑"按钮，点击后放回军营顶部（LIFO栈，后进先出），下次抽卡必然先抽出
- 新增自定义抽卡数量："下一步"旁增加输入框(1-4)，默认2；抽卡后自动重置为2
- 后端新增campTop(LIFO栈)机制，抽卡优先从campTop取出再从camp随机抽取
- 新增API端点 POST /api/simulation/card/camp-top
- 修改nextStep()接受customDrawCount参数
- 撤回逻辑增强：undo时同时从campTop移除lastDrawnCards

### v1.2.1 - Bug修复：放回军营顶抽卡逻辑+撤回联动 (2026-06-08)

- 修复放回军营顶后抽卡不从campTop抽的bug：moveToCampTop()增加session.setCanUndo(false)和session.setLastDrawnCards(null)，防止撤回后重抽逻辑覆盖campTop抽卡
- 修复撤回时campTop卡错误放回camp的bug：GameSession新增lastDrawnFromCampTopIds字段追踪上次抽卡中来自campTop的武将ID；undoLastStep()根据该字段将卡正确放回campTop或camp
- 约束：点击放回军营顶后，撤回上一次抽卡按钮被禁用（canUndo=false）

### v1.3.0 - 放回军营底部+战报增强 (2026-06-08)

- 新增"放回军营底部"功能：武将卡悬浮显示"↓"按钮，点击后放回军营底部（队列末尾，最后放入的最后被抽出），抽卡优先级：campTop→camp→campBottom
- 新增战报增强：战斗日志记录所有操作类型（上阵/撤回/放回军营顶/放回军营底/贴乐不思蜀/移入休整区/恢复至战场）
- 后端新增campBottom(队列)机制，抽卡在campTop和camp不足时从campBottom末尾取出
- 新增API端点 POST /api/simulation/card/camp-bottom
- 撤回逻辑增强：undoLastStep()根据lastDrawnFromCampBottomIds区分来源，来自campBottom的放回campBottom末尾
- 约束：点击放回军营底后，撤回上一次抽卡按钮被禁用（canUndo=false），清除lastDrawnCards

### v1.3.1 - Bug修复：武将数量异常+抽卡顺序控制 (2026-06-08)

- **严重bug修复**：修复撤回操作导致武将数量增加的bug（军营出现41张卡）
  - **bug根源**：undoLastStep()使用field.removeAll(lastDrawn)时，如果卡牌已被移到campTop/campBottom/restArea，removeAll无操作，但后续循环又将卡牌放回，导致重复添加
  - **修复方案**：undoLastStep()改为先检查卡牌是否仍在field中，只移除并放回仍在field中的卡牌；已移到其他区域的卡牌不再重复放回
- **新增抽卡顺序控制**："下一步"旁新增下拉框，可选择"先手抽卡"/"后手抽卡"，默认"默认轮流"
  - 选择后抽一次卡自动恢复为"默认轮流"
  - 后端nextStep()接受forcePlayer参数，抽卡后清除forcePlayerSpecified标记并切换nextIsFirstPlayer
  - GameSession新增forcePlayerSpecified字段追踪是否手动指定了玩家

### v1.4.0 - 横置功能+骰子+休整区弹窗+战报格式调整 (2026-06-08)

- **新增横置功能**：武将卡悬浮显示"↻"按钮，点击切换横置状态，横置时武将卡旋转90°显示，再次点击恢复
  - 纯前端状态，不持久化到后端，通过CSS类`.horizontal`+`transform: rotate(90deg)`实现
  - 按钮样式：紫色圆形，与"乐"按钮色系区分
- **新增骰子功能**：控制区下方新增骰子区域，含3D骰子+下拉框(1-15)+结果展示
  - 点击骰子触发旋转动画(1.5s)，从[1,x]随机抽取整数(x为用户选择的数字，默认4)
  - 骰子使用CSS 3D transform+`@keyframes diceRoll`实现6面旋转效果
  - 结果数字带弹出动画(`@keyframes resultPop`)
  - 全局变量`isDiceRolling`防止连续点击
- **调整休整区弹窗**：武将点击×进入休整区后，立即触发查看图片弹窗(`showRestCardPopup`)
  - 弹窗内容与技能弹窗类似：武将图片+名称+"武将已置入休整区"提示
  - `moveToRestArea()`函数在API调用成功后调用`showRestCardPopup()`
- **调整战报格式**：移除"回合"概念
  - `addBattleLog()`不再显示`[第X回合]`前缀
  - `updateStatusBar()`中`第 X 回合`改为`当前状态`
  - 后端回合逻辑(turn计数)保持不变，仅前端展示不再体现回合数

---

## 功能模块释义

### M1: 数据模型层 (`org.example.sgs.model`)

| 类 | 职责 | 关键说明 |
|---|---|---|
| `GeneralCard` | 武将卡实体 | id格式: `{faction}_{name}_{index}`，faction为魏/蜀/吴/群；新增isCommander(主帅标记)、skillDescription(技能描述口子)、skillImagePath(技能图片口子) |
| `PresetDeck` | 预组实体 | 包含卡牌列表，totalCount=卡牌数 |
| `GameSession` | 对局会话状态 | 存储于HttpSession，含双方军营/军营顶部(LIFO栈)/军营底部(队列)/战场/休整区/乐不思蜀/撤回数据/回合状态；新增lastDrawnFromCampTopIds和lastDrawnFromCampBottomIds追踪上次抽卡来源 |
| `DrawResult` | 抽卡结果 | 含drawnCards、finished标志、双方剩余/已上阵/休整区/乐不思蜀/军营顶部/军营底部/撤回标记 |

**GameSession状态流转**:
- `nextIsFirstPlayer`: 标识下一步行动方
- `isFirstTurn`: 仅先手首回合为true，控制首回合抽1张
- 回合数递增时机：后手行动完毕后+1（一轮=先手+后手各行动一次）
- **休整区**: `firstPlayerRestArea/secondPlayerRestArea`，List<GeneralCard>
- **乐不思蜀**: `firstPlayerLeBuSiShu/secondPlayerLeBuSiShu`，Set<String>(卡牌ID集合)，进入休整区时自动移除
- **撤回**: `lastDrawnCards`(上次抽出卡牌引用)+`lastDrawPlayer`+`lastDrawWasFirstTurn`+`lastDrawTurn`+`canUndo`标志+`lastDrawnFromCampTopIds`(上次抽卡中来自campTop的武将ID集合，用于撤回时正确放回)
- **战场上限校验**: 双方战场均>5时阻止抽卡，FIELD_MAX_LIMIT=5；若任一方战场存在"徐庶"则豁免校验
- **军营顶部(campTop)**: `firstPlayerCampTop/secondPlayerCampTop`，List<GeneralCard>，LIFO栈（add到末尾、remove从末尾），放回军营顶的武将下次抽卡优先抽出
- **军营底部(campBottom)**: `firstPlayerCampBottom/secondPlayerCampBottom`，List<GeneralCard>，队列末尾（add到末尾、remove从末尾），放回军营底的武将最后才被抽出；抽卡优先级：campTop→camp→campBottom
- **自定义抽卡数量**: nextStep(customDrawCount)支持1-4，默认由getDrawCount()决定（先手首回合1，其余2）
- **撤回追踪**: `lastDrawnFromCampTopIds`和`lastDrawnFromCampBottomIds`分别记录上次抽卡中来自campTop和campBottom的武将ID，撤回时正确放回对应位置

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
| `SimulationService` | 模拟器核心逻辑 | 随机抽卡、回合管理、结束判定、双方>5校验(徐庶豁免)、撤回(重抽同批)、休整区操作、乐不思蜀切换、军营顶LIFO操作、军营底队列操作、主帅卡自动上场、自定义抽卡数量 |

**抽卡逻辑要点**:
- `randomDraw()`: 从军营浅拷贝中随机取牌，返回的卡牌对象与军营中同一引用
- `camp.removeAll(drawn)`: 依赖引用相等性移除（GeneralCard未重写equals）
- 先手首回合: `getDrawCount()`返回1，其余回合返回2
- 武将不足判定: `actualDraw < drawCount`时标记finished并提示
- **双方>5校验**: nextStep()中检查双方field.size()均≤FIELD_MAX_LIMIT，否则返回阻断消息；若hasXuShuOnField()为true则豁免校验
- **撤回重抽同批**: 撤回后lastDrawnCards非null且canUndo=false时，nextStep()使用lastDrawnCards而非drawFromCampAndTop()
- **军营顶部LIFO**: drawFromCampTopBottom()先从campTop末尾(LIFO)取出，不足时再randomDraw()从camp随机抽取，最后从campBottom末尾取出
- **放回军营顶**: moveToCampTop()将战场武将add到campTop末尾，同时移除乐不思蜀状态，设置canUndo=false并清除lastDrawnCards（防止撤回后重抽覆盖campTop逻辑）
- **放回军营底**: moveToCampBottom()将战场武将add到campBottom末尾，同时移除乐不思蜀状态，设置canUndo=false并清除lastDrawnCards（防止撤回后重抽覆盖campBottom逻辑）
- **自定义抽卡数量**: nextStep(customDrawCount)，1-4有效，否则使用getDrawCount()默认值
- **撤回增强**: undoLastStep()根据lastDrawnFromCampTopIds和lastDrawnFromCampBottomIds区分来源，来自campTop的放回campTop栈顶，来自campBottom的放回campBottom末尾，其他放回camp；防止撤回后武将位置错误
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
| | `POST /api/simulation/card/camp-bottom` | 放回军营底部(player+cardId) |
| | `GET /api/card/skill` | 获取武将技能描述(name参数) |
| | `GET /api/simulation/state` | 查询当前状态 |
| | `POST /api/simulation/reset` | 重置模拟 |

**会话管理**: 游戏状态存储在`HttpSession`的`gameSession`属性中，无数据库。

### M5: 前端页面 (`static/index.html`)

单文件HTML+CSS+JS，无外部框架依赖。

**UI结构**:
- 上方: 先手玩家区（金色主题）+ 休整区
- 下方: 后手玩家区（紫色主题）+ 休整区
- 中间: 控制按钮（开启模拟/下一步/抽卡数量输入/抽卡顺序下拉/撤回上一次抽卡/重置）+ 状态栏 + 战斗日志（记录所有操作）
- 中间下方: 骰子区域（3D骰子+随机范围下拉框1-15+结果展示）
- 背景: Canvas粒子动画 + CSS径向渐变
- 弹窗: 休整区详情弹窗/武将技能弹窗/休整区图片弹窗（共用modal-overlay）

**势力颜色映射**: 魏=#4a90d9(蓝), 蜀=#d94a4a(红), 吴=#4ad97a(绿), 群=#9b59b6(紫)

**新增功能（v1.4.0）**:
- **横置按钮**: `createCardElement()`中新增`.card-btn-horizontal`按钮(紫色圆形，旋转图标`↻`)，点击调用`toggleHorizontal(cardEl)`
  - `.horizontal` CSS类: `transform: rotate(90deg)`，`transform-origin: center center`
  - 横置时卡牌内层加紫色发光阴影
  - `cardEl.addEventListener('click')`需过滤`card-btn-horizontal`，避免误触发技能弹窗
- **骰子区域**: `.dice-area`包含3D骰子+下拉框+结果数字
  - `rollDice()`: 读取`#diceMaxSelect`值作为上限，`Math.floor(Math.random() * maxNum) + 1`生成结果
  - `#dice`添加`.rolling`类触发`@keyframes diceRoll`(1.5s)动画
  - 结果展示`#diceResult`使用`@keyframes resultPop`弹出动画
  - `isDiceRolling`标志防止动画期间重复点击
- **休整区图片弹窗**: `showRestCardPopup(cardName, cardFaction, cardImagePath)`复用技能弹窗样式展示武将图片
  - 在`moveToRestArea()`成功后调用，不依赖skill API
- **战报格式**: `addBattleLog()`移除`[第X回合]`前缀；`updateStatusBar()`显示"当前状态"而非回合数

**前端交互流程**:
1. 下拉框选预组 → 启用"开启模拟"按钮
2. 点击"开启模拟" → 禁用下拉框，启用"下一步"；主帅卡自动出现在战场
3. 悬浮战场武将卡 → 显示"x"(置入休整区)、"乐"(乐不思蜀)、"↑"(放回军营顶)、"↓"(放回军营底)、"↻"(横置)按钮
4. 点击"x" → 调用rest API → 武将置入休整区，乐不思蜀状态消失，战报记录，**并立即弹出休整区图片弹窗**
5. 点击"乐" → 调用lebusishu API → 切换乐不思蜀标记，卡牌显示浮动标签，战报记录
6. 点击"↻" → 切换卡牌`.horizontal`类，旋转90°显示，Toast提示
7. 点击武将卡 → 调用skill API → 弹窗显示技能描述+图片
8. 点击休整区叠卡 → 弹窗显示所有休整区武将，悬浮显示"恢复至战场"按钮，战报记录
9. 点击"下一步" → 若双方战场任一>5且无徐庶 → 提示"请先将战场上武将卡弃置至5或5以下"
10. 点击"下一步" → POST /next {drawCount:N} → 渲染卡牌动画+更新状态+添加日志+启用撤回；抽卡数量输入框重置为2
11. 点击"↑"按钮 → 调用camp-top API → 武将放回军营顶部，下次抽卡优先抽出；同时禁用撤回按钮，战报记录
12. 点击"↓"按钮 → 调用camp-bottom API → 武将放回军营底部，最后才被抽出；同时禁用撤回按钮，战报记录
13. 点击"撤回上一次抽卡" → POST /undo → 撤回上次抽卡，来自campTop的武将放回campTop，来自campBottom的放回campBottom，其他放回军营；再次"下一步"抽出同一批武将，战报记录
14. 点击骰子 → 读取下拉框值 → 播放1.5s旋转动画 → 显示随机结果 + Toast提示
15. 武将不足 → 禁用"下一步"，Toast提示
16. "重置" → 清空所有状态

**动画效果**: 卡牌翻转出现(cardAppear) + 新卡发光(cardGlow) + 全屏闪光(drawFlash) + 日志滑入(logAppear) + 乐不思蜀浮动(lebuFloat) + 骰子旋转(diceRoll) + 结果弹出(resultPop)

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

### 修改放回军营顶/底逻辑时
- 影响文件: `SimulationService.java`(moveToCampTop/moveToCampBottom)
- 关联影响: `GameSession`(campTop/campBottom/lastDrawnFromCampTopIds/lastDrawnFromCampBottomIds字段)、`DrawResult`(firstPlayerCampTop/firstPlayerCampBottom字段)、`ApiController`(camp-top/camp-bottom端点)、前端createCardElement()(悬浮按钮)+moveToCampTop()/moveToCampBottom()函数+addBattleLog()(战报记录)
- 注意: 放回军营顶/底后需设置canUndo=false并清除lastDrawnCards，防止撤回后重抽逻辑覆盖；撤回时需根据lastDrawnFromCampTopIds/lastDrawnFromCampBottomIds正确放回对应位置

### 修改战报功能时
- 影响文件: `static/index.html`(addBattleLog函数)
- 关联影响: 所有操作函数(nextStep/undoLastStep/moveToCampTop/moveToCampBottom/moveToRestArea/restoreFromRestArea/toggleLeBuSiShu)需调用addBattleLog记录日志
- 注意: addBattleLog支持7种操作类型(draw/undo/campTop/campBottom/lebusishu/rest/restore)，每种类型有不同的显示样式；v1.4.0起不再显示`[第X回合]`前缀

### 修改横置功能时
- 影响文件: `static/index.html`(createCardElement/toggleHorizontal/.horizontal CSS)
- 关联影响: 纯前端状态，不涉及后端；但`cardEl.addEventListener('click')`需过滤`card-btn-horizontal`避免误触发技能弹窗
- 注意: 横置状态不持久化（刷新后丢失），仅作为视觉标记；卡牌旋转90°时需保证`transform-origin: center center`防止位置偏移

### 修改骰子功能时
- 影响文件: `static/index.html`(rollDice函数/.dice-area/3D骰子CSS)
- 关联影响: 纯前端工具，不涉及后端和游戏状态；骰子与抽卡/武将等功能完全独立
- 注意: 下拉框值范围1-15，默认选中4；`isDiceRolling`标志需在动画结束后重置；骰子6面使用CSS `translateZ(30px)`+`rotateY/X`组合定位
