package com.bbsmart.pda.blackberry.traffic.model;

import net.rim.device.api.ui.Color;

/**
 * file: Block.java
 * 
 * last modified: 17/04/07
 * 
 * author: Neil Sainsbury
 */
public class Block {
	/*
	 * Block acts as a template for Block objects. Blocks are the things that
	 * get moved around in the game world (Also referred to as 'cars' in the
	 * context of the game)
	 */

	private int identifier; // block identifier
	private char orientation; // block orientation: 'H' or 'V'
	private int length; // block length (length >= 2)
	private boolean target; // identifies target block
	private int row, col; // position of top left square of block
	private int colour; // colour of the block
	private boolean selected; // whether the block is currently selected
	private boolean active; // whether the block is active (able to be moved) or not

	/**
	 * Block(identifier, row, col, orientation, length, target) creates a new
	 * instance of Block with the passed parameters. By default, the block is
	 * Red.
	 */
	public Block(int identifier, int row, int col, char orientation,
			int length, boolean target) {
		this.identifier = identifier;
		this.row = row;
		this.col = col;
		this.orientation = orientation;
		this.length = length;
		this.target = target;
		colour = Color.RED; // initial colour of block; for compiler
	}

	/** Accessor methods */
	public char getOrientation() {
		return orientation;
	}

	public int getIdentity() {
		return identifier;
	}

	public int getRow() {
		return row;
	}

	public int getCol() {
		return col;
	}
	
	public float getRowCenter() {
		if (isHorizontal()) {
			return (row + 0.5f);
		} else {
			return (row + length/2.0f);
		}
	}
	
	public float getColCenter() {
		if (isHorizontal()) {
			return (col + length/2.0f);
		} else {
			return (col + 0.5f);
		}
	}

	public int getLength() {
		return length;
	}

	public boolean isTarget() {
		return target;
	}

	public int getColour() {
		return colour;
	}
	
	public boolean isVertical() {
		return (orientation == 'V');
	}
	
	public boolean isHorizontal() {
		return (orientation == 'H');
	}

	public void setPos(int row, int col) {
		this.row = row;
		this.col = col;
	}

	public void setColour(int colour) {
		this.colour = colour;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public boolean isActive() {
		return active;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public boolean isSelected() {
		return selected;
	}
}