/** Program: DATA STRUCTURES FINAL PROJECT - CONNECT FOUR GAME
Written by: Jake Nelson
Program Description: This is a node which represents a possible connect four game state. 
Challenges: Figuring out how nodes can relate to each other in terms of their game state, and determining a system for giving a numeric "score" for game states.
Time Spent: 30-35 hours between various previous versions of this project, including the time spent learning/figuring out/writing the minimax algorithm.
Revision Log
Date:                   By:                  Action:
12.11.2020               JN                  Created the basis for a 2-player connect four game with 2D array.
12.12.2020               JN                  Polished the basic game and created checking winner system.
12.13.2020               JN                  Attempted to phase out 2D array with stack data structure.
12.14.2020               JN                  Worked out logistics for a array of stacks, the idea being that newly-placed pieces
                                             would be the head of the stack for easier checking.
12.15.2020               JN                  Realized an array of stacks absolutely does not outperform a 2D array, scratched the idea, started
                                             to build nodes for a graph structure, where graph nodes are game pieces.
12.16.2020               JN                  Worked more on graph structure.
12.17.2020               JN                  Started to dislike graph idea and number of check operations that had to be written, started
                                             to embrace idea of returning to 2D array with AI opponent system as the data structure.
12.18.2020               JN                  Studied minimax algorithm and began brainstorming/planning how that would fit into connect four.
12.19.2020               JN                  Created GameNode.java + GameTree.java, gave GameNode some class members and method signatures.
12.20.2020               JN                  Created a board-scoring method so that each node in the tree has a "game state" and a corresponding score.
                                             Also began assembling the tree and its composition with nodes
12.21.2020               JN                  Started to write minimax algorithm for tree, as well as helper functions for minimax. Lots of logistics
                                             worked out, lots of testing.
12.22.2020               JN                  Get minimax function working and involved into the game. Played a ton of connect four with AI of my own
                                             creation
12.23.2020               JN                  Improved the game display, made it so it randomly selects who goes first, added some final comments.                                            
---------------------------------------------------
**/

public class GameNode
{
    private int index;
    public int score;
    private boolean turn = false;
    public GameNode[] children = new GameNode[7];
    public static int h = 6;
    public static int w = 7;
    public char[][] gameState = new char[h][w];
    
    public GameNode(char[][] g, boolean turn)
    {
        //copy the array given by constructor
        for(int i = 0; i < h; i++)
        {
            for(int j = 0; j < w; j++)
            {
                this.gameState[i][j] = g[i][j];
            }
        }
        //turn determines which character (x or o) is being used
        //getscore assigns a point value to the current state of the board
        this.turn = turn;
        this.score = getScore(g);
        
        //set children to null
        for(int i = 0; i < w; i++)
        {
            children[i] = null;
        }
    }
    
    public void setData(char[][] a)
    {
        this.gameState = a;
    }
    
    public void setIndex(int i)
    {
        this.index = i;
    }
    
    public void setTurn(boolean turn)
    {
        this.turn = turn;
    }
    
    public int getIndex()
    {
        return this.index;
    }
    
    public char[][] getData()
    {
        return this.gameState;
    }
    
    public char getChar()
    {
        if(turn)
        {
            return 'O';
        }
        else
        {
            return 'X';
        }
    }
    
    //assigns a score to the gameboard based on the number of connected pieces or
    //possible future victories/losses.
    //+2 for every pair of two, +5 for every pair of three,
    //1000000 for victory, -1000000 for defeat.
    public int getScore(char[][] a)
    {
        int score = 0;
        char p = turn? 'O' : 'X';
        
        if(checkBoard(a, p))
        {
            //if current state of board results in victory, assign high value to score
            if(p == 'O')
            {
                score = 1000000;
            }
            //if current state results in defeat, assign low value to score
            else
            {
                score = -1000000;
            }
            //set node score equal to this score
            this.score = score;
            return score;
        }
        
        //COUNT AI'S TWO-IN-A-ROWS:
        
        //horizontal
        for(int i = 0; i < h; i++)
        {
            for(int j = 0; j < w; j++)
            {
                try //because this will try to index out of bounds
                {
                    if(a[i][j] == p && a[i][j+1] == p)
                    {
                        score = score + 2;
                        this.score = score;
                    }
                }
                catch(Exception e){}
            }
        }
        
        //vertical
        for(int i = 0; i < w; i++)
        {
            for(int j = 0; j < h; j++)
            {
                try
                {
                    if (a[i][j] == p && a[i+1][j] == p)
                    {
                        score = score + 2;
                        this.score = score;
                    }
                }
                catch(Exception e){}
            }
        }
        
        //diagonal down
        for(int i = 0; i < w; i++)
        {
            for(int j = 0; j < h; j++)
            {
                try
                {
                    if(a[i][j] == p && a[i-1][j-1] == p)
                    {
                        score = score + 2;
                        this.score = score;
                    }
                }
                catch(Exception e){}
            }
        }
        
        //diagonal up
        for(int i = 0; i < w; i++)
        {
            for(int j = 0; j < h; j++)
            {
                try
                {
                    if(a[i][j] == p && a[i-1][j+1] == p)
                    {
                        score = score + 2;
                        this.score = score;
                    }
                }
                catch(Exception e){}
            }
        }
        
        //COUNT AI'S THREE-IN-A-ROWS
        
        //horizontal
        for(int i = 0; i < h; i++)
        {
            for(int j = 0; j < w; j++)
            {
                try
                {
                    if(a[i][j] == p && a[i][j+1] == p && a[i][j+2] == p)
                    {
                        score = score + 5;
                        this.score = score;
                    }
                }
                catch(Exception e){}
            }
        }
        
        //vertical
        for(int i = 0; i < w; i++)
        {
            for(int j = 0; j < h; j++)
            {
                try
                {
                    if (a[i][j] == p && a[i+1][j] == p && a[i+2][j] == p)
                    {
                        score = score + 5;
                        this.score = score;
                    }
                }
                catch(Exception e){}
            }
        }
        
        //downward diagonal
        for(int i = 0; i < w; i++)
        {
            for(int j = 0; j < h; j++)
            {
                try
                {
                    if(a[i][j] == p && a[i-1][j-1] == p && a[i-2][j-2] == p)
                    {
                        score = score + 5;
                        this.score = score;
                    }
                }
                catch(Exception e){}
                
            }
        }
        
        //upward diagonal
        for(int i = 0; i < w; i++)
        {
            for(int j = 0; j < h; j++)
            {
                try
                {
                    if(a[i][j] == p && a[i-1][j+1] == p && a[i-2][j+2] == p)
                    {
                        score = score + 5;
                        this.score = score;
                    }    
                }
                catch(Exception e){}
            }
        }
        return this.score;
    }
    
    //simple constraints put on checking board: only checks parts of the board
    //where connecting 4 is possible.
    public boolean checkBoard(char[][] a, char p)
    {
        //horizontal check
        for(int i = 0; i < h - 3; i++)
        {
            for(int j = 0; j < w; j++)
            {
                try
                {
                    if(a[i][j] == p && a[i][j+1] == p && a[i][j+2] == p && a[i][j+3] == p)
                    {
                        return true;
                    }
                }
                catch(Exception e) {}
            }
        }
        
        //vertical check
        for(int i = 0; i < w - 3; i++)
        {
            for(int j = 0; j < h; j++)
            {
                try
                {
                    if (a[i][j] == p && a[i+1][j] == p && a[i+2][j] == p && a[i+3][j] == p)
                    {
                        return true;
                    } 
                }
                catch(Exception e){}
            }
        }
        
        //downward diagonal check
        for(int i = 3; i < w; i++)
        {
            for(int j = 3; j < h; j++)
            {
                try
                {
                    if(a[i][j] == p && a[i-1][j-1] == p && a[i-2][j-2] == p && a[i-3][j-3] == p)
                    {
                       return true;
                    }
                }
                catch(Exception e){}
                
            }
        }
        
        //upward diagonal check
        for(int i = 3; i < w; i++)
        {
            for(int j = 0; j < h - 3; j++)
            {
                try
                {
                    if(a[i][j] == p && a[i-1][j+1] == p && a[i-2][j+2] == p && a[i-3][j+3] == p)
                    {
                        return true;
                    }  
                }
                catch(Exception e){} 
            }
        }
        
        return false;
    }
}
