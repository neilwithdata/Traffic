package com.bbsmart.pda.blackberry.traffic.io;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

import net.rim.device.api.io.LineReader;
import net.rim.device.api.util.StringUtilities;

import com.bbsmart.pda.blackberry.traffic.model.Block;

/**
 * ProblemReader reads puzzle problems from a file. Problems are stored in
 * the following file hierarchy:
 *		/easy
 *			e1
 *			e2
 *			...
 *		/medium
 *			m1
 *			m2
 *			...
 *		/hard
 *			h1
 *			h2
 *			...
 */
public class ProblemReader {
	public static final char PROBLEM_EASY = 'e';
	public static final char PROBLEM_MID = 'm';
	public static final char PROBLEM_HARD = 'h';

	private Block[] blocks; // The blocks read from file
	private int numMoves; // The optimal number of moves to solve the puzzle

	public boolean readProblem(char difficulty, int problemNum) {
		InputStream is = getClass().getResourceAsStream(
				"/" + difficulty + problemNum);
		
		if (is == null) { // Problem could not be found
			return false;
		} else {
			try {
				parseProblemFile(is);
				return true;
			} catch (IOException ioe) {
				return false;
			}
		}
	}

	private void parseProblemFile(InputStream is) throws IOException {
		LineReader reader = new LineReader(is);
		int rowCntr = 0;
		try {
			String line;
			while (true) {
				line = new String(reader.readLine());
				
				if (rowCntr == 0) {
					// first line in the file contains number of blocks
					int numBlocks = Integer.parseInt(line);
					blocks = new Block[numBlocks];
				} else if (rowCntr == blocks.length + 1) {
					// Last line contains optimal number of moves for puzzle
					numMoves = Integer.parseInt(line);
				} else {
					// Standard block row
					readBlock(rowCntr, line);
				}
				rowCntr++;
			}
		} catch (EOFException eofe) {
			// Finished reading all the lines in the file
		} catch (NumberFormatException nfe) {
			throw new IOException("Couldn't read number of blocks");
		} catch (Exception e) {
			throw new IOException(e.getMessage());
		}
	}
	
	/**
	 * Each line in a problem file (except for the first) represents a block.
	 * The following information (in the following form) is expected to be on
	 * each line: block_id block_row block_col alignment length [target]\n
	 * 
	 * @param line
	 * @throws IOException
	 */
	private void readBlock(int rowNum, String line) throws IOException {
		String[] fields = StringUtilities.stringToWords(line);

		try {
			if (fields.length < 5) {
				throw new Exception("Incorrect num fields");
			}
			
			int blockID = Integer.parseInt(fields[0]);
			blocks[blockID] = new Block(
					blockID,						// Block ID
					Integer.parseInt(fields[1]),	// Row
					Integer.parseInt(fields[2]),	// Col
					fields[3].charAt(0),			// Orientation
					Integer.parseInt(fields[4]),	// Length
					(fields.length == 6) // Optional target
				);
		} catch (Exception e) {
			throw new IOException("Error on row " + rowNum + 1 + ":"
					+ e.getMessage());
		}
	}
	
	public boolean problemExists(char difficulty, int problemNum) {
		return (getClass().getResourceAsStream("/" + difficulty + problemNum) != null);
	}
	
	public Block[] getBlocks() {
		return blocks;
	}
	
	public int getMoves() {
		return numMoves;
	}
}