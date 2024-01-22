package com.magisterka.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CardDTO {
    private Integer cardNumber;

    public String getSuit() {
        int suitValue = cardNumber / 13;
        // You can define an array to map numeric suit values to actual suits.
        String[] suits = {"Spades", "Clubs", "Diamonds", "Hearts"};
        return suits[suitValue];
    }

    public Integer getRank() {
        return cardNumber / 4;
    }
}
