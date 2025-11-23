package com.kursch.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HighScoreManager {
    private static final String HIGHSCORE_FILE = "highscores.txt";
    private static final int MAX_SCORES = 10;

    public static class HighScoreEntry implements Comparable<HighScoreEntry> {
        public String playerName;
        public int score;

        public HighScoreEntry(String playerName, int score) {
            this.playerName = playerName;
            this.score = score;
        }

        @Override
        public int compareTo(HighScoreEntry other) {
            return Integer.compare(other.score, this.score); // По убыванию
        }
    }

    private List<HighScoreEntry> highScores;

    public HighScoreManager() {
        highScores = new ArrayList<>();
        loadHighScores();
    }

    /**
     * Загружает рекорды из файла
     */
    private void loadHighScores() {
        FileHandle file = Gdx.files.local(HIGHSCORE_FILE);

        if (file.exists()) {
            String content = file.readString();
            String[] lines = content.split("\n");

            for (String line : lines) {
                line = line.trim();
                if (!line.isEmpty()) {
                    try {
                        String[] parts = line.split(":");
                        if (parts.length == 2) {
                            String name = parts[0].trim();
                            int score = Integer.parseInt(parts[1].trim());
                            highScores.add(new HighScoreEntry(name, score));
                        }
                    } catch (Exception e) {
                        Gdx.app.error("HighScoreManager", "Ошибка чтения рекорда: " + line);
                    }
                }
            }
        }

        // Сортируем по убыванию
        Collections.sort(highScores);
    }

    /**
     * Сохраняет рекорды в файл
     */
    private void saveHighScores() {
        FileHandle file = Gdx.files.local(HIGHSCORE_FILE);
        StringBuilder sb = new StringBuilder();

        for (HighScoreEntry entry : highScores) {
            sb.append(entry.playerName).append(":").append(entry.score).append("\n");
        }

        file.writeString(sb.toString(), false);
    }

    /**
     * Добавляет новый счет и возвращает true, если он попал в топ-10
     */
    public boolean addScore(String playerName, int score) {
        boolean isHighScore = false;

        // Если список не заполнен, просто добавляем
        if (highScores.size() < MAX_SCORES) {
            highScores.add(new HighScoreEntry(playerName, score));
            isHighScore = true;
        }
        // Если счет больше минимального в топ-10
        else if (score > highScores.get(highScores.size() - 1).score) {
            highScores.set(highScores.size() - 1, new HighScoreEntry(playerName, score));
            isHighScore = true;
        }

        // Сортируем по убыванию
        Collections.sort(highScores);

        // Сохраняем в файл
        saveHighScores();

        return isHighScore;
    }

    /**
     * Возвращает список рекордов
     */
    public List<HighScoreEntry> getHighScores() {
        return new ArrayList<>(highScores);
    }

    /**
     * Возвращает позицию счета в таблице рекордов (1-10) или -1, если не попал
     */
    public int getScoreRank(String playerName, int score) {
        for (int i = 0; i < highScores.size(); i++) {
            if (highScores.get(i).score == score && highScores.get(i).playerName.equals(playerName)) {
                return i + 1;
            }
        }
        return -1;
    }

    /**
     * Проверяет, является ли счет рекордом
     */
    public boolean isHighScore(int score) {
        if (highScores.size() < MAX_SCORES) {
            return true;
        }
        return score > highScores.get(highScores.size() - 1).score;
    }
}