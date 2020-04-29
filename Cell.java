import javalib.impworld.*;
import java.awt.Color;
import javalib.worldimages.*;

// Represents a single square of the game area
class Cell {
  
  // The Pixel size of each cell
  static final int CELL_SIZE = 30;
  
  // In logical coordinates, with the origin at the top-left corner of the screen
  int x;
  int y;
  Color color;
  boolean flooded;  // Whether or not this cell has been flooded
  
  // the four adjacent cells to this one
  // null represents a non-existing cell, i.e. there is no cell above the cell on the 
  // cell above cells on row 0, so they are null
  Cell left;
  Cell top;
  Cell right;
  Cell bottom;
  
  Cell(int x, int y, Color color, boolean flooded, Cell left, Cell top, Cell right, Cell bottom) {
    
    this.x = x;
    this.y = y;
    this.color = color;
    this.top = top;
    this.flooded = flooded;
    this.right = right;
    this.left = left;
    this.top = right;
    this.bottom = bottom;
  }
  
  Cell(int x, int y, Color color, boolean flooded) {
    
    this.x = x;
    this.y = y;
    this.color = color;
    this.flooded = flooded;
    this.top = null;
    this.right = null;
    this.left = null;
    this.top = null;
    this.bottom = null;
  }
  
  // Renders this cell as a WorldImage
  WorldImage renderCell() {
    
    return new RectangleImage(CELL_SIZE, CELL_SIZE, "solid", this.color);
  }
  
  // EFFECT : updates the given WorldScene by drawing this Cell on it
  void drawOn(WorldScene ws) {
    
    int pixelX = this.transformX();
    int pixelY = this.transformY();
    
    ws.placeImageXY(this.renderCell(), pixelX, pixelY);
  }
  
  // The pixel x value of this cell's location
  int transformX() {
    
    return CELL_SIZE / 2 + this.x * CELL_SIZE + FloodItWorld.BORDER_SIZE;
  }
  
  // The pixel y value of this cell's location
  int transformY() {
    
    return CELL_SIZE / 2 + this.y * CELL_SIZE + FloodItWorld.BORDER_SIZE;
  }
  
  // EFFECT : updates this cell's adjacent cell and updates the given cell
  // if they are indeed adjacent. Throws an exception if they are not adjacent
  void updateAdjacent(Cell c) {
    
    int xDif = c.x - this.x;
    int yDif = c.y - this.y;
    
    if (xDif == 1 && yDif == 0) {
      
      this.right = c;
      c.left = this;
    }
    else if (xDif == -1 && yDif == 0) {
      
      this.left = c;
      c.right = this;
    }
    else if (xDif == 0 && yDif == 1) {

      this.bottom = c;
      c.top = this;
    }
    else if (xDif == 0 && yDif == -1 ) {

      this.top = c;
      c.bottom = this;
    }
    else {
      
      throw new IllegalArgumentException("Cannot update non-adjascent cells: "
          + "(" + this.x + "," + this.y + ") " + "(" + c.x + "," + c.y + ")");
    }
  }
  
  // Determines if the given Posn is within the boundaries of this cell
  // A Posn on the left border of a cell is considered inside a cell, but a Posn
  // on the right border of a cell is not
  boolean isAtCord(Posn p) {
    
    int pixelX = this.transformX();
    int pixelY = this.transformY();
    
    return (pixelX - CELL_SIZE / 2) <= p.x && p.x < (pixelX + CELL_SIZE / 2)
        && (pixelY - CELL_SIZE / 2) <= p.y && p.y < (pixelY + CELL_SIZE / 2);
  }
  
  // EFFECT : Makes this cell flooded
  void makeFlooded() {
    
    this.flooded = true;
  }
  
  // EFFECT : Updates this cell with the given color
  void updateColor(Color color) {
    
    this.color = color;
  }
  
  // Determines if this cell's color is the same as the given color
  boolean sameColor(Color color) {
    
    return this.color.equals(color);
  }
}