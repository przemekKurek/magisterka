package com.magisterka.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlayersStrategyDTO implements Serializable {
    private String fisrtPlayerStrategySequence;
    private String secondPlayerStrategySequence;
}
