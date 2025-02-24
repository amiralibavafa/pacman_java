import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashSet;
import java.util.Random;
import java.util.List;
import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.Collections;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.Timer;

public class PacMan extends JPanel implements ActionListener, KeyListener{
    class Block{
        int x;
        int y;
        int width;
        int height;
        int startX;
        int startY;
        Image image;

        char direction = 'U';
        int velocityX = 0;
        int velocityY = 0;

        Block(int x, int y, int width, int height, Image image){ 
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.startX = this.x;   
            this.startY = this.y;
            this.image = image;
        }

        void updateDirection (char direction){
            char prevDirection = this.direction; 
            this.direction = direction;
            updateVelocity();
            this.x += this.velocityX;
            this.y += this.velocityY;
            for (Block wall : walls){
                if (checkCollision(this, wall)){
                    this.x -= this.velocityX;
                    this.y -= this.velocityY;
                    this.direction = prevDirection;
                    updateVelocity();
                    break;
                }
            } 
        }

        void updateVelocity(){
            if(direction == 'U'){
                velocityX = 0;
                velocityY = -tileSize/4;
            } else if (direction == 'D'){
                velocityX = 0;
                velocityY = tileSize/4;
            } else if (direction == 'L'){
                velocityX = -tileSize/4;
                velocityY = 0;
            } else if (direction == 'R'){
                velocityX = tileSize/4 ;
                velocityY = 0;
            }
        }

        void reset(){
            this.x = this.startX;
            this.y = this.startY;
        }

    }

    private int rowCount = 21;
    private int columnCount = 19;
    private int tileSize = 32;

    private int width = columnCount * tileSize;
    private int height = rowCount * tileSize;

    private Image wallImage;
    private Image powerFood;

    private Image redGhostImage;
    private Image pinkGhostImage;
    private Image blueGhostImage;
    private Image orangeGhostImage;

    private Image pacmanRight;
    private Image pacmanLeft;  
    private Image pacmanUp;
    private Image pacmanDown;

    //X = wall, O = skip, P = pac man, ' ' = food
    //Ghosts: b = blue, o = orange, p = pink, r = red
    private String[] tileMap = {
        "XXXXXXXXXXXXXXXXXXX",
        "X        X        X",
        "X XX XXX X XXX XX X",
        "X                 X",
        "X XX X XXXXX X XX X",
        "X    X       X    X",
        "XXXX XXXX XXXX XXXX",
        "OOOX         X XOOO",
        "XXXX X XXrXX X XXXX",
        "O       bpo       O",
        "XXXX X XXXXX X XXXX",
        "OOOX X       X XOOO",
        "XXXX X XXXXX X XXXX",
        "X        X        X",
        "X XX XXX X XXX XX X",
        "X  X     P     X  X",
        "XX X X XXXXX X X XX",
        "X    X   X   X    X",
        "X XXXXXX X XXXXXX X",
        "X                 X",
        "XXXXXXXXXXXXXXXXXXX" 
    };

    HashSet<Block> walls;
    HashSet<Block> foods;
    HashSet<Block> ghosts;
    Block pacman;

    Timer gameLoop;

    char[] directions = {'U', 'D', 'L', 'R'};
    Random random = new Random();
    int score = 0;
    int lives = 3; 
    boolean gameOver = false;


    PacMan(){
        setPreferredSize(new Dimension(width, height));
        setBackground(Color.BLACK);

        addKeyListener(this);
        setFocusable(true);

        //load images
        wallImage = new ImageIcon(getClass().getResource("/wall.png")).getImage();
        powerFood = new ImageIcon(getClass().getResource("/powerFood.png")).getImage(); 

        redGhostImage = new ImageIcon(getClass().getResource("/redGhost.png")).getImage();
        pinkGhostImage = new ImageIcon(getClass().getResource("/pinkGhost.png")).getImage();
        blueGhostImage = new ImageIcon(getClass().getResource("/blueGhost.png")).getImage();
        orangeGhostImage = new ImageIcon(getClass().getResource("/orangeGhost.png")).getImage();

        pacmanRight = new ImageIcon(getClass().getResource("/pacmanRight.png")).getImage();
        pacmanLeft = new ImageIcon(getClass().getResource("/pacmanLeft.png")).getImage();
        pacmanUp = new ImageIcon(getClass().getResource("/pacmanUp.png")).getImage();
        pacmanDown = new ImageIcon(getClass().getResource("/pacmanDown.png")).getImage();

        loadMap();

        for (Block ghost : ghosts){
            int ghostRow = ghost.y / tileSize;
            int ghostCol = ghost.x / tileSize;
            int pacmanRow = pacman.y / tileSize;
            int pacmanCol = pacman.x / tileSize;
        
            // Use A* to get the path from the ghost to Pac-Man
            List<int[]> path = AStar.aStar(ghostRow, ghostCol, pacmanRow, pacmanCol);
        
            if (path != null && path.size() > 1) {
                // Move the ghost to the next position in the path
                int[] nextStep = path.get(1); // the second position is the next step
                int nextRow = nextStep[0];
                int nextCol = nextStep[1];
        
                // Calculate direction based on the next step
                if (nextRow > ghostRow) ghost.updateDirection('D');
                else if (nextRow < ghostRow) ghost.updateDirection('U');
                else if (nextCol > ghostCol) ghost.updateDirection('R');
                else if (nextCol < ghostCol) ghost.updateDirection('L');
            }
        }
        gameLoop = new Timer(50, this);
        gameLoop.start();
    }

    public void loadMap(){
        walls = new HashSet<Block>();
        foods = new HashSet<Block>();
        ghosts = new HashSet<Block>();

        for (int r = 0 ; r < rowCount; r++){
            for (int c = 0; c < columnCount; c++){
                String row = tileMap[r];
                char tile = row.charAt(c);
                int x = c * tileSize;
                int y = r * tileSize;  

                if (tile == 'X'){
                    walls.add(new Block(x, y, tileSize, tileSize, wallImage));
                } else if (tile == 'O'){
                    foods.add(new Block(x, y, tileSize, tileSize, null));
                } else if (tile == 'P'){
                    pacman = new Block(x, y, tileSize, tileSize, pacmanRight);
                } else if (tile == ' '){
                    foods.add(new Block(x +14, y+14, 4, 4, powerFood));
                } else if (tile == 'r'){
                    ghosts.add(new Block(x, y, tileSize, tileSize, redGhostImage));
                } else if (tile == 'p'){
                    ghosts.add(new Block(x, y, tileSize, tileSize, pinkGhostImage));
                } else if (tile == 'b'){
                    ghosts.add(new Block(x, y, tileSize, tileSize, blueGhostImage));
                } else if (tile == 'o'){
                    ghosts.add(new Block(x, y, tileSize, tileSize, orangeGhostImage));
                }
            }
        }
    }

    public void paintComponent(Graphics g){
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g){
        g.drawImage(pacman.image, pacman.x, pacman.y, pacman.width, pacman.height, null);

        for (Block ghost : ghosts){
            g.drawImage(ghost.image, ghost.x, ghost.y, ghost.width, ghost.height, null);
        }

        for (Block wall : walls){
            g.drawImage(wall.image, wall.x, wall.y, wall.width, wall.height, null);
        }

        for (Block food : foods){
            if (food.image != null){
                g.drawImage(food.image, food.x, food.y, food.width, food.height, null);
            }
        }

        //score
        g.setFont(new Font("Arial", Font.PLAIN, 20));
        g.setColor(Color.WHITE);
        if (gameOver){
             g.drawString("Game Over "+ String.valueOf(score), tileSize/2 + 2, tileSize/2 + 2);
        }

        else{
            g.drawString("x"+ String.valueOf(lives)+" Score: "+String.valueOf(score), tileSize/2 + 2 , tileSize/2 + 2);
        }

    }

    void move(){
        pacman.x += pacman.velocityX;
        pacman.y += pacman.velocityY;

        //check for collision with walls
        for (Block wall : walls){
            if (checkCollision(pacman, wall) || pacman.x < 0 || pacman.x + pacman.width > width){
                pacman.x -= pacman.velocityX;
                pacman.y -= pacman.velocityY;
            }
        }

        for (Block ghost : ghosts){
            if (checkCollision(pacman, ghost)){
                lives--;
                if (lives == 0){
                    gameOver = true;
                } else {
                    pacman.reset();
                    pacman.velocityX = 0;
                    pacman.velocityY = 0;   
                    for (Block ghost2 : ghosts){
                        ghost2.reset();
                        int ghostRow = ghost.y / tileSize;
                        int ghostCol = ghost.x / tileSize;
                        int pacmanRow = pacman.y / tileSize;
                        int pacmanCol = pacman.x / tileSize;
                    
                        // Use A* to get the path from the ghost to Pac-Man
                        List<int[]> path = AStar.aStar(ghostRow, ghostCol, pacmanRow, pacmanCol);
                    
                        if (path != null && path.size() > 1) {
                            // Move the ghost to the next position in the path
                            int[] nextStep = path.get(1); // the second position is the next step
                            int nextRow = nextStep[0];
                            int nextCol = nextStep[1];
                    
                            // Calculate direction based on the next step
                            if (nextRow > ghostRow) ghost.updateDirection('D');
                            else if (nextRow < ghostRow) ghost.updateDirection('U');
                            else if (nextCol > ghostCol) ghost.updateDirection('R');
                            else if (nextCol < ghostCol) ghost.updateDirection('L');
                        }
                    }
                }
            }

            if (ghost.y == tileSize * 9 && ghost.direction != 'U' && ghost.direction != 'D'){
                ghost.updateDirection('U');
            }
            ghost.x += ghost.velocityX;
            ghost.y += ghost.velocityY;

            for (Block wall : walls){
                if (checkCollision(ghost, wall) || ghost.x < 0 || ghost.x + ghost.width > width){
                    ghost.x -= ghost.velocityX;
                    ghost.y -= ghost.velocityY;
                    ghost.direction = directions[random.nextInt(4)];
                    ghost.updateVelocity();
                }
            }
        }

        //food collision
        for (Block food : foods){
            if (checkCollision(pacman, food)){
                foods.remove(food);
                score += 10;
                break;
            }
        }
    }
    
    

    public boolean checkCollision(Block a, Block b){
        return a.x < b.x + b.width && a.x + a.width > b.x && a.y < b.y + b.height && a.y + a.height > b.y;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        repaint(); 
        if (gameOver){
            gameLoop.stop();
        }
        if (foods.size() == 0){
            gameLoop.stop();
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {

    } 

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_UP){
            pacman.updateDirection('U');
        } else if (e.getKeyCode() == KeyEvent.VK_DOWN){
            pacman.updateDirection('D');
        } else if (e.getKeyCode() == KeyEvent.VK_LEFT){
            pacman.updateDirection('L');
        } else if (e.getKeyCode() == KeyEvent.VK_RIGHT){
            pacman.updateDirection('R');
        }

        if (pacman.direction == 'U'){
            pacman.image = pacmanUp;
        } else if (pacman.direction == 'D'){
            pacman.image = pacmanDown;
        } else if (pacman.direction == 'L'){
            pacman.image = pacmanLeft;
        } else if (pacman.direction == 'R'){
            pacman.image = pacmanRight;
        }   
    
    }
}
