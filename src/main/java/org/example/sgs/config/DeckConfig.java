package org.example.sgs.config;

import org.example.sgs.model.GeneralCard;
import org.example.sgs.model.PresetDeck;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 预组数据配置
 * 包含4个预组的武将卡信息
 */
@Configuration
public class DeckConfig {

    @Autowired
    private ImageConfig imageConfig;

    @Autowired
    private SkillConfig skillConfig;

    /** 预组ID -> 预组 */
    private Map<String, PresetDeck> decks = new HashMap<>();

    @PostConstruct
    public void init() {
        decks.put("wei", createWeiDeck());
        decks.put("shu", createShuDeck());
        decks.put("wu", createWuDeck());
        decks.put("qun", createQunDeck());
    }

    /**
     * 【预组1-魏国-主帅曹操】
     * 乐进、曹昂、徐晃、荀彧、邓艾、王双、程昱、夏侯杰、文聘、甄姬、张虎、荀攸、曹真
     * 每张武将卡均有3张
     */
    private PresetDeck createWeiDeck() {
        List<GeneralCard> cards = new ArrayList<>();
        String[] names = {"乐进", "曹昂", "徐晃", "荀彧", "邓艾", "王双", "程昱", "夏侯杰", "文聘", "甄姬", "张虎", "荀攸", "曹真"};
        for (String name : names) {
            for (int i = 0; i < 3; i++) {
                String cardId = "wei_" + name + "_" + i;
                cards.add(new GeneralCard(cardId, name, "魏", imageConfig.getCardImagePath("wei/" + name)));
            }
        }
        return new PresetDeck("wei", "魏国-主帅曹操", "魏", "曹操", cards);
    }

    /**
     * 【预组2-蜀国-主帅刘备】
     * 刘备*2、孙乾*3、马云禄*1、雷铜*3、吴班*3、陈到*3、关羽*3、周仓*3、赵广*3、高翔*3、杨仪*3、关平*3、王平*3、吴毅*3
     */
    private PresetDeck createShuDeck() {
        List<GeneralCard> cards = new ArrayList<>();
        String[][] nameCounts = {
                {"刘备", "2"}, {"孙乾", "3"}, {"马云禄", "1"}, {"雷铜", "3"}, {"吴班", "3"},
                {"陈到", "3"}, {"关羽", "3"}, {"周仓", "3"}, {"赵广", "3"}, {"高翔", "3"},
                {"杨仪", "3"}, {"关平", "3"}, {"王平", "3"}, {"吴毅", "3"}
        };
        for (String[] nc : nameCounts) {
            String name = nc[0];
            int count = Integer.parseInt(nc[1]);
            for (int i = 0; i < count; i++) {
                String cardId = "shu_" + name + "_" + i;
                cards.add(new GeneralCard(cardId, name, "蜀", imageConfig.getCardImagePath("shu/" + name)));
            }
        }
        return new PresetDeck("shu", "蜀国-主帅刘备", "蜀", "刘备", cards);
    }

    /**
     * 【预组3-吴国-孙尚香主帅】
     * 朱治、唐咨、凌操、鲁肃、韩当、许贡、马忠、潘璋、吕岱、徐琨、是仪、谢灵毓、大乔
     * 每张武将卡均有3张
     */
    private PresetDeck createWuDeck() {
        List<GeneralCard> cards = new ArrayList<>();
        String[] names = {"朱治", "唐咨", "凌操", "鲁肃", "韩当", "许贡", "马忠", "潘璋", "吕岱", "徐琨", "是仪", "谢灵毓", "大乔"};
        for (String name : names) {
            for (int i = 0; i < 3; i++) {
                String cardId = "wu_" + name + "_" + i;
                cards.add(new GeneralCard(cardId, name, "吴", imageConfig.getCardImagePath("wu/" + name)));
            }
        }
        return new PresetDeck("wu", "吴国-孙尚香主帅", "吴", "孙尚香", cards);
    }

    /**
     * 【预组4-群雄-貂蝉主帅】
     * 李儒、王允、裴元绍、潘凤、张任、公孙修、陈登、武安国、牛辅、张济、韩遂、张燕、董卓
     * 每张武将卡均有3张
     */
    private PresetDeck createQunDeck() {
        List<GeneralCard> cards = new ArrayList<>();
        String[] names = {"李儒", "王允", "裴元绍", "潘凤", "张任", "公孙修", "陈登", "武安国", "牛辅", "张济", "韩遂", "张燕", "董卓"};
        for (String name : names) {
            for (int i = 0; i < 3; i++) {
                String cardId = "qun_" + name + "_" + i;
                cards.add(new GeneralCard(cardId, name, "群", imageConfig.getCardImagePath("qun/" + name)));
            }
        }
        return new PresetDeck("qun", "群雄-貂蝉主帅", "群", "貂蝉", cards);
    }

    public Map<String, PresetDeck> getDecks() {
        return decks;
    }

    public PresetDeck getDeck(String id) {
        return decks.get(id);
    }

    public List<PresetDeck> getAllDecks() {
        return new ArrayList<>(decks.values());
    }

    /** 根据预组ID创建主帅武将卡 */
    public GeneralCard createCommanderCard(String deckId) {
        PresetDeck deck = decks.get(deckId);
        if (deck == null) {
            return null;
        }
        String commanderName = deck.getCommander();
        String faction = deck.getFaction();
        String commanderId = deckId + "_commander_" + commanderName;
        GeneralCard commanderCard = new GeneralCard(commanderId, commanderName, faction,
                imageConfig.getCardImagePath(deckId + "/" + commanderName), true);
        commanderCard.setSkillDescription(skillConfig.getSkillDescription(commanderName));
        commanderCard.setSkillImagePath(skillConfig.getSkillImagePath(commanderName));
        return commanderCard;
    }
}
