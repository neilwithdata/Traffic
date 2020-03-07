package com.bbsmart.pda.blackberry.traffic.model;

import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.Comparator;

/**
 * file: Board.java
 * 
 * last modified: 17/04/07
 * 
 * author: Neil Sainsbury
 */
public class Board {
	/*
	 * Board stores the positions of all the blocks on a 2D grid. It is also
	 * responsible for ensuring block movements are valid. At any given time,
	 * Board maintains a complete 'model' representation of the playing board.
	 */
	
	// Block movement constants
	public static final char DIR_LEFT = 'l';
	public static final char DIR_RIGHT = 'r';
	public static final char DIR_UP = 'u';
	public static final char DIR_DOWN = 'd';

	private int width; // The width of the playing board (in blocks)
	private int height; // the height of the playing board (in blocks)
	private Block[][] grid; // Board grid
	private Block[] blocks; // Blocks on grid
	private Block target; // target block
	
	private int selectedIndx; // the currently selected block

	/**
	 * Board() creates a new width x height instance of Board with null
	 * references.
	 */
	public Board(int width, int height, Block[] blocks) {
		this.width = width;
		this.height = height;
		this.blocks = blocks;

		grid = new Block[width][height];
		
		placeBlocks(blocks);
		
		orderBlocksBySelection();
		updateSelectedBlock(0);
	}

	/** nullGrid() nulls the board (for posterity) */
	private void nullGrid() {
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				grid[i][j] = null;
			}
		}
	}
	
	private void updateSelectedBlock(int newSelectedBlockIndx) {
		// Tell the old block it is no longer selected
		blocks[selectedIndx].setSelected(false);
		blocks[selectedIndx].setActive(false);
		
		// Inform the new block it is selected
		selectedIndx = newSelectedBlockIndx;
		blocks[selectedIndx].setSelected(true);
	}

	/**
	 * placeBlocks(block) receives an array of blocks and places them onto the
	 * grid. Assumed that the blocks are valid references and not null
	 */
	public void placeBlocks(Block[] block) {
		int hMove = 0, vMove = 0;
		
		nullGrid();

		for (int i = 0; i < block.length; i++) { // for each block to place
			for (int move = 0; move < block[i].getLength(); move++) {
				grid[block[i].getRow() + vMove][block[i].getCol() + hMove] = block[i];
				if (block[i].isHorizontal()) {
					hMove++;
				} else {
					vMove++;
				}
			}
			hMove = vMove = 0;
			
			// Mark the target block for future reference
			if (block[i].isTarget()) {
				this.target = block[i];
			}
		}
	}
	
	/**
	 * selectNextBlock
	 * 
	 * @param direction
	 *            +1 for a downwards scroll action, -1 for an upwards scroll
	 *            action
	 * @param upDownModifier
	 *            true if the user was holding the modifier key while scrolling
	 *            to indicate up/down not left/right selection
	 * @return true if the selected block has changed
	 */
	public boolean selectNextBlock(int direction, boolean upDownModifier) {
		if ((selectedIndx == 0 && direction < 0)
				|| (selectedIndx == blocks.length - 1 && direction > 0)) {
			return false;
		} else {
			updateSelectedBlock(selectedIndx + direction);
			return true;
		}
	}
	
	public Block getSelectedBlock() {
		return blocks[selectedIndx];
	}
	
	private void orderBlocksBySelection() {
		Arrays.sort(blocks, new SelectionOrderComparator());
	}

	// TODO: For a more natural progression between blocks, should sort
	// by "central" position of block rather than top left/right position
	class SelectionOrderComparator implements Comparator {
		public int compare(Object o1, Object o2) {
			Block b1 = (Block) o1;
			Block b2 = (Block) o2;

			if (b1.getRowCenter() < b2.getRowCenter()) {
				return -1;
			} else if (b1.getRowCenter() == b2.getRowCenter()) {
				if (b1.getColCenter() < b2.getColCenter()) {
					return -1;
				} else if (b1.getColCenter() == b2.getColCenter()) {
					return 0;
				} else {
					return 1;
				}
			} else {
				return 1;
			}
		}
	}

	/**
	 * moveBlock moves the block with the specified id, 1 spaces in the given
	 * direction if the move is valid.
	 */
	public boolean moveBlock(Block block, char direction) {
		// Throw away invalid move requests first
		if (block.isHorizontal() && (direction == DIR_UP || direction == DIR_DOWN)) {
			return false; // Horizontal blocks can't move up or down
		}
		
		if (block.isVertical() && (direction == DIR_LEFT || direction == DIR_RIGHT)) {
			return false; // Vertical blocks can move left or right
		}
		
		int nDir = (direction == DIR_LEFT || direction == DIR_UP) ? -1 : 1;

		if (canMove(block, nDir)) {
			positionBlock(block, nDir);
			updateSelectionOrdering();
			return true;
		} else {
			return false;
		}
	}
	
	private void updateSelectionOrdering() {
		// since a block has moved, two things need to happen
		// first we need to regenerate the ordering
		orderBlocksBySelection();
		
		// then we need to re-find the selected block index
		for (int i = 0; i < blocks.length; i++) {
			if (blocks[i].isSelected()) {
				selectedIndx = i;
				break;
			}
		}
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
	
	public Block[] getBlocks() {
		return blocks;
	}

	/** returns block at position row, col */
	public Block getBlockAtPosition(final int row, final int col) {
		return grid[row][col];
	}

	/**
	 * canMove(block, direction, nDir) determines whether a given block can
	 * validly move one space in the given direction. Assumed that block is not
	 * null
	 */
	private boolean canMove(Block block, int nDir) {
		int row = block.getRow();
		int col = block.getCol();
		int length = block.getLength();
		int newPos;

		if (block.isHorizontal()) {
			newPos = col + nDir;
			if ((newPos + length <= width) && (newPos >= 0)) { // within bounds
				if (nDir == 1) {
					col += length;
				} else {
					col--;
				}
				return (grid[row][col] == null); // no collisions
			}
		} else {
			newPos = row + nDir;
			if ((newPos + length <= height) && (newPos >= 0)) { // within bounds
				if (nDir == 1) {
					row += length;
				} else {
					row--;
				}
				return (grid[row][col] == null); // no collisions
			}
		}
		return false;
	}

	/**
	 * positionBlock(index, nDir) moves the block with array index in the
	 * specified direction (nDir) the number 1 space.
	 */
	private void positionBlock(Block block, int nDir) {
		int row = block.getRow();
		int column = block.getCol();

		if (block.isHorizontal()) {
			column += nDir;
		} else {
			row += nDir;
		}
		block.setPos(row, column); // Update the block
		placeBlocks(blocks); // Update the board
	}

	/**
	 * solved() specifies whether the board has been solved (target block has
	 * exited)
	 */
	public boolean isSolved() {
		if (target != null) {
			return (target.getCol() + target.getLength() > 5);
		}
		return false;
	}
}