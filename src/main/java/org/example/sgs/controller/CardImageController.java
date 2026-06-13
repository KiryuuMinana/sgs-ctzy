package org.example.sgs.controller;

import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 武将卡图片控制器
 * 解决 Railway/Linux JAR 环境下中文文件名静态资源 404 的问题
 * 将中文 URL 请求映射到拼音文件名
 */
@RestController
public class CardImageController {

    private static final Map<String, String> NAME_TO_PINYIN = new HashMap<>();

    static {
        // 魏国 (14人)
        NAME_TO_PINYIN.put("乐进", "yuejin");
        NAME_TO_PINYIN.put("夏侯杰", "xiahoudjie");
        NAME_TO_PINYIN.put("张虎", "zhanghu");
        NAME_TO_PINYIN.put("徐晃", "xuhuang");
        NAME_TO_PINYIN.put("文聘", "wenpin");
        NAME_TO_PINYIN.put("曹操", "caocao");
        NAME_TO_PINYIN.put("曹昂", "caoang");
        NAME_TO_PINYIN.put("曹真", "caozhen");
        NAME_TO_PINYIN.put("王双", "wangshuang");
        NAME_TO_PINYIN.put("甄姬", "zhenji");
        NAME_TO_PINYIN.put("程昱", "chengyu");
        NAME_TO_PINYIN.put("荀彧", "xunyu");
        NAME_TO_PINYIN.put("荀攸", "xunyou");
        NAME_TO_PINYIN.put("邓艾", "dengai");

        // 蜀国 (14人)
        NAME_TO_PINYIN.put("关平", "guanping");
        NAME_TO_PINYIN.put("关羽", "guanyu");
        NAME_TO_PINYIN.put("刘备", "liubei");
        NAME_TO_PINYIN.put("吴毅", "wuyi");
        NAME_TO_PINYIN.put("吴班", "wuban");
        NAME_TO_PINYIN.put("周仓", "zhoucang");
        NAME_TO_PINYIN.put("孙乾", "sunqian");
        NAME_TO_PINYIN.put("杨仪", "yangyi");
        NAME_TO_PINYIN.put("王平", "wangping");
        NAME_TO_PINYIN.put("赵广", "zhaoguang");
        NAME_TO_PINYIN.put("陈到", "chendao");
        NAME_TO_PINYIN.put("雷铜", "leitong");
        NAME_TO_PINYIN.put("马云禄", "mayunlu");
        NAME_TO_PINYIN.put("高翔", "gaoxiang");

        // 吴国 (14人)
        NAME_TO_PINYIN.put("凌操", "lingcao");
        NAME_TO_PINYIN.put("吕岱", "lvdai");
        NAME_TO_PINYIN.put("唐咨", "tangzi");
        NAME_TO_PINYIN.put("大乔", "daqiao");
        NAME_TO_PINYIN.put("孙尚香", "sunshangxiang");
        NAME_TO_PINYIN.put("徐琨", "xukun");
        NAME_TO_PINYIN.put("是仪", "shiyi");
        NAME_TO_PINYIN.put("朱治", "zhuzhi");
        NAME_TO_PINYIN.put("潘璋", "panzhang");
        NAME_TO_PINYIN.put("许贡", "xugong");
        NAME_TO_PINYIN.put("谢灵毓", "xielingyu");
        NAME_TO_PINYIN.put("韩当", "handang");
        NAME_TO_PINYIN.put("马忠", "mazhong");
        NAME_TO_PINYIN.put("鲁肃", "lusu");

        // 群雄 (14人)
        NAME_TO_PINYIN.put("公孙修", "gongsunxiu");
        NAME_TO_PINYIN.put("张任", "zhangren");
        NAME_TO_PINYIN.put("张济", "zhangji");
        NAME_TO_PINYIN.put("张燕", "zhangyan");
        NAME_TO_PINYIN.put("李儒", "liru");
        NAME_TO_PINYIN.put("武安国", "wuanguo");
        NAME_TO_PINYIN.put("潘凤", "panfeng");
        NAME_TO_PINYIN.put("牛辅", "niufu");
        NAME_TO_PINYIN.put("王允", "wangyun");
        NAME_TO_PINYIN.put("董卓", "dongzhuo");
        NAME_TO_PINYIN.put("裴元绍", "peiyuanshao");
        NAME_TO_PINYIN.put("貂蝉", "diaochan");
        NAME_TO_PINYIN.put("陈登", "chendeng");
        NAME_TO_PINYIN.put("韩遂", "hansui");
    }

    @GetMapping("/images/cards/{faction}/{filename}")
    public ResponseEntity<?> getCardImage(
            @PathVariable String faction,
            @PathVariable String filename) throws IOException {

        // filename 格式: "曹操.png"
        String name = filename.replace(".png", "");
        String pinyin = NAME_TO_PINYIN.get(name);

        if (pinyin == null) {
            return ResponseEntity.notFound().build();
        }

        String resourcePath = "static/images/cards/" + faction + "/" + pinyin + ".png";
        ClassPathResource resource = new ClassPathResource(resourcePath);

        if (!resource.exists()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .contentLength(resource.contentLength())
                .body(resource);
    }
}
