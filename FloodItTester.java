import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import javalib.impworld.WorldScene;
import javalib.worldimages.FontStyle;
import javalib.worldimages.Posn;
import javalib.worldimages.RectangleImage;
import javalib.worldimages.TextImage;
import javalib.worldimages.WorldEnd;
import javalib.worldimages.WorldImage;
import tester.Tester;

// Represents a class where the game can be played easily
class PlayFloodIt {

  // The tick rate of the game in sec / tick
  static final double TICK_RATE = 0.02;

  // To have the tester library run the game
  void testGame(Tester t) {

    int boardSize = 20;
    int numColors = 6;

    this.runGame(boardSize, numColors);
  }

  // To have bigBang instantiate a game with the given size
  void runGame(int boardSize, int numColors) {

    FloodItWorld flood = new FloodItWorld(boardSize, numColors);
    int boardWidth = flood.getBoardWidth();
    int boardHeight = flood.getBoardHeight();
    flood.bigBang(boardWidth, boardHeight, TICK_RATE);
  }
}

// TURN YOUR SOUND OFF THE TESTS STILL CALL THE AUDIO METHOD
// To represent examples of the FloodIt Game
class ExamplesFloodIt {

  int seed = 1337;
  Random randSeed = new Random(seed);

  Cell c00;
  Cell c10;
  Cell c01;
  Cell c11;

  // cm stands for Cell Made, i.e. these cells will eventually have references
  // to adjacent cells in the tests below
  Cell cm1;
  Cell cm2; 
  Cell cm3; 
  Cell cm4;
  Cell cm5; 
  Cell cm6; 
  Cell cm7; 
  Cell cm8;
  Cell cm9; 

  // c is just a cell that has null references everywhere to use for testing
  Cell c1; 
  Cell c2;
  Cell c3;
  Cell c4; 
  Cell c5;
  Cell c6;
  Cell c7;
  Cell c8;
  Cell c9;

  ArrayList<Cell> arrBoard1;

  ArrayList<Cell> nullBoard;

  ArrayList<Cell> by2Board;

  FloodItWorld nullFlood;

  FloodItWorld flood1;

  FloodItWorld by2Flood;

  FloodItWorld randFlood;
  FloodItWorld sizeColorFlood; 

  // initialize all the variables to represent a initial state of a game
  void initStartGame() {

    c00 = new Cell(0,0, Color.GREEN, true);
    c10 = new Cell(1,0, Color.BLACK, false);
    c01 = new Cell(0,1, Color.GREEN, false);
    c11 = new Cell(1,1, Color.BLACK, false);
    
    // cm1 cm2 cm3      
    // cm4 cm5 cm6
    // cm7 cm8 cm9
    
    cm1 = new Cell(0, 0, Color.CYAN, true);
    cm2 = new Cell(1, 0, Color.BLACK, false);
    cm3 = new Cell(2, 0, Color.BLUE, false);
    cm4 = new Cell(0, 1, Color.GREEN, false);
    cm5 = new Cell(1, 1, Color.BLUE, false);
    cm6 = new Cell(2, 1, Color.GREEN, false);
    cm7 = new Cell(0, 2, Color.BLACK, false);
    cm8 = new Cell(1, 2, Color.CYAN, false);
    cm9 = new Cell(2, 2, Color.GREEN, false);

    c1 = new Cell(0, 0, Color.CYAN, true);
    c2 = new Cell(1,0, Color.BLACK, false);
    c3 = new Cell(2,0, Color.BLUE, false);
    c4 = new Cell(0,1, Color.GREEN, false);
    c5 = new Cell(1,1, Color.BLUE, false);
    c6 = new Cell(2,1, Color.GREEN, false);
    c7 = new Cell(0,2, Color.BLACK, false);
    c8 = new Cell(1,2, Color.CYAN, false);
    c9 = new Cell(2,2, Color.GREEN, false);

    arrBoard1 = new ArrayList<Cell>(Arrays.asList(cm1, cm2, cm3, cm4, cm5, cm6, cm7, cm8, cm9));
    flood1 = new FloodItWorld(arrBoard1);
    flood1.linkCells();
    
    by2Board = new ArrayList<Cell>(Arrays.asList(c00, c10, c01, c11));
    by2Flood = new FloodItWorld(by2Board);
    by2Flood.linkCells();

    nullBoard = new ArrayList<Cell>(Arrays.asList(c1, c2, c3, c4, c5, c6, c7, c8, c9));
    nullFlood = new FloodItWorld(nullBoard);
    
    randFlood = new FloodItWorld();
    sizeColorFlood = new FloodItWorld(100, 6); 
  }

  // to test various properties of the board to see if the constructors worked properly
  void testBoard(Tester t) {

    this.initStartGame();
    t.checkExpect(cm1.right, cm2);
    t.checkExpect(cm1.left, null);
    t.checkExpect(cm1.top, null);
    t.checkExpect(cm1.bottom, cm4);
    t.checkExpect(cm4.top, cm1);
    t.checkExpect(cm4.right.right, cm6);
    t.checkExpect(cm9.left.top, cm5);

    FloodItWorld floodRand = new FloodItWorld(new Random(), 14, 6);
    t.checkExpect(floodRand.toBeFlooded.isEmpty(), false);   
    t.checkExpect(floodRand.floodColor, floodRand.board.get(0).color);

    ArrayList<Cell> oneCell = new ArrayList<Cell>(Arrays.asList(c1));
    FloodItWorld oneByOne = new FloodItWorld(oneCell);
    t.checkExpect(oneByOne.floodColor, c1.color);
  }

  /*
   *  To test methods in the Cell class
   */

  // To test the renderCell() method
  void testrenderCell(Tester t) {

    this.initStartGame();
    t.checkExpect(cm1.renderCell(), 
        new RectangleImage(Cell.CELL_SIZE, Cell.CELL_SIZE, "solid", cm1.color));
    t.checkExpect(cm2.renderCell(), 
        new RectangleImage(Cell.CELL_SIZE, Cell.CELL_SIZE, "solid", cm2.color));
    
    this.initStartGame();
    t.checkExpect(cm2.renderCell(), 
        new RectangleImage(Cell.CELL_SIZE, Cell.CELL_SIZE, "solid", cm2.color));
    t.checkExpect(cm3.renderCell(), 
        new RectangleImage(Cell.CELL_SIZE, Cell.CELL_SIZE, "solid", cm3.color));
  }

  // To test the drawOn(WorldScene) method PART1
  void testDrawnOn(Tester t) {

    this.initStartGame();
    WorldScene ws = new WorldScene(100, 100);
    WorldScene ws2 = new WorldScene(100, 100);
    cm1.drawOn(ws);
    ws2.placeImageXY(cm1.renderCell(), cm1.transformX(), cm1.transformY());
    t.checkExpect(ws, ws2);
    WorldScene ws3 = new WorldScene(100, 100);
    WorldScene ws4 = new WorldScene(100, 100);
    c2.drawOn(ws3);
    c3.drawOn(ws3);
    ws4.placeImageXY(c2.renderCell(), c2.transformX(), c2.transformY());
    ws4.placeImageXY(c3.renderCell(), c3.transformX(), c3.transformY());
    t.checkExpect(ws3, ws4);
  }

  // To test the transformX() method PART1
  void testTransformX(Tester t) {

    this.initStartGame();
    t.checkExpect(c1.transformX(), 
        Cell.CELL_SIZE / 2 + 0 * Cell.CELL_SIZE + FloodItWorld.BORDER_SIZE);
    t.checkExpect(c2.transformX(), 
        Cell.CELL_SIZE / 2 + 1 * Cell.CELL_SIZE + FloodItWorld.BORDER_SIZE);
    t.checkExpect(c6.transformX(), 
        Cell.CELL_SIZE / 2 + 2 * Cell.CELL_SIZE + FloodItWorld.BORDER_SIZE);
  }

  // To test the transformY() method PART1
  void testTransformY(Tester t) {

    this.initStartGame();
    t.checkExpect(c1.transformY(), 
        Cell.CELL_SIZE / 2 + 0 * Cell.CELL_SIZE + FloodItWorld.BORDER_SIZE);
    t.checkExpect(c4.transformY(), 
        Cell.CELL_SIZE / 2 + 1 * Cell.CELL_SIZE + FloodItWorld.BORDER_SIZE);
    t.checkExpect(c9.transformY(), 
        Cell.CELL_SIZE / 2 + 2 * Cell.CELL_SIZE + FloodItWorld.BORDER_SIZE);
  }

  // To test the updateAdjacent(Cell) method PART1
  void testUpdateAdjacent(Tester t) {

    this.initStartGame();
    t.checkExpect(c1.right, null);
    t.checkExpect(c2.left, null);
    t.checkExpect(c1.top, null);
    t.checkExpect(c2.bottom, null);
    c1.updateAdjacent(c2);
    t.checkExpect(c1.right, c2);
    t.checkExpect(c2.left, c1);
    t.checkExpect(c1.top, null);
    t.checkExpect(c2.bottom, null);

    this.initStartGame();
    t.checkExpect(c1.right, null);
    t.checkExpect(c3.left, null);
    t.checkException(new IllegalArgumentException("Cannot update non-adjascent cells: "
        + "(0,0) (2,0)"),
        c1, "updateAdjacent", c3);

    this.initStartGame();
    t.checkExpect(c5.left, null);
    t.checkExpect(c5.top, null);
    t.checkExpect(c5.right, null);
    t.checkExpect(c5.bottom, null);

    c5.updateAdjacent(c4);
    c5.updateAdjacent(c2);
    c5.updateAdjacent(c6);
    c5.updateAdjacent(c8);

    t.checkExpect(c5.left, c4);
    t.checkExpect(c5.top, c2);
    t.checkExpect(c5.right, c6);
    t.checkExpect(c5.bottom, c8);

    this.initStartGame();
    t.checkException(new IllegalArgumentException("Cannot update non-adjascent cells: "
        + "(0,0) (1,1)"),
        c1, "updateAdjacent", c5);
  }

  // To test the isAtCord(Posn) method
  void testIsAtCord(Tester t) {

    // c1's center is at the pixel value (25, 25), cell width is 30
    // We decided to include points on the left side of a border, but not on the right
    
    this.initStartGame();
    t.checkExpect(c1.isAtCord(new Posn(25, 25)), true);
    t.checkExpect(c1.isAtCord(new Posn(45, 25)), false);
    t.checkExpect(c1.isAtCord(new Posn(10, 25)), true);
    t.checkExpect(c1.isAtCord(new Posn(0, 0)), false);
  }

  // To test the makeFlooded() method
  void testMakeFlooded(Tester t) {

    this.initStartGame();
    t.checkExpect(cm1.flooded, true);
    cm1.makeFlooded();
    t.checkExpect(cm1.flooded, true);
    cm2.makeFlooded();
    t.checkExpect(cm2.flooded, true);
    this.initStartGame();
    
  }

  // To test the updateColor(Color) method
  void testUpdateColor(Tester t) {

    this.initStartGame();
    t.checkExpect(c1.color, Color.CYAN);
    c1.updateColor(Color.RED);
    t.checkExpect(c1.color, Color.RED);
  }
  
  // To test the sameColor(Color) method
  void testSameColor(Tester t) {
    
    this.initStartGame();
    t.checkExpect(c1.sameColor(Color.CYAN), true);
    t.checkExpect(c1.sameColor(Color.BLUE), false);
  }

  /*
   *  To test methods in the FloodItWorld class
   */
 
  // to test the reset(Random, int, int) method 
  void testReset(Tester t) {

    this.initStartGame();

    t.checkExpect(flood1.board.size(), 9);
    t.checkExpect(flood1.time, 0);
    t.checkExpect(flood1.floodColor, flood1.board.get(0).color);
    t.checkExpect(flood1.colors, FloodItWorld.MAX_COLORS);
    t.checkExpect(flood1.boardSize, 3);

    flood1.reset(new Random(), flood1.boardSize, flood1.colors.size());

    t.checkExpect(flood1.board.size(), 9);
    t.checkExpect(flood1.time, 0);
    t.checkExpect(flood1.floodColor, flood1.board.get(0).color);
    t.checkExpect(flood1.colors, FloodItWorld.MAX_COLORS);
    t.checkExpect(flood1.boardSize, 3);

    flood1.reset(new Random(), 4, 5);

    t.checkExpect(flood1.board.size(), 16);
    t.checkExpect(flood1.time, 0);
    t.checkExpect(flood1.floodColor, flood1.board.get(0).color);
    t.checkExpect(flood1.colors.size(), 5);
    t.checkExpect(flood1.boardSize, 4);

    // default constructor call reset(), default size is 14, numColors is 6
    FloodItWorld flood3 = new FloodItWorld();

    t.checkExpect(flood3.board.size(), 196);
    t.checkExpect(flood3.time, 0);
    t.checkExpect(flood3.floodColor, flood3.board.get(0).color);
    t.checkExpect(flood3.colors.size(), 8);
    t.checkExpect(flood3.boardSize, 14);

    t.checkExpect(flood3.board.get(0).right, flood3.board.get(1));
    t.checkExpect(flood3.board.get(1).left, flood3.board.get(0));
  }
  
  // to test the linkCells()
  void testlinkCells(Tester t) {

    this.initStartGame();

    this.nullFlood.linkCells();

    t.checkExpect(this.nullFlood.board.get(0).left, null);
    t.checkExpect(this.nullFlood.board.get(0).top, null);
    t.checkExpect(this.nullFlood.board.get(0).right, c2);
    t.checkExpect(this.nullFlood.board.get(0).bottom, c4);

    t.checkExpect(this.nullFlood.board.get(1).left, c1);
    t.checkExpect(this.nullFlood.board.get(1).top, null);
    t.checkExpect(this.nullFlood.board.get(1).right, c3);
    t.checkExpect(this.nullFlood.board.get(1).bottom, c5);

    t.checkExpect(this.nullFlood.board.get(2).left, c2);
    t.checkExpect(this.nullFlood.board.get(2).top, null);
    t.checkExpect(this.nullFlood.board.get(2).right, null);
    t.checkExpect(this.nullFlood.board.get(2).bottom, c6);

    t.checkExpect(this.nullFlood.board.get(3).left, null);
    t.checkExpect(this.nullFlood.board.get(3).top, c1);
    t.checkExpect(this.nullFlood.board.get(3).right, c5);
    t.checkExpect(this.nullFlood.board.get(3).bottom, c7);

    t.checkExpect(this.nullFlood.board.get(4).left, c4);
    t.checkExpect(this.nullFlood.board.get(4).top, c2);
    t.checkExpect(this.nullFlood.board.get(4).right, c6);
    t.checkExpect(this.nullFlood.board.get(4).bottom, c8);

    t.checkExpect(this.nullFlood.board.get(5).left, c5);
    t.checkExpect(this.nullFlood.board.get(5).top, c3);
    t.checkExpect(this.nullFlood.board.get(5).right, null);
    t.checkExpect(this.nullFlood.board.get(5).bottom, c9);

    t.checkExpect(this.nullFlood.board.get(6).left, null);
    t.checkExpect(this.nullFlood.board.get(6).top, c4);
    t.checkExpect(this.nullFlood.board.get(6).right, c8);
    t.checkExpect(this.nullFlood.board.get(6).bottom, null);

    t.checkExpect(this.nullFlood.board.get(7).left, c7);
    t.checkExpect(this.nullFlood.board.get(7).top, c5);
    t.checkExpect(this.nullFlood.board.get(7).right, c9);
    t.checkExpect(this.nullFlood.board.get(7).bottom, null);

    t.checkExpect(this.nullFlood.board.get(8).left, c8);
    t.checkExpect(this.nullFlood.board.get(8).top, c6);
    t.checkExpect(this.nullFlood.board.get(8).right, null);
    t.checkExpect(this.nullFlood.board.get(8).bottom, null);
  }


  // to test the getAdjacentIndexes(int) method 
  void testGetAdjacentIndexes(Tester t) {

    this.initStartGame();
    // Gets the index in right, left, top, bottom order
    t.checkExpect(nullFlood.getAdjacentIndexes(0), Arrays.asList(1, 3));
    t.checkExpect(nullFlood.getAdjacentIndexes(1), Arrays.asList(2, 0, 4));
    t.checkExpect(nullFlood.getAdjacentIndexes(2), Arrays.asList(1, 5));
    t.checkExpect(nullFlood.getAdjacentIndexes(3), Arrays.asList(4, 0, 6));
    t.checkExpect(nullFlood.getAdjacentIndexes(4), Arrays.asList(5, 3, 1, 7));
    t.checkExpect(nullFlood.getAdjacentIndexes(5), Arrays.asList(4, 2, 8));
    t.checkExpect(nullFlood.getAdjacentIndexes(6), Arrays.asList(7, 3));
    t.checkExpect(nullFlood.getAdjacentIndexes(7), Arrays.asList(8, 6, 4));
    t.checkExpect(nullFlood.getAdjacentIndexes(8), Arrays.asList(7, 5));
    
    t.checkExpect(by2Flood.getAdjacentIndexes(0), Arrays.asList(1, 2));
    t.checkExpect(by2Flood.getAdjacentIndexes(1), Arrays.asList(0, 3));
    t.checkExpect(by2Flood.getAdjacentIndexes(2), Arrays.asList(3, 0));
    t.checkExpect(by2Flood.getAdjacentIndexes(3), Arrays.asList(2, 1));
    
    t.checkException(new IndexOutOfBoundsException("Cannot get adjascent indexes of "
        + "out of bounds index"), 
        by2Flood, 
        "getAdjacentIndexes", 
        4);
  }
  
  // to test the getCellAt(Posn) method
  void testGetCellAt(Tester t) {

    this.initStartGame();
    int boardWidth = flood1.getBoardWidth();

    t.checkExpect(flood1.getCellAt(new Posn(0, 0)), null);
    t.checkExpect(flood1.getCellAt(new Posn(50, 0)), null);
    t.checkExpect(flood1.getCellAt(new Posn(boardWidth, boardWidth)), null);

    t.checkExpect(flood1.getCellAt(new Posn(9, 25)), null);
    t.checkExpect(flood1.getCellAt(new Posn(10, 25)), cm1);
    t.checkExpect(flood1.getCellAt(new Posn(25, 25)), cm1);
    t.checkExpect(flood1.getCellAt(new Posn(40, 25)), cm2);
    t.checkExpect(flood1.getCellAt(new Posn(99, 25)), cm3); 
    t.checkExpect(flood1.getCellAt(new Posn(100, 25)), null); 

    t.checkExpect(flood1.getCellAt(new Posn(25, 9)), null);
    t.checkExpect(flood1.getCellAt(new Posn(25, 10)), cm1);
    t.checkExpect(flood1.getCellAt(new Posn(25, 25)), cm1);
    t.checkExpect(flood1.getCellAt(new Posn(25, 40)), cm4);
    t.checkExpect(flood1.getCellAt(new Posn(25, 99)), cm7); 
    t.checkExpect(flood1.getCellAt(new Posn(25, 100)), null);
  }
  
  // to test the getToBeFlooded(int) method
  void testGetToBeFlooded(Tester t) {

    this.initStartGame();
    
    // Note we use this.onMouseClicked(Posn) and this.tick
    // to mutate things; Refers to the tests for those methods
    
    // Initial Board         
    // Cy Bk Bl
    // G  Bl G
    // Bk Cy G
    
    
    flood1.onMouseClicked(new Posn(55, 25)); // Click on Black
    flood1.onTick();  // Update (0, 0)
    t.checkExpect(flood1.getToBeFlooded(0), Arrays.asList(1));
    flood1.onTick();  // Update (1, 0)
    t.checkExpect(flood1.getToBeFlooded(0), new ArrayList<Integer>());
    t.checkExpect(flood1.getToBeFlooded(1), new ArrayList<Integer>());
    
    flood1.onMouseClicked(new Posn(85, 25)); // Click on Blue
    flood1.onTick(); // Update (0, 0)
    t.checkExpect(flood1.getToBeFlooded(0), Arrays.asList(1));
    flood1.onTick(); // Update (1, 0)
    t.checkExpect(flood1.getToBeFlooded(1), Arrays.asList(2, 4));
    flood1.onTick(); // Update (2, 0) (1, 1)
    t.checkExpect(flood1.getToBeFlooded(2), new ArrayList<Integer>());
    t.checkExpect(flood1.getToBeFlooded(4), new ArrayList<Integer>());
        
    flood1.onMouseClicked(new Posn(85, 55)); // Click on Green
    flood1.onTick(); // Update (0, 0)
    t.checkExpect(flood1.getToBeFlooded(0), Arrays.asList(1, 3));
    flood1.onTick(); // Update (1, 0)
    t.checkExpect(flood1.getToBeFlooded(1), Arrays.asList(2, 4));
    flood1.onTick(); // Update (2, 0) (1, 1)
    t.checkExpect(flood1.getToBeFlooded(2), Arrays.asList(5));
    t.checkExpect(flood1.getToBeFlooded(4), Arrays.asList(5));
    flood1.onTick(); // Update (0, 1) and (2, 1)
    t.checkExpect(flood1.getToBeFlooded(4), new ArrayList<Integer>());
    t.checkExpect(flood1.getToBeFlooded(5), Arrays.asList(8));
    flood1.onTick(); // Update (2, 2)
    t.checkExpect(flood1.getToBeFlooded(8), new ArrayList<Integer>());
      
    flood1.onMouseClicked(new Posn(55, 85)); // Click on Cyan
    flood1.onTick();
    t.checkExpect(flood1.getToBeFlooded(0), Arrays.asList(1, 3));
    flood1.onTick();
    t.checkExpect(flood1.getToBeFlooded(1), Arrays.asList(2, 4));
    t.checkExpect(flood1.getToBeFlooded(3), Arrays.asList(4));
    flood1.onTick();
    t.checkExpect(flood1.getToBeFlooded(2), Arrays.asList(5));
    t.checkExpect(flood1.getToBeFlooded(4), Arrays.asList(5, 7));
    flood1.onTick();
    t.checkExpect(flood1.getToBeFlooded(5), Arrays.asList(8));
    t.checkExpect(flood1.getToBeFlooded(7), Arrays.asList(8));
    flood1.onTick();
    
    flood1.onMouseClicked(new Posn(25, 85)); // Click on Black
    flood1.onTick();
    t.checkExpect(flood1.getToBeFlooded(0), Arrays.asList(1, 3));
    flood1.onTick();
    t.checkExpect(flood1.getToBeFlooded(1), Arrays.asList(2, 4));
    t.checkExpect(flood1.getToBeFlooded(3), Arrays.asList(4, 6));
    flood1.onTick();
    t.checkExpect(flood1.getToBeFlooded(2), Arrays.asList(5));
    t.checkExpect(flood1.getToBeFlooded(4), Arrays.asList(5, 7));
    t.checkExpect(flood1.getToBeFlooded(6), Arrays.asList(7));
    flood1.onTick();
    t.checkExpect(flood1.getToBeFlooded(5), Arrays.asList(8));
    t.checkExpect(flood1.getToBeFlooded(7), Arrays.asList(8));
    flood1.onTick();
    t.checkExpect(flood1.getToBeFlooded(8), new ArrayList<Integer>());
    
    // Game is finished
    t.checkExpect(flood1.allSameColor(), true);
  }

  // to test the getBoardWidth() method
  void testGetBoardWidth(Tester t) {

    this.initStartGame();

    t.checkExpect(flood1.getBoardWidth(), 110);
    t.checkExpect(nullFlood.getBoardWidth(), 110);
    t.checkExpect(by2Flood.getBoardWidth(), 80);
    t.checkExpect(randFlood.getBoardWidth(), 440);
    t.checkExpect(sizeColorFlood.getBoardWidth(), 3020);
  }

  // to test the getBoardHeight() method
  void testGetBoardHeight(Tester t) {

    this.initStartGame();

    t.checkExpect(randFlood.getBoardHeight(), 513);
    t.checkExpect(sizeColorFlood.getBoardHeight(), 3523);
    t.checkExpect(flood1.getBoardHeight(), 128);
    t.checkExpect(nullFlood.getBoardHeight(), 128);
    t.checkExpect(by2Flood.getBoardHeight(), 93);

  }
  
  // to test the getBeginIndex() method
  void testGetBeginFloodIndex(Tester t) {
    
    this.initStartGame();
    t.checkExpect(flood1.getBeginFloodIndex(), 0);
    t.checkExpect(nullFlood.getBeginFloodIndex(), 0);
  }

  // to test the getBottomHeight() method
  void testGetBottomHeight(Tester t) {

    this.initStartGame();
    t.checkExpect(randFlood.getBottomHeight(), 73);
    t.checkExpect(sizeColorFlood.getBottomHeight(), 503);
    t.checkExpect(flood1.getBottomHeight(), 18);
    t.checkExpect(nullFlood.getBottomHeight(), 18);
    t.checkExpect(by2Flood.getBottomHeight(), 13);
  }

  // to test the getMaxSteps() method
  void testGetMaxSteps(Tester t) {

    this.initStartGame();
    t.checkExpect(randFlood.getMaxSteps(), 33);
    t.checkExpect(sizeColorFlood.getMaxSteps(), 180);
    t.checkExpect(flood1.getMaxSteps(), 7);
    t.checkExpect(nullFlood.getMaxSteps(), 7);
    t.checkExpect(by2Flood.getMaxSteps(), 4);
  }

  // to test the getWS() method
  void testGetWorldScene(Tester t) {

    this.initStartGame();
    WorldScene ws1 = new WorldScene(randFlood.getBoardWidth(), randFlood.getBoardHeight());
    WorldImage bg1 = new RectangleImage(randFlood.getBoardWidth(), 
        randFlood.getBoardHeight(), "solid", Color.BLACK);
    WorldImage btBottom1 = 
        new RectangleImage(randFlood.getBoardWidth(), randFlood.getBottomHeight(), 
            "solid", Color.DARK_GRAY);

    ws1.placeImageXY(bg1, randFlood.getBoardWidth() / 2, randFlood.getBoardWidth() / 2);
    ws1.placeImageXY(btBottom1, randFlood.getBoardWidth() / 2, 
        randFlood.getBoardHeight() - randFlood.getBottomHeight() / 2);
    t.checkExpect(randFlood.getWS(), ws1);
    
    WorldScene ws2 = new WorldScene(by2Flood.getBoardWidth(), by2Flood.getBoardHeight());
    WorldImage bg2 = new RectangleImage(by2Flood.getBoardWidth(), 
        by2Flood.getBoardHeight(), "solid", Color.BLACK);
    WorldImage btBottom2 = 
        new RectangleImage(by2Flood.getBoardWidth(), by2Flood.getBottomHeight(), 
            "solid", Color.DARK_GRAY);

    ws2.placeImageXY(bg2, by2Flood.getBoardWidth() / 2, by2Flood.getBoardWidth() / 2);
    ws2.placeImageXY(btBottom2, by2Flood.getBoardWidth() / 2, 
        by2Flood.getBoardHeight() - by2Flood.getBottomHeight() / 2);
    t.checkExpect(by2Flood.getWS(), ws2);
    WorldScene ws3 = new WorldScene(nullFlood.getBoardWidth(), nullFlood.getBoardHeight());
    WorldImage bg3 = new RectangleImage(nullFlood.getBoardWidth(), 
        nullFlood.getBoardHeight(), "solid", Color.BLACK);
    WorldImage btBottom3 = 
        new RectangleImage(nullFlood.getBoardWidth(), nullFlood.getBottomHeight(), 
            "solid", Color.DARK_GRAY);

    ws3.placeImageXY(bg3, nullFlood.getBoardWidth() / 2, nullFlood.getBoardWidth() / 2);
    ws3.placeImageXY(btBottom3, nullFlood.getBoardWidth() / 2, 
        nullFlood.getBoardHeight() - nullFlood.getBottomHeight() / 2);
    t.checkExpect(nullFlood.getWS(), ws3);
  }

  // to test the makeScene()
  void testmakeScene(Tester t) {

    this.initStartGame();

    int maxSteps = flood1.getMaxSteps();  
    int boardWidth = flood1.getBoardWidth();
    int bottomHeight = flood1.getBottomHeight();
    int boardHeight = flood1.getBoardHeight();
    WorldScene ws = flood1.getWS();

    // Place all the cells
    for (Cell c : flood1.board) {

      c.drawOn(ws); 
    }

    int textSize = boardWidth / 16;
    int offSet = FloodItWorld.BORDER_SIZE + boardWidth / 10;
    
    // Place the score 
    WorldImage score = new TextImage("0/" + maxSteps, textSize, Color.WHITE);
    ws.placeImageXY(score, offSet, boardHeight - bottomHeight / 2);

    // Place the time
    String scaledTime = String.format("%.2f", 0.00) + " s";
    WorldImage time = new TextImage(scaledTime, textSize, Color.WHITE);
    ws.placeImageXY(time, boardWidth - offSet, boardHeight - bottomHeight / 2);

    t.checkExpect(flood1.makeScene(), ws);
  }
  
  // to test the makeScene() method
  void testMakeScene2(Tester t) {
    
    this.initStartGame();

    FloodItWorld floodRand = new FloodItWorld();
    
    int maxSteps = floodRand.getMaxSteps();  
    int boardWidth = floodRand.getBoardWidth();
    int bottomHeight = floodRand.getBottomHeight();
    int boardHeight = floodRand.getBoardHeight();
    WorldScene ws = floodRand.getWS();

    // Place all the cells
    for (Cell c : floodRand.board) {

      c.drawOn(ws); 
    }

    int textSize = boardWidth / 16;
    int offSet = FloodItWorld.BORDER_SIZE + boardWidth / 10;
    
    // Place the score 
    WorldImage score = new TextImage("0/" + maxSteps, textSize, Color.WHITE);
    ws.placeImageXY(score, offSet, boardHeight - bottomHeight / 2);

    // Place the time
    String scaledTime = String.format("%.2f", 0.00) + " s";
    WorldImage time = new TextImage(scaledTime, textSize, Color.WHITE);
    ws.placeImageXY(time, boardWidth - offSet, boardHeight - bottomHeight / 2);
    
    t.checkExpect(floodRand.makeScene(), ws);
  }

  // To test the onMouseClicked(Posn) method
  void testOnMouseClicked(Tester t) {

    this.initStartGame();
    t.checkExpect(flood1.floodColor, Color.CYAN);
    t.checkExpect(flood1.toBeFlooded.isEmpty(), true);
    t.checkExpect(flood1.stepsMade, 0);
    flood1.onMouseClicked(new Posn(50, 25));
    t.checkExpect(flood1.stepsMade, 1);
    t.checkExpect(flood1.floodColor, Color.BLACK);
    t.checkExpect(flood1.toBeFlooded, Arrays.asList(0));
    
    // Clicking on a cell with the same color as floodColor does nothing
    flood1.onMouseClicked(new Posn(50, 25));
    t.checkExpect(flood1.stepsMade, 1);
    t.checkExpect(flood1.floodColor, Color.BLACK);
    t.checkExpect(flood1.toBeFlooded, Arrays.asList(0));
    
    // Click somewhere off the board does nothing
    flood1.onMouseClicked(new Posn(0, 0));
    t.checkExpect(flood1.stepsMade, 1);
    t.checkExpect(flood1.floodColor, Color.BLACK);
    t.checkExpect(flood1.toBeFlooded, Arrays.asList(0));
    
    this.initStartGame();
    t.checkExpect(flood1.toBeFlooded.isEmpty(), true);
    t.checkExpect(flood1.stepsMade, 0);
    flood1.onMouseClicked(new Posn(80, 25));
    t.checkExpect(flood1.stepsMade, 1);
    t.checkExpect(flood1.floodColor, Color.BLUE);
    t.checkExpect(flood1.toBeFlooded, Arrays.asList(0));
    
    // Simulate the game flooding everything by clearing this ArrayList
    flood1.toBeFlooded.clear();
    
    flood1.onMouseClicked(new Posn(55, 85));
    t.checkExpect(flood1.stepsMade, 2);
    t.checkExpect(flood1.floodColor, Color.CYAN);
    t.checkExpect(flood1.toBeFlooded, Arrays.asList(0));
    
    // Click while there is something being flooded does nothing
    this.initStartGame();
    t.checkExpect(flood1.floodColor, Color.CYAN);
    flood1.toBeFlooded.add(1);
    t.checkExpect(flood1.stepsMade, 0);
    flood1.onMouseClicked(new Posn(80, 25));
    t.checkExpect(flood1.stepsMade, 0);
    t.checkExpect(flood1.floodColor, Color.CYAN);
  }
  
  // to test the onKeyEvent(String) method
  void testOnKeyEvent(Tester t) {
    
    FloodItWorld colorEdgeCase = new FloodItWorld(3,2);
    this.initStartGame();
    t.checkExpect(flood1.boardSize, 3);
    t.checkExpect(flood1.colors.size(), 8);
    t.checkExpect(sizeColorFlood.boardSize, 100);
    t.checkExpect(sizeColorFlood.colors.size(), 6);

    flood1.onKeyEvent("a");
    t.checkExpect(flood1.boardSize, 3);
    t.checkExpect(flood1.colors.size(), 8);

    sizeColorFlood.onKeyEvent("b");
    t.checkExpect(sizeColorFlood.boardSize, 100);
    t.checkExpect(sizeColorFlood.colors.size(), 6);

    this.initStartGame();
    flood1.onKeyEvent("right");
    t.checkExpect(flood1.boardSize, 3);
    t.checkExpect(flood1.colors.size(), 8);

    sizeColorFlood.onKeyEvent("right");
    t.checkExpect(sizeColorFlood.boardSize, 100);
    t.checkExpect(sizeColorFlood.colors.size(), 7);

    this.initStartGame();
    flood1.onKeyEvent("left");
    t.checkExpect(flood1.boardSize, 3);
    t.checkExpect(flood1.colors.size(), 7);

    colorEdgeCase.onKeyEvent("left");
    t.checkExpect(colorEdgeCase.boardSize, 3);
    t.checkExpect(colorEdgeCase.colors.size(), 2);

    this.initStartGame();
    flood1.onKeyEvent("up");
    t.checkExpect(flood1.boardSize, 4);
    t.checkExpect(flood1.colors.size(), 8);

    sizeColorFlood.onKeyEvent("up");
    t.checkExpect(sizeColorFlood.boardSize, 101);
    t.checkExpect(sizeColorFlood.colors.size(), 6);

    this.initStartGame();
    flood1.onKeyEvent("down");
    t.checkExpect(flood1.boardSize, 2);
    t.checkExpect(flood1.colors.size(), 8);
    flood1.onKeyEvent("down");
    t.checkExpect(flood1.boardSize, 2);

    sizeColorFlood.onKeyEvent("down");
    t.checkExpect(sizeColorFlood.boardSize, 99);
    t.checkExpect(sizeColorFlood.colors.size(), 6);
  }
  
  // To test worldEnds() method 
  void testWorldEnds(Tester t) {
    
    this.initStartGame();
    t.checkExpect(flood1.worldEnds(), new WorldEnd(false, flood1.makeScene()));
    t.checkExpect(nullFlood.worldEnds(), new WorldEnd(false, flood1.makeScene()));
    
    ArrayList<Cell> finishedBoard = new ArrayList<Cell>(Arrays.asList(c1, c1, c1, c1));
    FloodItWorld finishedWorld = new FloodItWorld(finishedBoard);
    t.checkExpect(finishedWorld.worldEnds(), 
        new WorldEnd(true, finishedWorld.makeEndScene("You Win")));
    
    finishedWorld.stepsMade = finishedWorld.getMaxSteps();
    
    // Finished on the last step, should win
    t.checkExpect(finishedWorld.worldEnds(), 
        new WorldEnd(true, finishedWorld.makeEndScene("You Win")));
    
    ArrayList<Cell> finishedBoard2 = new ArrayList<Cell>(Arrays.asList(c1, c1, c1, c2));
    FloodItWorld finishedWorld2 = new FloodItWorld(finishedBoard2);
    t.checkExpect(finishedWorld2.worldEnds(), 
        new WorldEnd(false, finishedWorld2.makeScene()));
    
    finishedWorld2.stepsMade = finishedWorld2.getMaxSteps();
    t.checkExpect(finishedWorld2.worldEnds(), 
        new WorldEnd(true, finishedWorld.makeEndScene("You Lose")));
  }
  
  //To test the makeEndScene() method
  void testMakeEndScene(Tester t) {
    
    this.initStartGame();
    
    int textSize = flood1.getBoardHeight() / 16;

    String text = "test";
    WorldScene ws = flood1.makeScene();
    TextImage response = new TextImage(text, textSize, FontStyle.BOLD, Color.WHITE);

    ws.placeImageXY(response, flood1.getBoardWidth() / 2, 
        flood1.getBoardHeight() - flood1.getBottomHeight() / 2);

    t.checkExpect(flood1.makeEndScene(text), ws);
  }
  
  //To test the makeEndScene() method
  void testMakeEndScene2(Tester t) {

    this.initStartGame();
    
    int textSize = randFlood.getBoardHeight() / 16;

    String text = "test";
    WorldScene ws = randFlood.makeScene();
    TextImage response = new TextImage(text, textSize, FontStyle.BOLD, Color.WHITE);

    ws.placeImageXY(response, randFlood.getBoardWidth() / 2, 
        randFlood.getBoardHeight() - randFlood.getBottomHeight() / 2);

    t.checkExpect(randFlood.makeEndScene(text), ws);
  }
  
  // to test the allSameColor() method
  void testAllSameColor(Tester t) {
    
    this.initStartGame();
    t.checkExpect(nullFlood.allSameColor(), false);
    t.checkExpect(flood1.allSameColor(), false);
    
    FloodItWorld one = new FloodItWorld(1, 2);
    t.checkExpect(one.allSameColor(), true);
    
    FloodItWorld sameColor = new FloodItWorld(16, 1);
    t.checkExpect(sameColor.allSameColor(), true);
    sameColor.board.get(0).updateColor(Color.BLACK);
    t.checkExpect(sameColor.allSameColor(), false);
  }
  
  // to test the flood() method
  void testFlood(Tester t) {
        
    // This method just makes the cells at the indexes in the flood's toBeFlooded
    // to the flood's flood color and make the cell flooded
    
    this.initStartGame();
    
    t.checkExpect(flood1.board.get(0).color, Color.CYAN);
    t.checkExpect(flood1.board.get(1).color, Color.BLACK);
    t.checkExpect(flood1.board.get(2).color, Color.BLUE);
    t.checkExpect(flood1.board.get(3).color, Color.GREEN);
    t.checkExpect(flood1.board.get(4).color, Color.BLUE);
    t.checkExpect(flood1.board.get(5).color, Color.GREEN);
    t.checkExpect(flood1.board.get(6).color, Color.BLACK);
    t.checkExpect(flood1.board.get(7).color, Color.CYAN);
    t.checkExpect(flood1.board.get(8).color, Color.GREEN);
    
    t.checkExpect(flood1.board.get(0).flooded, true);
    
    for (int i = 1; i < flood1.board.size(); i++) {
      
      t.checkExpect(flood1.board.get(i).flooded, false);
    }
    
    t.checkExpect(flood1.toBeFlooded.isEmpty(), true);
    flood1.flood();
    
    t.checkExpect(flood1.board.get(0).flooded, true);

    for (int i = 1; i < flood1.board.size(); i++) {

      t.checkExpect(flood1.board.get(i).flooded, false);
    }
    
    t.checkExpect(flood1.board.get(0).color, Color.CYAN);
    t.checkExpect(flood1.board.get(1).color, Color.BLACK);
    t.checkExpect(flood1.board.get(2).color, Color.BLUE);
    t.checkExpect(flood1.board.get(3).color, Color.GREEN);
    t.checkExpect(flood1.board.get(4).color, Color.BLUE);
    t.checkExpect(flood1.board.get(5).color, Color.GREEN);
    t.checkExpect(flood1.board.get(6).color, Color.BLACK);
    t.checkExpect(flood1.board.get(7).color, Color.CYAN);
    t.checkExpect(flood1.board.get(8).color, Color.GREEN);
    
    t.checkExpect(flood1.floodColor, Color.CYAN);
    flood1.toBeFlooded = new ArrayList<Integer>(Arrays.asList(0, 1, 2, 3));
    flood1.flood();
    
    for (int i : Arrays.asList(0, 1, 2, 3)) {
      
      t.checkExpect(flood1.board.get(i).color, Color.CYAN);
      t.checkExpect(flood1.board.get(i).flooded, true);
    }
    
  }
  
  // to test the onTick() method
  void testOnTick(Tester t) {
    
    // Original start of flood1 board
    // Cy Bk Bl
    // G  Bl G
    // Bk Cy G
    
    // These test the following sequence of clicks
    // OnMouseClicked(Posn) is used to mutate fields easily
    // Start Game -> Black -> Blue -> Green -> Black -> Cyan -> End Game
    
    this.initStartGame();
    
    t.checkExpect(flood1.floodColor, Color.CYAN);
    t.checkExpect(flood1.toBeFlooded, new ArrayList<Integer>());
    t.checkExpect(flood1.board.get(0).color, Color.CYAN);
    t.checkExpect(flood1.board.get(0).flooded, true);
    t.checkExpect(flood1.board.get(1).color, Color.BLACK);
    t.checkExpect(flood1.board.get(1).flooded, false);
    t.checkExpect(flood1.time, 0);
   
    // If you click on a cell thats that has the same color as the floodColor
    // nothing happens
    flood1.onMouseClicked(new Posn(25, 25));
    flood1.onTick();
    t.checkExpect(flood1.time, 1);
    
    t.checkExpect(flood1.floodColor, Color.CYAN);
    t.checkExpect(flood1.toBeFlooded, new ArrayList<Integer>());
    t.checkExpect(flood1.board.get(0).color, Color.CYAN);
    t.checkExpect(flood1.board.get(0).flooded, true);
    t.checkExpect(flood1.board.get(1).color, Color.BLACK);
    t.checkExpect(flood1.board.get(1).flooded, false);
    
    flood1.onMouseClicked(new Posn(55, 25));
    flood1.onTick();
    t.checkExpect(flood1.time, 2);
    
    t.checkExpect(flood1.floodColor, Color.BLACK);
    t.checkExpect(flood1.board.get(0).color, Color.BLACK);
    t.checkExpect(flood1.board.get(0).flooded, true);
    t.checkExpect(flood1.board.get(1).color, Color.BLACK);
    t.checkExpect(flood1.board.get(1).flooded, false);
    t.checkExpect(flood1.board.get(2).color, Color.BLUE);
    t.checkExpect(flood1.board.get(2).flooded, false);
    t.checkExpect(flood1.board.get(4).color, Color.BLUE);
    t.checkExpect(flood1.board.get(4).flooded, false);
    
    flood1.onTick();
    t.checkExpect(flood1.time, 3);
    
    t.checkExpect(flood1.floodColor, Color.BLACK);
    t.checkExpect(flood1.board.get(0).color, Color.BLACK);
    t.checkExpect(flood1.board.get(0).flooded, true);
    t.checkExpect(flood1.board.get(1).color, Color.BLACK);
    t.checkExpect(flood1.board.get(1).flooded, true);
    t.checkExpect(flood1.board.get(2).color, Color.BLUE);
    t.checkExpect(flood1.board.get(2).flooded, false);
    t.checkExpect(flood1.board.get(4).color, Color.BLUE);
    t.checkExpect(flood1.board.get(4).flooded, false);
    
    flood1.onMouseClicked(new Posn(85, 25));
    flood1.onTick();
    t.checkExpect(flood1.time, 4);
    
    t.checkExpect(flood1.floodColor, Color.BLUE);
    t.checkExpect(flood1.board.get(0).color, Color.BLUE);
    t.checkExpect(flood1.board.get(0).flooded, true);
    t.checkExpect(flood1.board.get(1).color, Color.BLACK);
    t.checkExpect(flood1.board.get(1).flooded, true);
    t.checkExpect(flood1.board.get(2).color, Color.BLUE);
    t.checkExpect(flood1.board.get(2).flooded, false);
    t.checkExpect(flood1.board.get(4).color, Color.BLUE);
    t.checkExpect(flood1.board.get(4).flooded, false);
    
    flood1.onTick();
    t.checkExpect(flood1.time, 5);
    
    t.checkExpect(flood1.floodColor, Color.BLUE);
    t.checkExpect(flood1.board.get(0).color, Color.BLUE);
    t.checkExpect(flood1.board.get(0).flooded, true);
    t.checkExpect(flood1.board.get(1).color, Color.BLUE);
    t.checkExpect(flood1.board.get(1).flooded, true);
    t.checkExpect(flood1.board.get(2).color, Color.BLUE);
    t.checkExpect(flood1.board.get(2).flooded, false);
    t.checkExpect(flood1.board.get(4).color, Color.BLUE);
    t.checkExpect(flood1.board.get(4).flooded, false);
    
    flood1.onTick();
    t.checkExpect(flood1.time, 6);
    
    t.checkExpect(flood1.floodColor, Color.BLUE);
    t.checkExpect(flood1.board.get(0).color, Color.BLUE);
    t.checkExpect(flood1.board.get(0).flooded, true);
    t.checkExpect(flood1.board.get(1).color, Color.BLUE);
    t.checkExpect(flood1.board.get(1).flooded, true);
    t.checkExpect(flood1.board.get(2).color, Color.BLUE);
    t.checkExpect(flood1.board.get(2).flooded, true);
    t.checkExpect(flood1.board.get(4).color, Color.BLUE);
    t.checkExpect(flood1.board.get(4).flooded, true);
    
    flood1.onMouseClicked(new Posn(25, 55));
    flood1.onTick();
    t.checkExpect(flood1.time, 7);
    
    t.checkExpect(flood1.floodColor, Color.GREEN);
    t.checkExpect(flood1.board.get(0).color, Color.GREEN);
    t.checkExpect(flood1.board.get(0).flooded, true);
    t.checkExpect(flood1.board.get(1).color, Color.BLUE);
    t.checkExpect(flood1.board.get(1).flooded, true);
    t.checkExpect(flood1.board.get(2).color, Color.BLUE);
    t.checkExpect(flood1.board.get(2).flooded, true);
    t.checkExpect(flood1.board.get(4).color, Color.BLUE);
    t.checkExpect(flood1.board.get(4).flooded, true);
    
    flood1.onTick();
    t.checkExpect(flood1.time, 8);
    
    t.checkExpect(flood1.floodColor, Color.GREEN);
    t.checkExpect(flood1.board.get(0).color, Color.GREEN);
    t.checkExpect(flood1.board.get(0).flooded, true);
    t.checkExpect(flood1.board.get(1).color, Color.GREEN);
    t.checkExpect(flood1.board.get(1).flooded, true);
    t.checkExpect(flood1.board.get(2).color, Color.BLUE);
    t.checkExpect(flood1.board.get(2).flooded, true);
    t.checkExpect(flood1.board.get(4).color, Color.BLUE);
    t.checkExpect(flood1.board.get(4).flooded, true);
    
    flood1.onTick();
    t.checkExpect(flood1.time, 9);
    
    t.checkExpect(flood1.floodColor, Color.GREEN);
    t.checkExpect(flood1.board.get(0).color, Color.GREEN);
    t.checkExpect(flood1.board.get(0).flooded, true);
    t.checkExpect(flood1.board.get(1).color, Color.GREEN);
    t.checkExpect(flood1.board.get(1).flooded, true);
    t.checkExpect(flood1.board.get(2).color, Color.GREEN);
    t.checkExpect(flood1.board.get(2).flooded, true);
    t.checkExpect(flood1.board.get(4).color, Color.GREEN);
    t.checkExpect(flood1.board.get(4).flooded, true);
    
    flood1.onTick();
    t.checkExpect(flood1.time, 10);
    t.checkExpect(flood1.floodColor, Color.GREEN);
    t.checkExpect(flood1.board.get(3).color, Color.GREEN);
    t.checkExpect(flood1.board.get(3).flooded, true);
    t.checkExpect(flood1.board.get(5).color, Color.GREEN);
    t.checkExpect(flood1.board.get(5).flooded, true);
    
    flood1.onTick();
    t.checkExpect(flood1.time, 11);
    t.checkExpect(flood1.floodColor, Color.GREEN);
    t.checkExpect(flood1.board.get(8).color, Color.GREEN);
    t.checkExpect(flood1.board.get(8).flooded, true);
    

    // Changes between each tick follow same logic as before, end tick cases are
    // checked now
    
    flood1.onMouseClicked(new Posn(25, 85));
    flood1.onTick();
    flood1.onTick();
    
    t.checkExpect(flood1.floodColor, Color.BLACK);
    t.checkExpect(flood1.board.get(3).color, Color.BLACK);
    t.checkExpect(flood1.board.get(3).flooded, true);
    t.checkExpect(flood1.board.get(5).color, Color.GREEN);
    t.checkExpect(flood1.board.get(5).flooded, true);
    
    // If you click on a color while it is flooding game state should not change
    flood1.onMouseClicked(new Posn(25, 85));
    
    t.checkExpect(flood1.floodColor, Color.BLACK);
    t.checkExpect(flood1.board.get(3).color, Color.BLACK);
    t.checkExpect(flood1.board.get(3).flooded, true);
    t.checkExpect(flood1.board.get(5).color, Color.GREEN);
    t.checkExpect(flood1.board.get(5).flooded, true);
    
    flood1.onTick();
    flood1.onTick();
    flood1.onTick();
    
    for (int i : Arrays.asList(0, 1, 2, 3, 4, 5, 6, 8)) {
      
      t.checkExpect(flood1.board.get(i).color, Color.BLACK);
      t.checkExpect(flood1.board.get(i).flooded, true);  
    }
    
    flood1.onMouseClicked(new Posn(55, 85));
    flood1.onTick();
    flood1.onTick();
    flood1.onTick();
    flood1.onTick();
    flood1.onTick();
    
    for (int i : Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8)) {

      t.checkExpect(flood1.board.get(i).color, Color.CYAN);
      t.checkExpect(flood1.board.get(i).flooded, true);  
    }
    
    t.checkExpect(flood1.allSameColor(), true); 
  }
  

  /*
   *  To test the methods in FloodItUtilites class
   */

  FloodItUtilities util = new FloodItUtilities();

  // to test the addIfInBound(ArrayList<Integer>, int, Integer...) method
  void testAddIfInBound(Tester t) {

    this.initStartGame();
    int max = 9;
    t.checkExpect(util.addIfInBound(max, -1, -1, 1, 3), Arrays.asList(1, 3));
    t.checkExpect(util.addIfInBound(max, 0, 1, 1, 14), Arrays.asList(0, 1, 1));
    t.checkExpect(util.addIfInBound(max, 1, -1, 5, -1), Arrays.asList(1, 5));
    t.checkExpect(util.addIfInBound(max, -1, 0, 4, 6), Arrays.asList(0, 4, 6));
    t.checkExpect(util.addIfInBound(max, 1, 3, 5, 7), Arrays.asList(1, 3, 5, 7));
    t.checkExpect(util.addIfInBound(max, 2, 4, 8, -1), Arrays.asList(2, 4, 8));
    t.checkExpect(util.addIfInBound(max, -1, 15, -1), new ArrayList<Integer>());
  }  

  // to test the makeInitialBoard(ArrayList<Cell>) method
  void testMakeInitialBoard(Tester t) {

    Cell c1 = new Cell(0, 0, Color.PINK, false);
    Cell c2 = new Cell(1, 0, Color.YELLOW, false);
    Cell c3 = new Cell(0, 1, Color.PINK, false);
    Cell c4 = new Cell(1, 1, Color.ORANGE, false);

    ArrayList<Cell> initBoard = new ArrayList<Cell>(Arrays.asList(c1, c2, c3, c4));

    this.initStartGame();
    t.checkExpect(util.makeInitialBoard(randSeed, 2, FloodItWorld.MAX_COLORS), initBoard);

    ArrayList<Color> tempColor = new ArrayList<Color>(Arrays.asList(Color.RED, 
        Color.BLACK, Color.BLUE));

    ArrayList<Cell> randBoard = util.makeInitialBoard(new Random(), 3, tempColor);
    t.checkExpect(randBoard.size(), 9);
    for (int i = 0; i < randBoard.size(); i++) {

      t.checkExpect(tempColor.contains(randBoard.get(i).color), true);
    }
  }
  
  // to test the removeRepeats(ArrayList<T>)
  void testRemoveRepeats(Tester t) {
     
    ArrayList<Integer> arr = new ArrayList<Integer>(Arrays.asList(1, 2, 2, 3, 4, 5, 6, 7));
    
    util.removeRepeats(arr);
    t.checkExpect(arr, Arrays.asList(1, 2, 3, 4, 5, 6, 7));
    
    ArrayList<Integer> empty = new ArrayList<Integer>();
    util.removeRepeats(empty);
    t.checkExpect(empty, new ArrayList<Integer>());
    
    ArrayList<Integer> arr2 = new ArrayList<Integer>(Arrays.asList(1, 2, 1, 3, 4, 3, 6, 2));
    util.removeRepeats(arr2);
    t.checkExpect(arr2, Arrays.asList(1, 2, 3, 4, 6));
  }
  
  // to test the playSound() method play the game with sound on and see if sound is played
  // t.irlExpect(POP)
}