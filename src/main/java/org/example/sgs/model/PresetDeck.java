package org.example.sgs.model;

import java.util.List;

/**
 * 预组（军营）模型
 */
public class PresetDeck {

    /** 预组ID */
    private String id;

    /** 预组名称，如"魏国-主帅曹操" */
    private String name;

    /** 势力 */
    private String faction;

    /** 主帅名称 */
    private String commander;

    /** 预组包含的武将卡列表（包含重复卡牌） */
    private List<GeneralCard> cards;

    public PresetDeck() {
    }

    public PresetDeck(String id, String name, String faction, String commander, List<GeneralCard> cards) {
        this.id = id;
        this.name = name;
        this.faction = faction;
        this.commander = commander;
        this.cards = cards;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFaction() {
        return faction;
    }

    public void setFaction(String faction) {
        this.faction = faction;
    }

    public String getCommander() {
        return commander;
    }

    public void setCommander(String commander) {
        this.commander = commander;
    }

    public List<GeneralCard> getCards() {
        return cards;
    }

    public void setCards(List<GeneralCard> cards) {
        this.cards = cards;
    }

    /** 预组中武将卡总数 */
    public int getTotalCount() {
        return cards != null ? cards.size() : 0;
    }
}
