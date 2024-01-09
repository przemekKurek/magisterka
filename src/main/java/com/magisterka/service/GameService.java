package com.magisterka.service;

import com.magisterka.model.Card;
import com.magisterka.model.Player;
import com.magisterka.model.Statistics;
import com.magisterka.model.dto.PlayersStrategyDTO;
import com.magisterka.model.dto.StrengthDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
public class GameService {

    public int game(Integer numberOfPlayers) {
        if (numberOfPlayers == 2) {
            List<Card> cards = initializeDeck();
            Player player1 = new Player();
            Player player2 = new Player();
            assignCardsToPlayers(cards, player1, player2);
            int counter = 0;
            while (!player1.getCards().isEmpty() && !player2.getCards().isEmpty() && counter < 1000) {
                if (player1.getCards().get(0).getRank() > player2.getCards().get(0).getRank()) {
                    handlePlayer1Wins(player1, player2);
                } else if (player1.getCards().get(0).getRank() == player2.getCards().get(0).getRank()) {
                    handleWar(player1, player2, true, new ArrayList<>());
                } else {
                    handlePlayer2Wins(player1, player2);
                }
                counter++;
            }
            if (player1.getCards().size() == 0 || player2.getCards().size() == 0) {
                return player1.getCards().isEmpty() ? 20 : 10;
            } else {
                return player1.getCards().size() > player2.getCards().size() ? 1 : 2;
            }
        }
        return 0;
    }


    private void handleWar(Player player1, Player player2, boolean firstWar, List<Card> warCards) {
        Integer[] cardNumbers = new Integer[2];

        setCardsForWar(player1, player2, warCards, firstWar, cardNumbers);

        if (isDeckEmpty(player1) && !isDeckEmpty(player2)) {
            player2.getCards().addAll(warCards);
        } else if (!isDeckEmpty(player1) && isDeckEmpty(player2)) {
            player1.getCards().addAll(warCards);
        } else if (!isDeckEmpty(player1) && !isDeckEmpty(player2)) {
            if (cardNumbers[0] / 4 > cardNumbers[1] / 4) {
                player1.getCards().addAll(warCards);
            } else if (cardNumbers[0] / 4 < cardNumbers[1] / 4) {
                player2.getCards().addAll(warCards);
            } else {
                handleWar(player1, player2, false, warCards);
            }
        }
    }


    private void setCardsForWar(Player player1, Player player2, List<Card> warCards, boolean isFirstWar, Integer[] cardNumbers) {
        int cardsToPlay = 2;
        if (isFirstWar) {
            cardsToPlay = 3;
        }
        // Each player puts three cards face down
        for (int i = 1; i <= cardsToPlay; i++) {
            if (!player1.getCards().isEmpty() && !player2.getCards().isEmpty()) {
                warCards.add(player1.getCards().get(0));
                warCards.add(player2.getCards().get(0));
                if (i == cardsToPlay) {
                    cardNumbers[0] = player1.getCards().get(0).getCardNumber();
                    cardNumbers[1] = player2.getCards().get(0).getCardNumber();
                }
                player1.getCards().remove(0);
                player2.getCards().remove(0);
            }
        }
    }

    private boolean isDeckEmpty(Player player) {
        return player.getCards().isEmpty();
    }

    private void handlePlayer1Wins(Player player1, Player player2) {
        Card player1Card = player1.getCards().get(0);
        Card player2Card = player2.getCards().get(0);
        removeCards(player1, player2);
        player1.getCards().addAll(Arrays.asList(player1Card, player2Card));
        player1.setWinCounter(player1.getWinCounter() + 1);
    }

    private void sortCardsAscending(List<Card> cards) {
        cards.sort(Comparator.comparing(Card::getCardNumber));
    }

    private void sortCardsDescending(List<Card> cards) {
        cards.sort(Comparator.comparing(Card::getCardNumber, Comparator.reverseOrder()));
    }

    private void handlePlayerWinWithStrategy(Player player1, Player player2, boolean hasPlayer1Won) {
        Card player1Card = player1.getCards().get(0);
        Card player2Card = player2.getCards().get(0);
        List<Card> cardsToGet = new ArrayList<>();
        cardsToGet.add(player1Card);
        cardsToGet.add(player2Card);
        removeCards(player1, player2);
        if (hasPlayer1Won) {
            sortCardsAccordingToStrategy(player1, player2, cardsToGet, true);
            player1.getCards().addAll(cardsToGet);
            player1.winCounterIncrement();
        } else {
            sortCardsAccordingToStrategy(player1, player2, cardsToGet, false);
            player2.getCards().addAll(cardsToGet);
            player2.winCounterIncrement();
        }
    }

    private void sortCardsAccordingToStrategy(Player player1, Player player2, List<Card> cardsToGet, boolean hasPlayer1Won) {
        Player winnerOfTheRound = hasPlayer1Won ? player1 : player2;
        if (getStrategy(winnerOfTheRound) == 'H') {
            sortCardsDescending(cardsToGet);
        } else if (getStrategy(winnerOfTheRound) == 'L') {
            sortCardsAscending(cardsToGet);
        } else if (getStrategy(winnerOfTheRound) == 'R') {
            shuffleDeck(cardsToGet);
        } else if (getStrategy(winnerOfTheRound) == 'G' || getStrategy(winnerOfTheRound) == 'A' || getStrategy(winnerOfTheRound) == 'N') {
            if (getStrategy(winnerOfTheRound) == 'G') {
                distributeCardsGreedy(cardsToGet, player1, player2, 'G', hasPlayer1Won);
            } else if (getStrategy(winnerOfTheRound) == 'A') {
                distributeCardsGreedy(cardsToGet, player1, player2, 'A', hasPlayer1Won);
            } else {
                distributeCardsGreedy(cardsToGet, player1, player2, 'N', hasPlayer1Won);

            }
        }
    }

    private void distributeCardsGreedy(List<Card> cardsToGet, Player player1, Player player2, char greedyOption, boolean hasPlayer1Won) {
        Player winner = hasPlayer1Won ? player1 : player2;
        Player loser = hasPlayer1Won ? player2 : player1;
        if (winner.getCards().size() >= loser.getCards().size()) {
            shuffleDeck(cardsToGet);
        } else {
            int winnerDeckSize = winner.getCards().size();
            List<Card> loserCardsToCompare = new ArrayList<>();
            loserCardsToCompare.add(loser.getCards().get(winnerDeckSize));
            loserCardsToCompare.add(loser.getCards().get(winnerDeckSize + 1));
            sortCardsAscending(cardsToGet);
            Integer c1 = cardsToGet.get(0).getRank();
            Integer c2 = cardsToGet.get(1).getRank();
            Integer p1 = loserCardsToCompare.get(0).getRank();
            Integer p2 = loserCardsToCompare.get(1).getRank();
            long winnerAces = winner.getCards().stream().filter(card -> card.getRank() == 12).count();
            long loserAces = loser.getCards().stream().filter(card -> card.getRank() == 12).count();
            boolean moreAces = winnerAces > loserAces;
            // b -> better, e -> equal, w - worse
            boolean bb = c1 > p1 && c2 > p2;
            boolean be = c1 > p1 && c2 == p2;
            boolean bw = c1 > p1 && c2 < p2;
            boolean eb = c1 == p1 && c2 > p2;
            boolean ee = c1 == p1 && c2 == p2;
            boolean ew = c1 == p1 && c2 < p2;
            boolean wb = c1 < p1 && c2 > p2;
            boolean we = c1 < p1 && c2 == p2;
            boolean ww = c1 < p1 && c2 < p2;
            if (bb || bw || ww) {
                sortCardsDescending(cardsToGet);
            } else if (be || eb || ee || ew || we) {
                if (greedyOption == 'A') {
                    sortCardsDescending(cardsToGet);
                } else if (greedyOption == 'N') {
                    sortCardsAscending(cardsToGet);
                } else {
                    if (moreAces) {
                        sortCardsDescending(cardsToGet);
                    } else {
                        sortCardsAscending(cardsToGet);
                    }
                }
            }
            else if (wb) {
                sortCardsAscending(cardsToGet);
            }
        }
    }


    private char getStrategy(Player player) {
        int indexOfCharacter = player.getWinCounter().intValue() % player.getStrategySequence().length();
        return player.getStrategySequence().charAt(indexOfCharacter);
    }

    private void handlePlayer2Wins(Player player1, Player player2) {
        Card player1Card = player1.getCards().get(0);
        Card player2Card = player2.getCards().get(0);
        List<Card> cardsToGet = new ArrayList<>();
        cardsToGet.add(player1Card);
        cardsToGet.add(player2Card);
        removeCards(player1, player2);
        player2.getCards().addAll(cardsToGet);
        player2.winCounterIncrement();
    }

    private void removeCards(Player player1, Player player2) {
        player2.getCards().remove(0);
        player1.getCards().remove(0);
    }

    private List<Card> initializeDeck() {
        List<Card> cards = new ArrayList<>();
        for (int i = 0; i < 52; i++) {
            cards.add(new Card(i));
        }
        return cards;
    }

    private static void shuffleDeck(List<Card> cards) {
        long seed = System.nanoTime(); // Using nanoTime for a more fine-grained seed
        Collections.shuffle(cards, new Random(seed));
    }

    private void assignCardsToPlayers(List<Card> cards, Player player1, Player player2) {
        shuffleDeck(cards);
        for (int i = 0; i < 52; i = i + 2) {
            player1.getCards().add(cards.get(i));
            player2.getCards().add(cards.get(i + 1));
        }
    }

    private boolean playerHasCards(Player player) {
        return !player.getCards().isEmpty();
    }

    private Card getPlayerCard(Player player) {
        return player.getCards().get(0);
    }


    public Integer gameWithStrategy(String strategy) {
        List<Card> cards = initializeDeck();
        Player player1 = new Player();
        Player player2 = new Player();
        player1.setStrategySequence(strategy);
        assignCardsToPlayers(cards, player1, player2);
        int counter = 0;
        int warCounter = 0;
        while (playerHasCards(player1) && playerHasCards(player2) && counter < 10000) {
            if (getPlayerCard(player1).getRank() > getPlayerCard(player2).getRank()) {
                handlePlayerWinWithStrategy(player1, player2, true);
            } else if (Objects.equals(getPlayerCard(player1).getRank(), getPlayerCard(player2).getRank())) {
                handleWar(player1, player2, true, new ArrayList<>());
                warCounter++;
            } else {
                handlePlayerWinWithStrategy(player1, player2, false);
            }
            counter++;
        }
        log.info("Player1 has " + player1.getCards().size() + " cards.");
        log.info("Player2 has " + player2.getCards().size() + " cards.");
        log.info("War counter " + warCounter);
        log.info("Round counter " + counter);
        if (player2.getCards().isEmpty()) {
            return 1;
        } else if (player1.getCards().isEmpty()) {
            return 2;
        } else {
            return 0;
        }
    }

    public Statistics getStatistics(String strategy) {
        int player1WinsCounter = 0;
        int player2WinsCounter = 0;
        int drawCounter = 0;
        int gameAmount = 1000;
        for (int i = 0; i < gameAmount; i++) {
            Integer result = gameWithStrategy(strategy);
            if (result == 1) {
                player1WinsCounter++;
            } else if (result == 2) {
                player2WinsCounter++;
            } else if (result == 0) {
                drawCounter++;
            }
            if (i % 1000 == 0) {
                log.info("Computed " + i / 1000 + "%");
            }
        }
        Statistics stats = new Statistics();
        stats.setFirstPlayerWonGames(player1WinsCounter * 100.0 / gameAmount);
        stats.setSecondPlayerWonGames(player2WinsCounter * 100.0 / gameAmount);
        stats.setDraws(drawCounter * 100.0 / gameAmount);
        stats.setFirstPlayerStrategy(strategy);
        return stats;
    }

    public Integer[] gameWithStrategies(PlayersStrategyDTO playersStrategyDTO) {
        List<Card> cards = initializeDeck();
        Player player1 = new Player();
        Player player2 = new Player();
        player1.setStrategySequence(playersStrategyDTO.getFisrtPlayerStrategySequence());
        player2.setStrategySequence(playersStrategyDTO.getSecondPlayerStrategySequence());
        assignCardsToPlayers(cards, player1, player2);
        int counter = 0;
        int warCounter = 0;
        while (playerHasCards(player1) && playerHasCards(player2) && counter < 1000000) {
            if (getPlayerCard(player1).getRank() > getPlayerCard(player2).getRank()) {
                handlePlayerWinWithStrategy(player1, player2, true);
            } else if (Objects.equals(getPlayerCard(player1).getRank(), getPlayerCard(player2).getRank())) {
                handleWar(player1, player2, true, new ArrayList<>());
                warCounter++;
            } else {
                handlePlayerWinWithStrategy(player1, player2, false);
            }
            counter++;
        }
        log.info("Player1 has " + player1.getCards().size() + " cards.");
        log.info("Player2 has " + player2.getCards().size() + " cards.");
        log.info("War counter " + warCounter);
        log.info("Round counter " + counter);
        Integer[] result = new Integer[2];
        if (player2.getCards().isEmpty()) {
            result[0] = 1;
            result[1] = counter;
        } else if (player1.getCards().isEmpty()) {
            result[0] = 2;
            result[1] = counter;
        } else {
            result[0] = 0;
            result[1] = counter;
        }
        return result;
    }

    public Statistics getStatisticsForTwoPlayers(PlayersStrategyDTO playersStrategyDTO) {
        int player1WinsCounter = 0;
        int player2WinsCounter = 0;
        int drawCounter = 0;
        int gameAmount = 10000;
        int roundsCounter = 0;
        for (int i = 0; i < gameAmount; i++) {
            Integer[] result = gameWithStrategies(playersStrategyDTO);
            if (result[0] == 1) {
                player1WinsCounter++;
            } else if (result[0] == 2) {
                player2WinsCounter++;
            } else if (result[0] == 0) {
                drawCounter++;
            }
            if (i % 10 == 0) {
                log.info("Computed " + i / 10 + "%");
            }
            roundsCounter += result[1];
        }
        Statistics stats = new Statistics();
        stats.setFirstPlayerWonGames(player1WinsCounter * 100.0 / gameAmount);
        stats.setSecondPlayerWonGames(player2WinsCounter * 100.0 / gameAmount);
        stats.setDraws(drawCounter * 100.0 / gameAmount);
        stats.setFirstPlayerStrategy(playersStrategyDTO.getFisrtPlayerStrategySequence());
        stats.setSecondPlayerStrategy(playersStrategyDTO.getSecondPlayerStrategySequence());
        stats.setAverageAmountOfRounds(roundsCounter/gameAmount);

        return stats;
    }

    public List<Statistics> compareStrategyWithBasicStrategies(String strategy) {
        List<Statistics> result = new ArrayList<>();
        PlayersStrategyDTO toRandom = new PlayersStrategyDTO(strategy, "R");
        Statistics compareToRandom = getStatisticsForTwoPlayers(toRandom);

        PlayersStrategyDTO toGetHigher = new PlayersStrategyDTO(strategy, "H");
        Statistics compareToGetHigher = getStatisticsForTwoPlayers(toGetHigher);

        PlayersStrategyDTO toGetLower = new PlayersStrategyDTO(strategy, "L");
        Statistics compareToGetLower = getStatisticsForTwoPlayers(toGetLower);

        result.add(compareToRandom);
        result.add(compareToGetHigher);
        result.add(compareToGetLower);
        return result;
    }


    //compare strength
    public Statistics compareStrength(StrengthDTO strengthDTO) {
        int player1WinsCounter = 0;
        int player2WinsCounter = 0;
        int drawCounter = 0;
        int gameAmount = 10000;
        for (int i = 0; i < gameAmount; i++) {
            Integer result = gameWithStrategiesForStrenghtComparison(strengthDTO);
            if (result == 1) {
                player1WinsCounter++;
            } else if (result == 2) {
                player2WinsCounter++;
            } else if (result == 0) {
                drawCounter++;
            }
            if (i % 1000 == 0) {
                log.info("Computed " + i / 1000 + "%");
            }
        }
        Statistics stats = new Statistics();
        stats.setFirstPlayerWonGames(player1WinsCounter * 100.0 / gameAmount);
        stats.setSecondPlayerWonGames(player2WinsCounter * 100.0 / gameAmount);
        stats.setDraws(drawCounter * 100.0 / gameAmount);
        stats.setFirstPlayerStrategy(strengthDTO.getPlayersStrategyDTO().getFisrtPlayerStrategySequence());
        stats.setSecondPlayerStrategy(strengthDTO.getPlayersStrategyDTO().getSecondPlayerStrategySequence());

        return stats;
    }


    public Integer gameWithStrategiesForStrenghtComparison(StrengthDTO strengthDTO) {
        Player player1 = new Player();
        Player player2 = new Player();
        player1.setStrategySequence(strengthDTO.getPlayersStrategyDTO().getFisrtPlayerStrategySequence());
        player2.setStrategySequence(strengthDTO.getPlayersStrategyDTO().getSecondPlayerStrategySequence());
        List<Card> player1Cards = new ArrayList<>(strengthDTO.getPlayer1Cards());
        List<Card> player2Cards = new ArrayList<>(strengthDTO.getPlayer2Cards());
        shuffleDeck(player1Cards);
        shuffleDeck(player2Cards);
        player1.setCards(player1Cards);
        player2.setCards(player2Cards);
        int counter = 0;
        int warCounter = 0;
        while (playerHasCards(player1) && playerHasCards(player2) && counter < 100000) {
            if (getPlayerCard(player1).getRank() > getPlayerCard(player2).getRank()) {
                handlePlayerWinWithStrategy(player1, player2, true);
            } else if (Objects.equals(getPlayerCard(player1).getRank(), getPlayerCard(player2).getRank())) {
                handleWar(player1, player2, true, new ArrayList<>());
                warCounter++;
            } else {
                handlePlayerWinWithStrategy(player1, player2, false);
            }
            counter++;
        }
        log.info("Player1 has " + player1.getCards().size() + " cards.");
        log.info("Player2 has " + player2.getCards().size() + " cards.");
        log.info("War counter " + warCounter);
        log.info("Round counter " + counter);
        if (player2.getCards().isEmpty()) {
            return 1;
        } else if (player1.getCards().isEmpty()) {
            return 2;
        } else {
            return 0;
        }

    }


}
