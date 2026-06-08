package org.example.sgs.service;

import org.example.sgs.config.DeckConfig;
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

    @Autowired
    private DeckConfig deckConfig;

    public DeckConfig getDeckConfig() {
        return deckConfig;
    }

    /**
     * 开始模拟
     * @param firstPlayerDeckId  先手玩家预组ID
     * @param secondPlayerDeckId 后手玩家预组ID
     * @return 初始的对局会话
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

        return session;
    }

    /**
     * 执行下一步（抽卡）
     * @param session 当前对局会话
     * @return 抽卡结果
     */
    public DrawResult nextStep(GameSession session) {
        DrawResult result = new DrawResult();

        if (session.isFinished()) {
            result.setFinished(true);
            result.setMessage("模拟已结束");
            return result;
        }

        boolean isFirstPlayer = session.isNextIsFirstPlayer();
        List<GeneralCard> camp = isFirstPlayer ? session.getFirstPlayerCamp() : session.getSecondPlayerCamp();
        List<GeneralCard> field = isFirstPlayer ? session.getFirstPlayerField() : session.getSecondPlayerField();

        int drawCount = session.getDrawCount();

        result.setPlayer(isFirstPlayer ? "first" : "second");
        result.setTurn(session.getCurrentTurn());

        // 检查军营中是否有足够的武将
        if (camp.isEmpty()) {
            result.setFinished(true);
            result.setMessage("武将数不足，本次模拟结束");
            result.setDrawnCards(Collections.emptyList());
            session.setFinished(true);
            fillResultState(result, session);
            return result;
        }

        // 实际可抽取数量
        int actualDraw = Math.min(drawCount, camp.size());
        if (actualDraw < drawCount) {
            // 不足以上阵指定数量的武将
            result.setMessage("武将数不足，本次模拟结束");
            result.setFinished(true);
            session.setFinished(true);
        } else {
            result.setMessage("上阵成功");
            result.setFinished(false);
        }

        // 随机抽取武将
        List<GeneralCard> drawn = randomDraw(camp, actualDraw);
        result.setDrawnCards(drawn);

        // 从军营移除，加入已上阵列表
        camp.removeAll(drawn);
        field.addAll(drawn);

        // 更新回合状态
        if (isFirstPlayer && session.isFirstTurn()) {
            // 先手首回合结束，标记不再是首回合
            session.setFirstTurn(false);
        }

        // 切换行动方
        session.setNextIsFirstPlayer(!isFirstPlayer);

        // 如果切换到先手玩家，回合数+1（后手行动完后算一个完整轮次）
        // 实际上回合数按先手方行动次数计算
        // 先手 -> 后手 -> 先手(回合+1) -> 后手 -> ...
        // 但这里的"回合"更自然的理解是：每次有人行动就是一个回合
        // 按需求理解：先手首回合 -> 后手回合 -> 先手回合 -> 后手回合...
        // 回合数在先手玩家行动时递增
        if (!isFirstPlayer) {
            // 后手行动完，下一个是先手，回合数+1
            session.setCurrentTurn(session.getCurrentTurn() + 1);
        }

        fillResultState(result, session);
        return result;
    }

    /**
     * 随机从军营中抽取指定数量的武将
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
        result.setFirstPlayerCampRemaining(session.getFirstPlayerCamp().size());
        result.setSecondPlayerCampRemaining(session.getSecondPlayerCamp().size());
        result.setFirstPlayerField(new ArrayList<>(session.getFirstPlayerField()));
        result.setSecondPlayerField(new ArrayList<>(session.getSecondPlayerField()));
    }
}
