package com.magisterka.service;

import com.magisterka.model.Card;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
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
                player2.add(cards.get(i+1));
            }
            int counter = 0;
            while (!player1.isEmpty() && !player2.isEmpty() && counter < 10000) {
                if (player1.get(0).getCardNumber() > player2.get(0).getCardNumber()) {
                    Card player1Card = player1.get(0);
                    Card player2Card = player2.get(0);
                    player2.remove(0);
                    player1.remove(0);
                    player1.add(player1Card);
                    player1.add(player2Card);
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

}
