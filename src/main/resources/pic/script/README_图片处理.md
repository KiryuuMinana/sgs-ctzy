# 武将卡牌图片处理工具包

## 📦 工具包内容

本工具包包含以下脚本和文档，帮助您完成武将卡牌的裁剪、配置和部署：

### 脚本文件

1. **`crop_cards.py`** - 批量裁剪工具
   - 自动从整体图片中裁剪出所有武将卡牌
   - 按预组分类保存
   - 支持自定义卡牌尺寸和位置

2. **`crop_smart.py`** - 智能裁剪工具
   - 手动输入卡牌坐标进行精确裁剪
   - 位置预览功能（在图片上画标记）
   - 适合调整和测试

3. **`copy_cards.py`** - 快速复制工具
   - 将裁剪后的图片复制到项目静态资源目录
   - 验证卡牌完整性
   - 清理已复制的卡牌

### 文档文件

1. **`图片裁剪指南.md`** - 详细使用指南
   - 完整的操作步骤
   - 配置说明
   - 常见问题解答

2. **`README_图片处理.md`** - 本文档
   - 快速开始指南
   - 工具汇总

## 🚀 快速开始（3步完成）

### 步骤 1：准备整体图片

将四张预组整体图片放到项目根目录的 `images/` 文件夹：

```bash
# 创建目录
mkdir images

# 放置图片（请根据您的实际图片重命名）
# images/wei_overall.png   - 魏国整体图片
# images/shu_overall.png   - 蜀国整体图片
# images/wu_overall.png    - 吴国整体图片
# images/qun_overall.png   - 群雄整体图片
```

### 步骤 2：裁剪卡牌

#### 方法 A：使用批量裁剪（推荐）

```bash
# 安装依赖（首次使用）
pip install Pillow

# 运行批量裁剪
python crop_cards.py
```

**注意**：如果裁剪位置不准，请打开 `crop_cards.py` 修改 `LAYOUT_CONFIG` 配置。

#### 方法 B：使用智能裁剪（手动调整）

```bash
# 运行智能裁剪工具
python crop_smart.py

# 选择"2. 位置预览"模式，输入坐标查看效果
# 确认位置正确后，选择"1. 手动裁剪"模式
```

### 步骤 3：复制到项目并验证

```bash
# 复制裁剪后的图片到项目
python copy_cards.py
# 选择"1. 复制卡牌"

# 验证卡牌完整性
python copy_cards.py
# 选择"2. 验证卡牌"

# 重启应用后访问 http://localhost:8080
```

## 📊 预组武将列表

### 魏国（wei）- 14张
- 主帅：曹操
- 武将：乐进、曹昂、徐晃、荀彧、邓艾、王双、程昱、夏侯杰、文聘、甄姬、张虎、荀攸、曹真

### 蜀国（shu）- 15张
- 主帅：刘备
- 武将：孙乾、马云禄、雷铜、吴班、陈到、关羽、周仓、赵广、高翔、杨仪、关平、王平、吴毅

### 吴国（wu）- 14张
- 主帅：孙尚香
- 武将：朱治、唐咨、凌操、鲁肃、韩当、许贡、马忠、潘璋、吕岱、徐琨、是仪、谢灵毓、大乔

### 群雄（qun）- 14张
- 主帅：貂蝉
- 武将：李儒、王允、裴元绍、潘凤、张任、公孙修、陈登、武安国、牛辅、张济、韩遂、张燕、董卓

## 🔧 详细配置说明

### 卡牌尺寸配置

打开 `crop_cards.py`，修改 `CARD_SIZE`：

```python
CARD_SIZE = {
    "width": 180,   # 卡牌宽度（像素）
    "height": 250,  # 卡牌高度（像素）
    "spacing_x": 20, # 卡牌间水平间距
    "spacing_y": 20, # 卡牌间垂直间距
}
```

### 卡牌位置配置

修改 `LAYOUT_CONFIG`：

```python
LAYOUT_CONFIG = {
    "wei": {
        "start_position": (100, 400),  # 第一张卡牌的坐标
        "rows": [
            ["曹操"],  # 主帅
            ["乐进", "曹昂", "徐晃"],  # 第一行
            ["荀彧", "邓艾", "王双"],  # 第二行
            ...
        ]
    }
}
```

## ⚙️ 高级用法

### 使用 FFmpeg 裁剪

如果您更喜欢使用 FFmpeg：

```bash
# 示例：裁剪魏国卡牌
mkdir -p images/cards/wei

# 曹操（坐标需要根据实际图片调整）
ffmpeg -i images/wei_overall.png -vf "crop=180:250:100:400" -y images/cards/wei/曹操.png

# 乐进
ffmpeg -i images/wei_overall.png -vf "crop=180:250:300:400" -y images/cards/wei/乐进.png

# ... 其他武将依此类推
```

### 批量添加标签

使用 FFmpeg 为卡牌添加名称标签：

```bash
ffmpeg -i card.png -vf "drawtext=text='曹操':fontsize=20:fontcolor=white:x=(w-text_w)/2:y=h-50" card_with_name.png
```

## 🎯 验证检查清单

完成以下检查确保配置正确：

- [ ] 已准备四张整体图片（wei/shu/wu/qun）
- [ ] 已安装 Pillow：`pip install Pillow`
- [ ] 已运行 `crop_cards.py` 裁剪所有卡牌
- [ ] 已检查裁剪后的图片质量
- [ ] 已运行 `copy_cards.py` 复制到项目
- [ ] 已运行 `copy_cards.py` 验证完整性
- [ ] 已重启应用
- [ ] 已访问网站检查显示效果

## 📁 目录结构

完成后，项目结构如下：

```
sgs-ctzy-demo/
├── images/                      # 整体图片（裁剪前）
│   ├── wei_overall.png
│   ├── shu_overall.png
│   ├── wu_overall.png
│   ├── qun_overall.png
│   └── cards/                   # 裁剪后的卡牌（临时）
│       ├── wei/
│       ├── shu/
│       ├── wu/
│       └── qun/
├── src/main/resources/static/
│   └── images/
│       └── cards/               # 项目使用的卡牌（最终）
│           ├── wei/
│           │   ├── 曹操.png
│           │   ├── 乐进.png
│           │   └── ...
│           ├── shu/
│           ├── wu/
│           └── qun/
└── (工具脚本...)
```

## 🐛 常见问题

### Q: 裁剪位置不准怎么办？

**A:** 
1. 使用 `crop_smart.py` 的"位置预览"功能
2. 输入坐标后查看生成的预览图
3. 调整坐标直到位置正确
4. 更新 `crop_cards.py` 中的 `LAYOUT_CONFIG`

### Q: 卡牌图片太小/太大？

**A:** 修改 `crop_cards.py` 中的 `CARD_SIZE` 配置，然后重新裁剪。

### Q: 如何确定卡牌坐标？

**A:** 
1. 使用 Photoshop、GIMP 或其他图片编辑工具
2. 打开整体图片
3. 将鼠标移到卡牌左上角
4. 记录坐标值（X, Y）

### Q: 可以手动复制图片吗？

**A:** 可以，直接将 `images/cards/` 下的文件复制到 `src/main/resources/static/images/cards/` 即可。

## 📞 需要帮助？

如果遇到问题：

1. 查看 `图片裁剪指南.md` 获取详细说明
2. 检查 `DeckConfig.java` 确认武将名称
3. 检查 `ImageConfig.java` 确认图片路径配置
4. 使用 `copy_cards.py` 的"验证卡牌"功能检查完整性

## 🎨 推荐图片规格

- **格式**：PNG（支持透明背景）
- **尺寸**：180×250 或 360×500 像素
- **质量**：72-150 DPI
- **文件大小**：< 100KB/张

---

**工具版本**：1.0  
**最后更新**：2026-06-08
