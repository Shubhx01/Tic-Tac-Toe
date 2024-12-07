// File: MultiModeTicTacToe.java
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

public class MultiModeTicTacToe extends JFrame {
    private char[][] board = new char[3][3];
    private JButton[][] buttons = new JButton[3][3];
    private char currentPlayer;
    private char player1Symbol, player2Symbol;
    private boolean isPlayer1Turn = true;
    private boolean playAgainstAI = false;
    private Random random = new Random();

    // Scoreboard variables
    private int player1Wins = 0;
    private int player2Wins = 0;
    private int draws = 0;
    private JLabel scoreLabel;

    // AI Difficulty Level
    private int difficultyLevel = 1; // 1: Easy, 2: Medium, 3: Hard

    public MultiModeTicTacToe() {
        setTitle("Tic Tac Toe");
        setSize(500, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        selectGameMode();
        initializeBoard();
        initializeUI();
    }

    // Prompt user to select the game mode
    private void selectGameMode() {
        String[] options = {"Play Against Computer", "Two Player Mode"};
        int choice = JOptionPane.showOptionDialog(
            this,
            "Choose the game mode:",
            "Welcome to Tic Tac Toe!",
            JOptionPane.DEFAULT_OPTION,
            JOptionPane.INFORMATION_MESSAGE,
            null,
            options,
            options[0]
        );

        playAgainstAI = (choice == 0); // True if playing against computer

        if (playAgainstAI) {
            // Prompt to select AI difficulty
            String[] difficulties = {"Easy", "Medium", "Hard"};
            difficultyLevel = JOptionPane.showOptionDialog(
                this,
                "Select AI Difficulty:",
                "AI Difficulty",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                difficulties,
                difficulties[0]
            ) + 1; // Map to 1, 2, 3
        }

        selectSymbols(); // Proceed to symbol selection
    }

    // Prompt Player 1 to select their symbol
    private void selectSymbols() {
        String[] options = {"X", "O"};
        int choice = JOptionPane.showOptionDialog(
            this,
            "Player 1, choose your symbol:",
            "Choose Symbol",
            JOptionPane.DEFAULT_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            null,
            options,
            options[0]
        );

        if (choice == 0) {
            player1Symbol = 'X';
            player2Symbol = 'O';
        } else {
            player1Symbol = 'O';
            player2Symbol = 'X';
        }

        currentPlayer = player1Symbol; // Player 1 always starts
    }

    private void initializeBoard() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                board[i][j] = ' ';
            }
        }
    }

    private void initializeUI() {
        setLayout(new BorderLayout());

        // Board panel
        JPanel boardPanel = new JPanel();
        boardPanel.setLayout(new GridLayout(3, 3, 1, 1)); // 1-pixel gap for grey grid lines
        boardPanel.setBackground(Color.WHITE);
        buttons = new JButton[3][3];

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                buttons[i][j] = new JButton("");
                buttons[i][j].setFont(new Font("Arial", Font.BOLD, 70)); // Larger font for symbols
                buttons[i][j].setFocusPainted(false);
                buttons[i][j].setBackground(Color.WHITE);
                buttons[i][j].setOpaque(true);
                buttons[i][j].setBorder(BorderFactory.createLineBorder(Color.GRAY, 1)); // Grey grid lines
                int finalI = i, finalJ = j;
                buttons[i][j].addActionListener(e -> handleMove(finalI, finalJ));
                boardPanel.add(buttons[i][j]);
            }
        }

        // Control panel
        JPanel controlPanel = new JPanel();
        controlPanel.setBackground(Color.WHITE);

        // Scoreboard
        scoreLabel = new JLabel("Player 1 Wins: 0 | Player 2 Wins: 0 | Draws: 0");
        scoreLabel.setForeground(Color.BLACK);
        controlPanel.add(scoreLabel);

        // Reset button
        JButton resetButton = new JButton("Reset Game");
        resetButton.addActionListener(e -> resetGame());
        controlPanel.add(resetButton);

        add(boardPanel, BorderLayout.CENTER);
        add(controlPanel, BorderLayout.SOUTH);
    }

    private void handleMove(int row, int col) {
        if (board[row][col] == ' ') {
            // Place the symbol for the current player
            board[row][col] = currentPlayer;

            // Set text and color based on the symbol
            if (currentPlayer == 'X') {
                buttons[row][col].setText("X");
                buttons[row][col].setForeground(Color.BLUE);
            } else {
                buttons[row][col].setText("O");
                buttons[row][col].setForeground(Color.RED);
            }

            // Check if the game is over
            if (checkGameOver()) return;

            if (playAgainstAI) {
                handleAIMove();
            } else {
                // Alternate turns for two-player mode
                isPlayer1Turn = !isPlayer1Turn;
                currentPlayer = isPlayer1Turn ? player1Symbol : player2Symbol;
            }
        }
    }

    private void handleAIMove() {
        int[] move;
        if (shouldMakeOptimalMove()) {
            move = findBestMove(); // Optimal move
        } else {
            move = findRandomMove(); // Random move
        }

        board[move[0]][move[1]] = player2Symbol;

        // Set text and color for AI's move
        if (player2Symbol == 'X') {
            buttons[move[0]][move[1]].setText("X");
            buttons[move[0]][move[1]].setForeground(Color.BLUE);
        } else {
            buttons[move[0]][move[1]].setText("O");
            buttons[move[0]][move[1]].setForeground(Color.RED);
        }

        // Check if the game is over
        if (checkGameOver()) return;

        currentPlayer = player1Symbol; // Player 1's turn again
    }

    private boolean shouldMakeOptimalMove() {
        int chance = switch (difficultyLevel) {
            case 1 -> 10; // 10% optimal for Easy
            case 2 -> 25; // 25% optimal for Medium
            case 3 -> 50; // 50% optimal for Hard
            default -> 50;
        };
        return random.nextInt(100) < chance;
    }

    private int[] findRandomMove() {
        int row, col;
        do {
            row = random.nextInt(3);
            col = random.nextInt(3);
        } while (board[row][col] != ' ');
        return new int[]{row, col};
    }

    private int[] findBestMove() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i][j] == ' ') {
                    return new int[]{i, j}; // Return the first empty cell
                }
            }
        }
        return null; // Should never happen
    }

    private boolean checkGameOver() {
        if (checkWin(currentPlayer)) {
            if (currentPlayer == player1Symbol) {
                player1Wins++;
            } else {
                player2Wins++;
            }
            updateScoreboard();
            JOptionPane.showMessageDialog(this, (playAgainstAI && currentPlayer == player2Symbol ? "Computer" : 
                    "Player " + (currentPlayer == player1Symbol ? "1" : "2")) + " wins!");
            resetGame();
            return true;
        } else if (isBoardFull()) {
            draws++;
            updateScoreboard();
            JOptionPane.showMessageDialog(this, "It's a draw!");
            resetGame();
            return true;
        }
        return false;
    }

    private void updateScoreboard() {
        scoreLabel.setText("Player 1 Wins: " + player1Wins + " | Player 2 Wins: " + player2Wins + " | Draws: " + draws);
    }

    private boolean checkWin(char player) {
        // Check rows and columns
        for (int i = 0; i < 3; i++) {
            if (board[i][0] == player && board[i][1] == player && board[i][2] == player) return true;
            if (board[0][i] == player && board[1][i] == player && board[2][i] == player) return true;
        }

        // Check diagonals
        return (board[0][0] == player && board[1][1] == player && board[2][2] == player) ||
               (board[0][2] == player && board[1][1] == player && board[2][0] == player);
    }

    private boolean isBoardFull() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i][j] == ' ') return false;
            }
        }
        return true;
    }

    private void resetGame() {
        initializeBoard();
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                buttons[i][j].setText("");
            }
        }
        isPlayer1Turn = true;
        currentPlayer = player1Symbol; // Reset to Player 1's symbol
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MultiModeTicTacToe game = new MultiModeTicTacToe();
            game.setVisible(true);
        });
    }
}
