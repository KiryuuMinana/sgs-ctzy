#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
武将卡牌裁剪工具
用于从预组整体图片中裁剪出各个武将卡牌

使用方法：
1. 安装依赖：pip install Pillow
2. 将四张预组整体图片放到对应的预组文件夹
3. 运行：python crop_cards.py

预组结构：
- 魏国：曹操（主帅）+ 乐进、曹昂、徐晃、荀彧、邓艾、王双、程昱、夏侯杰、文聘、甄姬、张虎、荀攸、曹真
- 蜀国：刘备（主帅）+ 孙乾、马云禄、雷铜、吴班、陈到、关羽、周仓、赵广、高翔、杨仪、关平、王平、吴毅
- 吴国：孙尚香（主帅）+ 朱治、唐咨、凌操、鲁肃、韩当、许贡、马忠、潘璋、吕岱、徐琨、是仪、谢灵毓、大乔
- 群雄：貂蝉（主帅）+ 李儒、王允、裴元绍、潘凤、张任、公孙修、陈登、武安国、牛辅、张济、韩遂、张燕、董卓
"""

import os
from PIL import Image

# ==================== 配置区域（请根据实际情况修改） ====================

# 预组整体图片路径配置
OVERALL_IMAGES = {
    "wei": "images/wei_overall.png",    # 魏国整体图片
    "shu": "images/shu_overall.png",    # 蜀国整体图片
    "wu": "images/wu_overall.png",      # 吴国整体图片
    "qun": "images/qun_overall.png",    # 群雄整体图片
}

# 输出目录
OUTPUT_DIR = "images/cards"

# 卡牌尺寸配置（像素）- 请根据实际图片调整
# 格式：(起始X, 起始Y, 宽度, 高度)
CARD_SIZE = {
    "width": 180,   # 卡牌宽度
    "height": 250,  # 卡牌高度
    "spacing_x": 20, # 卡牌间水平间距
    "spacing_y": 20, # 卡牌间垂直间距
}

# 每个预组的卡牌布局配置
# 格式：列表，每个元素为 (x偏移, y偏移) 相对于上一张卡牌的位置
LAYOUT_CONFIG = {
    "wei": {
        # 魏国：主帅 + 13张普通武将 = 14张
        "start_position": (100, 400),  # 第一张卡牌的起始位置
        "rows": [
            ["曹操"],  # 主帅单独一行
            ["乐进", "曹昂", "徐晃"],  # 第一行
            ["荀彧", "邓艾", "王双"],  # 第二行
            ["程昱", "夏侯杰", "文聘"],  # 第三行
            ["甄姬", "张虎", "荀攸"],  # 第四行
            ["曹真"],  # 第五行
        ]
    },
    "shu": {
        # 蜀国：主帅 + 14张普通武将 = 15张
        "start_position": (100, 400),
        "rows": [
            ["刘备"],  # 主帅
            ["孙乾", "马云禄", "雷铜"],
            ["吴班", "陈到", "关羽"],
            ["周仓", "赵广", "高翔"],
            ["杨仪", "关平", "王平"],
            ["吴毅"],
        ]
    },
    "wu": {
        # 吴国：主帅 + 13张普通武将 = 14张
        "start_position": (100, 400),
        "rows": [
            ["孙尚香"],  # 主帅
            ["朱治", "唐咨", "凌操"],
            ["鲁肃", "韩当", "许贡"],
            ["马忠", "潘璋", "吕岱"],
            ["徐琨", "是仪", "谢灵毓"],
            ["大乔"],
        ]
    },
    "qun": {
        # 群雄：主帅 + 13张普通武将 = 14张
        "start_position": (100, 400),
        "rows": [
            ["貂蝉"],  # 主帅
            ["李儒", "王允", "裴元绍"],
            ["潘凤", "张任", "公孙修"],
            ["陈登", "武安国", "牛辅"],
            ["张济", "韩遂", "张燕"],
            ["董卓"],
        ]
    }
}

# ====================================================================


def crop_card(image, x, y, width, height):
    """裁剪单张卡牌"""
    return image.crop((x, y, x + width, y + height))


def process_deck(deck_id):
    """处理单个预组"""
    print(f"\n{'='*60}")
    print(f"开始处理预组：{deck_id}")
    print(f"{'='*60}")
    
    # 读取整体图片
    image_path = OVERALL_IMAGES.get(deck_id)
    if not image_path or not os.path.exists(image_path):
        print(f"❌ 找不到整体图片：{image_path}")
        return
    
    print(f"✓ 读取整体图片：{image_path}")
    overall_image = Image.open(image_path)
    print(f"  图片尺寸：{overall_image.size}")
    
    # 获取布局配置
    layout = LAYOUT_CONFIG.get(deck_id)
    if not layout:
        print(f"❌ 找不到布局配置：{deck_id}")
        return
    
    # 创建输出目录
    output_dir = os.path.join(OUTPUT_DIR, deck_id)
    os.makedirs(output_dir, exist_ok=True)
    print(f"✓ 输出目录：{output_dir}")
    
    # 开始裁剪
    start_x, start_y = layout["start_position"]
    card_width = CARD_SIZE["width"]
    card_height = CARD_SIZE["height"]
    spacing_x = CARD_SIZE["spacing_x"]
    spacing_y = CARD_SIZE["spacing_y"]
    
    total_cards = 0
    for row_idx, row in enumerate(layout["rows"]):
        print(f"\n第 {row_idx + 1} 行：{len(row)} 张卡牌")
        
        for col_idx, general_name in enumerate(row):
            # 计算卡牌位置
            x = start_x + col_idx * (card_width + spacing_x)
            y = start_y + row_idx * (card_height + spacing_y)
            
            # 裁剪卡牌
            card_image = crop_card(overall_image, x, y, card_width, card_height)
            
            # 保存卡牌（按武将名命名）
            output_path = os.path.join(output_dir, f"{general_name}.png")
            card_image.save(output_path, "PNG")
            
            print(f"  ✓ {general_name:8s} -> {output_path}")
            total_cards += 1
    
    print(f"\n✓ 预组 {deck_id} 处理完成，共裁剪 {total_cards} 张卡牌")


def main():
    """主函数"""
    print("="*60)
    print("武将卡牌裁剪工具")
    print("="*60)
    
    # 检查 Pillow 是否安装
    try:
        from PIL import Image
    except ImportError:
        print("❌ 请先安装 Pillow：pip install Pillow")
        return
    
    # 创建输出目录
    os.makedirs(OUTPUT_DIR, exist_ok=True)
    print(f"✓ 输出目录：{OUTPUT_DIR}")
    
    # 处理所有预组
    for deck_id in ["wei", "shu", "wu", "qun"]:
        process_deck(deck_id)
    
    print(f"\n{'='*60}")
    print("✓ 所有预组处理完成！")
    print(f"{'='*60}")
    print(f"\n下一步操作：")
    print(f"1. 检查裁剪后的卡牌图片是否正确")
    print(f"2. 如果位置不准，调整 CARD_SIZE 和 LAYOUT_CONFIG 配置")
    print(f"3. 将裁剪后的图片放到项目的 src/main/resources/static/images/cards/ 目录")
    print(f"4. 重启应用即可看到效果")


if __name__ == "__main__":
    main()
