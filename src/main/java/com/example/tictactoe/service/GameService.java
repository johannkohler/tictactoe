package com.example.tictactoe.service;

import com.example.tictactoe.model.Game;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class GameService {

    private static final String[] ADJECTIVES = {
        "Fluffy", "Grumpy", "Sneaky", "Wobbly", "Dramatic", "Clumsy", "Sassy",
        "Dizzy", "Bouncy", "Gloomy", "Spicy", "Soggy", "Wiggly", "Cranky", "Jolly"
    };
    private static final String[] NOUNS = {
        "Penguin", "Noodle", "Toaster", "Raccoon", "Pickle", "Hamster", "Burrito",
        "Platypus", "Waffle", "Cactus", "Narwhal", "Muffin", "Sloth", "Biscuit", "Gecko"
    };

    private final File storeFile;
    private final ObjectMapper mapper;
    private final Map<String, Game> games;
    private final Random random = new Random();

    public GameService(@Value("${game.store.path:games.json}") String storePath) {
        this.mapper = new ObjectMapper().registerModule(new JavaTimeModule());
        this.storeFile = new File(storePath);
        this.games = loadFromFile();
    }

    public Game newGame() {
        String name = ADJECTIVES[random.nextInt(ADJECTIVES.length)]
                    + NOUNS[random.nextInt(NOUNS.length)];
        Game game = new Game(name);
        games.put(game.getId(), game);
        saveToFile();
        return game;
    }

    public Game getGame(String id) {
        return games.get(id);
    }

    public Collection<Game> listGames() {
        return games.values();
    }

    public Game makeMove(String id, int position) {
        Game game = games.get(id);
        if (game == null) return null;
        game.makeMove(position);
        saveToFile();
        return game;
    }

    private Map<String, Game> loadFromFile() {
        if (storeFile.exists()) {
            try {
                return mapper.readValue(storeFile, new TypeReference<ConcurrentHashMap<String, Game>>() {});
            } catch (IOException e) {
                System.err.println("Could not load games from file, starting fresh: " + e.getMessage());
            }
        }
        return new ConcurrentHashMap<>();
    }

    private void saveToFile() {
        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(storeFile, games);
        } catch (IOException e) {
            System.err.println("Could not save games to file: " + e.getMessage());
        }
    }
}
