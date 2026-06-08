package org.example.sgs.config;

import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * 武将技能描述配置
 *
 * 【预留口子】后续只需在 SKILL_DESC_MAP 中添加武将技能描述即可
 * 格式：武将名 → 技能描述文本
 * 同理，SKILL_IMAGE_MAP 可配置技能相关图片路径
 */
@Configuration
public class SkillConfig {

    /** 武将技能描述映射（预留口子，后续填充具体文案） */
    private static final Map<String, String> SKILL_DESC_MAP = new HashMap<>();

    /** 武将技能图片映射（预留口子，后续填充具体图片地址） */
    private static final Map<String, String> SKILL_IMAGE_MAP = new HashMap<>();

    // 后续在此添加具体文案，例如：
    // static {
    //     SKILL_DESC_MAP.put("刘备", "仁德：出牌阶段，你可以将任意数量的手牌以任意方式交给其他角色...");
    //     SKILL_IMAGE_MAP.put("刘备", "/images/skills/liubei.png");
    // }

    /** 获取武将技能描述 */
    public String getSkillDescription(String generalName) {
        return SKILL_DESC_MAP.getOrDefault(generalName, "技能描述待补充");
    }

    /** 获取武将技能图片路径 */
    public String getSkillImagePath(String generalName) {
        return SKILL_IMAGE_MAP.getOrDefault(generalName, "");
    }
}