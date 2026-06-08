package org.example.sgs.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    /** 先手玩家军营顶部（放回军营顶的武将，LIFO栈） */
    private List<GeneralCard> firstPlayerCampTop;

    /** 后手玩家军营顶部（放回军营顶的武将，LIFO栈） */
    private List<GeneralCard> secondPlayerCampTop;

    /** 先手玩家军营底部（放回军营底的武将，最后才被抽出） */
    private List<GeneralCard> firstPlayerCampBottom;

    /** 后手玩家军营底部（放回军营底的武将，最后才被抽出） */
    private List<GeneralCard> secondPlayerCampBottom;

    /** 先手玩家已上阵武将 */
    private List<GeneralCard> firstPlayerField;

    /** 后手玩家已上阵武将 */
    private List<GeneralCard> secondPlayerField;

    /** 先手玩家休整区 */
    private List<GeneralCard> firstPlayerRestArea;

    /** 后手玩家休整区 */
    private List<GeneralCard> secondPlayerRestArea;

    /** 先手玩家乐不思蜀武将ID集合 */
    private Set<String> firstPlayerLeBuSiShu;

    /** 后手玩家乐不思蜀武将ID集合 */
    private Set<String> secondPlayerLeBuSiShu;

    /** 上一次抽出的武将卡（用于撤回） */
    private List<GeneralCard> lastDrawnCards;

    /** 上一次抽卡中来自campTop的武将ID集合（用于撤回时正确放回） */
    private Set<String> lastDrawnFromCampTopIds;

    /** 上一次抽卡中来自campBottom的武将ID集合（用于撤回时正确放回） */
    private Set<String> lastDrawnFromCampBottomIds;

    /** 上一次抽卡的玩家（"first"或"second"） */
    private String lastDrawPlayer;

    /** 上一次抽卡前是否为先手首回合 */
    private boolean lastDrawWasFirstTurn;

    /** 上一次抽卡前的回合数 */
    private int lastDrawTurn;

    /** 是否可以撤回 */
    private boolean canUndo;

    /** 当前回合数（从1开始） */
    private int currentTurn;

    /** 下一步是否为先手玩家行动 */
    private boolean nextIsFirstPlayer;

    /** 是否是先手玩家的首个回合 */
    private boolean isFirstTurn;

    /** 模拟是否已结束 */
    private boolean finished;

    /** 是否手动指定了抽卡玩家（抽卡后自动清除） */
    private boolean forcePlayerSpecified = false;

    public GameSession() {
        this.firstPlayerField = new ArrayList<>();
        this.secondPlayerField = new ArrayList<>();
        this.firstPlayerRestArea = new ArrayList<>();
        this.secondPlayerRestArea = new ArrayList<>();
        this.firstPlayerCampTop = new ArrayList<>();
        this.secondPlayerCampTop = new ArrayList<>();
        this.firstPlayerCampBottom = new ArrayList<>();
        this.secondPlayerCampBottom = new ArrayList<>();
        this.firstPlayerLeBuSiShu = new HashSet<>();
        this.secondPlayerLeBuSiShu = new HashSet<>();
        this.lastDrawnCards = null;
        this.lastDrawnFromCampTopIds = null;
        this.lastDrawnFromCampBottomIds = null;
        this.lastDrawPlayer = null;
        this.lastDrawWasFirstTurn = false;
        this.lastDrawTurn = 0;
        this.canUndo = false;
        this.currentTurn = 1;
        this.nextIsFirstPlayer = true;
        this.isFirstTurn = true;
        this.finished = false;
        this.forcePlayerSpecified = false;
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

    /** 获取当前行动方战场上的武将数量 */
    public int getCurrentPlayerFieldCount() {
        return nextIsFirstPlayer ? firstPlayerField.size() : secondPlayerField.size();
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

    public List<GeneralCard> getLastDrawnCards() {
        return lastDrawnCards;
    }

    public void setLastDrawnCards(List<GeneralCard> lastDrawnCards) {
        this.lastDrawnCards = lastDrawnCards;
    }

    public Set<String> getLastDrawnFromCampTopIds() {
        return lastDrawnFromCampTopIds;
    }

    public void setLastDrawnFromCampTopIds(Set<String> lastDrawnFromCampTopIds) {
        this.lastDrawnFromCampTopIds = lastDrawnFromCampTopIds;
    }

    public Set<String> getLastDrawnFromCampBottomIds() {
        return lastDrawnFromCampBottomIds;
    }

    public void setLastDrawnFromCampBottomIds(Set<String> lastDrawnFromCampBottomIds) {
        this.lastDrawnFromCampBottomIds = lastDrawnFromCampBottomIds;
    }

    public String getLastDrawPlayer() {
        return lastDrawPlayer;
    }

    public void setLastDrawPlayer(String lastDrawPlayer) {
        this.lastDrawPlayer = lastDrawPlayer;
    }

    public boolean isLastDrawWasFirstTurn() {
        return lastDrawWasFirstTurn;
    }

    public void setLastDrawWasFirstTurn(boolean lastDrawWasFirstTurn) {
        this.lastDrawWasFirstTurn = lastDrawWasFirstTurn;
    }

    public int getLastDrawTurn() {
        return lastDrawTurn;
    }

    public void setLastDrawTurn(int lastDrawTurn) {
        this.lastDrawTurn = lastDrawTurn;
    }

    public boolean isCanUndo() {
        return canUndo;
    }

    public void setCanUndo(boolean canUndo) {
        this.canUndo = canUndo;
    }

    public boolean isForcePlayerSpecified() {
        return forcePlayerSpecified;
    }

    public void setForcePlayerSpecified(boolean forcePlayerSpecified) {
        this.forcePlayerSpecified = forcePlayerSpecified;
    }
}
