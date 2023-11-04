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
            List<Card> player1 = new ArrayList<>();
            List<Card> player2 = new ArrayList<>();

            for(int i =0; i<52;i++) {
                player1.add(new Card(i));
                player2.add(new Card(i));
            }
            Collections.shuffle(player1);
            while (!player1.isEmpty() && !player2.isEmpty()) {
                if(player1.get(0).getCardNumber() > player2.get(0).getCardNumber()) {
                    Card player1Card = player1.get(0);
                    Card player2Card = player2.get(0);
                    player2.remove(player2Card);
                    player1.remove(player1Card);
                    player1.add(player1Card);
                    player1.add(player2Card);
                } else {
                    Card player1Card = player1.get(0);
                    Card player2Card = player2.get(0);
                    player2.remove(player2Card);
                    player1.remove(player1Card);
                    player2.add(player1Card);
                    player2.add(player2Card);
                }
            }
            return player1.isEmpty() ? 2 : 1;
        }
        return 0;
    }

}
