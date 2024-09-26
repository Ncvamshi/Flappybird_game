import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;


public class FlappyBird extends JPanel implements ActionListener, KeyListener {
    
    int boardWidth = 1280;
        int boardHeight =  720;


    //Images
    Image backgroundImg;
    Image birdImg;
    Image topPipeImg;
    Image bottomPipeImg;

    //Bird
    int birdX = boardWidth/8;
    int birdY = boardHeight/2;
    int birdWidth = 34;
    int birdHeight = 24;

    class Bird
    {
        int x = birdX;
        int y = birdY;
        int width = birdWidth;
        int height = birdHeight;
        Image img;

        Bird(Image img)
        {
            this.img = img;
        }
    }

    //Pipes
    int pipeX= boardWidth;
    int pipeY = 0;
    int pipeHeight = 512;
    int pipeWidth = 64;

    class Pipe{

        int x = pipeX;
        int y = pipeY;
        int width = pipeWidth;
        int height = pipeHeight;
        Image img;
        boolean passed = false;


        Pipe(Image img){
            this.img = img;
        }
    }

    //game logic
    Bird bird;
    int VelocityX = -4;
    int VelocityY = 0;
    int gravity = 1;

    ArrayList<Pipe> pipes;
    Random random = new Random();


    Timer gameLoop;
    Timer placePipesTimer;

    boolean gameOver = false;
    double score = 0;

    FlappyBird() {
    
        setPreferredSize(new Dimension(boardWidth, boardHeight));
        //setBackground(Color.blue);

        setFocusable(true);
        addKeyListener(this);  
        
        //load image
        backgroundImg = new ImageIcon(getClass().getResource("./assets/flappybirdbg.png")).getImage();
        birdImg = new ImageIcon(getClass().getResource("./assets/flappybird.png")).getImage();
        topPipeImg = new ImageIcon(getClass().getResource("./assets/toppipe.png")).getImage();
        bottomPipeImg = new ImageIcon(getClass().getResource("./assets/bottompipe.png")).getImage();

        //bird
        bird = new Bird(birdImg);
        pipes = new ArrayList<Pipe>();

        //place pipes timer
        placePipesTimer = new Timer(1500, new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
                placePipes();
            }
        });
        placePipesTimer.start(); 

        //game timer
        gameLoop = new Timer(1000/60, this);
        gameLoop.start();

    }

    public void move() {

        //bird
        VelocityY += gravity;
        bird.y += VelocityY;
        bird.y = Math.max(bird.y, 0);

        //pipes
        for(int i=0; i<pipes.size(); i++)
        {
            Pipe pipe = pipes.get(i);
            pipe.x +=VelocityX;

            if(!pipe.passed && bird.x > pipe.x + pipe.width)
            {
                pipe.passed = true;
                score += 0.5; //0.5 for one pipe 
            }


            if(collision(bird, pipe))
            {
                gameOver = true;  
            }
        }

        if(bird.y >boardHeight)
        {
            gameOver = true;
        }

    }

    public boolean collision(Bird a, Pipe b)
    {
        return a.x <b.x + b.width && a.x+ a.width > b.x && a.y <b.y +b.height && a.y +a.height >b.y;
    }

    public void placePipes(){
        //(0 to 1) * pipeHeight/2 -> (0 to 256)
        //128
        //0-128 -(0-256) ---> pipeHeight/4 -> 3/4 pipeHeight

        int randomPipeY = (int)(pipeY - pipeHeight/4 - Math.random()*(pipeHeight/2));
        int openingSpace = boardHeight/4;

        //top pipe
        Pipe topPipe = new Pipe(topPipeImg);
        topPipe.y = randomPipeY;
        pipes.add(topPipe);

        //bottom pipe
        Pipe bottomPipe = new Pipe(bottomPipeImg);
        bottomPipe.y = topPipe.y + pipeHeight + openingSpace;
        pipes.add(bottomPipe);


    }

    @Override
    public void paintComponent(Graphics g)
    {   
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g)
    {
        //background
        g.drawImage(backgroundImg, 0, 0, boardWidth, boardHeight, null);

        //bird
        g.drawImage(bird.img, bird.x, bird.y, bird.width, bird.height, null);

        //pipes
        for(int i=0; i<pipes.size(); i++)
        { 
            Pipe pipe = pipes.get(i);
            g.drawImage(pipe.img, pipe.x, pipe.y, pipe.width, pipe.height, null);
        }

        //score 
        g.setColor(Color.white);
        g.setFont(new Font("Super Pixel", Font.PLAIN, 32)); 
        if(gameOver)
        {
            g.setColor(Color.RED);
            g.drawString("Game Over: " +String.valueOf((int) score), 10, 35);
        }
        else
        {
            g.setColor(Color.YELLOW );
            g.drawString(String.valueOf((int) score), 10, 35);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        
        move();
        repaint();
        if(gameOver)
        {
            placePipesTimer.stop();
            gameLoop.stop();
        }

    }

    @Override
    public void keyPressed(KeyEvent e) {

        if(e.getKeyCode() == KeyEvent.VK_SPACE) {
            VelocityY = -9;
            if(gameOver)
            {
                //restart by reset
                bird.y = birdY;
                VelocityY = 0;
                pipes.clear();
                score = 0;
                gameOver = false;
                gameLoop.start();
                placePipesTimer.start();
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {}

    @Override
    public void keyTyped(KeyEvent e) {}
    
}