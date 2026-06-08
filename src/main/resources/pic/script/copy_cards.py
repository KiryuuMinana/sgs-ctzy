#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
快速复制脚本
将裁剪后的卡牌图片复制到项目静态资源目录
"""

import os
import shutil
import glob

def copy_cards():
    """复制裁剪后的卡牌到项目目录"""
    
    # 源目录（裁剪后的图片）
    source_base = "images/cards"
    
    # 目标目录（项目静态资源）
    target_base = "src/main/resources/static/images/cards"
    
    print("="*60)
    print("快速复制工具")
    print("="*60)
    
    # 检查源目录是否存在
    if not os.path.exists(source_base):
        print(f"❌ 源目录不存在：{source_base}")
        print("  请先运行 crop_cards.py 裁剪卡牌")
        return
    
    # 创建目标目录
    os.makedirs(target_base, exist_ok=True)
    print(f"✓ 目标目录：{target_base}")
    
    # 统计复制的文件数量
    total_files = 0
    
    # 遍历所有预组
    for deck_id in ["wei", "shu", "wu", "qun"]:
        source_dir = os.path.join(source_base, deck_id)
        target_dir = os.path.join(target_base, deck_id)
        
        if not os.path.exists(source_dir):
            print(f"\n⚠ 跳过不存在的目录：{source_dir}")
            continue
        
        # 创建目标子目录
        os.makedirs(target_dir, exist_ok=True)
        
        # 获取所有PNG文件
        png_files = glob.glob(os.path.join(source_dir, "*.png"))
        
        if not png_files:
            print(f"\n⚠ {deck_id}: 没有PNG文件")
            continue
        
        # 复制文件
        copied = 0
        for png_file in png_files:
            filename = os.path.basename(png_file)
            target_file = os.path.join(target_dir, filename)
            
            try:
                shutil.copy2(png_file, target_file)
                copied += 1
            except Exception as e:
                print(f"  ❌ 复制失败 {filename}: {e}")
        
        print(f"\n✓ {deck_id}: 复制 {copied} 张卡牌")
        total_files += copied
    
    print(f"\n{'='*60}")
    print(f"✓ 复制完成，共 {total_files} 张卡牌")
    print(f"{'='*60}")
    print(f"\n下一步操作：")
    print(f"1. 重启应用（如果正在运行）")
    print(f"2. 访问 http://localhost:8080")
    print(f"3. 开始模拟并检查武将图片")


def verify_cards():
    """验证卡牌是否完整"""
    
    target_base = "src/main/resources/static/images/cards"
    
    print("="*60)
    print("卡牌验证工具")
    print("="*60)
    
    # 每个预组应有的武将列表
    expected = {
        "wei": ["曹操", "乐进", "曹昂", "徐晃", "荀彧", "邓艾", "王双", 
                "程昱", "夏侯杰", "文聘", "甄姬", "张虎", "荀攸", "曹真"],
        "shu": ["刘备", "孙乾", "马云禄", "雷铜", "吴班", "陈到", "关羽",
                "周仓", "赵广", "高翔", "杨仪", "关平", "王平", "吴毅"],
        "wu": ["孙尚香", "朱治", "唐咨", "凌操", "鲁肃", "韩当", "许贡",
               "马忠", "潘璋", "吕岱", "徐琨", "是仪", "谢灵毓", "大乔"],
        "qun": ["貂蝉", "李儒", "王允", "裴元绍", "潘凤", "张任", "公孙修",
                "陈登", "武安国", "牛辅", "张济", "韩遂", "张燕", "董卓"]
    }
    
    all_good = True
    
    for deck_id, generals in expected.items():
        deck_dir = os.path.join(target_base, deck_id)
        
        print(f"\n{deck_id.upper()} 预组：")
        
        if not os.path.exists(deck_dir):
            print(f"  ❌ 目录不存在：{deck_dir}")
            all_good = False
            continue
        
        missing = []
        found = 0
        
        for general in generals:
            png_file = os.path.join(deck_dir, f"{general}.png")
            if os.path.exists(png_file):
                found += 1
                size = os.path.getsize(png_file)
                print(f"  ✓ {general:8s} ({size:>7,} bytes)")
            else:
                missing.append(general)
                print(f"  ❌ {general:8s} - 缺失")
        
        print(f"\n  统计：{found}/{len(generals)} 张卡牌")
        
        if missing:
            print(f"  缺失：{', '.join(missing)}")
            all_good = False
    
    print(f"\n{'='*60}")
    if all_good:
        print("✓ 所有卡牌图片完整！")
    else:
        print("⚠ 部分卡牌缺失，请检查")
    print(f"{'='*60}")


def clean_cards():
    """清理已复制的卡牌（保留源文件）"""
    
    target_base = "src/main/resources/static/images/cards"
    
    print("="*60)
    print("清理工具")
    print("="*60)
    
    confirm = input("\n确认要删除已复制的卡牌吗？(y/n): ").strip().lower()
    if confirm != 'y':
        print("已取消")
        return
    
    if not os.path.exists(target_base):
        print(f"✓ 目标目录不存在，无需清理")
        return
    
    total_deleted = 0
    
    for deck_id in ["wei", "shu", "wu", "qun"]:
        deck_dir = os.path.join(target_base, deck_id)
        
        if not os.path.exists(deck_dir):
            continue
        
        # 删除目录及其内容
        try:
            shutil.rmtree(deck_dir)
            print(f"✓ 已删除：{deck_dir}")
            total_deleted += 1
        except Exception as e:
            print(f"❌ 删除失败 {deck_dir}: {e}")
    
    print(f"\n✓ 清理完成，删除 {total_deleted} 个目录")


def main():
    """主函数"""
    print("="*60)
    print("卡牌图片管理工具")
    print("="*60)
    
    while True:
        print("\n请选择操作：")
        print("1. 复制卡牌 - 将裁剪后的图片复制到项目")
        print("2. 验证卡牌 - 检查卡牌是否完整")
        print("3. 清理卡牌 - 删除已复制的卡牌")
        print("0. 退出")
        
        choice = input("\n请输入选项 (0-3): ").strip()
        
        if choice == '0':
            print("\n再见！")
            break
        elif choice == '1':
            copy_cards()
        elif choice == '2':
            verify_cards()
        elif choice == '3':
            clean_cards()
        else:
            print("❌ 无效选项")


if __name__ == "__main__":
    main()
