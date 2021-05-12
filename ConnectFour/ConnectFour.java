/** Program: DATA STRUCTURES FINAL PROJECT - CONNECT FOUR GAME
Written by: Jake Nelson
Program Description: This program uses a tree-based AI system to verse the player in a game of Connect Four. More info can be found
in the descriptions of the other files. Opponent isn't incredibly hard to beat but definitely smart enough to win. A more challenging AI would require
exponentially more operations as more nodes in the tree equals more gamestates to analyze. In its current state, the AI creates a tree just large enough
to predict a few moves ahead. This program went through many permutations and I undoubtedly spent too much time on it, but it was incredibly fun to write
and I'm proud that I've created an actual, working AI.
Challenges: Deciding upon which data structure to use, writing a minimax algorithm, creating a "scoring system" for the AI.
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

import java.util.Random;
import java.util.Scanner;

public class ConnectFour
{
    //height and width of game board
    public static int h = 6;
    public static int w = 7;
    
    //player tokens
    public static final char PLAYER = 'X';
    public static final char OPPONENT = 'O';
    public static final char EMPTYSPACE = '-';
    
    //if it is the first turn (helps AI make first move)
    public static boolean firstTurn = true;
    
    public static void main(String[] args)
    {
        //board represented by character array
        char[][] a = new char[h][w];
        
        //display the introductory message
        intro();
        
        //initialize the array to have all empty space character '-'
        fillArray(a);
        
        //decide who will go first
        Random random = new Random();
        int n = random.nextInt(2);
        
        if(n == 0)
        {
            printArray(a);
            playerTurn(a);
        }
        else
        {
            cpuTurn(a);
        }
    }
    
    //message displayed only at the start of the program
    public static void intro()
    {
        System.out.println("CONNECT FOUR!");
        System.out.println("See if you can beat a simple tree-based AI in a match!\n");
        System.out.println("Player token: X");
        System.out.println("AI token: O\n");
    }
    
    
    //to initialize the array/gameboard with empty space character
    public static void fillArray(char[][] a)
    {
        for(int i = 0; i < h; i++)
        {
            for(int j = 0; j < w; j++)
            {
                a[i][j] = EMPTYSPACE;
            }
        }
    }
    
    //display the game board
    public static void printArray(char[][] a)
    {
        for(int i = 0; i < h; i++)
        {
            for(int j = 0; j < w; j++)
            {
                System.out.print(a[i][j] + " ");
            }
            System.out.println();
        }
    }
    
    //the user's turn to place a piece
    public static void playerTurn(char[][] a)
    {
        //is no longer first turn
        firstTurn = false;
        
        //prompt and input
        System.out.println("Enter column value (0 through 6) and press enter.");
        Scanner inp = new Scanner(System.in);
        int n = inp.nextInt();
        
        //place a piece at column n on board a using char PLAYER
        placePiece(n, a, PLAYER);
        
        //display board a
        printArray(a);
        
        //if it is true that the player has now won
        if(checkBoard(a, PLAYER))
        {
            System.out.println("You have won!");
        }
        //player hasnt won yet, so throw it to cpuTurn
        else
        {
            cpuTurn(a);
        }
    }
    
    public static void cpuTurn(char[][] a)
    {
        //if it is the first turn, the AI's first move will be randomized.
        //this is necessary because if handed an empty game board, the minimax
        //algorithm will return 0, and place it in column zero. This prevents that
        //resulting in a more interesting game.
        if(firstTurn)
        {
            firstTurn = false;
            Random random = new Random();
            int n = random.nextInt(7);
            placePiece(n, a, OPPONENT);
            System.out.println("The AI chose " + n + ".");
        }
        //if it is not the first turn, create a gametree consisting of nodes that
        //represent possible gamestates follwing current gamestate.
        //AI uses minimax to decide where best to place a piece.
        else
        {
            GameTree g = new GameTree(a, 3);
            int choice = g.miniMax(g.root, h, true);
            placePiece(choice, a, OPPONENT);
            System.out.println("The AI chose " + choice + ".");
        }
        //print after opponent decides and places a piece
        printArray(a);
        
        //if it is true that AI has won
        if(checkBoard(a, OPPONENT))
        {
            System.out.println("AI has won!");
        }
        //if still no victor, throw it back to playerTurn
        else
        {
            playerTurn(a);
        }
    }
    
    public static void placePiece(int column, char[][] a, char p)
    {
        //if one tries to place a piece in a non existent column
        if(column > 6 || column < 0)
        {
            throw new IllegalArgumentException("Out of bounds: " + column);
        }
        
        //find the top item to see where a piece will "land"
        int top = 0;
        boolean filled = true;
        
        //find the first empty space in the column, call that spot the top
        for(int i = h - 1; i >= 0; i--)
        {
            if(a[i][column] == EMPTYSPACE)
            {
                top = i;
                filled = false;
                break;
            }
        }
        
        //filled will remain true if it couldn't find an empty space in column (it is full)
        if(filled)
        {
            System.out.println("the " + column + " column is full");
        }
        //otherwise, put the piece p at the top of chosen column
        else
        {
           a[top][column] = p; 
        }
    }
    
    //returns true if in a, there is four in a row of p.
    //checks 4 adjacent values to see if they all equal p.
    //try blocks are there because it will involve checking out of bounds at some board postitions
    public static boolean checkBoard(char[][] a, char p)
    {
        //horizontal check
        for(int i = 0; i < h; i++)
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
                catch(Exception e){}
            }
        }
        
        //vertical check
        for(int i = 0; i < w; i++)
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
        for(int i = 0; i < w; i++)
        {
            for(int j = 0; j < h; j++)
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
        for(int i = 0; i < w; i++)
        {
            for(int j = 0; j < h; j++)
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
