package com.magisterka.web;

import com.magisterka.model.Statistics;
import com.magisterka.model.dto.PlayersStrategyDTO;
import com.magisterka.service.GameService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/game")
@AllArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
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

    @GetMapping(value = "/one/{strategy}/statistics")
    public Statistics gameWithStrategyForStatistics(@PathVariable String strategy) {
         return gameService.getStatistics(strategy);
    }

    @PostMapping(value = "/statistics")
    public Statistics gameWithStrategyForStatistics(@RequestBody PlayersStrategyDTO playersStrategyDTO) {
        return gameService.getStatisticsForTwoPlayers(playersStrategyDTO);
    }

    @GetMapping(value = "/compare/{strategy}")
    public List<Statistics> compareStrategyWithBasicStrategies(@PathVariable String strategy) {
        return gameService.compareStrategyWithBasicStrategies(strategy);
    }

}
