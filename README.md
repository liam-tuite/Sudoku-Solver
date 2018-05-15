This is a program to solve sudoku puzzles. The user is presented with a grid and enters all of the known values, then hits the "Solve" button. The program then fills in the remaining cells in the grid.

I put this program together as a fun personal challenge. The program first uses a simple elimination process to remove from each cell the possibility that a value fits if that value is already in the corresponding row, column, or block. Next, each container (column, row and block) is scanned for the distribution of possible values of the Cells within that container. If any value has only one possible Cell within a container, that Cell takes the value. After repeating these two simple algorithms until there are no more changes, the program performs a brute force algorithm to solve the rest of the grid. I will continue working on this to include more complex techniques for deducing cell values, which would be an improvement in terms of computability over brute force. Currently it solves most 9x9 and 16x16 cells quite quickly.

The program has a debugging mode which can be accessed by setting the static boolean DEBUGGING_MODE in the SudokuSolver class to 'true'. If this is enabled, the program goes straight to solving an internally represented sudoku grid. This is constructed from a static final array of chars. When running the program (in any mode) there is a "Print Data" button. When this is pressed, the program prints the chars in a comma separated sequence to the standard output. This output can be copied and pasted to the DEBUG_CELLS array in order to switch the internal scenario that is automatically solved in debug mode.

Another final static boolean, SUPER_SUDOKU, determines whether or not the grid will be 16x16. If this is set to false, a standard 9x9 grid is used. In future, I plan on adding user option to switch between different grid sizes in runtime.

The GUI using a standard 9x9 grid looks like this (after the user has entered the known values):

![GUI](https://github.com/liam-tuite/Sudoku-Solver/blob/master/SudokuSolver%20GUI.png)