package org.example.sgs.model;

/**
 * 武将卡模型
 */
public class GeneralCard {

    /** 武将唯一标识 */
    private String id;

    /** 武将名称 */
    private String name;

    /** 势力：魏、蜀、吴、群 */
    private String faction;

    /** 图片路径（可自定义替换） */
    private String imagePath;

    public GeneralCard() {
    }

    public GeneralCard(String id, String name, String faction, String imagePath) {
        this.id = id;
        this.name = name;
        this.faction = faction;
        this.imagePath = imagePath;
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

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    @Override
    public String toString() {
        return name + "(" + faction + ")";
    }
}
