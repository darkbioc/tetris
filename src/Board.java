
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.util.logging.*;
import javax.swing.*;
import javax.swing.Timer;
import sun.audio.*;
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author victor
 */
public final class Board extends JPanel implements ActionListener {
    class MyKeyAdapter extends KeyAdapter 
    {
        @Override
        public void keyPressed(KeyEvent e) 
        {
        switch (e.getKeyCode()) {
        case KeyEvent.VK_LEFT:
            if(!gameOver && timer.isRunning())
            {
                if(canMove(currentShape,currentRow,currentCol-1))
                {
                    currentCol--;
                }
            }
            break;
        case KeyEvent.VK_RIGHT:
            
            if(!gameOver && timer.isRunning())
            {
                if(canMove(currentShape,currentRow,currentCol+1))
                {
                    currentCol++;
                }
            }
            break;
        case KeyEvent.VK_UP:
            if(!gameOver && timer.isRunning())
            {
                rotate();
            }            
        break;
        case KeyEvent.VK_DOWN:
            if(!gameOver && timer.isRunning())
            {
                if(canMove(currentShape,currentRow+1, currentCol))
                {
                    currentRow++;
                }
            }
        break;
        case KeyEvent.VK_P:
            switchPause();
        break;
        
        case KeyEvent.VK_M:
            switchMusic();
            break;
        case KeyEvent.VK_C:
            if (!gameOver && timer.isRunning())
            {
                swap();
            }
            
        break;
        case KeyEvent.VK_SPACE:
            if (!gameOver && timer.isRunning())
            {
                for (int i = 0; i < NUM_ROWS+1; i++) 
                {
                    if(!canMove(currentShape, i, currentCol))
                    {
                        currentRow=i-1;
                        hit();
                        break;
                    }
                }
            }
            break;
        default:
        break;
        }
        repaint();
        }
    }
    public ScoreBoard scoreBoard;
    private JFrame parentFrame;

    public void setParentFrame(JFrame parentFrame) {
        this.parentFrame = parentFrame;
    }
    public static final int NUM_ROWS = 22;
    public static final int NUM_COLS = 10;

    private final Tetrominoes[][] matrix;
    private int deltaTime;
    private NextPiecePanel nextPiecePanel;
    private Shape currentShape;
    private int currentRow;
    private int currentCol;
    private HoldPanel holdPanel;
    private AudioStream audios=null;
    private InputStream music;
    private InputStream effect;
    private AudioStream effectAudio;
    private boolean gameOver;
    private boolean swapped;
    private boolean firstSwap;
    public Timer timer;
    
    private boolean musicStopped;
    MyKeyAdapter keyAdepter;
    
    public static final int INIT_ROW = -1;

    public Board() {
        super();
        matrix = new Tetrominoes[NUM_ROWS][NUM_COLS];
        initValues();
        timer = new Timer(deltaTime, this);
        keyAdepter= new MyKeyAdapter();
        
    }
    
    public void setNextPiecePanel(NextPiecePanel p)
    {
        nextPiecePanel = p;
    }
    public void setHoldPanel(HoldPanel p)
    {
        holdPanel = p;
    }
    public void setPausePanel(JPanel p)
    {
        pausePanel=p;
    }
    
    private JPanel pausePanel;

    public void initValues() {
        setFocusable(true);
        cleanBoard();
        deltaTime = 500;
        currentShape = null;
        currentRow = INIT_ROW;
        currentCol = NUM_COLS / 2;
    }

    public void initGame() {
        AudioPlayer.player.stop(audios);
        removeKeyListener(keyAdepter);
        initValues();
        scoreBoard.reset();
        if(scoreBoard.getLevel()!=1)
            deltaTime=deltaTime-(50*scoreBoard.getLevel());
        timer.setDelay(deltaTime);
        nextPiecePanel.generateNewShape();
        holdPanel.clearShape();
        currentShape = new Shape();
        gameOver=false;
        timer.start();
        swapped=false;
        firstSwap=true;
        addKeyListener(keyAdepter);
        playSong();
        
    }
    
    public void setScoreBoard(ScoreBoard scoreboard)
    {
        this.scoreBoard=scoreboard;
    }

    public void cleanBoard() {
        for (int row = 0; row < NUM_ROWS; row++) {
            for (int col = 0; col < NUM_COLS; col++) {
                matrix[row][col] = Tetrominoes.NoShape;
            }
        }
    }

    // Game Main Loop
    @Override
    public void actionPerformed(ActionEvent ae) 
    {
        if(canMove(currentShape,currentRow+1, currentCol))
        {
            //System.out.println("Current col: "+currentCol+" YMax: "+currentShape.getYMax()+" YMin: "+currentShape.getYMin()+" Current row: "+currentRow+" DTime: "+deltaTime+" Score: "+scoreBoard.getScore());
            currentRow++;
            repaint();
        }
        else
        {
            if(currentShape.getYMin()+currentRow<0)
            {
               
                try 
                {
                    gameOver();
                } 
                catch (IOException ex) 
                {
                    Logger.getLogger(Board.class.getName()).log(Level.SEVERE, null, ex);
                }
                timer.stop();
            }
            else
            {
                hit();
            }
            
        }
    }
    private void hit()
    {
        swapped=false;
                moveCurrentShapeToMatrix();
                try {
                    checkRows();
                } catch (IOException ex) {
                    Logger.getLogger(Board.class.getName()).log(Level.SEVERE, null, ex);
                }
                currentShape = nextPiecePanel.getShape();
                nextPiecePanel.generateNewShape();
                currentRow = INIT_ROW;
                currentCol = NUM_COLS/2;
    }
    private void moveCurrentShapeToMatrix()
    {
        int[][] squaresArray = currentShape.getCoordinates();
        for(int point = 0; point<=3; point++)
        {
            int row=currentRow+squaresArray[point][1];
            int col=currentCol+squaresArray[point][0];
            if(row>=0)
            {
                matrix[row][col]=currentShape.getShape();
            }
        }
        
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawBoard(g);
        if (currentShape!=null)
        currentShape.draw(g, currentRow, currentCol, squareWidth(), squareHeight());
    }
    public void drawBoard(Graphics g)
    {
        for (int row = 0; row < NUM_ROWS; row++) 
        {
            for (int col = 0; col < NUM_COLS; col++) 
            {
                Util.drawSquare(g, row, col, matrix[row][col],squareWidth(),squareHeight());
            }
        }
    }
 
    
    private int squareWidth() {
        return getWidth() / NUM_COLS;
    }
    
    private int squareHeight() {
        return getHeight() / NUM_ROWS;
    }
    
    private boolean canMove(Shape shape, int newRow, int newCol)
    {
        return !(!timer.isRunning() || newCol + shape.getXMin()<0 || (newCol+shape.getXMax()>= NUM_COLS || newRow + shape.getYMax()>=NUM_ROWS || hitWithMatrix(shape,newRow, newCol)));
    }
    private boolean hitWithMatrix(Shape shape, int row, int col)
    {
        int[][] squaresArray = shape.getCoordinates();
        for(int point = 0; point<=3; point++)
        {
            int mRow = row+squaresArray[point][1];
            int mCol = col+squaresArray[point][0];
            if(mRow >= 0)
            {
                if(matrix[mRow][mCol] != Tetrominoes.NoShape)
                {
                    return true;
                }
            }
        }
        return false;
    }
    public void checkRows() throws IOException
    {
        int dRows = 0;
        for (int i = 0; i < NUM_ROWS; i++) 
        {
            int  counter=0;
            for (int j = 0; j < NUM_COLS; j++) 
            {
                if(matrix[i][j]!=Tetrominoes.NoShape)
                {
                    counter++;
                }
                if(counter==10)
                {
                    dRows++;
                    scoreBoard.increment(100);
                    effect=getClass().getResourceAsStream("/Line.wav");
                    effectAudio = new AudioStream(effect);
                    AudioPlayer.player.start(effectAudio);
                    if(dRows==4)
                    {
                        effect=getClass().getResourceAsStream("/Tetris.wav");
                        effectAudio = new AudioStream(effect);
                        AudioPlayer.player.start(effectAudio);
                        scoreBoard.increment(100);
                    }
                    if(scoreBoard.getScore()%(500*scoreBoard.getLevel()*scoreBoard.getLevel())==0 && deltaTime!=50)
                    {
                        scoreBoard.newLevel();
                        deltaTime-=50;
                        timer.setDelay(deltaTime);
                        effect=getClass().getResourceAsStream("/LevelUp.wav");
                        effectAudio = new AudioStream(effect);
                        AudioPlayer.player.start(effectAudio);
                        AudioPlayer.player.stop(audios);
                        playSong();
                    }
                    for (int k = i; k > 0; k--) 
                    {
                        System.arraycopy(matrix[k-1], 0, matrix[k], 0, NUM_COLS);
                    }
                    for (int k = 0; k < NUM_COLS; k++) 
                    {
                        matrix[0][k]=Tetrominoes.NoShape;
                    }
                }
            }
        }
    }
    public void gameOver() throws FileNotFoundException, IOException
    {
        for (int row = 0; row < NUM_ROWS; row++) 
        {
            for (int col = 0; col < NUM_COLS; col++) 
            {
                matrix[row][col] = Tetrominoes.LineShape;
            }
        }
        currentShape=null;
        gameOver=true;
        repaint();
        scoreBoard.gameOver();
        AudioPlayer.player.stop(audios);
        music=getClass().getResourceAsStream("/High Scores.wav");
        audios = new AudioStream(music);
        nextPiecePanel.clearShape();
        nextPiecePanel.repaint();
        holdPanel.clearShape();
        holdPanel.repaint();
        if(!musicStopped)
        {
            AudioPlayer.player.start(audios);
        }
        RecordsDialog d = new RecordsDialog(parentFrame, true, scoreBoard.getScore());
        d.setVisible(true);
        
    }
    public void playSong() 
    {
        
        try {
            music = getClass().getResourceAsStream("/Level "+(scoreBoard.getLevel()-1)+".wav");
            audios = new AudioStream(music);
            if (!musicStopped)
            {
                AudioPlayer.player.start(audios);
            }
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.getLocalizedMessage());
        }
    }
    public void rotate()
    {
        Shape rotShape = currentShape.rotateRight();
            /*if(currentRow!=20)
            {*/
                if(currentCol==0 && !(currentShape.getXMax()==1 && currentShape.getXMin()==0 && currentShape.getYMax()==1 && currentShape.getYMin()==0))
                {
                    if(canMove(currentShape, currentRow, currentCol+1))
                    {
                        if(currentShape.getYMin()==-2 && canMove(currentShape, currentRow, currentCol+2))
                        {
                            if(canMove(rotShape, currentRow, currentCol+2))
                            {
                                currentCol+=2;
                                currentShape=rotShape;
                            }
                        }
                        else if(currentShape.getYMax()==2)
                        {
                            if(canMove(rotShape, currentRow, currentCol+1))
                            {
                                currentCol++;
                                currentShape=rotShape;
                            }
                        }
                        else if (currentShape.getYMin()!=-2 )
                        {
                            currentCol++;
                            currentShape=rotShape;
                        }                    
                    }
                }
                else if(currentCol==NUM_COLS-1)
                {
                    if(canMove(currentShape, currentRow, currentCol-1))
                    {
                        if(currentShape.getYMax()==2 && canMove(currentShape, currentRow, currentCol-2))
                        {
                            if(canMove(rotShape, currentRow, currentCol-2))
                            {
                                currentCol-=2;
                                currentShape=rotShape;
                            }                      
                        }
                        else if(currentShape.getYMin()==-2)
                        {
                            if(canMove(rotShape, currentRow, currentCol-1))
                            {
                                currentCol--;
                                currentShape=rotShape;
                            }
                        }
                        else if(currentShape.getYMax()!=2)
                        {
                            currentCol--;
                            currentShape=rotShape;
                        }
                    }
                }
                else if(currentCol==NUM_COLS-2 && currentShape.getYMax()==2)
                {
                    if(canMove(rotShape, currentRow, currentCol-1))
                    {
                        currentCol--;
                        currentShape=rotShape;
                    }
                }
                else if(currentCol==1 && currentShape.getYMin()==-2)
                {
                    if(canMove(rotShape, currentRow, currentCol+1))
                    {
                        currentCol++;
                        currentShape=rotShape;
                    }
                }
                else if(canMove(rotShape, currentRow, currentCol))
                {
                    currentShape=rotShape;
                }
                else if(!canMove(rotShape, currentRow, currentCol))
                {
                    if(canMove(rotShape, currentRow, currentCol+1))
                    {
                        currentCol++;
                        currentShape=rotShape;
                    }
                    if(canMove(rotShape, currentRow, currentCol-1))
                    {
                        currentCol--;
                        currentShape=rotShape;
                    }
                    if(!canMove(rotShape, currentRow, currentCol+1) && currentShape.getYMin()==-2)
                    {
                        if(canMove(rotShape, currentRow, currentCol+2))
                        {
                            currentCol+=2;
                            currentShape=rotShape;
                        }
                    }
                    if(!canMove(rotShape, currentRow, currentCol-1) && currentShape.getYMax()==2)
                    {
                        if(canMove(rotShape, currentRow, currentCol-2))
                        {
                            currentCol-=2;
                            currentShape=rotShape;
                        }
                    }
                //}
            /*if(canMove(rotShape,currentRow, currentCol))
            {
                currentShape=rotShape;
            }*/
            }
    }
    public void swap()
    {
        if(!swapped)
            {
                if(firstSwap)
                {
                    holdPanel.swapShape(currentShape);
                    currentShape = nextPiecePanel.getShape();
                    nextPiecePanel.generateNewShape();
                    firstSwap=false;
                }
                else
                {
                    currentShape=holdPanel.swapShape(currentShape);
                }
                currentRow = INIT_ROW;
                currentCol = NUM_COLS/2;
                swapped=true;
            }
    }
    public void switchMusic()
    {
        if (gameOver || timer.isRunning())
            {
                if (!musicStopped)
                    {
                        AudioPlayer.player.stop(audios);
                        musicStopped=true;
                    }
                    else
                    {
                        AudioPlayer.player.start(audios);
                        musicStopped=false;
                    }
            }
    }
    public void switchPause()
    {
        effect=getClass().getResourceAsStream("/Pause.wav");
        
            try 
            {
                effectAudio = new AudioStream(effect);
            } 
            catch (IOException ex) 
            {
                Logger.getLogger(Board.class.getName()).log(Level.SEVERE, null, ex);
            }
        
            AudioPlayer.player.start(effectAudio);
            if(!gameOver)
            {    
                if(!timer.isRunning())
                {
                    pausePanel.setVisible(false);
                    if(!musicStopped)
                        AudioPlayer.player.start(audios);
                    scoreBoard.resume();
                    timer.start();
                }
                else
                {
                    pausePanel.setVisible(true);
                    if(!musicStopped)
                        AudioPlayer.player.stop(audios);
                    timer.stop();
                    scoreBoard.pause();
                }
            }
    }
    public void setInitialLevel(int ilevel)
    {
        scoreBoard.setLevel(ilevel);
    }
}
