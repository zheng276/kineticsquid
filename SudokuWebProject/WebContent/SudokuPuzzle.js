function SudokuPuzzle(size) {
	
	/*
	 * Set all the elements of array "array" of size "size" to "value"
	 */
	function initializeArray(array, size, value) {
		for ( var i = 0; i < size; i++) {
			array[i] = value;
		}
	}
	
	/*
	 * Given the row and column indices for an element, return it's submatrix
	 * index, an integer between 0 and 8
	 */
	function getSubMatrixFor(row, column) {
		return this.subMatrixIndex[row][column];

	}
	
	/*
	 * Returns an Array of [row, column] representing the upper left element of
	 * a given subMatrix
	 */
	function getUpperLeftForSubMatrix(subMatrix) {
		return [ subMatrix - (subMatrix % 3), subMatrix % 3 * 3 ];
	}
	
	/*
	 * Returns whether or not the matrix is valid
	 */
	function isMatrixValid() {
		var matrixIsValid = true;
		var i = 0;
		while (matrixIsValid && i < this.matrixSize) {
			if (this.invalidRows[i] || this.invalidColumns[i] || this.invalidSubMatrices[i]) {
				matrixIsValid = false;
			}
			i++;
		}
		return matrixIsValid;
	}
	
	/*
	 * Returns an array of valid choices for cell [row, column]. Returns empty array if 
	 * there are no valid choices.
	 */
	function computeChoicesFor(row, column) {
		if (this.values[row][column] === 0) {
			// This element is blank so construct an Array with the valid
			// remaining choices
			var choices = [];
			for ( var i = 1; i < this.matrixSize + 1; i++) {
				var validChoice = !this.rowContents[row][i] && !this.columnContents[column][i] && !this.subMatrixContents[getSubMatrixFor.call(this,
								row, column)][i];
				if (validChoice) {
					choices.push(i);
				}
			}
			this.choicesRemainingForEmptyElement[row][column] = choices;
			// Now if this number of choices is a new minimum, set the
			// fields which hold the row and column indices
			// of this element
			if (this.emptyElementWithFewestRemainingChoicesColumn < 0) {
				this.emptyElementWithFewestRemainingChoicesRow = row;
				this.emptyElementWithFewestRemainingChoicesColumn = column;
			} else {
				if (this.choicesRemainingForEmptyElement[this.emptyElementWithFewestRemainingChoicesRow][this.emptyElementWithFewestRemainingChoicesColumn].length > choices.length) {
					this.emptyElementWithFewestRemainingChoicesRow = row;
					this.emptyElementWithFewestRemainingChoicesColumn = column;
				}
			}
		} else {
			// Else, element has a value, so it has no possible
			// remaining choices
			this.choicesRemainingForEmptyElement[row][column] = [];
		}
	}
	
	/*
	 * Whenever a matrix element changes, this method is called to validate the
	 * resulting matrix. It marks invalid cells, and tracks which currently
	 * empty element can take the fewest values row - row index of changed
	 * element column - column index of changed element
	 */
	function validateMatrix(row, column) {
		var rowValues = [];
		initializeArray(rowValues, this.matrixSize + 1, false);
		this.invalidRows[row] = false;
		// Check for invalid rows. Loop through each column in this row.
		for ( var iColumn = 0; iColumn < this.matrixSize; iColumn++) {
			// If the element has a value and we've already seen this value in
			// this row,
			// indicate an error in this row.
			if (this.values[row][iColumn] > 0) {
				if (rowValues[this.values[row][iColumn]]) {
					this.invalidRows[row] = true;
				}
				// Indicate that we've seen this value in this row.
				rowValues[this.values[row][iColumn]] = true;
			}
		}
		this.rowContents[row] = rowValues;

		var columnValues = [];
		initializeArray(columnValues, this.matrixSize + 1, false);
		this.invalidColumns[column] = false;
		// Check for invalid columns. Loop through each row in this column.
		for ( var iRow = 0; iRow < this.matrixSize; iRow++) {
			// If the element has a value and we've already seen this value in
			// this column,
			// indicate an error in this column.
			if (this.values[iRow][column] > 0) {
				if (columnValues[this.values[iRow][column]]) {
					this.invalidColumns[column] = true;
				}
				// Indicate that we've seen this value in this column.
				columnValues[this.values[iRow][column]] = true;
			}
		}
		this.columnContents[column] = columnValues;

		var subMatrixValues = [];
		initializeArray(subMatrixValues, this.matrixSize + 1, false);
		var thisSubMatrix = getSubMatrixFor.call(this, row, column);
		this.invalidSubMatrices[thisSubMatrix] = false;
		// Check for invalid submatrices. Loop through each element in the
		// submatrix to which
		// element[row, column] belongs.
		var startingRow = getUpperLeftForSubMatrix.call(this, thisSubMatrix)[0];
		var startingColumn = getUpperLeftForSubMatrix.call(this, thisSubMatrix)[1];
		for ( iRow = startingRow; iRow < startingRow + this.subMatrixSize; iRow++) {
			for ( iColumn = startingColumn; iColumn < startingColumn + this.subMatrixSize; iColumn++) {
				// If the element has a value and we've already seen this value
				// in this submatrix,
				// indicate an error in this submatrix.
				if (this.values[iRow][iColumn] > 0) {
					if (subMatrixValues[this.values[iRow][iColumn]]) {
						this.invalidSubMatrices[thisSubMatrix] = true;
					}
					// Indicate that we've seen this value in this submatrix.
					subMatrixValues[this.values[iRow][iColumn]] = true;
				}
			}
		}
		this.subMatrixContents[getSubMatrixFor.call(this, row, column)] = subMatrixValues;

		// Iterate through the entire matrix
		// calculating valid choices for empty elements.
		this.emptyElementWithFewestRemainingChoicesRow = -1;
		this.emptyElementWithFewestRemainingChoicesColumn = -1;
		for (iRow = 0; iRow < this.matrixSize; iRow++) {
		for (iColumn = 0; iColumn < this.matrixSize; iColumn++) {
				computeChoicesFor.call(this, iRow, iColumn);
			}
		}

	}
	
	function setInputFromMatrix(inputs) {
		for ( var i = 0; i < this.matrixSize; i++) {
			for ( var i2 = 0; i2 < this.matrixSize; i2++) {
				if (inputs[i][i2] !== 0) {
					this.values[i][i2] = inputs[i][i2];
					this.inputElements[i][i2] = true;
				}
			}
		}
		for (i = 0; i < this.matrixSize; i++) {
			for (var j = 0; j < this.matrixSize; j++) {
				validateMatrix.call(this, i, j);
			}
		}
	}

	/*
	 * Takes a JSON object as input e.g. 9x9 matrix and uses it to set the
	 * initial matrix to solve.
	 */

	this.setInput = function(jsonMatrix) {
		var inputs = JSON.parse(jsonMatrix);
		var valid = true;
		if (inputs.length === this.matrixSize) {
			var row = 0;
			while (valid && row < this.matrixSize) {
				if (inputs[row].length !== this.matrixSize) {
					valid = false;
				} else {
					for ( var column = 0; column < this.matrixSize; column++) {
						if (typeof inputs[row][column] !== 'number' || inputs[row][column] < 0 || inputs[row][column] > this.matrixSize) {
							valid = false;
						}
					}
					row++;
				}
			}
		} else {
			valid = false;
		}
		if (valid) {
			setInputFromMatrix.call(this, inputs);
			if (!isMatrixValid.call(this)) {
				return false;
			} else {
				return true;
			}
		} else {
			return false;
		}

	};

	/*
	 * Returns whether or not a solution has been generated for the puzzle.
	 */
	function isSolutionKnown() {
		return this.solutionKnown;
	}

	function isMatrixComplete() {
		var complete = true;
		var row = 0;
		while (complete && row < this.matrixSize) {
			var integer = 1;
			while (complete && integer < this.matrixSize + 1) {
				if (this.rowContents[row][integer] > 0) {
					integer++;
				} else {
					complete = false;
				}
			}
			row++;
		}
		return complete;
	}

	/*
	 * Solves the matrix recursively starting with the element (row,column). The
	 * solution attempts are contained in solutionMatrix.
	 * 
	 * row - Row of element to start solution calculation with column - Column
	 * of element to start solution calculation with
	 */
	function computeSolution() {
		if (isMatrixComplete.call(this) && isMatrixValid.call(this)) {
			this.solutionKnown = true;
			return;
		} else {
			var currentRow = this.emptyElementWithFewestRemainingChoicesRow;
			var currentColumn = this.emptyElementWithFewestRemainingChoicesColumn;
			// document.writeln("<br>" + currentRow + ", " + currentColumn);
			if (this.choicesRemainingForEmptyElement[currentRow][currentColumn].length > 0) {
				// copy array
				var remainingChoices = this.choicesRemainingForEmptyElement[currentRow][currentColumn].slice(0);
				while (remainingChoices.length > 0 && !this.solutionKnown) {
					var nextValueToTry = remainingChoices.pop();
					// document.writeln("<br> trying " + nextValueToTry + " at [" + currentRow + "," + currentColumn + "]");
					this.values[currentRow][currentColumn] = nextValueToTry;
					validateMatrix.call(this, currentRow, currentColumn);
					computeSolution.call(this);
					if (!this.solutionKnown) {
						// document.writeln("<br> clearing " + currentRow + "," + currentColumn + "]");
						this.values[currentRow][currentColumn] = 0;
						validateMatrix.call(this, currentRow, currentColumn);
					}
				}
			}
		}
	}
	/*
	 * Solves the puzzle starting with the first element. The solution is
	 * contained in solutionMatrix.
	 * 
	 * return true or false indicating whether or not the puzzle was solved.
	 */
	this.computeSolutionFromInputs = function() {
		this.solutionMatrix = new SudokuPuzzle(this.matrixSize);
		setInputFromMatrix.call(this.solutionMatrix, this.values);
		computeSolution.call(this.solutionMatrix);
		if (isSolutionKnown.call(this.solutionMatrix)) {
			this.solutionKnown = true;
			return true;
		} else {
			return false;
		}
	};

	/*
	 * Sets a value in values. This results in a call to validateMatrix() which
	 * which updates internal bookkeeping
	 * 
	 * row - Row of element to set column - Column of element to set value -
	 * Value to set
	 */
	function setValueAt(row, column, value) {
		this.values[row][column] = value;
		validateMatrix.call(this, row, column);
	}

	/*
	 * Clears a value in values. This results in a call to validateMatrix()
	 * which which updates internal bookkeeping
	 * 
	 * row - Row of element to set column - Column of element to set
	 */
	function clearValueAt(row, column) {
		this.values[row][column] = 0;
		validateMatrix.call(this, row, column);
	}
	
	this.getSolutionMatrix = function() {
		return this.solutionMatrix.values;
	};
	
		/*
	 * matrixSize is the size of the overall puzzle matrix. subMatrixSize is 1/3
	 * of the matrixSize.
	 */
	this.matrixSize = size;
	this.subMatrixSize = this.matrixSize / 3;
	this.solutionMatrix = null;
	this.subMatrixIndex = [];
	for ( var row = 0; row < this.matrixSize; row++) {
		this.subMatrixIndex[row] = [];
		for ( var column = 0; column < this.matrixSize; column++) {
			this.subMatrixIndex[row][column] = row - (row % this.subMatrixSize) + (column - (column % this.subMatrixSize)) / 3;
		}
	}
	/*
	 * Array of Arrays that contains the puzzle values. 0 indicates an element
	 * has no value. inputElements indicates which are input (initial) values.
	 */
	this.values = [];
	for ( var i = 0; i < this.matrixSize; i++) {
		this.values[i] = [];
		initializeArray(this.values[i], this.matrixSize, 0);
	}

	/*
	 * Array of Arrays that define if a row, column, and submatrix contains a
	 * value. The size of the element Arrays are are matrixSize + 1. The
	 * submatrices are numbered starting in the upper left, moving across the
	 * columns, and then down to the next row. For example, in a 9x9 puzzle, if
	 * element [0,4] contains a "9". Then rowContents[0][9] == true,
	 * columnContents[4][9] == true, and submatrixContents[3][9] == true.
	 * getSubMatrixFor(i, i) returns the submatrix index for a given row and
	 * column.
	 */
	this.rowContents = [];
	this.columnContents = [];
	this.subMatrixContents = [];
	for (i = 0; i < this.matrixSize; i++) {
		this.rowContents[i] = [];
		initializeArray(this.rowContents[i], this.matrixSize + 1, false);
		this.columnContents[i] = [];
		initializeArray(this.columnContents[i], this.matrixSize + 1, false);
		this.subMatrixContents[i] = [];
		initializeArray(this.subMatrixContents[i], this.matrixSize + 1, false);
	}

	/*
	 * Variables to indicate which rows, columns, and submatrices have errors.
	 * E.g. invalidRows.[2] == true means there is an error in the third row.
	 */
	this.invalidRows = [];
	initializeArray(this.invalidRows, this.matrixSize, false);
	this.invalidColumns = [];
	initializeArray(this.invalidColumns, this.matrixSize, false);
	this.invalidSubMatrices = [];
	initializeArray(this.invalidSubMatrices, this.matrixSize, false);

	/*
	 * matrix (Array of Arrays) of Arrays that indicate for each element in the
	 * matrix, what are the valid values that element can contain. E.g.
	 * choicesRemainingForEmptyElement[1][2] == an Array of numbers that
	 * represent the valid choices remaining for that element.
	 */
	this.choicesRemainingForEmptyElement = [];
	for (i = 0; i < this.matrixSize; i++) {
		this.choicesRemainingForEmptyElement[i] = [];
		for ( var i2 = 0; i2 < this.matrixSize; i2++) {
			var choices = [];
			for ( var i3 = 0; i3 < this.matrixSize; i3++) {
				choices[i3] = i3 + 1;
			}
			this.choicesRemainingForEmptyElement[i][i2] = choices;
		}
	}

	/*
	 * These two variables contain the row and column of the empty matrix
	 * element that has the least number of remaining valid values. If more than
	 * one empty element have the same number of fewest choices, these variables
	 * refer to the first one encountered.
	 */
	this.emptyElementWithFewestRemainingChoicesRow = 0;
	this.emptyElementWithFewestRemainingChoicesColumn = 0;

	/*
	 * Array of Arrays that indicates which elements are input elements. That is
	 * which elements had defined values when lockInputValues was invoked.
	 */
	this.inputElements = [];
	for (i = 0; i < this.matrixSize; i++) {
		this.inputElements[i] = [];
		initializeArray(this.inputElements[i], this.matrixSize, false);
	}

	/*
	 * Indicates a solution exists for the puzzle. The solution is defined in
	 * {@link #solutionMatrix}.
	 */
	this.solutionKnown = false;
}
