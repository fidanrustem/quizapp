package com.example.quizapp.controller;
import com.example.quizapp.model.User;
import com.example.quizapp.model.Question;
import com.example.quizapp.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.List;
import java.util.Map;
@Controller
public class QuizController {
    @Autowired
    private QuizUserDetailsService userService;
    @Autowired
    private QuestionsService questionsService;
    @GetMapping("/login")
    public String getLoginPage(){
        return "login";
    }
    @GetMapping("/register")
    public String getRegisterPage(Model model){
        model.addAttribute("user",new User());
        return "register";
    }
    @PostMapping("/register")
    public String registerUser(@ModelAttribute User user){
        userService.registerUser(user.getUsername(), user.getPassword(), user.getEmail(), user.getRole());
        return "redirect:/login?registered=true";
    }
    @GetMapping({"/", "/home"})
    public String getHomePage(Model model, Authentication authentication) {
        List<Question> questions = questionsService.loadQuizzes();
        model.addAttribute("questions", questions);

        // Check if the logged-in user is an ADMIN
        if (authentication != null && authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
            return "admin_home"; // Renders admin_home.html (can add/edit/delete)
        }

        return "home"; // Renders home.html (regular users taking the quiz)
    }
    @GetMapping("/addQuiz")
    public String getAddQuizPage(Model model) {
        model.addAttribute("question", new Question());
        return "addQuiz"; // Renders addQuiz.html
    }

    @PostMapping("/addQuiz")
    public String addQuizQuestion(@ModelAttribute Question question) {
        questionsService.addQuiz(question);
        return "redirect:/home";
    }

    @GetMapping("/editQuiz/{id}")
    public String getEditQuizPage(@PathVariable int id, Model model) {
        // Find the specific question to edit
        Question existingQuestion = questionsService.loadQuizzes().stream()
                .filter(q -> q.getId() == id)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid question Id:" + id));

        model.addAttribute("question", existingQuestion);
        return "editQuiz"; // Renders editQuiz.html
    }

    @PutMapping("/editQuiz")
    public String editQuizQuestion(@ModelAttribute Question question) {
        questionsService.editQuiz(question);
        return "redirect:/home";
    }

    @DeleteMapping("/deleteQuiz/{id}")
    public String deleteQuizQuestion(@PathVariable int id) {
        questionsService.deleteQuiz(id);
        return "redirect:/home";
    }

    // --- Quiz Taking Endpoints ---

    @PostMapping("/submitAnswers")
    public String submitAnswers(@RequestParam Map<String, String> allParams, RedirectAttributes redirectAttributes) {
        int score = 0;
        List<Question> quizzes = questionsService.loadQuizzes();

        // Loop through all questions and compare user's answer with the correct answer
        for (Question q : quizzes) {
            // HTML forms usually submit answers with names like "answer_1", "answer_2", etc.
            String userAnswer = allParams.get("answer_" + q.getId());
            if (userAnswer != null && userAnswer.equals(q.getCorrectAnswer())) {
                score++;
            }
        }

        // Pass the calculated score to the result page
        redirectAttributes.addFlashAttribute("score", score);
        redirectAttributes.addFlashAttribute("total", quizzes.size());
        return "redirect:/result";
    }

    @GetMapping("/result")
    public String getResultPage() {
        return "result"; // Renders result.html
    }
}
