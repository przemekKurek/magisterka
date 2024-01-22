package com.magisterka.service;

import com.magisterka.model.CardDTO;
import com.magisterka.model.Player;
import com.magisterka.model.StatisticsDTO;
import com.magisterka.model.dto.PlayersStrategyDTO;
import com.magisterka.model.dto.RoundInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class CycleService {

    private final GameService gameService;

    private final int GAME_AMOUNT = 43;

    public StatisticsDTO getStatisticsWithCyclesForTwoPlayers(PlayersStrategyDTO playersStrategyDTO) {
        int player1WinsCounter = 0;
        int player2WinsCounter = 0;
        int drawCounter = 0;
        int roundsCounter = 0;
        StatisticsDTO stats = new StatisticsDTO();
        for (int i = 0; i < GAME_AMOUNT; i++) {
            RoundInfo roundInfo = gameWithCyclesFinder(playersStrategyDTO);
            Integer result = roundInfo.getRoundResult();
            if (result == 1) {
                player1WinsCounter++;
            } else if (result == 2) {
                player2WinsCounter++;
            } else if (result == 0) {
                drawCounter++;
            }
            log.info("Game number " + i);
            roundsCounter += roundInfo.getRoundLength();
            if (roundInfo.getCycle() != null) {
                stats.getDetectedCycles().add(roundInfo.getCycle());
            }
        }
        stats.setFirstPlayerWonGames(player1WinsCounter * 100.0 / GAME_AMOUNT);
        stats.setSecondPlayerWonGames(player2WinsCounter * 100.0 / GAME_AMOUNT);
        stats.setDraws(drawCounter * 100.0 / GAME_AMOUNT);
        stats.setFirstPlayerStrategy(playersStrategyDTO.getFisrtPlayerStrategySequence());
        stats.setSecondPlayerStrategy(playersStrategyDTO.getSecondPlayerStrategySequence());
        stats.setAverageAmountOfRounds(roundsCounter / GAME_AMOUNT);
        return stats;
    }


    public StatisticsDTO getStatisticsWithBreakingCyclesForTwoPlayers(PlayersStrategyDTO playersStrategyDTO) {
        int player1WinsCounter = 0;
        int player2WinsCounter = 0;
        int drawCounter = 0;
        int roundsCounter = 0;
        StatisticsDTO stats = new StatisticsDTO();
        for (int i = 0; i < GAME_AMOUNT; i++) {
            RoundInfo roundInfo = gameWithBreakingCycles(playersStrategyDTO);
            Integer result = roundInfo.getRoundResult();
            if (result == 1) {
                player1WinsCounter++;
            } else if (result == 2) {
                player2WinsCounter++;
            } else if (result == 0) {
                drawCounter++;
            }
            log.info("Game number " + i);
            roundsCounter += roundInfo.getRoundLength();
            if (roundInfo.getCycle() != null) {
                stats.getDetectedCycles().add(roundInfo.getCycle());
            }
        }
        stats.setFirstPlayerWonGames(player1WinsCounter * 100.0 / GAME_AMOUNT);
        stats.setSecondPlayerWonGames(player2WinsCounter * 100.0 / GAME_AMOUNT);
        stats.setDraws(drawCounter * 100.0 / GAME_AMOUNT);
        stats.setFirstPlayerStrategy(playersStrategyDTO.getFisrtPlayerStrategySequence());
        stats.setSecondPlayerStrategy(playersStrategyDTO.getSecondPlayerStrategySequence());
        stats.setAverageAmountOfRounds(roundsCounter / GAME_AMOUNT);
        return stats;
    }

    private RoundInfo gameWithCyclesFinder(PlayersStrategyDTO playersStrategyDTO) {
        RoundInfo roundInfo = new RoundInfo();
        List<CardDTO> cards = GameUtils.initializeDeck();
        Player player1 = new Player();
        Player player2 = new Player();
        List<Integer> register = new ArrayList<>();
        player1.setStrategySequence(playersStrategyDTO.getFisrtPlayerStrategySequence());
        player2.setStrategySequence(playersStrategyDTO.getSecondPlayerStrategySequence());
        GameUtils.assignCardsToPlayers(cards, player1, player2);
        int counter = 0;
        while (GameUtils.playerHasCards(player1) && GameUtils.playerHasCards(player2) && counter < 10000) {
            if (GameUtils.getPlayerCard(player1).getRank() > GameUtils.getPlayerCard(player2).getRank()) {
                gameService.handlePlayerWinWithStrategyAndSetRegister(player1, player2, true, register);
            } else if (Objects.equals(GameUtils.getPlayerCard(player1).getRank(), GameUtils.getPlayerCard(player2).getRank())) {
                gameService.handleWarAndSetRegister(player1, player2, true, new ArrayList<>(), register);
            } else {
                gameService.handlePlayerWinWithStrategyAndSetRegister(player1, player2, false, register);
            }
            counter++;
            if (register.size() > 0 && register.size() % 2000 == 0) {
                if (hasRepeatingSubsequence(register).length() > 0) {
                    roundInfo.setRoundResult(0);
                    roundInfo.setCycle(register.toString());
                    roundInfo.setRoundLength(counter);
                    log.info(register.toString());
                    return roundInfo;
                }
            }
        }
        roundInfo.setRoundLength(counter);
        if (player2.getCards().isEmpty()) {
            roundInfo.setRoundResult(1);
        } else if (player1.getCards().isEmpty()) {
            roundInfo.setRoundResult(2);
        } else {
            roundInfo.setRoundResult(0);
        }
        return roundInfo;
    }

    private RoundInfo gameWithBreakingCycles(PlayersStrategyDTO playersStrategyDTO) {
        RoundInfo roundInfo = new RoundInfo();
        List<CardDTO> cards = GameUtils.initializeDeck();
        Player player1 = new Player();
        Player player2 = new Player();
        List<Integer> register = new ArrayList<>();
        player1.setStrategySequence(playersStrategyDTO.getFisrtPlayerStrategySequence());
        player2.setStrategySequence(playersStrategyDTO.getSecondPlayerStrategySequence());
        GameUtils.assignCardsToPlayers(cards, player1, player2);
        int counter = 0;
        while (GameUtils.playerHasCards(player1) && GameUtils.playerHasCards(player2) && counter < 1000000) {
            if (GameUtils.getPlayerCard(player1).getRank() > GameUtils.getPlayerCard(player2).getRank()) {
                gameService.handlePlayerWinWithStrategyAndSetRegister(player1, player2, true, register);
            } else if (Objects.equals(GameUtils.getPlayerCard(player1).getRank(), GameUtils.getPlayerCard(player2).getRank())) {
                gameService.handleWarAndSetRegister(player1, player2, true, new ArrayList<>(), register);
            } else {
                gameService.handlePlayerWinWithStrategyAndSetRegister(player1, player2, false, register);
            }
            counter++;
            if (register.size() % 2000 == 0) {
                if (hasRepeatingSubsequence(register).length() > 0) {
                    register.clear();
                    player1.setStrategySequence("R");
                    player2.setStrategySequence("R");
                    for (int i = 0; i < 2000; i++) {
                        if (!GameUtils.playerHasCards(player1) || !GameUtils.playerHasCards(player2)) {
                            break;
                        }
                        if (GameUtils.getPlayerCard(player1).getRank() > GameUtils.getPlayerCard(player2).getRank()) {
                            gameService.handlePlayerWinWithStrategyAndSetRegister(player1, player2, true, register);
                        } else if (Objects.equals(GameUtils.getPlayerCard(player1).getRank(), GameUtils.getPlayerCard(player2).getRank())) {
                            gameService.handleWarAndSetRegister(player1, player2, true, new ArrayList<>(), register);
                        } else {
                            gameService.handlePlayerWinWithStrategyAndSetRegister(player1, player2, false, register);
                        }
                        counter++;
                    }
                    player1.setStrategySequence(playersStrategyDTO.getFisrtPlayerStrategySequence());
                    player2.setStrategySequence(playersStrategyDTO.getSecondPlayerStrategySequence());
                }
            }
            if (register.size() > 0 && register.size() % 4000 == 0) {
                register.subList(0, 2000).clear();
            }
        }
        roundInfo.setRoundLength(counter);
        if (player2.getCards().isEmpty()) {
            roundInfo.setRoundResult(1);
        } else if (player1.getCards().isEmpty()) {
            roundInfo.setRoundResult(2);
        } else {
            roundInfo.setRoundResult(0);
        }
        return roundInfo;
    }


    private String hasRepeatingSubsequence(List<Integer> register) {
        HashSet<String> seenSubsequences = new HashSet<>();
        Integer subsequenceLength = 52*8;
        for (int i = 0; i <= register.size() - subsequenceLength; i++) {
            StringBuilder subsequenceBuilder = new StringBuilder();
            for (int j = i; j < i + subsequenceLength; j++) {
                subsequenceBuilder.append(register.get(j));
            }
            String subsequence = subsequenceBuilder.toString();

            if (seenSubsequences.contains(subsequence)) {
                return subsequence;

            }
            seenSubsequences.add(subsequence);
        }
        return "";
    }
}
