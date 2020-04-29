import java.awt.Color;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import javalib.impworld.World;
import javalib.impworld.WorldScene;
import javalib.worldimages.*;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

/*
 * EXTRA CREDIT THINGS WE ADDED:
 * 
 *  - You can adjust the size of the board by using the "up" and "down" key
 *    while the game is running and you haven't won / lost yet. It will generate
 *    a new board that is +/- 1 size different. Note there is no maximum size of
 *    the board, but the pop up window made my bigBang is limited by the size of 
 *    the initial board. The minimum size of the board is 2 so you won't win 
 *    automatically.
 *    
 *  - You can adjust the number of colors used in the board  using the "left" 
 *    and "right" keys in the same fashion as the above comment.  Note that
 *    the minimum number of colors you can have is 2 to again prevent winning
 *    automatically. Also not the maximum number of colors is dictated by 
 *    the size of FloodItWorld.MAX_COLORS
 *    
 *  - The current step / max steps is displayed at the bottom left of the screen.
 *    Note this number resets to 0 every time you change things about the board, i.e.
 *    pressing "r", "up", "down", "left", "right"
 *     
 *  - The time starts at the start of the game and is displayed in seconds in the
 *    bottom of the screen.  Every time you change things about the board as outlined
 *    above, the time resets to 0.  The time is displayed up to a second decimal
 *    i.e. 0.00
 *    
 *  - Every time the board is recreated or at the start of the game a "Pop" sound will
 *    be made.  Also, if you just finished flooding a section while playing the game
 *    a "Pop" sound will be made. Look at the bottom of onTick() to see more info 
 *    about when a "Pop" sound is made.
 *    
 *  - We added the option to chose where the initial flooding of the game starts. So in 
 *    the web-site version the flooding always starts from the top left, but we have a method
 *    that can put the initial flooding at any location on the board.
 *    
 *    Look at the method getBeginFloodIndex() if you want to change where the flooding starts.
 *    Right now its always starts at the top left.
 *    
 *    There are some recommended option in getBeginFloodIndex() like the top right, bottom 
 *    left, bottom right and the center.  Change the method to whatever you want, it won't
 *    affect the rest of the code.
 *    
 *    
 * 
 */

// To represent the world state for the FloodIt Game
class FloodItWorld extends World {

  // The width of the black border on the edge of the scene
  static final int BORDER_SIZE = Cell.CELL_SIZE / 3;

  //The maximum available colors that the cells can be made of
  static final ArrayList<Color> MAX_COLORS =
      new ArrayList<Color>(Arrays.asList(Color.RED, 
          Color.YELLOW, Color.GREEN, Color.BLUE, 
          Color.MAGENTA, Color.PINK, Color.ORANGE, Color.CYAN));
    
  // The number of squares in each row of the game
  int boardSize;
     
  // The available colors to make a cell in this FloodItWorld
  ArrayList<Color> colors;
  
  // All the cells of the game
  ArrayList<Cell> board;
    
  // The number of steps the player has made sofar
  int stepsMade;
  
  // The cells that have to be flooded
  ArrayList<Integer> toBeFlooded;
  
  // The current flood color
  Color floodColor;
  
  // The current time of the game in ticks defined by PlayMastermind.TICK_RATE;
  int time;
  
  FloodItWorld(int boardSize, 
      ArrayList<Color> colors, 
      ArrayList<Cell> board, 
      int stepsMade,
      ArrayList<Integer> toBeFlooded,
      Color floodColor,
      int time) {
    
    this.boardSize = boardSize;
    this.colors = colors;
    this.board = board;
    this.stepsMade = stepsMade;
    this.toBeFlooded = toBeFlooded;
    this.floodColor = floodColor;
    this.time = time;
  }
  
  // Used to test n x n game, without any cells linked
  // The cells are not linked here, so we can test this.linkCells();
  // This constructor is only used in testing, not during runtime of the game
  FloodItWorld(ArrayList<Cell> board) {
    
    this.board = board;
    this.boardSize = (int) Math.sqrt(board.size());
    this.stepsMade = 0;
    this.toBeFlooded = new ArrayList<Integer>();
    this.floodColor = this.board.get(this.getBeginFloodIndex()).color;
    this.time = 0;
    this.colors = MAX_COLORS;
  }
  
  // Default the game to 14 x 14 with 8 colors
  FloodItWorld() {
    
    this(14, MAX_COLORS.size());
  }
  
  FloodItWorld(int boardSize, int numColors) {
    
    this(new Random(), boardSize, numColors);
  }
  
  FloodItWorld(Random rand, int boardSize, int numColors) {
    
    this.reset(rand, boardSize, numColors); 
  }
  
  // EFFECT : Resets this FloodItWorld's fields to a new initial state where the board
  // is created with the given random, boardSize and numColors
  // reset is used in the constructors to make the initial game state as well
  void reset(Random rand, int boardSize, int numColors) {
    
    // set the boardSize to the given boardSIze
    this.boardSize = boardSize;
       
    // Add the given number of colors getting from MAX_COLORS
    this.colors = new ArrayList<Color>(); 
    for (int i = 0; i < numColors; i++) {
      
      this.colors.add(MAX_COLORS.get(i));
    }
    
    // Make the initial board with the Random object and given boardSize and this colors
    this.board = new FloodItUtilities().makeInitialBoard(rand, boardSize, this.colors);
    int beginIndex = this.getBeginFloodIndex();
    this.board.get(beginIndex).makeFlooded(); // Make the origin of flooding flooded
    this.linkCells();                         // Link all the cells together
      
    this.stepsMade = 0;                          // Set the number of steps made to 0
    this.toBeFlooded = new ArrayList<Integer>(); // Make toBeFlooded empty
    this.time = 0;                               // Set the time to 0
        
    // Set the floodColor to the color of cell at the origin of flooding
    this.floodColor = this.board.get(beginIndex).color;
    
    // Flood the initial board if there are similar colored cells around the 
    // origin of flooding at the start of the game
    this.toBeFlooded.add(beginIndex);
    this.flood(); 
  }
  
  // EFFECT : Updates each cell in the board to refer to each individual cells
  // adjacent cells.  Cells that have no reference are kept null.
  void linkCells() {
    
    for (int boardIndex = 0; boardIndex < board.size(); boardIndex++) {
      
      ArrayList<Integer> adjCellsIndex = this.getAdjacentIndexes(boardIndex);
      
      for (int adjIndex : adjCellsIndex) {
        
        this.board.get(boardIndex).updateAdjacent(this.board.get(adjIndex));
      }
    }
  }
  
  // Gets the indexes of the cells that are adjacent to the cell at the given boardIndex
  // PRECONDITOIN : The cells in this.board are in Left to Right, Top to Bottom order
  
  // This method lets us do operation on adjacent cells very quickly because we can access
  // adjacent cells in constant runtime with get(int)
  ArrayList<Integer> getAdjacentIndexes(int boardIndex) {

    if (boardIndex < 0 || boardIndex >= this.board.size()) {

      throw new IndexOutOfBoundsException("Cannot get adjascent indexes of out of bounds index");
    }

    int rIndex;
    if ((boardIndex + 1) % this.boardSize == 0 && boardIndex != 0) {

      rIndex = -1;
    }
    else {

      rIndex = boardIndex + 1;
    }

    int lIndex;
    if (boardIndex % this.boardSize == 0 && boardIndex != 0) {

      lIndex = -1;
    }
    else {

      lIndex = boardIndex - 1;
    }

    int tIndex = boardIndex - boardSize;
    int bIndex = boardIndex + boardSize;

    // Remove the ones that are out of bounds
    FloodItUtilities util = new FloodItUtilities();
    ArrayList<Integer> adjCellsIndex = 
        util.addIfInBound(this.board.size(), rIndex, lIndex, tIndex, bIndex);

    return adjCellsIndex;
  }
  
  // Get the pixel board width of this FloodItWorld
  int getBoardWidth() {
    
    return this.boardSize * Cell.CELL_SIZE + 2 * BORDER_SIZE;
  }
  
  // Get the pixel board height of this FloodItWorld
  int getBoardHeight() {
    
    return this.getBoardWidth() + this.getBottomHeight();
  }
  
  // Get the height of the bottom section of the game that holds the info about time 
  // and the score
  int getBottomHeight() {
    
    return this.getBoardWidth() / 6;
  }
  
  // Get the max number of steps for this FloodItWorld; Depends on the size of the board
  // and the number of colors used
  int getMaxSteps() {
    
    return (int)(0.3 * this.boardSize * this.colors.size()) ;
  }
  
  // Get the index of the origin of flooding in this FloodItWorld
  // If you want to change where the flooding starts update this function,
  // for now it return 0 i.e. the top left cell
  int getBeginFloodIndex() {
    
    //int center = this.boardSize/2 * (this.boardSize + 1);
    int topLeft = 0;
    //int topRight = this.boardSize - 1;
    //int bottomLeft = this.boardSize * this.boardSize;
    //int bottomRight = this.board.size() - 1;
     
    return topLeft;
  }
  
  // Get the WorldScene for this FloodItWorld that places the background images on it already
  WorldScene getWS() {
    
    int boardWidth = this.getBoardWidth();
    int bottomHeight = this.getBottomHeight();
    int boardHeight = this.getBoardHeight();
    WorldScene ws = new WorldScene(boardWidth, boardHeight);
    
    // Place the Background Images
    WorldImage bg = new RectangleImage(boardWidth, boardWidth, "solid", Color.BLACK);
    WorldImage bgBottom = 
        new RectangleImage(boardWidth, bottomHeight, "solid", Color.DARK_GRAY);
     
    ws.placeImageXY(bg, boardWidth / 2, boardWidth / 2);
    ws.placeImageXY(bgBottom, boardWidth / 2, boardHeight - bottomHeight / 2);
    
    return ws;
  }
  
  // Gets the cell that contains the given Posn, null if there is no cell
  // i.e. you click on the black border or on the bottom section of the game
  Cell getCellAt(Posn p) {

    for (Cell c : this.board) {

      if (c.isAtCord(p)) {

        return c;
      }
    }
    return null;
  }

  // Gets the indexes of the next cells that need to be flooded in this FloodItWorld relative
  // to the given boardIndex
  // PRECONDITION: the cell at boardIndex is already flooded
  ArrayList<Integer> getToBeFlooded(int boardIndex) {

    ArrayList<Integer> ajdIndexes = this.getAdjacentIndexes(boardIndex);
    ArrayList<Integer> toBeFlooded = new ArrayList<Integer>();

    for (int i : ajdIndexes) {

      // Get the index of adjacent cells that are either flooded or the same Color, but not
      // both

      // We used XOR here because if an adjacent cell is both flooded and the same color as
      // this.floodColor, then it means it was flooded previously. If we added it we would
      // create an infinite cycle
      if (this.board.get(i).flooded ^ this.board.get(i).sameColor(this.floodColor)) {

        toBeFlooded.add(i);
      }
    }

    return toBeFlooded;
  }
  
  // EFFECT : Updates the colors of this FloodItWorld by changing the colors of the cells
  // with indexes in this.toBeFlooded to  this.floodColor and updating the cells to be
  // flooded
  void flood() {

    for (int i : this.toBeFlooded) {

      this.board.get(i).updateColor(this.floodColor);
      this.board.get(i).makeFlooded();
    }
  }
  
  // Renders the game by placing all the cells, the score and the time 
  public WorldScene makeScene() {
    
    int maxSteps = this.getMaxSteps();  
    int boardWidth = this.getBoardWidth();
    int bottomHeight = this.getBottomHeight();
    int boardHeight = this.getBoardHeight();
    WorldScene ws = this.getWS();
        
    // Place all the cells
    for (Cell c : this.board) {
      
      c.drawOn(ws); 
    }
    
    int textSize = boardWidth / 16;
    int offSet = FloodItWorld.BORDER_SIZE + boardWidth / 10;
    
    // Place the score 
    WorldImage score = new TextImage(Integer.toString(this.stepsMade) + "/" + maxSteps,
        textSize, Color.WHITE);
    ws.placeImageXY(score, offSet, boardHeight - bottomHeight / 2);
    
    // Place the time
    String scaledTime = String.format("%.2f", this.time * PlayFloodIt.TICK_RATE) + " s";
    WorldImage time = new TextImage(scaledTime, textSize, Color.WHITE);
    ws.placeImageXY(time, boardWidth - offSet, boardHeight - bottomHeight / 2);
    
    return ws;
  }
    
  // EFFECT: Handles the mouse clicks in the game, causes flooding to occur when
  // a different color than the flood color is clicked on
  // NOTE : Does nothing if the board is currently being flooded
  public void onMouseClicked(Posn p) {
  
    Cell clickedOn = this.getCellAt(p);
    
    // Do nothing if the board is currently being flooded, or you click on somewhere that
    // is not on the board or you click on a cell that has the same color as the current
    // floodColor
    if (!this.toBeFlooded.isEmpty() 
        || clickedOn == null
        || clickedOn.color.equals(this.floodColor)) {
      
      return;
    }
    
    // Update the floodColor with the color of the cell you clicked on
    this.floodColor = clickedOn.color;
    
    // Add the origin of flooding to this.toBeFlooded
    this.toBeFlooded.add(this.getBeginFloodIndex());
    
    // Advance the number of steps made
    this.stepsMade++;
  }
  
  // EFFECT: Handles the key events for the game by reseting the game with r, 
  // and using the up and down keys to change the size of the game, and the left 
  // and right keys to change the number of colors used in the game.
  public void onKeyEvent(String key) {
    
    if (key.equals("r")) {
      
      this.reset(new Random(), this.boardSize, this.colors.size());
    }
    
    if (key.equals("down")) {
      
      // Keep the minimum boardSize at 2 because a 1x1 game will win instantly
      this.reset(new Random(), Math.max(2, this.boardSize - 1), this.colors.size());
    }
    
    if (key.equals("up")) {

      this.reset(new Random(), 
          this.boardSize + 1, this.colors.size());
    }
    
    if (key.equals("right")) {

      this.reset(new Random(), this.boardSize, 
          Math.min(MAX_COLORS.size(), this.colors.size() + 1));
    }

    if (key.equals("left")) {

      // Keep the minimum number of colors at 2 because a one color game will win instantly
      this.reset(new Random(), this.boardSize, Math.max(2, this.colors.size() - 1));
    }
  }
  
  // EFFECT: Handle the on tick by updating the time and flooding this.toBeFlooded
  // and then updating what this.toBeFlooded should be next
  public void onTick() {

    // Advance the time
    this.time++;

    // If there is nothing that needs to be flooded do nothing
    if (this.toBeFlooded.isEmpty()) {

      return;
    }

    // If there is something that needs to be flooded flood it first
    // NOTE : This updates all the colors and flooded of cells in this.toBeFlooded which
    // is IMPORTANT when calculated what needs to be flooded next
    // This must be called first.
    this.flood();

    ArrayList<Integer> nextToBeFlooded = new ArrayList<Integer>();

    // Get the indexes of all the adjacent cells of the cell that needs to be flooded
    // from the cells that were just flooded
    for (int i : this.toBeFlooded) {

      nextToBeFlooded.addAll(this.getToBeFlooded(i));
    }

    // Remove repeat cells to prevent unnecessarily large number of indexes to update
    new FloodItUtilities().removeRepeats(nextToBeFlooded);

    // Set this.toBeFlooded to the next indexes that needed to be flooded
    this.toBeFlooded = nextToBeFlooded;
    
    // If you just flooded the last cells that need to be flooded, and there is 
    // nothing to flood next make a "pop" sound
    if (nextToBeFlooded.isEmpty()) {
      
      // The audio file is provided in the submission
      new FloodItUtilities().playAudio("C:\\Users\\Nithin\\Documents\\"
          + "Fundamentals of Computer Science 2\\Assignment 9 Version 2\\"
          + "src\\PopSoundEffect.wav");
    }
  }
  
  // Ends the world if the number of steps made exceeds the max number of steps
  // or if all the colors on the board are the same. Renders the last scene.
  public WorldEnd worldEnds() {
    
    // End the game AFTER the board is flooded on the players last click, not as
    // soon as the player clicks on their last cell
    if (!this.toBeFlooded.isEmpty()) {
      
      return new WorldEnd(false, this.makeScene());
    }
    
    if (this.allSameColor() ) {
      
      return new WorldEnd(true, this.makeEndScene("You Win"));
    } 
    else if (this.stepsMade >= this.getMaxSteps()) {
      
      return new WorldEnd(true, this.makeEndScene("You Lose"));
    }
    else {
      
      return new WorldEnd(false, this.makeScene());
    }
  }
  
  // Make the last WorlScene in the game, by rendering the current WorldState and then
  // placing the given text at the bottom of the screen.
  WorldScene makeEndScene(String text) {
    
    int textSize = this.getBoardHeight() / 16;
    
    WorldScene ws = this.makeScene();
    TextImage response = new TextImage(text, textSize, FontStyle.BOLD, Color.WHITE);
    
    ws.placeImageXY(response, this.getBoardWidth() / 2, 
        this.getBoardHeight() - this.getBottomHeight() / 2);
    
    return ws;
  }
  
  // Determines if every cell in this.board is the same color as this.floodColor.
  boolean allSameColor() {
    
    for (Cell c : this.board) {
      
      if (!c.sameColor(this.floodColor)) {
        
        return false;
      }
    }
    
    return true;
  }
}

// To represent a class that has useful utility methods for FloodItWorld
class FloodItUtilities {
  
  // Returns an ArrayList<Integer> that filters Integer...ints if they are < 0 or 
  // > max
  ArrayList<Integer> addIfInBound(int max, Integer...ints) {
    
    ArrayList<Integer> arr = new ArrayList<Integer>();
    
    for (int i : ints) {
      
      if (i >= 0 && i < max) {
        
        arr.add(i);
      }
    }
    
    return arr;
  }
  
  // Makes an initial ArrayList<Cell> board with the given boardSize and the available colors
  // Makes the board in Top to Bottom, Left to Right order; i.e. (0, 0) is top left and 
  // (boardSize, boardSize) in bottom right.
  ArrayList<Cell> makeInitialBoard(Random rand, int boardSize, ArrayList<Color> colors) {
    
    ArrayList<Cell> result = new ArrayList<Cell>();
    
    for (int y = 0; y < boardSize; y++) {

      for (int x = 0; x < boardSize; x++) {

        Color randColor = colors.get(rand.nextInt(colors.size()));

        result.add(new Cell(x, y, randColor, false));
      }
    }
    
    return result;
  }
  
  // EFFECT: Remove repeat elements in the given array, implicitly uses equals()
  <T> void removeRepeats(ArrayList<T> arr) {

    ArrayList<T> toBeAdded = new ArrayList<T>();

    for (int i = 0; i < arr.size(); i++) {

      if (!toBeAdded.contains(arr.get(i))) {

        toBeAdded.add(arr.get(i));
      }
    }

    arr.clear();
    arr.addAll(toBeAdded);
  }

  // Get the .wav file in this computer that has the given filePath, and then plays 
  // the audio. Throw an exception if the file is not found or if anything while 
  // playing the audio errors.
  void playAudio(String filePath) {

    try {

      File soundFile = new File(filePath);
      AudioInputStream audioIn = AudioSystem.getAudioInputStream(soundFile);              
      // Get a sound clip resource.
      Clip clip = AudioSystem.getClip();
      // Open audio clip and load samples from the audio input stream.
      clip.open(audioIn);
      clip.start();
    }

    catch (Exception e) {

      e.printStackTrace(); 
    }
  }
}