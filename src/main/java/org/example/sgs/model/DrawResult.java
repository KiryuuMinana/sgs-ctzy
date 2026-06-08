package org.example.sgs.model;

import java.util.List;
import java.util.Set;

/**
 * 抽卡结果
 */
public class DrawResult {

    /** 本次行动的玩家：first 或 second */
    private String player;

    /** 本次抽到的武将卡 */
    private List<GeneralCard> drawnCards;

    /** 模拟是否已结束 */
    private boolean finished;

    /** 提示消息 */
    private String message;

    /** 当前回合数 */
    private int turn;

    /** 先手玩家军营剩余数量 */
    private int firstPlayerCampRemaining;

    /** 后手玩家军营剩余数量 */
    private int secondPlayerCampRemaining;

    /** 先手玩家已上阵武将列表 */
    private List<GeneralCard> firstPlayerField;

    /** 后手玩家已上阵武将列表 */
    private List<GeneralCard> secondPlayerField;

    /** 先手玩家休整区武将列表 */
    private List<GeneralCard> firstPlayerRestArea;

    /** 后手玩家休整区武将列表 */
    private List<GeneralCard> secondPlayerRestArea;

    /** 先手玩家乐不思蜀武将ID集合 */
    private Set<String> firstPlayerLeBuSiShu;

    /** 后手玩家乐不思蜀武将ID集合 */
    private Set<String> secondPlayerLeBuSiShu;

    /** 是否可以撤回 */
    private boolean canUndo;

    /** 先手玩家军营顶部武将列表 */
    private List<GeneralCard> firstPlayerCampTop;

    /** 后手玩家军营顶部武将列表 */
    private List<GeneralCard> secondPlayerCampTop;

    /** 先手玩家军营底部武将列表 */
    private List<GeneralCard> firstPlayerCampBottom;

    /** 后手玩家军营底部武将列表 */
    private List<GeneralCard> secondPlayerCampBottom;

    public DrawResult() {
    }

    // --- Getters and Setters ---

    public String getPlayer() {
        return player;
    }

    public void setPlayer(String player) {
        this.player = player;
    }

    public List<GeneralCard> getDrawnCards() {
        return drawnCards;
    }

    public void setDrawnCards(List<GeneralCard> drawnCards) {
        this.drawnCards = drawnCards;
    }

    public boolean isFinished() {
        return finished;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getTurn() {
        return turn;
    }

    public void setTurn(int turn) {
        this.turn = turn;
    }

    public int getFirstPlayerCampRemaining() {
        return firstPlayerCampRemaining;
    }

    public void setFirstPlayerCampRemaining(int firstPlayerCampRemaining) {
        this.firstPlayerCampRemaining = firstPlayerCampRemaining;
    }

    public int getSecondPlayerCampRemaining() {
        return secondPlayerCampRemaining;
    }

    public void setSecondPlayerCampRemaining(int secondPlayerCampRemaining) {
        this.secondPlayerCampRemaining = secondPlayerCampRemaining;
    }

    public List<GeneralCard> getFirstPlayerField() {
        return firstPlayerField;
    }

    public void setFirstPlayerField(List<GeneralCard> firstPlayerField) {
        this.firstPlayerField = firstPlayerField;
    }

    public List<GeneralCard> getSecondPlayerField() {
        return secondPlayerField;
    }

    public void setSecondPlayerField(List<GeneralCard> secondPlayerField) {
        this.secondPlayerField = secondPlayerField;
    }

    public List<GeneralCard> getFirstPlayerRestArea() {
        return firstPlayerRestArea;
    }

    public void setFirstPlayerRestArea(List<GeneralCard> firstPlayerRestArea) {
        this.firstPlayerRestArea = firstPlayerRestArea;
    }

    public List<GeneralCard> getSecondPlayerRestArea() {
        return secondPlayerRestArea;
    }

    public void setSecondPlayerRestArea(List<GeneralCard> secondPlayerRestArea) {
        this.secondPlayerRestArea = secondPlayerRestArea;
    }

    public Set<String> getFirstPlayerLeBuSiShu() {
        return firstPlayerLeBuSiShu;
    }

    public void setFirstPlayerLeBuSiShu(Set<String> firstPlayerLeBuSiShu) {
        this.firstPlayerLeBuSiShu = firstPlayerLeBuSiShu;
    }

    public Set<String> getSecondPlayerLeBuSiShu() {
        return secondPlayerLeBuSiShu;
    }

    public void setSecondPlayerLeBuSiShu(Set<String> secondPlayerLeBuSiShu) {
        this.secondPlayerLeBuSiShu = secondPlayerLeBuSiShu;
    }

    public boolean isCanUndo() {
        return canUndo;
    }

    public void setCanUndo(boolean canUndo) {
        this.canUndo = canUndo;
    }

    public List<GeneralCard> getFirstPlayerCampTop() {
        return firstPlayerCampTop;
    }

    public void setFirstPlayerCampTop(List<GeneralCard> firstPlayerCampTop) {
        this.firstPlayerCampTop = firstPlayerCampTop;
    }

    public List<GeneralCard> getSecondPlayerCampTop() {
        return secondPlayerCampTop;
    }

    public void setSecondPlayerCampTop(List<GeneralCard> secondPlayerCampTop) {
        this.secondPlayerCampTop = secondPlayerCampTop;
    }

    public List<GeneralCard> getFirstPlayerCampBottom() {
        return firstPlayerCampBottom;
    }

    public void setFirstPlayerCampBottom(List<GeneralCard> firstPlayerCampBottom) {
        this.firstPlayerCampBottom = firstPlayerCampBottom;
    }

    public List<GeneralCard> getSecondPlayerCampBottom() {
        return secondPlayerCampBottom;
    }

    public void setSecondPlayerCampBottom(List<GeneralCard> secondPlayerCampBottom) {
        this.secondPlayerCampBottom = secondPlayerCampBottom;
    }
}
