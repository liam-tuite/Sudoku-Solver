This is a program to solve sudoku puzzles. The user is presented with a grid and enters all of the known values, then hits the "Solve" button. The program then fills in the remaining cells in the grid.

I put this program together as a fun personal challenge. The program first uses a simple elimination process to remove from each cell the possibility that a value fits if that value is in the corresponding row, column, or square. Then the program performs a brute force algorithm to solve the rest of the grid. I will continue working on this to include more complex techniques for deducing cell values, which would be an improvement in terms of computability over brute force. I also plan on extending the program to work on not only 9x9 grids but also 16x16 grids.

The program has a debugging mode which can be accessed by setting the static boolean DEBUGGING_MODE in the SudokuSolver class to 'true'. If this is enabled, the program goes straight to solving an internally represented sudoku grid. This is constructed from a static final array of chars. When running the program (in any mode) there is a "Print Data" button. When this is pressed, the program prints the chars in a comma separated sequence to the standard output. This output can be copied and pasted to the DEBUG_CELLS array in order to switch the internal scenario that is automatically solved in debug mode.

The GUI looks like this (after the user has entered the known values):

![GUI](https://github.com/liam-tuite/Sudoku-Solver/blob/master/SudokuSolver%20GUI.png)