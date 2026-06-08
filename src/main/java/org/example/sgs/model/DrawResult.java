package org.example.sgs.model;

import java.util.List;

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
}
