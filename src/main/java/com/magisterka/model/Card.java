package com.magisterka.model;

import lombok.Data;

@Data
public class Card {
    private int cardNumber;

    public Card(int cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getSuit() {
        int suitValue = cardNumber / 13;
        // You can define an array to map numeric suit values to actual suits.
        String[] suits = {"Spades", "Clubs", "Diamonds", "Hearts"};
        return suits[suitValue];
    }

    public String getRank() {
        int rankValue = cardNumber % 13;
        // You can define an array to map numeric rank values to actual ranks.
        String[] ranks = {"2", "3", "4", "5", "6", "7", "8", "9", "10", "Jack", "Queen", "King", "Ace"};
        return ranks[rankValue];
    }
}
