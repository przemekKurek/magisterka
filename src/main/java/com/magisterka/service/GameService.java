package com.magisterka.service;

import com.magisterka.model.Card;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@Slf4j
public class GameService {

    public int game(Integer numberOfPlayers) {
        if (numberOfPlayers == 2) {
            List<Card> cards = new ArrayList<>();
            List<Card> player1 = new ArrayList<>();
            List<Card> player2 = new ArrayList<>();
            for (int i = 0; i < 52; i++) {
                cards.add(new Card(i));
            }
            Collections.shuffle(cards);
            for (int i = 0; i < 52; i = i + 2) {
                player1.add(cards.get(i));
                player2.add(cards.get(i + 1));
            }
            int counter = 0;
            while (!player1.isEmpty() && !player2.isEmpty() && counter < 10000) {
                if (player1.get(0).getCardNumber() / 4 > player2.get(0).getCardNumber() / 4) {
                    Card player1Card = player1.get(0);
                    Card player2Card = player2.get(0);
                    player2.remove(0);
                    player1.remove(0);
                    player1.add(player1Card);
                    player1.add(player2Card);
                } else if (player1.get(0).getCardNumber() / 4 == player2.get(0).getCardNumber() / 4) {
                    handleWar(player1, player2);
                } else {
                    Card player1Card = player1.get(0);
                    Card player2Card = player2.get(0);
                    player2.remove(0);
                    player1.remove(0);
                    player2.add(player1Card);
                    player2.add(player2Card);
                }
                counter++;
            }
            if (player1.size() == 0 || player2.size() == 0) {
                return player1.isEmpty() ? 20 : 10;
            } else {
                return player1.size() > player2.size() ? 1 : 2;
            }
        }
        return 0;
    }


    private void handleWar(List<Card> player1, List<Card> player2) {
        List<Card> warCards = new ArrayList<>();

        // Each player puts three cards face down
        for (int i = 0; i < 3; i++) {
            if (!player1.isEmpty() && !player2.isEmpty()) {
                warCards.add(player1.get(0));
                warCards.add(player2.get(0));
                player1.remove(0);
                player2.remove(0);

            }
        }

        // If one player runs out of cards, they lose the war
        if (player1.isEmpty() && !player2.isEmpty()) {
            player2.addAll(warCards);
        } else if (!player1.isEmpty() && player2.isEmpty()) {
            player1.addAll(warCards);
        } else if (!player1.isEmpty() && !player2.isEmpty()) {
            // Compare the next face-up cards after the war cards have been placed face down
            if (player1.get(0).getCardNumber() / 4 > player2.get(0).getCardNumber() / 4) {
                player1.addAll(warCards);
            } else if (player1.get(0).getCardNumber() / 4 < player2.get(0).getCardNumber() / 4) {
                player2.addAll(warCards);
            } else {
                // If another tie, you can recursively call handleWar
                handleWar(player1, player2);
            }
        }
    }


}
