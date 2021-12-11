package com.example.demo;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CSVUtilTest {

    @Test
    void converterData(){
        List<Player> list = CsvUtilFile.getPlayers();
        assert list.size() == 18207;
    }

    @Test
    void stream_filtrarJugadoresMayoresA35(){
        List<Player> list = CsvUtilFile.getPlayers();
        Map<String, List<Player>> listFilter = list.parallelStream()
                .filter(player -> player.age >= 35)
                .map(player -> {
                    player.name = player.name.toUpperCase(Locale.ROOT);
                    return player;
                })
                .flatMap(playerA -> list.parallelStream()
                        .filter(playerB -> playerA.club.equals(playerB.club))
                )
                .distinct()
                .collect(Collectors.groupingBy(Player::getClub));

        assert listFilter.size() == 322;
    }


    @Test
    void reactive_filtrarJugadoresMayoresA35(){
        List<Player> list = CsvUtilFile.getPlayers();
        Flux<Player> listFlux = Flux.fromStream(list.parallelStream()).cache();
        Mono<Map<String, Collection<Player>>> listFilter = listFlux
                .filter(player -> player.age >= 35)
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
