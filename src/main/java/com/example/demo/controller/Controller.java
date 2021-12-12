package com.example.demo.controller;

import com.example.demo.model.Player;
import com.example.demo.services.Service;
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
public class Controller {
    @Autowired
    Service service;

    @Autowired
    Repository repository;

    @GetMapping
    public Flux<Player> getFilteredPlayers(){
        service.getRankingPlayer();
        return service.getFilterPlayer()
                .buffer(100)
                .flatMap(player -> Flux.fromStream(player.parallelStream()));
    }

    @GetMapping("/listas")
    public Flux<List<Player>> getListasRankingPlayers(){
        return service.getRankingPlayer();
    }
}
