package com.magisterka.service;

import com.magisterka.model.Card;
import com.magisterka.model.Player;
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
            while (!player1.getCards().isEmpty() && !player2.getCards().isEmpty() && counter < 10000) {
                if (player1.getCards().get(0).getCardNumber() / 4 > player2.getCards().get(0).getCardNumber() / 4) {
                    handlePlayer1Wins(player1, player2);
                } else if (player1.getCards().get(0).getCardNumber() / 4 == player2.getCards().get(0).getCardNumber() / 4) {
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
        if(firstWar) {
            warCards = new ArrayList<>();
        }

        // Each player puts three cards face down
        for (int i = 0; i < 3; i++) {
            if (!player1.getCards().isEmpty() && !player2.getCards().isEmpty()) {
                warCards.add(player1.getCards().get(0));
                warCards.add(player2.getCards().get(0));
                player1.getCards().remove(0);
                player2.getCards().remove(0);

            }
        }
        // TODO poprawić wojnę
        // If one player runs out of cards, they lose the war
        if (player1.getCards().isEmpty() && !player2.getCards().isEmpty()) {
            player2.getCards().addAll(warCards);
        } else if (!player1.getCards().isEmpty() && player2.getCards().isEmpty()) {
            player1.getCards().addAll(warCards);
        } else if (!player1.getCards().isEmpty() && !player2.getCards().isEmpty()) {
            // Compare the next face-up cards after the war cards have been placed face down
            if (player1.getCards().get(0).getCardNumber() / 4 > player2.getCards().get(0).getCardNumber() / 4) {
                player1.getCards().addAll(warCards);
            } else if (player1.getCards().get(0).getCardNumber() / 4 < player2.getCards().get(0).getCardNumber() / 4) {
                player2.getCards().addAll(warCards);
            } else {
                // If another tie, you can recursively call handleWar
                handleWar(player1, player2, false, warCards);
            }
        }
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
        Collections.sort(cards, Comparator.comparing(Card::getCardNumber, Comparator.reverseOrder()));
    }

    private void handlePlayer1WinsWithStrategy(Player player1, Player player2) {
        Card player1Card = player1.getCards().get(0);
        Card player2Card = player2.getCards().get(0);
        List<Card> cardsToGet = new ArrayList<>();
        cardsToGet.add(player1Card);
        cardsToGet.add(player2Card);
        removeCards(player1, player2);
        if (getStrategy(player1) == 'H') {
            sortCardsAscending(cardsToGet);
        } else if (getStrategy(player1) == 'L') {
            sortCardsDescending(cardsToGet);
        }
        player1.getCards().addAll(cardsToGet);
        player1.winCounterIncrement();
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

    public static void shuffleDeck(List<Card> cards) {
        Collections.shuffle(cards);
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


    public void gameWithStrategy(String strategy) {
        List<Card> cards = initializeDeck();
        Player player1 = new Player();
        Player player2 = new Player();
        player1.setStrategySequence(strategy);
        assignCardsToPlayers(cards, player1, player2);
        int counter = 0;
        int warCounter = 0;
        while (playerHasCards(player1) && playerHasCards(player2) && counter < 10000) {
            if (getPlayerCard(player1).getCardNumber() / 4 > getPlayerCard(player2).getCardNumber() / 4) {
                handlePlayer1WinsWithStrategy(player1, player2);
            } else if (getPlayerCard(player1).getCardNumber() / 4 == getPlayerCard(player2).getCardNumber() / 4) {
                handleWar(player1, player2, true, new ArrayList<>());
                warCounter++;
            } else {
                handlePlayer2Wins(player1, player2);
            }
            counter++;
        }
        log.info("Player1 has " + player1.getCards().size() + " cards.");
        log.info("Player2 has " + player2.getCards().size() + " cards.");
        log.info("War counter " + warCounter);
    }
}
