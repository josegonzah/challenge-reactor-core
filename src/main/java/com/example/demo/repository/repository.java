package com.example.demo.repository;

import com.example.demo.model.Player;
import org.springframework.stereotype.Repository;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

@Repository
public interface repository extends ReactiveMongoRepository<Player, String>{

}
