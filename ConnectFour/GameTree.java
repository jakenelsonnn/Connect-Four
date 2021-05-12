/** Program: DATA STRUCTURES FINAL PROJECT - CONNECT FOUR GAME
Written by: Jake Nelson
Program Description: This is the tree which contains nodes relating to possible future game states.
Challenges: Figuring out how to build the three, figuring out how to build the nodes in the tree so that they represent game states
based on the previous game states, deciding tree depth (as it has an exponential relationship with the number of operations it must do).
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

public class GameTree
{
    public GameNode root;
    private int depth = 0;
    private static final int h = 6;
    private static final int w = 7;
    
    //infinity needed for minimax algorithm
    private final int inf = (int) Double.POSITIVE_INFINITY;
    
    //build tree based on given gamestate given upon instantiation
    public GameTree(char[][] a, int d)
    {
        this.root = new GameNode(a, true);
        this.depth = d;
        buildTree(a);
    }
    
    //builds a tree of max depth 3. 1st level involves placing AI tokens (because it is  AI's turn)
    //second level involves placing player tokens (because it is player's turn)
    //third level is AI's turn again
    public void buildTree(char[][] a)
    {
        for(int i = 0; i < 7; i++)
        {
            //game nodes are given board states based on all column selections (0 through 6) posisble for that turn
            root.children[i] = new GameNode(newPiece(i, a, 'O'), true);
            root.children[i].setIndex(i);
            if(depth > 1)
            {
                for(int j = 0; j < w; j++)
                {
                    root.children[i].children[j] = new GameNode(newPiece(j, a, 'X'), false);
                    root.children[i].children[j].setIndex(j);
                    if(depth > 2)
                    {
                        for(int k = 0; k < w; k++)
                        {
                            root.children[i].children[j].children[k] = new GameNode(newPiece(k, a, 'O'), true);
                            root.children[i].children[j].children[k].setIndex(k);
                        }
                    }
                }
            }
        }
    }
    
    //returns a board state after a single piece has been placed.
    //works similary to placePiece in ConnectFour.java except
    //it returns a new char[][] instead of updating the given one
    public static char[][] newPiece(int column, char[][] a, char p)
    {
        if(column > 6 || column < 0)
        {
            throw new IllegalArgumentException("Out of bounds");
        }
        
        int top = 0;
        boolean filled = true;
        
        //copy the array
        char[][] b = new char[h][w];
        
        for(int i = 0; i < h; i++)
        {
            for(int j = 0; j < w; j++)
            {
                b[i][j] = a[i][j];
            }
        }
        
        for(int i = h - 1; i >= 0; i--)
        {
            if(b[i][column] == '-')
            {
                top = i;
                filled = false;
                break;
            }
        }
        if(!filled)
        {
           b[top][column] = p; 
        }
        return b;
    }
    
    //since the game alternates between the turns of two players, recursion needs
    //to alternate between whether it wants to maximize the score or minimize it.
    //when maximizing, it will choose the gamestate that will give it the BEST score.
    //when minimizing, it will choose the gamestate that will give it the WORST score.
    //The AI looks at minimizing because that occurs during the player's turn; in other words,
    //the algorithm assumes that the non-AI player will place pieces that will result in the
    //lowest score for the AI. This assumption causes the AI to choose a node which represents
    //the best possible score based on the most likely future game state.
    public int miniMax(GameNode g, int depth, boolean maximizing)
    {
        //ends the recursion
        if(depth == 0)
        {
            return g.getIndex();
        }
        
        if(maximizing)
        {
            //to compare the score with negative infinity
            int maxEval = (-1 * inf);
            for(int i = 0; i < g.children.length; i++)
            {
                try
                {
                    int eval = miniMax(g.children[i], depth - 1, false);
                    //set maxEval to the larger between (-) infinity, and the highest-scoring child
                    maxEval = max(maxEval, maxInArray(g.children));
                }
                catch(Exception e){}
            }
            //if maxEval is the best-scoring child node, return the index stored in the node
            if(maxEval == maxInArray(g.children))
            {
                maxEval = maxIndex(g.children);
            }
            return maxEval;
        }
        else //"minimizing"
        {
            //to compare the score with positive infinity
            int minEval = inf;
            for(int i = 0; i < g.children.length; i++)
            {
                try
                {
                    int eval = miniMax(g.children[i], depth - 1, false);
                    //set mineval to the smaller between infinity and the lowestest-scoring child
                    minEval = min(minEval, minInArray(g.children));
                }
                catch(Exception e) {}
            }
            //if mineval is the lowest-scoring child node, return the index stored in the node.
            if(minEval == minInArray(g.children))
            {
                minEval = minIndex(g.children);
            }
            return minEval;
        }
    }
    
    //HELPER FUNCTIONS FOR MINIMAX
    
    //returns larger int
    public int max(int i, int j)
    {
        int max = i > j ? i : j;
        return max;
    }
    
    //returns lower int
    public int min(int i, int j)
    {
        int min = i < j ? i : j;
        return min;
    }
    
    //returns the child node with the best score
    public int maxInArray(GameNode[] g)
    {
        int max = g[0].score;
        for(int i = 0; i < g.length; i++)
        {
            if(g[i].score > max)
            {
                max = g[i].score;
            }
        }
        return max;
    }
    
    //returns the child node with the lowest score
    public int minInArray(GameNode[] g)
    {
        int min = g[0].score;
        for(int i = 0; i < g.length; i++)
        {
            if(g[i].score < min)
            {
                min = g[i].score;
            }
        }
        return min;
    }
    
    //get the index of the child node with the highest score
    public int maxIndex(GameNode[] g)
    {
        int index = g[0].getIndex();
        int max = g[0].score;
        for(int i = 0; i < g.length; i++)
        {
            //System.out.println("Score " + i + ": " + g[i].score);
            if(g[i].score > max)
            {
                max = g[i].score;
                index = i;
            }
        }
        return index;
    }
    
    //return the index of the child node with the lowest score
    public int minIndex(GameNode[] g)
    {
        int index = g[0].getIndex();
        int min = g[0].score;
        for(int i = 0; i < g.length; i++)
        {
            //System.out.println("Score " + i + ": " + g[i].score);
            if(g[i].score < min)
            {
                min = g[i].score;
                index = i;
            }
        }
        return index;
    }
}



