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
    public void gameWithStrategy(@PathVariable String strategy) {
         gameService.gameWithStrategy(strategy);
    }
}
