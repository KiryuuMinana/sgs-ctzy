package org.example.sgs.controller;

import org.example.sgs.config.ImageConfig;
import org.example.sgs.model.DrawResult;
import org.example.sgs.model.GameSession;
import org.example.sgs.model.PresetDeck;
import org.example.sgs.service.SimulationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
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
            response.put("firstPlayerCampSize", session.getFirstPlayerCamp().size());
            response.put("secondPlayerCampSize", session.getSecondPlayerCamp().size());
            response.put("isFirstTurn", true);
            response.put("nextIsFirstPlayer", true);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * 下一步（抽卡）
     */
    @PostMapping("/simulation/next")
    public ResponseEntity<?> nextStep(HttpSession httpSession) {
        GameSession session = (GameSession) httpSession.getAttribute("gameSession");

        if (session == null) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "请先开始模拟");
            return ResponseEntity.badRequest().body(error);
        }

        DrawResult result = simulationService.nextStep(session);
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
        state.put("firstPlayerCampRemaining", session.getFirstPlayerCamp().size());
        state.put("secondPlayerCampRemaining", session.getSecondPlayerCamp().size());
        state.put("firstPlayerField", session.getFirstPlayerField());
        state.put("secondPlayerField", session.getSecondPlayerField());
        state.put("firstPlayerDeck", session.getFirstPlayerDeck().getName());
        state.put("secondPlayerDeck", session.getSecondPlayerDeck().getName());
        return ResponseEntity.ok(state);
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
}
