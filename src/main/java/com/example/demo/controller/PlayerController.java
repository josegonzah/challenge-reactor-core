package com.example.demo.controller;

import com.example.demo.model.Player;
import com.example.demo.services.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping("/api")
public class PlayerController {
    @Autowired
    PlayerService playerService;

    @Autowired
    Repository repository;

    @GetMapping
    public Flux<Player> getFilteredPlayers(){
        playerService.getRankingPlayer();
        return playerService.getFilterPlayer()
                .buffer(100)
                .flatMap(player -> Flux.fromStream(player.parallelStream()));
    }

    @GetMapping("/listas")
    public Flux<List<Player>> getListasRankingPlayers(){
        return playerService.getRankingPlayer();
    }
}
