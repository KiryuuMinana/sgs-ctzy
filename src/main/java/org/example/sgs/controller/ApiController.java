package org.example.sgs.controller;

import org.example.sgs.config.ImageConfig;
import org.example.sgs.config.SkillConfig;
import org.example.sgs.model.DrawResult;
import org.example.sgs.model.GameSession;
import org.example.sgs.model.GeneralCard;
import org.example.sgs.model.PresetDeck;
import org.example.sgs.service.SimulationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * REST API 控制器
 */
@RestController
@RequestMapping("/api")
public class ApiController {

    @Autowired
    private SimulationService simulationService;

    @Autowired
    private ImageConfig imageConfig;

    @Autowired
    private SkillConfig skillConfig;

    /**
     * 获取所有可用预组
     */
    @GetMapping("/decks")
    public ResponseEntity<List<PresetDeck>> getDecks() {
        List<PresetDeck> decks = simulationService.getDeckConfig().getAllDecks();
        return ResponseEntity.ok(decks);
    }

    /**
     * 开始模拟
     */
    @PostMapping("/simulation/start")
    public ResponseEntity<?> startSimulation(
            @RequestBody Map<String, String> request,
            HttpSession httpSession) {

        String firstPlayerDeckId = request.get("firstPlayerDeckId");
        String secondPlayerDeckId = request.get("secondPlayerDeckId");

        if (firstPlayerDeckId == null || firstPlayerDeckId.isEmpty()
                || secondPlayerDeckId == null || secondPlayerDeckId.isEmpty()) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "请为双方玩家选择预组");
            return ResponseEntity.badRequest().body(error);
        }

        try {
            GameSession session = simulationService.startSimulation(firstPlayerDeckId, secondPlayerDeckId);
            httpSession.setAttribute("gameSession", session);

            Map<String, Object> response = new LinkedHashMap<>();
            response.put("message", "模拟开始");
            response.put("firstPlayerDeck", session.getFirstPlayerDeck().getName());
            response.put("secondPlayerDeck", session.getSecondPlayerDeck().getName());
            response.put("firstPlayerCampSize", session.getFirstPlayerCamp().size() + session.getFirstPlayerCampTop().size());
            response.put("secondPlayerCampSize", session.getSecondPlayerCamp().size() + session.getSecondPlayerCampTop().size());
            response.put("isFirstTurn", true);
            response.put("nextIsFirstPlayer", true);
            response.put("firstPlayerField", session.getFirstPlayerField());
            response.put("secondPlayerField", session.getSecondPlayerField());
            response.put("firstPlayerRestArea", session.getFirstPlayerRestArea());
            response.put("secondPlayerRestArea", session.getSecondPlayerRestArea());
            response.put("firstPlayerLeBuSiShu", session.getFirstPlayerLeBuSiShu());
            response.put("secondPlayerLeBuSiShu", session.getSecondPlayerLeBuSiShu());
            response.put("firstPlayerCampTop", session.getFirstPlayerCampTop());
            response.put("secondPlayerCampTop", session.getSecondPlayerCampTop());
            response.put("canUndo", false);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * 下一步（抽卡），支持自定义抽卡数量
     */
    @PostMapping("/simulation/next")
    public ResponseEntity<?> nextStep(@RequestBody(required = false) Map<String, Object> request,
                                      HttpSession httpSession) {
        GameSession session = (GameSession) httpSession.getAttribute("gameSession");

        if (session == null) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "请先开始模拟");
            return ResponseEntity.badRequest().body(error);
        }

        Integer drawCount = null;
        if (request != null && request.get("drawCount") != null) {
            try {
                drawCount = Integer.parseInt(request.get("drawCount").toString());
            } catch (NumberFormatException e) {
                // ignore, will use default
            }
        }

        DrawResult result = simulationService.nextStep(session, drawCount);
        return ResponseEntity.ok(result);
    }

    /**
     * 获取当前对局状态
     */
    @GetMapping("/simulation/state")
    public ResponseEntity<?> getState(HttpSession httpSession) {
        GameSession session = (GameSession) httpSession.getAttribute("gameSession");

        if (session == null) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "尚未开始模拟");
            return ResponseEntity.badRequest().body(error);
        }

        Map<String, Object> state = new LinkedHashMap<>();
        state.put("currentTurn", session.getCurrentTurn());
        state.put("nextIsFirstPlayer", session.isNextIsFirstPlayer());
        state.put("isFirstTurn", session.isFirstTurn());
        state.put("finished", session.isFinished());
        state.put("firstPlayerCampRemaining", session.getFirstPlayerCamp().size() + session.getFirstPlayerCampTop().size());
        state.put("secondPlayerCampRemaining", session.getSecondPlayerCamp().size() + session.getSecondPlayerCampTop().size());
        state.put("firstPlayerField", session.getFirstPlayerField());
        state.put("secondPlayerField", session.getSecondPlayerField());
        state.put("firstPlayerRestArea", session.getFirstPlayerRestArea());
        state.put("secondPlayerRestArea", session.getSecondPlayerRestArea());
        state.put("firstPlayerLeBuSiShu", session.getFirstPlayerLeBuSiShu());
        state.put("secondPlayerLeBuSiShu", session.getSecondPlayerLeBuSiShu());
        state.put("firstPlayerCampTop", session.getFirstPlayerCampTop());
        state.put("secondPlayerCampTop", session.getSecondPlayerCampTop());
        state.put("firstPlayerDeck", session.getFirstPlayerDeck().getName());
        state.put("secondPlayerDeck", session.getSecondPlayerDeck().getName());
        state.put("canUndo", session.isCanUndo());
        return ResponseEntity.ok(state);
    }

    /**
     * 撤回上一步
     */
    @PostMapping("/simulation/undo")
    public ResponseEntity<?> undoLastStep(HttpSession httpSession) {
        GameSession session = (GameSession) httpSession.getAttribute("gameSession");

        if (session == null) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "请先开始模拟");
            return ResponseEntity.badRequest().body(error);
        }

        DrawResult result = simulationService.undoLastStep(session);
        return ResponseEntity.ok(result);
    }

    /**
     * 将战场武将置入休整区
     */
    @PostMapping("/simulation/card/rest")
    public ResponseEntity<?> moveCardToRest(@RequestBody Map<String, String> request,
                                             HttpSession httpSession) {
        GameSession session = (GameSession) httpSession.getAttribute("gameSession");
        if (session == null) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("error", "请先开始模拟"));
        }
        String player = request.get("player");
        String cardId = request.get("cardId");
        if (player == null || cardId == null) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("error", "缺少参数"));
        }
        simulationService.moveToRestArea(session, player, cardId);
        return ResponseEntity.ok(getStateMap(session));
    }

    /**
     * 将休整区武将恢复至战场
     */
    @PostMapping("/simulation/card/restore")
    public ResponseEntity<?> restoreCard(@RequestBody Map<String, String> request,
                                          HttpSession httpSession) {
        GameSession session = (GameSession) httpSession.getAttribute("gameSession");
        if (session == null) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("error", "请先开始模拟"));
        }
        String player = request.get("player");
        String cardId = request.get("cardId");
        if (player == null || cardId == null) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("error", "缺少参数"));
        }
        simulationService.restoreFromRestArea(session, player, cardId);
        return ResponseEntity.ok(getStateMap(session));
    }

    /**
     * 切换乐不思蜀状态
     */
    @PostMapping("/simulation/card/lebusishu")
    public ResponseEntity<?> toggleLeBuSiShu(@RequestBody Map<String, String> request,
                                              HttpSession httpSession) {
        GameSession session = (GameSession) httpSession.getAttribute("gameSession");
        if (session == null) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("error", "请先开始模拟"));
        }
        String player = request.get("player");
        String cardId = request.get("cardId");
        if (player == null || cardId == null) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("error", "缺少参数"));
        }
        simulationService.toggleLeBuSiShu(session, player, cardId);
        return ResponseEntity.ok(getStateMap(session));
    }

    /**
     * 将战场武将放回军营顶部
     */
    @PostMapping("/simulation/card/camp-top")
    public ResponseEntity<?> moveCardToCampTop(@RequestBody Map<String, String> request,
                                                HttpSession httpSession) {
        GameSession session = (GameSession) httpSession.getAttribute("gameSession");
        if (session == null) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("error", "请先开始模拟"));
        }
        String player = request.get("player");
        String cardId = request.get("cardId");
        if (player == null || cardId == null) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("error", "缺少参数"));
        }
        simulationService.moveToCampTop(session, player, cardId);
        return ResponseEntity.ok(getStateMap(session));
    }

    /**
     * 获取武将技能描述
     */
    @GetMapping("/card/skill")
    public ResponseEntity<?> getCardSkill(@RequestParam String name) {
        Map<String, Object> skillInfo = new HashMap<>();
        skillInfo.put("name", name);
        skillInfo.put("description", skillConfig.getSkillDescription(name));
        skillInfo.put("imagePath", skillConfig.getSkillImagePath(name));
        return ResponseEntity.ok(skillInfo);
    }

    /**
     * 重置模拟
     */
    @PostMapping("/simulation/reset")
    public ResponseEntity<?> resetSimulation(HttpSession httpSession) {
        httpSession.removeAttribute("gameSession");
        Map<String, String> response = new HashMap<>();
        response.put("message", "模拟已重置");
        return ResponseEntity.ok(response);
    }

    /**
     * 构造完整状态Map
     */
    private Map<String, Object> getStateMap(GameSession session) {
        Map<String, Object> state = new LinkedHashMap<>();
        state.put("currentTurn", session.getCurrentTurn());
        state.put("nextIsFirstPlayer", session.isNextIsFirstPlayer());
        state.put("isFirstTurn", session.isFirstTurn());
        state.put("finished", session.isFinished());
        state.put("firstPlayerCampRemaining", session.getFirstPlayerCamp().size() + session.getFirstPlayerCampTop().size());
        state.put("secondPlayerCampRemaining", session.getSecondPlayerCamp().size() + session.getSecondPlayerCampTop().size());
        state.put("firstPlayerField", session.getFirstPlayerField());
        state.put("secondPlayerField", session.getSecondPlayerField());
        state.put("firstPlayerRestArea", session.getFirstPlayerRestArea());
        state.put("secondPlayerRestArea", session.getSecondPlayerRestArea());
        state.put("firstPlayerLeBuSiShu", session.getFirstPlayerLeBuSiShu());
        state.put("secondPlayerLeBuSiShu", session.getSecondPlayerLeBuSiShu());
        state.put("firstPlayerCampTop", session.getFirstPlayerCampTop());
        state.put("secondPlayerCampTop", session.getSecondPlayerCampTop());
        state.put("firstPlayerDeck", session.getFirstPlayerDeck().getName());
        state.put("secondPlayerDeck", session.getSecondPlayerDeck().getName());
        state.put("canUndo", session.isCanUndo());
        return state;
    }
}
