package com.magisterka.web;

import com.magisterka.model.StatisticsDTO;
import com.magisterka.model.dto.PlayersStrategyDTO;
import com.magisterka.model.dto.StrengthDTO;
import com.magisterka.service.CycleService;
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
    private final CycleService cycleService;

    @GetMapping(value = "/two")
    public int game() {
        return gameService.game(2);
    }

    @GetMapping(value = "/two/{strategy}")
    public Integer gameWithStrategy(@PathVariable String strategy) {
        return gameService.gameWithStrategy(strategy);
    }

    @GetMapping(value = "/one/{strategy}/statistics")
    public StatisticsDTO gameWithStrategyForStatistics(@PathVariable String strategy) {
         return gameService.getStatistics(strategy);
    }

    @PostMapping(value = "/statistics")
    public StatisticsDTO gameWithStrategyForStatistics(@RequestBody PlayersStrategyDTO playersStrategyDTO) {
        return gameService.getStatisticsForTwoPlayers(playersStrategyDTO);
    }

    @PostMapping(value = "/strength-comparison")
    public StatisticsDTO compareStrength(@RequestBody StrengthDTO strengthDTO) {
        return gameService.compareStrength(strengthDTO);
    }

    @GetMapping(value = "/compare/{strategy}")
    public List<StatisticsDTO> compareStrategyWithBasicStrategies(@PathVariable String strategy) {
        return gameService.compareStrategyWithBasicStrategies(strategy);
    }

    @PostMapping(value = "/statistics-with-cycles")
    public StatisticsDTO getStatisticsAndDetectCycles(@RequestBody PlayersStrategyDTO playersStrategyDTO) {
        return cycleService.getStatisticsWithCyclesForTwoPlayers(playersStrategyDTO);
    }

    @PostMapping(value = "/statistics-with-breaking-cycles")
    public StatisticsDTO getStatisticsWithBreakingCycles(@RequestBody PlayersStrategyDTO playersStrategyDTO) {
        return cycleService.getStatisticsWithBreakingCyclesForTwoPlayers(playersStrategyDTO);
    }

}
