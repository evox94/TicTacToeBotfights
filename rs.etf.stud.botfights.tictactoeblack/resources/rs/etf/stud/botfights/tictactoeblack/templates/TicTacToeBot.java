/*
* This is a template bot file intended for use
* with the Tic-Tac-Toe game for Botfights game simulator.
*
* In order to use this file with the simulator,
* you have to have a single public static method defined
* in the public class, that is capable of accepting an int[][] param;
*
* If you have multiple PUBLIC STATIC methods
* that can accept an int[][] param,
* it's NOT defined which one will be called, so be careful!
*
* You can change everything else about the file including:
* - public class name (Be sure to rename the file)
* - return type of the public static method(doesn't matter)
* - param name
* - omit or change package name;
* - add new methods , fields..
* - define new classes(They have to be defined in this file)
* - You can delete this description
*
*  More info about the game is available in the simulator itself.
* */
package com.example.mypackage;

public class TicTacToeBot {

    final public static int FREE_SPACE = 0;
    final public static int MY_SYMBOL = 1; //You are always 1
    final public static int OPPONENT = 2;

    public static void move(int[][] board){
        //your code goes here:
        //example code:
        //play on the first 'free' board field
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if(board[i][j] == FREE_SPACE){
                    //You output your move by using System.out.println() or System.out.print()
                    //If you have a need for debuging use System.err
                    System.out.println(i+" "+j);//this is one of the formats game accepts
                    return;                     //be sure to return unless you want to output more moves;
                }
            }
        }
    }
}
