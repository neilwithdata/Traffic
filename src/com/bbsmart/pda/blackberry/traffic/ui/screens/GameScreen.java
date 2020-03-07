package com.bbsmart.pda.blackberry.traffic.ui.screens;

import java.util.Date;
import java.util.Random;

import com.bbsmart.pda.blackberry.traffic.io.ProblemReader;
import com.bbsmart.pda.blackberry.traffic.model.Block;
import com.bbsmart.pda.blackberry.traffic.model.Board;
import com.bbsmart.pda.blackberry.traffic.ui.util.UiUtilities;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.Color;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.ui.container.MainScreen;

public class GameScreen extends MainScreen {
	private static final int UI_BOARD_WIDTH = Graphics.getScreenWidth() - 50;
	private static final int UI_BOARD_HEIGHT = Graphics.getScreenHeight() - 30;
	
	private final float BLOCK_WIDTH;
	private final float BLOCK_HEIGHT;
	
	private Bitmap exitBitmap;
	
	private Board board;
	private int numMoves;
	
	public GameScreen() {
		ProblemReader r = new ProblemReader();
		boolean success = r.readProblem(ProblemReader.PROBLEM_EASY, 1);
		if (success) {
			board = new Board(6, 6, r.getBlocks());
		} else {
			board = new Board(0, 0, new Block[0]);
		}
		
		numMoves = 0;
		
		exitBitmap = UiUtilities.getExitImage().getBitmap();
		
		randomiseBlockColours(board.getBlocks());
		
		BLOCK_WIDTH = UI_BOARD_WIDTH / board.getWidth();
		BLOCK_HEIGHT = Graphics.getScreenHeight() / board.getHeight();
	}
	
	private void randomiseBlockColours(Block[] blocks) {
		Random r = new Random(new Date().getTime());
		
		for (int i = 0; i < blocks.length; i++) {
			blocks[i].setColour(r.nextInt() << 2);
		}
	}
	
	protected void paint(Graphics graphics) {
		super.paint(graphics);
		
		graphics.clear();
		
		drawBoardBorder(graphics);
		
		Block[] blocks = board.getBlocks();
        for (int i = 0; i < blocks.length; i++) {
        	int xPos = (int) (blocks[i].getCol()* BLOCK_WIDTH);
        	int yPos = (int) (blocks[i].getRow() * BLOCK_HEIGHT);
        	int width = (int) (blocks[i].getLength() * BLOCK_WIDTH);
        	int height = (int) (blocks[i].getLength() * BLOCK_HEIGHT);
        	
        	// Highlight the selected block
        	if (blocks[i].isSelected()) {
        		graphics.setColor(Color.YELLOW);
        	} else {
        		graphics.setColor(blocks[i].getColour());
        	}
        	
        	if (blocks[i].isHorizontal()) {
        		graphics.fillRect(xPos, yPos, width, (int) BLOCK_HEIGHT);
        		graphics.setColor(Color.BLACK);
        		graphics.drawRect(xPos, yPos, width, (int) BLOCK_HEIGHT);
        	} else {
        		graphics.fillRect(xPos, yPos, (int) BLOCK_WIDTH, height);
        		graphics.setColor(Color.BLACK);
        		graphics.drawRect(xPos, yPos, (int) BLOCK_WIDTH, height);
        	}
        }
	}
	
	private void drawBoardBorder(Graphics graphics) {
		graphics.setColor(Color.AQUAMARINE);
		graphics.fillRect(UI_BOARD_WIDTH, 0, 2, Graphics.getScreenHeight());
		
		graphics.setColor(Color.BLUEVIOLET);
		graphics.drawText("Moves: ", UI_BOARD_WIDTH + 3, 0);
		graphics.drawLine(UI_BOARD_WIDTH + 10, 14, UI_BOARD_WIDTH + 50, 14);
		graphics.drawText(String.valueOf(numMoves), UI_BOARD_WIDTH + 26, 16);
		
		graphics.drawBitmap(UI_BOARD_WIDTH, 80, exitBitmap.getWidth(), exitBitmap.getHeight(), exitBitmap, 0, 0);
	}
	
	protected boolean trackwheelRoll(int amount, int status, int time) {
		Block selectedBlock = board.getSelectedBlock();
		
		if (selectedBlock.isActive()) {
			// If the block is active, then we can now allow it to be moved
			boolean moved;
			if (selectedBlock.isHorizontal()) {
				moved = board.moveBlock(selectedBlock, amount > 0 ? Board.DIR_RIGHT : Board.DIR_LEFT);
			} else {
				moved = board.moveBlock(selectedBlock, amount > 0 ? Board.DIR_DOWN : Board.DIR_UP);
			}
			
			if (moved) {
				numMoves++;
			}
		} else {
			// If the block is not active, then scrolling the wheel will now
			// select another block
			board.selectNextBlock((amount > 0) ? 1 : -1, false);
		}
		
		invalidate();
		
		return true;
	}
	
	protected boolean keyChar(char c, int status, int time) {
		switch (c) {
		case ' ':
			Block selectedBlock = board.getSelectedBlock();
			selectedBlock.setActive(!selectedBlock.isActive());
			return true;
		default:
			return super.keyChar(c, status, time);
		}
	}
	
	protected MenuItem restartLevelMenuItem = new MenuItem("Restart Level",
			0, 1) {
		public void run() {
			
		}
	};
	
	protected MenuItem exitMenuItem = new MenuItem("Exit Game",
			0, 1) {
		public void run() {
			close();
		}
	};
	
	protected void makeMenu(Menu menu, int instance) {
		menu.add(restartLevelMenuItem);
		menu.add(exitMenuItem);
	}
}
