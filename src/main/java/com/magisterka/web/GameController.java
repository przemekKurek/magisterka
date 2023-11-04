package com.magisterka.web;

import com.magisterka.service.GameService;
import lombok.Data;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Data
@RequestMapping("/")
public class GameController {

    private final GameService gameService;


    @GetMapping(value = "/game")
    public int game()
    {
      return gameService.game(2);
    }
}
