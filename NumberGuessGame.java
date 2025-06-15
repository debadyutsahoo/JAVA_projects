import javax.swing.*;
import java.awt.*;
import java.util.*;
import javax.sound.sampled.*;
import java.io.*;
import javax.swing.Timer;

public class NumberGuessGame extends JFrame {
    private int targetNumber, remainingAttempts, totalScore, gameRound;
    private JTextField inputField;
    private JLabel feedbackLabel, attemptsLabel, scoreLabel, roundLabel;
    private JButton submitButton;
    private java.util.List<Integer> highScores = new ArrayList<>();

    public NumberGuessGame() {
        setTitle("Guess The Number!");
        setSize(450, 300);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(7, 1));

        gameRound = 1;
        totalScore = 0;

        roundLabel = new JLabel("Round: 1", JLabel.CENTER);
        feedbackLabel = new JLabel("Enter a number between 1 and 100", JLabel.CENTER);
        inputField = new JTextField();
        submitButton = new JButton("Guess");
        attemptsLabel = new JLabel("", JLabel.CENTER);
        scoreLabel = new JLabel("Score: 0", JLabel.CENTER);

        add(roundLabel);
        add(feedbackLabel);
        add(inputField);
        add(submitButton);
        add(attemptsLabel);
        add(scoreLabel);

        JButton showScoresButton = new JButton("Leaderboard");
        add(showScoresButton);

        initializeRound();

        submitButton.addActionListener(e -> {
            flashButton(submitButton);
            processGuess();
        });

        showScoresButton.addActionListener(e -> displayLeaderboard());

        JMenuBar menuBar = new JMenuBar();
        JMenu gameOptions = new JMenu("Options");
        JMenuItem nextRound = new JMenuItem("Next Round");
        JMenuItem quitGame = new JMenuItem("Exit");

        nextRound.addActionListener(e -> {
            highScores.add(totalScore);
            highScores.sort(Collections.reverseOrder());
            if (highScores.size() > 3) highScores = highScores.subList(0, 3);
            totalScore = 0;
            gameRound++;
            initializeRound();
        });

        quitGame.addActionListener(e -> System.exit(0));

        gameOptions.add(nextRound);
        gameOptions.add(quitGame);
        menuBar.add(gameOptions);
        setJMenuBar(menuBar);
    }

    private void initializeRound() {
        targetNumber = new Random().nextInt(100) + 1;
        remainingAttempts = 7;
        roundLabel.setText("Round: " + gameRound);
        feedbackLabel.setText("Enter a number between 1 and 100");
        attemptsLabel.setText("Attempts Left: " + remainingAttempts);
        scoreLabel.setText("Score: " + totalScore);
        inputField.setText("");
        inputField.setEditable(true);
    }

    private void processGuess() {
        try {
            int userGuess = Integer.parseInt(inputField.getText());
            remainingAttempts--;

            if (userGuess == targetNumber) {
                totalScore += 10 + remainingAttempts * 2;
                feedbackLabel.setText("Well done! That's correct.");
                playAudio("correct.wav");
                inputField.setEditable(false);
            } else {
                feedbackLabel.setText(userGuess < targetNumber ? "Too low!" : "Too high!");
                playAudio("wrong.wav");
            }

            if (remainingAttempts == 0 && userGuess != targetNumber) {
                feedbackLabel.setText("Game over! The number was: " + targetNumber);
                playAudio("gameover.wav");
                inputField.setEditable(false);
            }

            attemptsLabel.setText("Attempts Left: " + remainingAttempts);
            scoreLabel.setText("Score: " + totalScore);

        } catch (NumberFormatException e) {
            feedbackLabel.setText("Please enter a valid number!");
        }
    }

    private void flashButton(JButton button) {
        Color defaultColor = button.getBackground();
        button.setBackground(Color.CYAN);
        Timer flash = new Timer(150, evt -> button.setBackground(defaultColor));
        flash.setRepeats(false);
        flash.start();
    }

    private void playAudio(String filename) {
        try {
            File audioFile = new File(filename);
            if (!audioFile.exists()) return;
            AudioInputStream stream = AudioSystem.getAudioInputStream(audioFile);
            Clip clip = AudioSystem.getClip();
            clip.open(stream);
            clip.start();
        } catch (Exception e) {
            System.out.println("Audio Error: " + e.getMessage());
        }
    }

    private void displayLeaderboard() {
        StringBuilder scores = new StringBuilder("Top Scores:\n");
        for (int i = 0; i < highScores.size(); i++) {
            scores.append((i + 1)).append(". ").append(highScores.get(i)).append("\n");
        }
        if (highScores.isEmpty()) scores.append("No scores yet.");
        JOptionPane.showMessageDialog(this, scores.toString(), "Leaderboard", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new NumberGuessGame().setVisible(true));
    }
}
