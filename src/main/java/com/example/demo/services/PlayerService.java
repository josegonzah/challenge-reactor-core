package com.example.demo.services;


import com.example.demo.model.Player;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
@org.springframework.stereotype.Service
public class PlayerService {

    @Autowired
    Repository repository;

    private Flux<Player> getAll(){
        return repository.findAll()
                .buffer(100)
                .flatMap(players -> Flux.fromStream(players.parallelStream()));
    }

    private Flux<Player> mayores34(){
        return getAll()
                .buffer(100)
                .flatMap(player -> Flux.fromStream(player.parallelStream()))
                .filter(player -> {
                    try {
                        return player.getAge() > 34;
                    } catch (Exception e){
                        return false;
                    }
                })
                .onErrorContinue((e, i) ->
                        System.out.println("error" +i)
                );

    }

    private Flux<Player> clubEspecifico() {
        return mayores34()
                .buffer(100)
                .flatMap(juga -> Flux.fromStream(juga.parallelStream()))
                .filter(jugador -> {
                    try {
                        return jugador.getClub().equals("FC Barcelona");
                    } catch (Exception e) {
                        return false;
                    }
                })
                .onErrorContinue((e, i) ->
                        System.out.println("error por filtro 1 "+i)
                );
    }

    public Flux<Player> getFilterPlayer(){
        return clubEspecifico()
                .buffer(100)
                .flatMap(player -> Flux.fromStream(player.parallelStream()))
                .onErrorContinue((e, i) ->
                        System.out.println("erro filtrando club"+i));
    }

    public Flux<List<Player>> getRankingPlayer() {

        return getAll()
                .buffer(100)
                .flatMap(juga -> Flux.fromStream(juga.parallelStream()))
                .distinct()
                .groupBy(Player::getNational)
                .flatMap(Flux::collectList)
                .map(lista -> {
                    Collections.sort(lista);
                    return lista;
                })
                .onErrorContinue((e, i) ->
                        System.out.println("error filtrandoListas "+i));

    }


}
