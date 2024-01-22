package com.magisterka.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class StatisticsDTO {
    private double firstPlayerWonGames;
    private double secondPlayerWonGames;
    private double draws;
    private String firstPlayerStrategy;
    private String secondPlayerStrategy;
    private double averageAmountOfRounds;
    private List<String> detectedCycles = new ArrayList<>();
}
