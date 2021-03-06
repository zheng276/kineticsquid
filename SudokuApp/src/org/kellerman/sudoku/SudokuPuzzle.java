package org.kellerman.sudoku;

public class SudokuPuzzle {

	/**
	 * Constant that defines the number of elements in each row and column of the matrix
	 */
	protected int matrixSize;
	/**
	 * Constant that defines the number of elements in each row and column of each
	 * submatrix of the matrix. This is {@link #matrixSize }/3.
	 */
	protected int subMatrixSize;
	/**
	 * Indicates that the values in the matrix are valid. It is set 
	 * when the puzzle is solved. The puzzle is solved when the values that are currently set in the
	 * {@link #valuesMatrix } are locked by {@link #lockInputValues() }
	 */
	protected boolean matrixValid;
	/**
	 * Indicates the input values are locked and the puzzle is ready to be solved. 
	 * It is set by {@link #lockInputValues() } and reset by {@link #unlockInputValues() }.
	 */
	protected boolean inputsLocked;
	/**
	 * Indicates the puzzle is solved. The solution is defined in
	 * {@link #solutionMatrix }.
	 */
	protected boolean puzzleSolved;
	/**
	 * Matrix[x, y, z] which provides references to locations of values
	 * in {@link #valuesMatrix }. X is the value (1 to {@link #matrixSize }. To make the
	 * code simpler, this length of the x dimension is {@link #matrixSize }+1. Y and z hold
	 * <code>boolean</code> values that indicate if the matrix element at Y = row and Z = column 
	 * contain the value in x.
	 */
	protected boolean valueLocations[][][];
	/**
	 * Matrix that contains the puzzle values. 0 indicates an element has no value. Matrix 
	 * {@link #inputElements }indicates which are input values. 
	 */
	protected int valuesMatrix[][];
	/**
	 * Contains the solution to the puzzle. It is set by {@link #solve() }
	 */
	protected SudokuPuzzle solutionMatrix;
	/**
	 * Matrix which indicates the elements in {@link #valuesMatrix } are invalid.  Invalid
	 * values are those that appear more than once in a row, column, or submatrix.
	 */
	protected boolean invalidElements[][];
	/**
	 * Matrix which indicates which elements in {@link #valuesMatrix } contain input values. Input
	 * values are those that are set in the puzzle. Values that are not inputs are those the puzzle
	 * solver must determine.
	 */
	protected boolean inputElements[][];
	
	/**
	 * Class which contains a matrix reference. It is added so as not to require a dependency
	 * on SWT for class <code>Point</code>.
	 */
	private class ElementRef {
		int row;
		int column;
		public ElementRef(int row, int column) {
			super();
			this.row = row;
			this.column = column;
		}
	}
	
	/**
	 * Constructs a new instance of this class given the size of the matrix. If the initial 
	 * size is not a multiple of 3, it is adjusted to be so.
	 * 
	 * @param size Number of elements in each row and column of the puzzle.
	 */
	public SudokuPuzzle(int size) {
		
		subMatrixSize = size / 3;
		matrixSize = subMatrixSize * 3;
		valuesMatrix = new int[matrixSize][matrixSize];
		invalidElements = new boolean[matrixSize][matrixSize];
		inputElements = new boolean[matrixSize][matrixSize];
		for (int row = 0; row < matrixSize; row++) {
			for (int column = 0; column < matrixSize; column++) {
				valuesMatrix[row][column] = 0;
				invalidElements[row][column] = false;
				inputElements[row][column] = false;
			}
		}

		valueLocations = new boolean[matrixSize+1][matrixSize][matrixSize];
		for (int value = 0; value < matrixSize+1; value++) {
			for (int row = 0; row < matrixSize; row++) {
				for (int column = 0; column < matrixSize; column++) {
					valueLocations[value][row][column]=false;
				}
			}
		}
		matrixValid = true;
		inputsLocked = false;
		puzzleSolved = false;
	}
	
	/**
	 * Solves the puzzle starting with the first element. The solution is contained in
	 * {@link #solutionMatrix }.  
	 * 
	 * @return true or false indicating whether or not the puzzle was solved.
	 */
	public boolean solve() {
		solutionMatrix = new SudokuPuzzle(matrixSize);
		puzzleSolved = solveForRowColumn(0,0);
		if (!puzzleSolved) {
			solutionMatrix = null;
		}
		return puzzleSolved;
	}
	
	/**
	 * Solves the matrix recursively starting with the element (<code>row</code>, 
	 * <code>column</code>.  The solution attempts are contained in {@link #solutionMatrix }.
	 * 
	 * @param row Row of element to start solution calculation with
	 * @param column Column of element to start solution calculation with
	 * @return whether or not the matrix is solved at this point.
	 */
	private boolean solveForRowColumn(int row, int column) {
		int nextRow, nextColumn;
		
		//If we're past the end of the matrix, meaning it's solved, return true.
		if (row == matrixSize) {
			return true;
		} else {
			//Else, calculate the row and column of the next element to try.
			if (column == matrixSize-1) {
				nextRow = row + 1;
				nextColumn = 0;
			} else {
				nextRow = row;
				nextColumn = column + 1;
			}
			//If the the value of the current element is defined, copy it to the solution matrix and
			//proceed to the next element.  Note, we are ignoring which are input elements and which
			//have been later added by the solver. This is because the matrix may have more than one
			//solution.
			if (valuesMatrix[row][column]>0) {
				solutionMatrix.setValueAt(row, column, valuesMatrix[row][column]);
				return solveForRowColumn(nextRow, nextColumn);
			} else {
				//Otherwise start with 1 and successively try each value to see if the resulting
				//matrix can be solved. 
				int nextValueToTry = 1;
				boolean solved = false;
				//Loop until a solution is found or we run out of values to try.
				while (!solved & nextValueToTry <= matrixSize) {
					//Set the value to try. This will cause the matrix to be validated with
					//the currently defined values.
					solutionMatrix.setValueAt(row, column, nextValueToTry);
					if (solutionMatrix.isMatrixValid()) {
						//If the matrix with this value we're trying is valid, proceed to the
						//next element.
						solved = solveForRowColumn(nextRow, nextColumn);
						if (!solved) {
							//If we can't solve the matrix with the current value to try starting
							//with the next element, clear the current value we're trying.
							solutionMatrix.clearValueAt(row, column);
						}
					} else {
						//This means the current value we're trying results in invalid matrix, so
						//clear it.
						solutionMatrix.clearValueAt(row, column);
					}
					//Try the next value.
					nextValueToTry++;
				}
				//After exiting the loop, return whether or not we were able to solve the puzzle
				//to this point.
				return solved;
			}
		}
	}
	
	/**
	 * Returns the value at element (row, column)
	 * 
	 * @param row Row of element to return
	 * @param column of element to return
	 * @return value in {@link #valuesMatrix } at (row, column)
	 */
	public int getValueAt(int row, int column){
		return valuesMatrix[row][column];
	}
	
	/**
	 * Returns the value of the solution at element (row, column)
	 * 
	 * @param row Row of element to return
	 * @param column of element to return
	 * @return value in {@link #solutionMatrix } at (row, column)
	 */
	public int getSolutionValueAt(int row, int column){
		return solutionMatrix.getValueAt(row, column);
	}
	
	/**
	 * Sets a value in {@link #valuesMatrix }. This results in a call to {@link #validateMatrix() }
	 * which causes the values in {@link #invalidElements } to be updated.
	 * 
	 * @param row Row of element to set
	 * @param column Column of element to set
	 * @param value Value to set
	 */
	public void setValueAt(int row, int column, int value) {
		if (valuesMatrix[row][column] > 0) {
			clearValueAt(row, column);
		}
		valuesMatrix[row][column] = value;
		valueLocations[value][row][column]=true;
		validateMatrix();
	}
	
	/**
	 * Clears a value in {@link #valuesMatrix }. This results in a call to {@link #validateMatrix() }
	 * which causes the values in {@link #invalidElements } to be updated.
	 * 
	 * @param row Row of element to clear
	 * @param column Column of element to clear
	 */
	public void clearValueAt(int row, int column) {
		int value = valuesMatrix[row][column];
		valuesMatrix[row][column] = 0;
		valueLocations[value][row][column]=false;
		validateMatrix();
	}
	
	/**
	 * Validates the values in {@link #valuesMatrix }, updates {@link #invalidElements }, and sets
	 * {@link #matrixValid }.  This method is invoked on the instance of <code>SudokuPuzzle</code>
	 * defined by {@link #solutionMatrix }. So, while the references here are to, for example, 
	 * {@link #valuesMatrix }, they are the values in the solution matrix.
	 */
	private void validateMatrix() {
		//First reset the values in invalidElements to indicate all elements are currently
		//valid.
		for (int row = 0; row < matrixSize; row++) {
			for (int column = 0; column < matrixSize; column++) {
				invalidElements[row][column] = false;
			}
		}
		matrixValid = true;
		//Validate each row by looping through each row and trying each value.
		for (int row = 0; row < matrixSize; row++) {
			for (int value = 1; value <= matrixSize; value++) {
				validateRowForValue(row, value);
			}
		}
		//Validate each column by looping through each column and trying each value.
		for (int column = 0; column < matrixSize; column++) {
			for (int value = 1; value <= matrixSize; value++) {
				validateColumnForValue(column, value);
			}
		}
		//Validate each submatrix by looping through each submatrix and trying each value.
		for (int row = 0; row < matrixSize; row = row+subMatrixSize) {
			for (int column = 0; column < matrixSize; column = column+subMatrixSize) {
				for (int value = 1; value <= matrixSize; value++) {
					validateSubMatrixForValue(row, column, value);
				}
			}
		}
	}
	
	/**
	 * Validate a row of {@link #valuesMatrix } to see if it is valid for the specified value.
	 * 
	 * @param row Row to validate
	 * @param value Value to check.
	 */
	private void validateRowForValue(int row, int value){
		//FirstOccurance holds the reference to the first occurance of value found in the row
		ElementRef firstOccurance = null;
		for (int column = 0; column < matrixSize; column++) {
			if (valueLocations[value][row][column]) {
				//If we've found the value in the row
				if (firstOccurance == null) {
					//If this is the first time encountered, record it
					firstOccurance = new ElementRef(row, column);
				} else {
					//Otherwise, we've found it before, so mark the matrix invalid and
					//mark the two elements where we've found the values as invalid.
					matrixValid = false;
					invalidElements[row][column] =  true;
					invalidElements[firstOccurance.row][firstOccurance.column] =  true;
				}
			}
		}
	}

	/**
	 * Validate a column of {@link #valuesMatrix } to see if it is valid for the specified value.
	 * 
	 * @param column Column to validate
	 * @param value Value to check.
	 */
	private void validateColumnForValue(int column, int value) {
		//FirstOccurance holds the reference to the first occurance of value found in the column
		ElementRef firstOccurance = null;
		for (int row = 0; row < matrixSize; row++) {
			if (valueLocations[value][row][column]) {
				//If we've found the value in a column
				if (firstOccurance == null) {
					//If this is the first time encountered, record it
					firstOccurance = new ElementRef(row, column);
				} else {
					//Otherwise, we're found it before, so mark the matrix invalid and
					//mark the two elements where we've found the values as invalid.
					matrixValid = false;
					invalidElements[row][column] =  true;
					invalidElements[firstOccurance.row][firstOccurance.column] =  true;
				}
			}
		}
	}

	/**
	 * Validate a submatrix of {@link #valuesMatrix } to see if it is valid for the specified value.
	 * 
	 * @param row Row index of submatrix to validate
	 * @param column Column index of submatrix to validate
	 * @param value Value to check.
	 */
	private void validateSubMatrixForValue(int row, int column, int value) {
		//FirstOccurance holds the reference to the first occurance of value found in the submatrix
		ElementRef firstOccurance = null;
		//Iterate through the rows of the submatrix
		for (int sRow = row; sRow < row+subMatrixSize; sRow++) {
			//Iterate through the columns of the submatrix
			for (int sColumn = column; sColumn < column+subMatrixSize; sColumn++) {
				//If we've found the value in the submatrix
				if (valueLocations[value][sRow][sColumn]) {
					if (firstOccurance == null) {
						//If this is the first time encountered, record it
						firstOccurance = new ElementRef(sRow, sColumn);
					} else {
						//Otherwise, we're found it before, so mark the matrix invalid and
						//mark the two elements where we've found the values as invalid.
						matrixValid = false;
						invalidElements[sRow][sColumn] =  true;
						invalidElements[firstOccurance.row][firstOccurance.column] =  true;
					}
				}
			}
		}
	}
	
	/**
	 * Return whether or not the specified element is valid, i.e. it's value is not repeated in the
	 * same row, column, or submatrix
	 * 
	 * @param row Row index of element
	 * @param column Column index of element
	 * @return true if the element has a valid value
	 */
	public boolean isElementValidAt(int row, int column){
		return !invalidElements[row][column];
	}
	
	/**
	 * Return whether or not the specified element is marked as an input. This value is set by
	 * {@link #lockInputValues() } and reset by {@link #unlockInputValues() }.
	 * 
	 * @param row Row index of element
	 * @param column Column index of element
	 * @return true if the element is an input
	 */
	public boolean isElementInputAt(int row, int column){
		return inputElements[row][column];
	}
	
	/**
	 * Marks the elements with defined values, that is the elements in {@link #valuesMatrix } that are
	 * non zero, as input elements.
	 */
	public void lockInputValues(){
		for (int row = 0; row < matrixSize; row++) {
			for (int column = 0; column < matrixSize; column++) {
				if (valuesMatrix[row][column] != 0) {
					inputElements[row][column] = true;
				}
			}
		}
		inputsLocked = true;
	}
	
	/**
	 * Marks all elements as not being input elements. This method also resets {@link #puzzleSolved }
	 * to mark the puzzle as not solved.
	 */
	public void unlockInputValues () {
		for (int row = 0; row < matrixSize; row++) {
			for (int column = 0; column < matrixSize; column++) {
				inputElements[row][column] = false;
			}
		}
		inputsLocked = false;
		puzzleSolved = false;
	}

	/**
	 * Returns whether or not the input elements have been locked.
	 * 
	 * @return Value of {@link #inputsLocked }
	 */
	public boolean isInputsLocked() {
		return inputsLocked;
	}

	/**
	 * Returns whether or not the matrix is valud
	 * 
	 * @return Value of {@link #isMatrixValid() }
	 */
	public boolean isMatrixValid() {
		return matrixValid;
	}

	/**
	 * Returns whether or not a solution has been generated for the puzzle.
	 * 
	 * @return Value of {@link #puzzleSolved }
	 */
	public boolean isPuzzleSolved() {
		return puzzleSolved;
	}

	/**
	 * Returns the size of the matrix.
	 * 
	 * @return Value of {@link #matrixSize }
	 */
	public int getMatrixSize() {
		return matrixSize;
	}

	/**
	 * Returns the size of a submatrix of the matrix.
	 * 
	 * @return Value of {@link #subMatrixSize }
	 */
	public int getSubMatrixSize() {
		return subMatrixSize;
	}

	/**
	 * Sets solution matrix.
	 * 
	 * @param solutionMatrix Instance of <code>Sudoku</code> the represents a solution
	 * to the puzzle.
	 */
	public void setSolutionMatrix(SudokuPuzzle solutionMatrix) {
		this.solutionMatrix = solutionMatrix;
	}
}
