#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
智能武将卡牌裁剪工具
支持鼠标点击选择卡牌位置，或使用默认配置
"""

import os
from PIL import Image, ImageDraw, ImageFont

def manual_crop():
    """手动选择卡牌位置进行裁剪"""
    print("="*60)
    print("手动裁剪模式")
    print("="*60)
    
    # 提示用户输入
    deck_id = input("\n请输入预组ID (wei/shu/wu/qun): ").strip().lower()
    if deck_id not in ["wei", "shu", "wu", "qun"]:
        print("❌ 无效的预组ID")
        return
    
    image_path = input(f"请输入 {deck_id} 整体图片路径: ").strip()
    if not os.path.exists(image_path):
        print(f"❌ 找不到图片：{image_path}")
        return
    
    # 打开图片
    print(f"\n✓ 打开图片：{image_path}")
    image = Image.open(image_path)
    print(f"  图片尺寸：{image.size[0]} x {image.size[1]}")
    
    # 提示用户输入卡牌信息
    print("\n请按以下格式输入卡牌信息：")
    print("武将名,x坐标,y坐标")
    print("示例：曹操,100,400")
    print("输入 'q' 退出\n")
    
    cards = []
    while True:
        line = input(f"第 {len(cards)+1} 张卡牌: ").strip()
        if line.lower() == 'q':
            break
        
        try:
            parts = line.split(',')
            if len(parts) != 3:
                print("❌ 格式错误，请使用：武将名,x,y")
                continue
            
            name = parts[0].strip()
            x = int(parts[1].strip())
            y = int(parts[2].strip())
            
            cards.append((name, x, y))
            print(f"  ✓ {name} at ({x}, {y})")
        except Exception as e:
            print(f"❌ 解析失败：{e}")
            continue
    
    if not cards:
        print("❌ 没有输入任何卡牌")
        return
    
    # 设置卡牌尺寸
    print("\n设置卡牌尺寸：")
    width = int(input("  卡牌宽度 (默认 180): ").strip() or "180")
    height = int(input("  卡牌高度 (默认 250): ").strip() or "250")
    
    # 创建输出目录
    output_dir = os.path.join("images/cards", deck_id)
    os.makedirs(output_dir, exist_ok=True)
    print(f"\n✓ 输出目录：{output_dir}")
    
    # 开始裁剪
    print(f"\n开始裁剪 {len(cards)} 张卡牌...")
    for name, x, y in cards:
        card = image.crop((x, y, x + width, y + height))
        output_path = os.path.join(output_dir, f"{name}.png")
        card.save(output_path, "PNG")
        print(f"  ✓ {name:8s} -> {output_path}")
    
    print(f"\n✓ 裁剪完成，共 {len(cards)} 张卡牌")


def preview_positions():
    """预览卡牌位置（在图片上画标记）"""
    print("="*60)
    print("位置预览模式")
    print("="*60)
    
    # 提示用户输入
    deck_id = input("\n请输入预组ID (wei/shu/wu/qun): ").strip().lower()
    if deck_id not in ["wei", "shu", "wu", "qun"]:
        print("❌ 无效的预组ID")
        return
    
    image_path = input(f"请输入 {deck_id} 整体图片路径: ").strip()
    if not os.path.exists(image_path):
        print(f"❌ 找不到图片：{image_path}")
        return
    
    # 打开图片
    print(f"\n✓ 打开图片：{image_path}")
    image = Image.open(image_path).convert('RGB')
    draw = ImageDraw.Draw(image)
    
    # 尝试加载字体
    try:
        font = ImageFont.truetype("arial.ttf", 20)
    except:
        font = ImageFont.load_default()
    
    # 设置卡牌尺寸
    width = int(input("卡牌宽度 (默认 180): ").strip() or "180")
    height = int(input("卡牌高度 (默认 250): ").strip() or "250")
    
    # 提示用户输入卡牌信息
    print("\n请按以下格式输入卡牌信息：")
    print("武将名,x坐标,y坐标")
    print("输入 'q' 退出\n")
    
    cards = []
    while True:
        line = input(f"第 {len(cards)+1} 张卡牌: ").strip()
        if line.lower() == 'q':
            break
        
        try:
            parts = line.split(',')
            if len(parts) != 3:
                print("❌ 格式错误")
                continue
            
            name = parts[0].strip()
            x = int(parts[1].strip())
            y = int(parts[2].strip())
            
            cards.append((name, x, y))
            
            # 在图片上画矩形和文字
            draw.rectangle([x, y, x+width, y+height], outline='red', width=3)
            draw.text((x, y-25), name, fill='red', font=font)
            
        except Exception as e:
            print(f"❌ 解析失败：{e}")
            continue
    
    if not cards:
        print("❌ 没有输入任何卡牌")
        return
    
    # 保存预览图
    preview_path = f"{deck_id}_preview.png"
    image.save(preview_path, "PNG")
    print(f"\n✓ 预览图已保存：{preview_path}")
    print("  请打开查看位置是否准确")


def batch_crop():
    """批量裁剪（使用预设配置）"""
    print("="*60)
    print("批量裁剪模式")
    print("="*60)
    
    # 这里可以使用 crop_cards.py 中的配置
    print("\n提示：批量裁剪请使用 crop_cards.py 脚本")
    print("      本工具主要用于手动调整和预览位置")


def main():
    """主函数"""
    print("="*60)
    print("智能武将卡牌裁剪工具")
    print("="*60)
    
    # 检查 Pillow 是否安装
    try:
        from PIL import Image
    except ImportError:
        print("❌ 请先安装 Pillow：pip install Pillow")
        return
    
    while True:
        print("\n请选择模式：")
        print("1. 手动裁剪 - 输入卡牌名称和坐标")
        print("2. 位置预览 - 在图片上画标记检查位置")
        print("3. 批量裁剪 - 使用预设配置（推荐用 crop_cards.py）")
        print("0. 退出")
        
        choice = input("\n请输入选项 (0-3): ").strip()
        
        if choice == '0':
            print("\n再见！")
            break
        elif choice == '1':
            manual_crop()
        elif choice == '2':
            preview_positions()
        elif choice == '3':
            batch_crop()
        else:
            print("❌ 无效选项")


if __name__ == "__main__":
    main()
