package com.magisterka.web;

import com.magisterka.service.GameService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/game")
@AllArgsConstructor
public class GameController {

    private final GameService gameService;

    @GetMapping(value = "/two")
    public int game() {
        return gameService.game(2);
    }

    @GetMapping(value = "/two/{strategy}")
    public Integer gameWithStrategy(@PathVariable String strategy) {
        return gameService.gameWithStrategy(strategy);
    }

    @GetMapping(value = "/two/{strategy}/statistics")
    public void gameWithStrategyForStatistics(@PathVariable String strategy) {
         gameService.getStatistics(strategy);
    }
}
