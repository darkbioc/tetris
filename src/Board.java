
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;

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
            if(canMove(currentShape,currentRow,currentCol-1))
                currentCol--;
            break;
        case KeyEvent.VK_RIGHT:
            if(canMove(currentShape,currentRow,currentCol+1))
                currentCol++;
            break;
        case KeyEvent.VK_UP:
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
            
        break;
        case KeyEvent.VK_DOWN:
            if(canMove(currentShape,currentRow+1, currentCol))
                currentRow++;
        break;
        case KeyEvent.VK_P:
            if(!timer.isRunning())
            {
                scoreBoard.resume();
                timer.start();
            }
            else
            {
                timer.stop();
                scoreBoard.pause();
            }
        break;
        default:
        break;
        }
        repaint();
        }
    }
    public ScoreBoard scoreBoard;
    
    public static final int NUM_ROWS = 22;
    public static final int NUM_COLS = 10;

    private final Tetrominoes[][] matrix;
    private int deltaTime;

    private Shape currentShape;

    private int currentRow;
    private int currentCol;

    private final Timer timer;
    
    private final boolean paused=false;
    MyKeyAdapter keyAdepter;
    
    public static final int INIT_ROW = -1;

    public Board() {
        super();
        matrix = new Tetrominoes[NUM_ROWS][NUM_COLS];
        initValues();
        timer = new Timer(deltaTime, this);
        keyAdepter= new MyKeyAdapter();
        
    }

    public void initValues() {
        setFocusable(true);
        cleanBoard();
        deltaTime = 500;
        currentShape = null;
        currentRow = INIT_ROW;
        currentCol = NUM_COLS / 2;
    }

    public void initGame() {
        removeKeyListener(keyAdepter);
        initValues();
        timer.setDelay(deltaTime);
        currentShape = new Shape();
        timer.start();
        addKeyListener(keyAdepter);
        
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
            System.out.println("Current col: "+currentCol+" YMax: "+currentShape.getYMax()+" YMin: "+currentShape.getYMin()+" Current row: "+currentRow+" DTime: "+deltaTime+" Score: "+scoreBoard.getScore());
            currentRow++;
            repaint();
        }
        else
        {
            if(currentShape.getYMin()+currentRow<0)
            {
               
                gameOver();
                timer.stop();
            }
            else
            {
                moveCurrentShapeToMatrix();
                checkRows();
                currentShape = new Shape();
                currentRow = INIT_ROW;
                currentCol = NUM_COLS/2;
            }
            
        }
    }
    private void moveCurrentShapeToMatrix()
    {
        int[][] squaresArray = currentShape.getCoordinates();
        for(int point = 0; point<=3; point++)
        {
            matrix[currentRow+squaresArray[point][1]][currentCol+squaresArray[point][0]]=currentShape.getShape();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawBoard(g);
        if (currentShape!=null)
        drawCurrentShape(g);
    }
    public void drawBoard(Graphics g)
    {
        for (int row = 0; row < NUM_ROWS; row++) 
        {
            for (int col = 0; col < NUM_COLS; col++) 
            {
                drawSquare(g, row, col, matrix[row][col]);
            }
        }
    }
    private void drawSquare(Graphics g, int row, int col, Tetrominoes shape) {
        Color colors[] = {new Color(0, 0, 0),
            new Color(204, 102, 102),
            new Color(102, 204, 102), new Color(102, 102, 204),
            new Color(204, 204, 102), new Color(204, 102, 204),
            new Color(102, 204, 204), new Color(218, 170, 0)
        };
        int x = col * squareWidth();
        int y = row * squareHeight();
        Color color = colors[shape.ordinal()];
        g.setColor(color);
        g.fillRect(x + 1, y + 1, squareWidth() - 2,
                squareHeight() - 2);
        g.setColor(color.brighter());
        g.drawLine(x, y + squareHeight() - 1, x, y);
        g.drawLine(x, y, x + squareWidth() - 1, y);
        g.setColor(color.darker());
        g.drawLine(x + 1, y + squareHeight() - 1,
                x + squareWidth() - 1, y + squareHeight() - 1);
        g.drawLine(x + squareWidth() - 1,
                y + squareHeight() - 1,
                x + squareWidth() - 1, y + 1);
    }
    
    private int squareWidth() {
        return getWidth() / NUM_COLS;
    }
    
    private int squareHeight() {
        return getHeight() / NUM_ROWS;
    }
    private void drawCurrentShape(Graphics g) 
    {
    int[][] squaresArray = currentShape.getCoordinates();
    for(int point = 0; point<=3; point++)
        {
        drawSquare(g, currentRow + squaresArray[point][1], currentCol + squaresArray[point][0], currentShape.getShape());
        }
    }
    
    private boolean canMove(Shape shape, int newRow, int newCol)
    {
        return !(newCol + shape.getXMin()<0 || (newCol+shape.getXMax()>= NUM_COLS || newRow + shape.getYMax()>=NUM_ROWS || hitWithMatrix(shape,newRow, newCol)));
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
    public void checkRows()
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
                    if(dRows==4)
                        scoreBoard.increment(50);
                    if(scoreBoard.getScore()%(500*scoreBoard.getLevel()*scoreBoard.getLevel())==0 && deltaTime!=50)
                    {
                        scoreBoard.newLevel();
                        deltaTime-=100;
                        timer.setDelay(deltaTime);
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
    public void gameOver()
    {
        for (int row = 0; row < NUM_ROWS; row++) 
        {
            for (int col = 0; col < NUM_COLS; col++) 
            {
                matrix[row][col] = Tetrominoes.LineShape;
            }
        }
        currentShape=null;
        repaint();
        scoreBoard.gameOver();
    }
}
