package com.magisterka.model.dto;

import com.magisterka.model.Card;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StrengthDTO implements Serializable {
    private List<Card> player1Cards;
    private List<Card> player2Cards;
    private PlayersStrategyDTO playersStrategyDTO;
}
