package com.magisterka.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Player {
    private List<CardDTO> cards = new ArrayList<>();
    private String strategySequence;
    private Long winCounter = 0L;

    public void winCounterIncrement() {
        this.winCounter++;
    }
}
