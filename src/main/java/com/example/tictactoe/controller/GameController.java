package com.example.tictactoe.controller;

import com.example.tictactoe.model.Game;
import com.example.tictactoe.service.GameService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/game")
public class GameController {

    private final GameService gameService;

    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @PostMapping("/new")
    public ResponseEntity<GameResponse> newGame() {
        Game game = gameService.newGame();
        return ResponseEntity.ok(GameResponse.from(game));
    }

    @GetMapping
    public ResponseEntity<List<GameSummary>> listGames() {
        List<GameSummary> list = gameService.listGames().stream()
                .sorted(Comparator.comparing(Game::getStartDate).reversed())
                .map(GameSummary::from)
                .toList();
        return ResponseEntity.ok(list);
    }

    @GetMapping("/{id}")
    public ResponseEntity<GameResponse> getGame(@PathVariable String id) {
        Game game = gameService.getGame(id);
        if (game == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(GameResponse.from(game));
    }

    @PostMapping("/{id}/move")
    public ResponseEntity<GameResponse> makeMove(
            @PathVariable String id,
            @RequestBody Map<String, Integer> body) {
        Integer position = body.get("position");
        if (position == null) return ResponseEntity.badRequest().build();
        Game game = gameService.makeMove(id, position);
        if (game == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(GameResponse.from(game));
    }

    public record GameSummary(String id, String name, Instant startDate, String status) {
        static GameSummary from(Game game) {
            return new GameSummary(game.getId(), game.getName(), game.getStartDate(), game.getStatus().name());
        }
    }

    public record GameResponse(
            String id,
            String name,
            Instant startDate,
            String[] board,
            String currentPlayer,
            String status) {

        static GameResponse from(Game game) {
            String[] board = new String[9];
            for (int i = 0; i < 9; i++) {
                board[i] = game.getBoard()[i] == ' ' ? "" : String.valueOf(game.getBoard()[i]);
            }
            return new GameResponse(
                    game.getId(),
                    game.getName(),
                    game.getStartDate(),
                    board,
                    String.valueOf(game.getCurrentPlayer()),
                    game.getStatus().name());
        }
    }
}
