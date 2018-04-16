
import javax.swing.JLabel;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author victor
 */
public class ScoreBoard extends JLabel{
    
    private int score;
    private int level;
    
    public ScoreBoard() {
        super();
        score = 0;
        level = 1;
        setText("Score: "+score+" Level:"+level );
    }
    
    public void increment(int points) {
        score += points;
        setText("Score: "+score+" Level:"+level);
    }
    
    public void newLevel()
    {
        level++;
        setText("Score: "+score+" Level:"+level);
    }
    
    public void reset() {
        score = 0;
        level=1;
        setText("Score: "+score+" Level:"+level);
    }
    
    public void pause()
    {
        setText("PAUSED");
    }
    
    public void resume()
    {
        setText("Score: "+score+" Level:"+level);
    }
    public int getScore()
    {
        return score;
    }
    public int getLevel()
    {
        return level;
    }
    public void gameOver()
    {
        setText("GAME OVER! Score: "+score);
    }
}
