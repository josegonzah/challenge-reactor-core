package com.example.demo;

import com.example.demo.model.Player;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.*;

public class CSVUtilTest {

    @Test
    void converterData(){
        List<Player> list = CsvUtilFile.getPlayers();
        assert list.size() == 18207;
    }


    @Test
    void reactive_filtrarJugadoresMayoresA34(){
        List<Player> list = CsvUtilFile.getPlayers();
        Flux<Player> listFlux = Flux.fromStream(list.parallelStream()).cache();
        Mono<Map<String, Collection<Player>>> listFilter = listFlux
                .filter(player -> player.age >= 34)
                .map(player -> {
                    player.name = player.name.toUpperCase(Locale.ROOT);
                    return player;
                })
                .buffer(100)
                .flatMap(playerA -> listFlux
                         .filter(playerB -> playerA.stream()
                                 .anyMatch(a ->  a.club.equals(playerB.club)))
                )
                .distinct()
                .collectMultimap(Player::getClub);

        assert listFilter.block().size() == 322;
    }

    @ParameterizedTest
    @CsvSource({ "Los Angeles FC,25", "Paris Saint-Germain,30" })
    void reactive_filtrarJugadoresPorClub(String club, int countClub) {
        List<Player> list = CsvUtilFile.getPlayers();
        Mono<List<Player>> listFilter = Flux.fromStream(list.parallelStream()).cache()
                .filter(player -> player.club.equals(club)).distinct().collectList();
        assert listFilter.block().size() == countClub;
    }

    @ParameterizedTest
    @CsvSource({ "Uruguay,149", "Colombia,618" })
    void reactive_filtrarJugadoresPorNacionalidadYOrdenadosPorVictorias(String nationality, int countPlayers) {
        List<Player> list = CsvUtilFile.getPlayers();
        Mono<List<Player>> listFilter = Flux.fromStream(list.parallelStream()).cache()
                .filter(player -> player.national.equals(nationality))
                .sort(Comparator.comparing(Player::getWinners).reversed()).distinct().collectList();

        assert listFilter.block().size() == countPlayers;
    }



}
