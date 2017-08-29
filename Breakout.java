//Breakout: Term Project
//Amanda Zhu, Pd. 3


import javax.swing.*;
import java.awt.*;
import java.util.*;

public class Breakout extends JComponentWithEvents {
  
  //instance variables
  
  //board variables
  private int rows = 22;
  private int cols = 16;
  private int width = getWidth();
  private int height = getHeight();
  private int topBorder = 30;
  private Random random = new Random();
  private Color blockColor;
  private Color emptyColor = Color.black;
  private Color[][] board = new Color[rows][cols];
  private boolean paintStart = true;
  private int score = 0;
  private int highScore = 0;
  
  //block dimensions
  private int blockWidth = width/cols; //45 in this launch window
  private int blockHeight = (height - topBorder)/rows; //20 in this launch window
  
  //player dimensions
  private int playerWidth = 90; //2*blockWidth, made it a number so that the player platform
                                //does not get larger when it hits certain colored blocks
  private int playerHeight = blockHeight/2; //10 in this launch window
  private int playerX = width/2 - playerWidth/2;
  private int playerY = height - playerHeight;
  
  //ball dimensions
  private int radius = 8;
  private int ballX = width/2 - radius;
  private int ballY = height - (playerHeight + 2*radius);
  private int moveBallX = random.nextInt(blockWidth/8 + 1) + blockWidth/16; 
  //The 8 narrows the x movement range, the 16 makes sure it does not start from 0
  private int moveBallY = random.nextInt(blockWidth/16 + 1) + blockWidth/6; 
  //The 16 narrows the y movement range, the 6 makes sure the ball goes up-down more than side-side
  
  //speed changes
  private int speed = 40;
  private boolean ballMode = false;
  private boolean playerMode = false;
  private boolean normalMode = true;
  
  //starting the game and movement
  private boolean moveRight = false;
  private boolean moveUp = true;
  private boolean startGame = true;
  private boolean isGameOver = false;
  private boolean isPaused = false;
  private boolean winGame = false;
  
  //block colors
  private static Color[] BREAKOUT_COLORS = {
    Color.red, Color.yellow, Color.magenta, Color.pink,
    Color.cyan, Color.green, Color.orange, Color.gray, 
    Color.blue,
  }; 

  public void start() {
    setTimerDelay(speed);
    for (int row = 0; row < rows/2; row++) {
      for (int col = 0; col < cols; col++) {
        int blockIndex = random.nextInt(BREAKOUT_COLORS.length);
        blockColor = BREAKOUT_COLORS[blockIndex];
        board[row][col] = blockColor;
      }
    }
    for (int row = rows/2; row < rows; row++) {
      for (int col = 0; col < cols; col++) {
        board[row][col] = emptyColor;
      }
    }
  }
  
  public void resetGame() {
    if (isGameOver) {
      start();
      ballX = width/2 - radius;
      ballY = height - (playerHeight + 2*radius);
      playerX = width/2 - playerWidth/2;
      playerY = height - playerHeight;
      playerWidth = 90;
      moveRight = false;
      moveUp = true;
      startGame = true;
      isGameOver = false;
      paintStart = true;
      score = 0;
    }
  }
  
  public void timerFired() {
    if (isPaused == true) {
      return;
    }
    if (!startGame && !isGameOver) {
      int constantBallX = moveBallX;
      int constantBallY = moveBallY;
      if (moveRight) { //move right
        ballX += constantBallX;
        if (ballX >= width - 2*radius) {
          ballX = width - 2*radius;
          moveRight = false;
        }
      }
      else { //move left
        ballX -= constantBallX;
        if (ballX <= 0) {
          ballX = 0;
          moveRight = true;
        }
      }
      if (moveUp) { //move up
        ballY -= constantBallY;
        if (ballY <= (height - topBorder)/2 + topBorder) { //hitting the colored blocks
          hitBlock(ballY, ballX);
        }
        int row = 0; int col = 0; //winning the game
        if (clearBoard(row,col)) {
          winGame = true;
        }
        if (ballY <= topBorder) {
          ballY = topBorder;
          moveUp = false;
        }
      }
      else { //move down
        ballY += constantBallY;
        hitPlayer(ballX, ballY);
        if (ballY >= height) {
          isGameOver = true;
          paintStart = false;
        }
      }
    }
    if (score > highScore) highScore = score;
  }
     
  public void hitBlock(int ballY, int ballX) {
    int row = (ballY - topBorder)/blockHeight;
    int col = ballX/blockWidth;
    if (board[row][col] == Color.magenta || board[row][col] == Color.yellow) {
      moveUp = false;
      board[row][col] = emptyColor;
      ballMode = true;
      playerMode = false;
      normalMode = false;
      setTimerDelay(speed/2); //ball moves twice as fast
      score += 3;
    }
    if (board[row][col] == Color.cyan || board[row][col] == Color.gray) {
      moveUp = false;
      board[row][col] = emptyColor;
      playerMode = true;
      ballMode = false;
      normalMode = false;
      playerWidth = (playerWidth * 2) / 3; //player platform is 2/3 of its original size
      score += 5;
    }
    if (board[row][col] == Color.red || board[row][col] == Color.pink || 
             board[row][col] == Color.green || board[row][col] == Color.orange ||
             board[row][col] == Color.blue) { //the rest of the possible block colors
      moveUp = false;
      board[row][col] = emptyColor;
      normalMode = true;
      ballMode = false;
      playerMode = false;
      setTimerDelay(speed); //reset to original speed
      playerWidth = 90; //reset to original player length
      score++;
    }
  }
  
  public void hitPlayer(int ballX, int ballY) {
    if (!moveUp) {
      if (((ballY + 2*radius) >= playerY) && ((ballY + 2*radius) <= (playerY + playerHeight)) && 
          ((ballX + radius) >= playerX) && ((ballX + radius) <= (playerX + (playerWidth/2)))) {
          //hits left half of the player platform
        moveUp = true;
        if (moveRight) moveRight = false;
        //ball bounces back to the left if it comes from the left side moving right
      }
      if (((ballY + 2*radius) >= playerY) && ((ballY + 2*radius) <= (playerY + playerHeight)) && 
          ((ballX + radius) >= playerX + (playerWidth/2)) && ((ballX + radius) <= (playerX + playerWidth))) {
          //hits right half of the player platform
        moveUp = true;
        if (!moveRight) moveRight = true;
       //ball bounces back to the right if it comes from the right side moving left
      }
    }
  }
  
  public boolean clearBoard(int row, int col) {
    for (row = 0; row < rows; row++) {
      for (col = 0; col < cols; col++) {
        if (board[row][col] != emptyColor) {
          return false;
        }
      }
    }
    return true;
  }
  
  public void keyPressed(char key) {
    if (!startGame) {
      if (key == LEFT) {
        if (playerX >= 0) playerX -= 36;
      }
      else if (key == RIGHT) {
        if (playerX + playerWidth <= width) playerX += 36;
      }
    }
    if ((startGame == true) && (key == 'y')) startGame = false;
    if ((winGame == true) && (key == 'y')) winGame = false;
    else if ((isGameOver == true) && (key == 'r')) resetGame();
    if (key == 'p') isPaused = !isPaused;
  }
  
  //modified from Tetris's paintBoard method
  public void paintBoard(Graphics2D page, int row, int col, Color color) {
    int left = col * width / cols;
    int right = (col + 1) * width / cols; 
    int top  = row * (height - topBorder) / rows;
    int bottom = (row + 1) * (height - topBorder) / rows;
    page.setColor(color);
    page.fillRect(left, top + topBorder, right-left, bottom-top);
    page.setColor(Color.black);
    page.drawRect(left, top + topBorder, right-left, bottom-top);
  }
    
  public void paint(Graphics2D page) {
    page.setColor(Color.black);
    page.fillRect(0, 0, width, height);
    for (int row = 0; row < rows; row++) 
      for (int col = 0; col < cols; col++) 
        paintBoard(page, row, col, board[row][col]);
    page.setColor(Color.white);
    page.setFont(new Font("SansSerif", Font.BOLD, 15));
    page.drawString("Score: " + score, 10, 20);
    page.drawString("High Score: " + highScore, 315, 20);
    if (paintStart) {
      page.setColor(Color.lightGray);
      page.fillRect(playerX, playerY, playerWidth, playerHeight);
      page.setColor(Color.white);
      page.fillOval(ballX, ballY, 2*radius, 2*radius);
    }
    if (startGame) {
      page.setColor(Color.black);
      page.setFont(new Font("SansSerif", Font.BOLD, 50));
      page.drawString("Press 'y' to Start Game", 88, 230);
    }
    if (isGameOver) {
      page.setColor(Color.white);
      page.setFont(new Font("SansSerif", Font.BOLD, 100));
      page.drawString("GAME OVER", 60, 200);
      page.setFont(new Font("SansSerif", Font.BOLD, 50));
      page.drawString("Press 'r' to restart", 145, 300);
    }
    if (winGame) {
     page.setFont(new Font("SansSerif", Font.BOLD, 100));
     page.drawString("YOU WIN!", 125, 200);
     page.setFont(new Font("SansSerif", Font.BOLD, 50));
     page.drawString("Play again? Press 'y'", 115, 300);
    }
  }
  
  public static void main(String[] args) { launch(720, 470); }
}
