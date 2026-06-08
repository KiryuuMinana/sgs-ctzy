package org.example.sgs.service;

import org.example.sgs.config.DeckConfig;
import org.example.sgs.config.SkillConfig;
import org.example.sgs.model.DrawResult;
import org.example.sgs.model.GameSession;
import org.example.sgs.model.GeneralCard;
import org.example.sgs.model.PresetDeck;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 模拟器核心服务
 */
@Service
public class SimulationService {

    /** 战场武将上限（大于此值不可抽卡） */
    private static final int FIELD_MAX_LIMIT = 5;

    @Autowired
    private DeckConfig deckConfig;

    @Autowired
    private SkillConfig skillConfig;

    public DeckConfig getDeckConfig() {
        return deckConfig;
    }

    /**
     * 开始模拟
     * 主帅卡自动放置到战场
     */
    public GameSession startSimulation(String firstPlayerDeckId, String secondPlayerDeckId) {
        PresetDeck firstDeck = deckConfig.getDeck(firstPlayerDeckId);
        PresetDeck secondDeck = deckConfig.getDeck(secondPlayerDeckId);

        if (firstDeck == null || secondDeck == null) {
            throw new IllegalArgumentException("无效的预组ID");
        }

        GameSession session = new GameSession();
        session.setFirstPlayerDeck(firstDeck);
        session.setSecondPlayerDeck(secondDeck);
        session.initCamps();
        // 初始状态：先手玩家的首个回合
        session.setNextIsFirstPlayer(true);
        session.setFirstTurn(true);
        session.setCurrentTurn(1);
        session.setFinished(false);

        // 主帅卡自动上场
        GeneralCard firstCommander = deckConfig.createCommanderCard(firstPlayerDeckId);
        GeneralCard secondCommander = deckConfig.createCommanderCard(secondPlayerDeckId);
        if (firstCommander != null) {
            session.getFirstPlayerField().add(firstCommander);
        }
        if (secondCommander != null) {
            session.getSecondPlayerField().add(secondCommander);
        }

        return session;
    }

    /**
     * 执行下一步（抽卡）
     * 支持自定义抽卡数量，优先从军营顶部抽取
     */
    public DrawResult nextStep(GameSession session, Integer customDrawCount) {
        DrawResult result = new DrawResult();
    
        if (session.isFinished()) {
            result.setFinished(true);
            result.setMessage("模拟已结束");
            fillResultState(result, session);
            return result;
        }
    
        boolean isFirstPlayer = session.isNextIsFirstPlayer();
        List<GeneralCard> camp = isFirstPlayer ? session.getFirstPlayerCamp() : session.getSecondPlayerCamp();
        List<GeneralCard> campTop = isFirstPlayer ? session.getFirstPlayerCampTop() : session.getSecondPlayerCampTop();
        List<GeneralCard> field = isFirstPlayer ? session.getFirstPlayerField() : session.getSecondPlayerField();
    
        // check both players field >5, xu shu exemption
        boolean hasXuShu = hasXuShuOnField(session);
        if (!hasXuShu && (session.getFirstPlayerField().size() > FIELD_MAX_LIMIT
                || session.getSecondPlayerField().size() > FIELD_MAX_LIMIT)) {
            result.setFinished(false);
            result.setMessage("请先将战场上武将卡弃置至5或5以下");
            result.setPlayer(isFirstPlayer ? "first" : "second");
            result.setTurn(session.getCurrentTurn());
            result.setDrawnCards(Collections.emptyList());
            fillResultState(result, session);
            return result;
        }
    
        // draw count: custom or default
        int drawCount = (customDrawCount != null && customDrawCount >= 1 && customDrawCount <= 4)
                ? customDrawCount : session.getDrawCount();
    
        result.setPlayer(isFirstPlayer ? "first" : "second");
        result.setTurn(session.getCurrentTurn());
    
        // check if camp + campTop has enough generals
        int totalAvailable = camp.size() + campTop.size();
        if (totalAvailable == 0) {
            result.setFinished(true);
            result.setMessage("武将数不足，本次模拟结束");
            result.setDrawnCards(Collections.emptyList());
            session.setFinished(true);
            fillResultState(result, session);
            return result;
        }
    
        // actual draw count
        int actualDraw = Math.min(drawCount, totalAvailable);
        if (actualDraw < drawCount) {
            result.setMessage("武将数不足，本次模拟结束");
            result.setFinished(true);
            session.setFinished(true);
        } else {
            result.setMessage("上阵成功");
            result.setFinished(false);
        }
    
        // draw cards: if re-draw after undo, use same cards
        List<GeneralCard> drawn;
        Set<String> drawnFromCampTopIds = new HashSet<>();
        if (session.getLastDrawnCards() != null && !session.isCanUndo()
                && session.getLastDrawnCards().size() == actualDraw) {
            // re-draw after undo: use same cards (not random)
            drawn = new ArrayList<>(session.getLastDrawnCards());
            // 恢复之前的 campTop 来源标记
            if (session.getLastDrawnFromCampTopIds() != null) {
                drawnFromCampTopIds = new HashSet<>(session.getLastDrawnFromCampTopIds());
            }
        } else {
            drawn = drawFromCampAndTop(camp, campTop, actualDraw, drawnFromCampTopIds);
        }
        result.setDrawnCards(drawn);

        // record undo info (save state before draw)
        session.setLastDrawnCards(new ArrayList<>(drawn));
        session.setLastDrawnFromCampTopIds(new HashSet<>(drawnFromCampTopIds));
        session.setLastDrawPlayer(isFirstPlayer ? "first" : "second");
        session.setLastDrawWasFirstTurn(session.isFirstTurn());
        session.setLastDrawTurn(session.getCurrentTurn());
        session.setCanUndo(true);
    
        // remove drawn from camp (campTop already removed in drawFromCampAndTop)
        camp.removeAll(drawn);
        field.addAll(drawn);
    
        // update turn state
        if (isFirstPlayer && session.isFirstTurn()) {
            session.setFirstTurn(false);
        }
    
        // switch player
        session.setNextIsFirstPlayer(!isFirstPlayer);
    
        if (!isFirstPlayer) {
            session.setCurrentTurn(session.getCurrentTurn() + 1);
        }
    
        fillResultState(result, session);
        return result;
    }

    /**
     * 撤回上一步抽卡
     * 将抽出的武将放回军营，恢复回合状态
     * 注意：撤回后再次"下一步"时，抽出的是同一批武将（非随机重新抽）
     * 增强：撤回时区分来自campTop和camp的卡，分别放回
     */
    public DrawResult undoLastStep(GameSession session) {
        DrawResult result = new DrawResult();
    
        if (!session.isCanUndo() || session.getLastDrawnCards() == null) {
            result.setFinished(false);
            result.setMessage("无法撤回：没有可撤回的操作");
            fillResultState(result, session);
            return result;
        }
    
        List<GeneralCard> lastDrawn = session.getLastDrawnCards();
        Set<String> fromCampTopIds = session.getLastDrawnFromCampTopIds();
        String lastPlayer = session.getLastDrawPlayer();
        boolean isFirstPlayer = "first".equals(lastPlayer);
    
        List<GeneralCard> camp = isFirstPlayer ? session.getFirstPlayerCamp() : session.getSecondPlayerCamp();
        List<GeneralCard> field = isFirstPlayer ? session.getFirstPlayerField() : session.getSecondPlayerField();
        List<GeneralCard> campTop = isFirstPlayer ? session.getFirstPlayerCampTop() : session.getSecondPlayerCampTop();
    
        // 从field中移除这些卡
        field.removeAll(lastDrawn);
        // 区分放回：来自campTop的放回campTop，其他放回camp
        for (GeneralCard card : lastDrawn) {
            if (fromCampTopIds != null && fromCampTopIds.contains(card.getId())) {
                campTop.add(card); // 放回campTop栈顶
            } else {
                camp.add(card); // 放回军营
            }
        }
    
        // 恢复回合状态
        session.setNextIsFirstPlayer(isFirstPlayer);
        session.setFirstTurn(session.isLastDrawWasFirstTurn());
        session.setCurrentTurn(session.getLastDrawTurn());
        // 清除撤回标记，但保留lastDrawnCards（再次抽时使用同一批）
        session.setCanUndo(false);
    
        result.setMessage("撤回成功：上次抽出的武将已返回军营");
        result.setPlayer(lastPlayer);
        result.setTurn(session.getCurrentTurn());
        result.setFinished(false);
        result.setDrawnCards(Collections.emptyList());
        fillResultState(result, session);
        return result;
    }

    /**
     * 将战场武将置入休整区
     * 进入休整区时，乐不思蜀状态消失
     */
    public void moveToRestArea(GameSession session, String player, String cardId) {
        boolean isFirstPlayer = "first".equals(player);
        List<GeneralCard> field = isFirstPlayer ? session.getFirstPlayerField() : session.getSecondPlayerField();
        List<GeneralCard> restArea = isFirstPlayer ? session.getFirstPlayerRestArea() : session.getSecondPlayerRestArea();
        Set<String> leBuSiShu = isFirstPlayer ? session.getFirstPlayerLeBuSiShu() : session.getSecondPlayerLeBuSiShu();

        GeneralCard targetCard = null;
        for (GeneralCard card : field) {
            if (card.getId().equals(cardId)) {
                targetCard = card;
                break;
            }
        }
        if (targetCard != null) {
            field.remove(targetCard);
            restArea.add(targetCard);
            // 进入休整区时，乐不思蜀状态消失
            leBuSiShu.remove(cardId);
        }
    }

    /**
     * 将休整区武将恢复至战场
     */
    public void restoreFromRestArea(GameSession session, String player, String cardId) {
        boolean isFirstPlayer = "first".equals(player);
        List<GeneralCard> field = isFirstPlayer ? session.getFirstPlayerField() : session.getSecondPlayerField();
        List<GeneralCard> restArea = isFirstPlayer ? session.getFirstPlayerRestArea() : session.getSecondPlayerRestArea();

        GeneralCard targetCard = null;
        for (GeneralCard card : restArea) {
            if (card.getId().equals(cardId)) {
                targetCard = card;
                break;
            }
        }
        if (targetCard != null) {
            restArea.remove(targetCard);
            field.add(targetCard);
        }
    }

    /**
     * 将战场武将放回军营顶部（LIFO栈）
     * 放回时移除乐不思蜀状态
     */
    public void moveToCampTop(GameSession session, String player, String cardId) {
        boolean isFirstPlayer = "first".equals(player);
        List<GeneralCard> field = isFirstPlayer ? session.getFirstPlayerField() : session.getSecondPlayerField();
        List<GeneralCard> campTop = isFirstPlayer ? session.getFirstPlayerCampTop() : session.getSecondPlayerCampTop();
        Set<String> leBuSiShu = isFirstPlayer ? session.getFirstPlayerLeBuSiShu() : session.getSecondPlayerLeBuSiShu();

        GeneralCard targetCard = null;
        for (GeneralCard card : field) {
            if (card.getId().equals(cardId)) {
                targetCard = card;
                break;
            }
        }
        if (targetCard != null) {
            field.remove(targetCard);
            campTop.add(targetCard); // LIFO: add to end, draw from end
            leBuSiShu.remove(cardId); // remove lebusishu status

            // 放回军营顶后，禁用撤回功能（防止状态冲突）
            session.setCanUndo(false);
            // 清除 lastDrawnCards，确保下次抽卡使用 drawFromCampAndTop 而不是重抽旧卡
            session.setLastDrawnCards(null);
        }
    }

    /**
     * 切换乐不思蜀状态
     */
    public void toggleLeBuSiShu(GameSession session, String player, String cardId) {
        boolean isFirstPlayer = "first".equals(player);
        Set<String> leBuSiShu = isFirstPlayer ? session.getFirstPlayerLeBuSiShu() : session.getSecondPlayerLeBuSiShu();

        if (leBuSiShu.contains(cardId)) {
            leBuSiShu.remove(cardId);
        } else {
            leBuSiShu.add(cardId);
        }
    }

    /**
     * Check if "Xu Shu" exists on either player's battlefield
     */
    private boolean hasXuShuOnField(GameSession session) {
        for (GeneralCard card : session.getFirstPlayerField()) {
            if ("\u5F90\u5E9A".equals(card.getName())) return true;
        }
        for (GeneralCard card : session.getSecondPlayerField()) {
            if ("\u5F90\u5E9A".equals(card.getName())) return true;
        }
        return false;
    }

    /**
     * 从军营顶部+军营中抽取武将（优先从campTop栈顶抽取，再从camp随机抽取）
     * @param drawnFromCampTopIds 用于记录本次抽卡中来自campTop的武将ID
     */
    private List<GeneralCard> drawFromCampAndTop(List<GeneralCard> camp, List<GeneralCard> campTop, int count, Set<String> drawnFromCampTopIds) {
        List<GeneralCard> drawn = new ArrayList<>();

        // first, draw from campTop (LIFO: pop from end)
        while (drawn.size() < count && !campTop.isEmpty()) {
            GeneralCard card = campTop.remove(campTop.size() - 1);
            drawn.add(card);
            if (drawnFromCampTopIds != null) {
                drawnFromCampTopIds.add(card.getId());
            }
        }

        // then, randomDraw from camp for remaining
        int remaining = count - drawn.size();
        if (remaining > 0 && !camp.isEmpty()) {
            int actualRemaining = Math.min(remaining, camp.size());
            List<GeneralCard> randomDrawn = randomDraw(camp, actualRemaining);
            drawn.addAll(randomDrawn);
        }

        return drawn;
    }

    /**
     * Randomly draw specified number of generals from camp
     */
    private List<GeneralCard> randomDraw(List<GeneralCard> camp, int count) {
        List<GeneralCard> result = new ArrayList<>();
        Random random = new Random();
        List<GeneralCard> tempCamp = new ArrayList<>(camp);

        for (int i = 0; i < count; i++) {
            int index = random.nextInt(tempCamp.size());
            result.add(tempCamp.remove(index));
        }

        return result;
    }

    /**
     * 填充结果中的状态信息
     */
    private void fillResultState(DrawResult result, GameSession session) {
        result.setFirstPlayerCampRemaining(session.getFirstPlayerCamp().size() + session.getFirstPlayerCampTop().size());
        result.setSecondPlayerCampRemaining(session.getSecondPlayerCamp().size() + session.getSecondPlayerCampTop().size());
        result.setFirstPlayerField(new ArrayList<>(session.getFirstPlayerField()));
        result.setSecondPlayerField(new ArrayList<>(session.getSecondPlayerField()));
        result.setFirstPlayerRestArea(new ArrayList<>(session.getFirstPlayerRestArea()));
        result.setSecondPlayerRestArea(new ArrayList<>(session.getSecondPlayerRestArea()));
        result.setFirstPlayerLeBuSiShu(new HashSet<>(session.getFirstPlayerLeBuSiShu()));
        result.setSecondPlayerLeBuSiShu(new HashSet<>(session.getSecondPlayerLeBuSiShu()));
        result.setFirstPlayerCampTop(new ArrayList<>(session.getFirstPlayerCampTop()));
        result.setSecondPlayerCampTop(new ArrayList<>(session.getSecondPlayerCampTop()));
        result.setCanUndo(session.isCanUndo());
    }
}
