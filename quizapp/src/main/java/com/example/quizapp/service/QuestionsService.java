package com.example.quizapp.service;


import com.example.quizapp.model.Question;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class QuestionsService {

    // In-memory data structure to store questions, using the question ID as the key
    private final Map<Integer, Question> questions = new HashMap<>();

    // Simple counter to simulate auto-incrementing database IDs
    private int currentId = 1;

    /**
     * Returns a List of all quizzes currently in memory.
     */
    public List<Question> loadQuizzes() {
        return new ArrayList<>(questions.values());
    }

    /**
     * Accepts a Question object and adds it to the collection.
     */
    public void addQuiz(Question question) {
        // Assign an ID if one isn't already set
        if (question.getId() == 0) {
            question.setId(currentId++);
        }
        questions.put(question.getId(), question);
    }

    /**
     * Accepts a Question object and updates the corresponding Question in the collection.
     */
    public void editQuiz(Question question) {
        if (questions.containsKey(question.getId())) {
            questions.put(question.getId(), question);
        } else {
            throw new IllegalArgumentException("Question with ID " + question.getId() + " does not exist.");
        }
    }

    /**
     * Accepts a question ID and removes the corresponding question from the collection.
     */
    public void deleteQuiz(int id) {
        questions.remove(id);
    }
}