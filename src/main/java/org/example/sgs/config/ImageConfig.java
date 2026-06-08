package org.example.sgs.config;

import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * 图片路径配置
 * 
 * 【重要】修改图片路径的口子：
 * 1. 修改 CARD_IMAGE_BASE_PATH 可更换武将卡图片的基础路径
 * 2. 修改 CARD_IMAGE_MAP 可为每个武将单独指定图片路径
 * 3. 武将图片的查找顺序：CARD_IMAGE_MAP中精确匹配 -> BASE_PATH + 武将名 + CARD_IMAGE_SUFFIX
 * 
 * 图片路径可以是：
 * - 本地相对路径（如 /images/cards/xxx.png），放在 src/main/resources/static/ 下
 * - 外部URL（如 https://example.com/cards/xxx.png）
 */
@Configuration
public class ImageConfig {

    // ==================== 图片路径配置区（可自由修改） ====================

    /** 武将卡图片基础路径 */
    private static final String CARD_IMAGE_BASE_PATH = "/images/cards/";

    /** 武将卡图片后缀 */
    private static final String CARD_IMAGE_SUFFIX = ".png";

    /** 卡背图片路径 */
    private static final String CARD_BACK_IMAGE = "/images/card-back.png";

    /** 背景图片路径 */
    private static final String BACKGROUND_IMAGE = "/images/bg.jpg";

    /**
     * 武将名 -> 自定义图片路径的映射
     * 如果某个武将的图片名与武将名不一致，可在此处指定
     * 留空表示使用默认规则（BASE_PATH + 武将名 + SUFFIX）
     */
    private static final Map<String, String> CARD_IMAGE_MAP = new HashMap<>();

    // 如果需要为特定武将指定自定义图片路径，取消下面的注释并修改：
    // static {
    //     CARD_IMAGE_MAP.put("曹操", "/images/cards/custom_caocao.png");
    //     CARD_IMAGE_MAP.put("刘备", "https://example.com/liubei.png");
    // }

    // ==================================================================

    /**
     * 获取武将卡图片路径
     * 优先使用CARD_IMAGE_MAP中的自定义路径，否则使用默认规则
     */
    public String getCardImagePath(String generalName) {
        if (CARD_IMAGE_MAP.containsKey(generalName)) {
            return CARD_IMAGE_MAP.get(generalName);
        }
        return CARD_IMAGE_BASE_PATH + generalName + CARD_IMAGE_SUFFIX;
    }

    public String getCardBackImage() {
        return CARD_BACK_IMAGE;
    }

    public String getBackgroundImage() {
        return BACKGROUND_IMAGE;
    }

    public String getCardImageBasePath() {
        return CARD_IMAGE_BASE_PATH;
    }

    public String getCardImageSuffix() {
        return CARD_IMAGE_SUFFIX;
    }
}
