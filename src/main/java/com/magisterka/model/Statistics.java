package com.magisterka.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Statistics {
    private double firstPlayerWonGames;
    private double secondPlayerWonGames;
    private double draws;
    private String firstPlayerStrategy;
    private String secondPlayerStrategy;
    private double averageAmountOfRounds;
}
