package com.magisterka;

import com.magisterka.service.GameService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class App {
    private static GameService gameService = new GameService();

    public App(GameService gameService) {
        this.gameService = gameService;
    }

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }
}
