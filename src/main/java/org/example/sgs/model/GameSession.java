package org.example.sgs.model;

import java.util.ArrayList;
import java.util.List;

/**
 * 对局会话状态
 */
public class GameSession {

    /** 先手玩家预组 */
    private PresetDeck firstPlayerDeck;

    /** 后手玩家预组 */
    private PresetDeck secondPlayerDeck;

    /** 先手玩家军营（剩余未上阵的武将） */
    private List<GeneralCard> firstPlayerCamp;

    /** 后手玩家军营（剩余未上阵的武将） */
    private List<GeneralCard> secondPlayerCamp;

    /** 先手玩家已上阵武将 */
    private List<GeneralCard> firstPlayerField;

    /** 后手玩家已上阵武将 */
    private List<GeneralCard> secondPlayerField;

    /** 当前回合数（从1开始） */
    private int currentTurn;

    /** 下一步是否为先手玩家行动 */
    private boolean nextIsFirstPlayer;

    /** 是否是先手玩家的首个回合 */
    private boolean isFirstTurn;

    /** 模拟是否已结束 */
    private boolean finished;

    public GameSession() {
        this.firstPlayerField = new ArrayList<>();
        this.secondPlayerField = new ArrayList<>();
        this.currentTurn = 1;
        this.nextIsFirstPlayer = true;
        this.isFirstTurn = true;
        this.finished = false;
    }

    /** 初始化军营（深拷贝预组的卡牌） */
    public void initCamps() {
        this.firstPlayerCamp = new ArrayList<>(firstPlayerDeck.getCards());
        this.secondPlayerCamp = new ArrayList<>(secondPlayerDeck.getCards());
    }

    /** 获取当前行动方应抽取的武将数量 */
    public int getDrawCount() {
        if (isFirstTurn && nextIsFirstPlayer) {
            return 1; // 先手首回合仅上阵1名武将
        }
        return 2;
    }

    // --- Getters and Setters ---

    public PresetDeck getFirstPlayerDeck() {
        return firstPlayerDeck;
    }

    public void setFirstPlayerDeck(PresetDeck firstPlayerDeck) {
        this.firstPlayerDeck = firstPlayerDeck;
    }

    public PresetDeck getSecondPlayerDeck() {
        return secondPlayerDeck;
    }

    public void setSecondPlayerDeck(PresetDeck secondPlayerDeck) {
        this.secondPlayerDeck = secondPlayerDeck;
    }

    public List<GeneralCard> getFirstPlayerCamp() {
        return firstPlayerCamp;
    }

    public void setFirstPlayerCamp(List<GeneralCard> firstPlayerCamp) {
        this.firstPlayerCamp = firstPlayerCamp;
    }

    public List<GeneralCard> getSecondPlayerCamp() {
        return secondPlayerCamp;
    }

    public void setSecondPlayerCamp(List<GeneralCard> secondPlayerCamp) {
        this.secondPlayerCamp = secondPlayerCamp;
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

    public int getCurrentTurn() {
        return currentTurn;
    }

    public void setCurrentTurn(int currentTurn) {
        this.currentTurn = currentTurn;
    }

    public boolean isNextIsFirstPlayer() {
        return nextIsFirstPlayer;
    }

    public void setNextIsFirstPlayer(boolean nextIsFirstPlayer) {
        this.nextIsFirstPlayer = nextIsFirstPlayer;
    }

    public boolean isFirstTurn() {
        return isFirstTurn;
    }

    public void setFirstTurn(boolean firstTurn) {
        isFirstTurn = firstTurn;
    }

    public boolean isFinished() {
        return finished;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }
}
