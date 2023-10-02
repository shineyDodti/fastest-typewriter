import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class TyperacerGame extends JFrame {
    private JLabel sentenceLabel;
    private JTextField userInputField;
    private JButton startButton;
    private JButton stopButton;
    private JButton viewHighScoreButton; // New button to view high scores
    private JLabel timerLabel;
    private Timer timer;
    private String currentSentence;
    private int currentIndex;
    private long startTime;
    private boolean gameInProgress;
    private List<Player> players;
    private Player currentPlayer;
    private String username;
    private int penaltyTime;
    private int attempts;

    public TyperacerGame() {
        setTitle("Typeracer Game");
        setSize(400, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        sentenceLabel = new JLabel();
        userInputField = new JTextField(20);
        startButton = new JButton("Start");
        startButton.addActionListener(new StartButtonListener());

        stopButton = new JButton("Stop");
        stopButton.addActionListener(new StopButtonListener());
        stopButton.setEnabled(false);

        viewHighScoreButton = new JButton("View High Scores");
        viewHighScoreButton.addActionListener(new ViewHighScoreButtonListener());

        JPanel topPanel = new JPanel();
        topPanel.add(sentenceLabel);
        add(topPanel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel();
        centerPanel.add(userInputField);
        add(centerPanel, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel();
        timerLabel = new JLabel("Timer: 0s");
        bottomPanel.add(timerLabel);
        bottomPanel.add(startButton);
        bottomPanel.add(stopButton);
        bottomPanel.add(viewHighScoreButton); // Add View High Scores button
        add(bottomPanel, BorderLayout.SOUTH);

        timer = new Timer(1000, new TimerListener());

        players = new ArrayList<>();
    }

    private void generateRandomSentence() {
        String[] sentences = {
            "The sun is shining.",
            "Coding is fun!",
            "Typeracer is a great game.",
            "Swing is a Java GUI toolkit."
        };

        Random rand = new Random();
        currentSentence = sentences[rand.nextInt(sentences.length)];
        sentenceLabel.setText(currentSentence);
    }

    private class StartButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            username = JOptionPane.showInputDialog("Enter your username:");
            if (username != null && !username.isEmpty()) {
                startButton.setEnabled(false);
                stopButton.setEnabled(true);
                viewHighScoreButton.setEnabled(false); // Disable View High Scores during the game
                currentIndex = 0;
                userInputField.setText("");
                userInputField.setEnabled(true);
                userInputField.requestFocus();
                timer.start();
                startTime = System.currentTimeMillis();
                gameInProgress = true;
                penaltyTime = 0;
                attempts = 0;
                generateRandomSentence();
                currentPlayer = new Player(username);
            }
        }
    }

    private class StopButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (gameInProgress) {
                displayWinner();
                generateRandomSentence();
            }
        }
    }

    private class TimerListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (gameInProgress) {
                long elapsedTime = (System.currentTimeMillis() - startTime) / 1000 + penaltyTime;
                timerLabel.setText("Timer: " + elapsedTime + "s");
            }
        }
    }

    private void displayWinner() {
        gameInProgress = false;
        timer.stop();
        userInputField.setEnabled(false);
        startButton.setEnabled(true);
        stopButton.setEnabled(false);
        viewHighScoreButton.setEnabled(true); // Enable View High Scores after the game

        if (username != null && !username.isEmpty()) {
            currentPlayer.setTime((System.currentTimeMillis() - startTime) / 1000 + penaltyTime);
            players.add(currentPlayer);

            JOptionPane.showMessageDialog(null, "Winner: " + currentPlayer.getName() + " (" + currentPlayer.getTime() + "s)");
            userInputField.setText("");
            attempts++;

            if (attempts == 5) {
                displayHighScore();
                attempts = 0;
            }
        }
    }

    private void displayHighScore() {
        Collections.sort(players, (p1, p2) -> Long.compare(p1.getTime(), p2.getTime()));

        StringBuilder highScoreMessage = new StringBuilder("High Scores:\n");
        int rank = 1;
        for (Player player : players) {
            highScoreMessage.append(rank).append(". ").append(player.getName()).append(" - ").append(player.getTime()).append("s\n");
            rank++;
        }

        JOptionPane.showMessageDialog(null, highScoreMessage.toString(), "High Scores", JOptionPane.INFORMATION_MESSAGE);
    }

    private class ViewHighScoreButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            displayHighScore();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            TyperacerGame game = new TyperacerGame();
            game.setVisible(true);
        });
    }
}

class Player {
    private String name;
    private long time;

    public Player(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
