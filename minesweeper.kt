import kotlin.random.Random 
import java.util.Scanner

fun generateGrid(height: Int, width: Int): Array<Array<Char>> {
    //create a 2d array 
    return Array(height) {Array(width) {'.'}}
}

fun mineLocation(grid: Array<Array<Char>>, numMines: Int, firstX: Int, firstY: Int) {
    // val height = grid.size
    // val width = grid[0].size
    var minesPlaced = 0

    //create protected cells 
    val protectedCells = mutableSetOf<Pair<Int, Int>>()
    val directions = listOf(
        -1 to -1, -1 to 0, -1 to 1,
        0 to 1, 0 to -1, 
        1 to -1, 1 to 0, 1 to 1
    )

    //add first click and neighbors are protected 
    for ((dx, dy) in directions) {
        val nx = firstX + dx
        val ny = firstY + dy
        if (nx in grid.indices && ny in grid[0].indices) {
            protectedCells.add(Pair(nx, ny))
        }
    }

    //create list of all possible coordinates
    val possibleCoordinates = mutableListOf<Pair<Int, Int>>()
    for (x in grid.indices) {
        for (y in grid[0].indices) {
            val pair = Pair(x, y)
            if (pair !in protectedCells) {
                possibleCoordinates.add(pair)
            }
        }
    }

    //check space left for mines 
    val actualMines = minOf(numMines, possibleCoordinates.size)
    if (actualMines < numMines) {
        println("Warning: Requested $numMines mines, but only $actualMines can be placed.")
    }
    
    possibleCoordinates.shuffle()
    
    //place mines using shuffled list
    for (i in 0 until actualMines) {
        val (x, y) = possibleCoordinates[i]
        grid[x][y] = '*'
    }
}

fun printGrid(grid: Array<Array<Char>>, revealed: Array<Array<Boolean>>, flags: Array<Array<Boolean>>) {
    //adjust spacing for readability
    // val spacing = 3
    //print col no
    print("   ")
    for (col in grid[0].indices) {
        print(" ${col + 1} ".padStart(3))
    }
    println()

    //print row no
    for (row in grid.indices) {
        print("${row + 1}".padStart(2) + " ") //row no
        for (col in grid[0].indices) {
            when {
                flags[row][col] -> print(" F ".padStart(3))
                revealed[row][col] -> {
                    if (grid[row][col] == '.') {
                        //reveal empty
                        print("   ")
                    } else {
                        //reveal number
                        print(" ${grid[row][col]} ".padStart(3))
                    } 
                } 
                else -> print(" . ".padStart(3)) //not revealed
            }
        }
        //move to next row 
        println()
    }
} 

fun getValidInput(scanner: Scanner, prompt: String, min: Int, max: Int): Int {
    while (true) {
        print(prompt)
        val input = scanner.nextLine()
        try {
            val num = input.toInt()
            if (num in min..max) {
                return num 
            } else {
                println("Invalid input! Please enter a number between $min and $max.")
            }
        } catch (e: NumberFormatException) {
            println("Invalid input! Please enter a valid number.")
        }
    }
}

fun mineLogic(grid: Array<Array<Char>>) {
    val directions = listOf(
        -1 to -1, -1 to 0, -1 to 1, 
        0 to -1, 0 to 1, 
        1 to -1, 1 to 0, 1 to 1
    )

    for (x in grid.indices) {
        for (y in grid[0].indices) {
            if(grid[x][y] == '*') continue //skip all the mines

            var mineCount = 0

            for ((dx, dy) in directions) {
                val newX = x + dx
                val newY = y + dy
                if (newX in grid.indices && newY in grid[0].indices && grid[newX][newY] == '*') {
                    mineCount++
                }
            }
            if (mineCount > 0) {
                grid[x][y] = ('0' + mineCount)
            }
        }
    }
}

fun revealCell(grid: Array<Array<Char>>, revealed: Array<Array<Boolean>>, x: Int, y:Int, height: Int, width: Int): Boolean {
    //out of grid or alrdy revealed, do nothing 
    if(x < 0 || x >= height || y < 0 || y >= width || revealed[x][y] || grid[x][y] == 'F') return true

    //mark revealed cells 
    revealed[x][y] = true 

    //mine, game ends 
    if (grid[x][y] == '*') return false

    //number, reveal 
    if (grid[x][y] != '.') return true 

    //empty, reveal surrounding cells recursively 
    val directions = listOf(
        -1 to -1, -1 to 0, -1 to 1,
        0 to 1, 0 to -1, 
        1 to -1, 1 to 0, 1 to 1
    )

    for ((dx, dy) in directions) {
        val nx = x + dx
        val ny = y + dy
        if (nx in grid.indices && ny in grid[0].indices && !revealed[nx][ny]) {
            revealCell(grid, revealed, x + dx, y + dy, height, width)
        }
    }
    return true 
}

fun toggleFlag(flags: Array<Array<Boolean>>, revealed: Array<Array<Boolean>>, row: Int, col: Int) {
    if (revealed[row][col]) {
        println("Cannot place a flag on revealed cell!")
    } else {
        flags[row][col] = !flags[row][col]
    }
}

fun checkWin(grid: Array<Array<Char>>, revealed: Array<Array<Boolean>>, flags: Array<Array<Boolean>>): Boolean {
    var nonMinesRevealed = true 
    var minesFlagged = true 
    
    for (x in grid.indices) {
        for (y in grid[0].indices) {
            if (grid[x][y] != '*' && !revealed[x][y]) {
                nonMinesRevealed = false
                break
            }
        }
        if (!nonMinesRevealed) break
    }
    return nonMinesRevealed
}

fun revealAll(grid: Array<Array<Char>>, revealed: Array<Array<Boolean>>, flags: Array<Array<Boolean>>) {
    for (x in grid.indices) {
        for (y in grid[0].indices) {
            if (!flags[x][y]) {
                revealed[x][y] = true
            }
        }
    }
}

fun firstClick(grid: Array<Array<Char>>, firstX: Int, firstY: Int, totalMines: Int) {
    grid[firstX][firstY] = '.' //first click is empty

    val directions = listOf(
        -1 to -1, -1 to 0, -1 to 1,
        0 to 1, 0 to -1, 
        1 to -1, 1 to 0, 1 to 1
    )
    
    val height = grid.size 
    val width = grid[0].size 

    var minesRemoved = 0

    //clear neighboring mines
    for((dx, dy) in directions) {
        val nx = firstX + dx
        val ny = firstY + dy
        if (nx in grid.indices && ny in grid[0].indices) {
            if (grid[nx][ny] == '*') {
                grid[nx][ny] = '.' //remove mine
                minesRemoved++
            }
        }
    }

    //total mines should remain the same
    val random = java.util.Random()
    while (minesRemoved > 0) {
        val x = random.nextInt(height)
        val y = random.nextInt(width)

        //only if empty or not in safe zone
        if (grid[x][y] == '.' && (x !in firstX -1..firstX + 1 || y !in firstY - 1..firstY + 1)) {
            grid[x][y] = '*'
            minesRemoved--
        }
    }

    //clear old numbers 
    for (i in grid.indices) {
        for (j in grid[0].indices) {
            if (grid[i][j] != '*') {
                grid[i][j] = '.'
            }
        }
    }

    //recalculate after removing mines
    mineLogic(grid)
}

fun coutMines(grid: Array<Array<Char>>): Int {
    var count = 0 
    for (row in grid) {
        for (cell in row) {
            if (cell == '*') count++
        }
    }
    return count
}

fun checkGrid(height: Int, width: Int): Boolean {
    val totalCells = height * width
    return totalCells < 4
}

fun countRevealedCells(revealed: Array<Array<Boolean>>): Int {
    var count = 0
    for (row in revealed) {
        for (cell in row) {
            if (cell) count++
        }
    }
    return count
}

fun countSafeCells(grid: Array<Array<Char>>): Int {
    var count = 0
    for (row in grid) {
        for (cell in row) {
            if (cell != '*') count++
        }
    }
    return count
}

fun countFlaggesMines(grid: Array<Array<Char>>, flags: Array<Array<Boolean>>): Int {
    var count = 0
    for (x in grid.indices) {
        for (y in grid[0].indices) {
            if (grid[x][y] == '*' && flags[x][y]) count++
        }
    }
    return count
}

fun main() {
    val scanner = Scanner(System.`in`)

    //loop until grid size is valid 
    var height: Int 
    var width: Int 
    var validGrid = false

    do {
        width = getValidInput(scanner, "Enter grid width: ", 1, Int.MAX_VALUE)
        height = getValidInput(scanner, "Enter grid height: ", 1, Int.MAX_VALUE)

        if (checkGrid(height, width)) {
            println("Grid is too small for a proper game.")
            println("Enter a new grid size.")
        } else {
            validGrid = true 
        }
    } while (!validGrid)

    //calculate max mines 
    val totalCells = height * width 
    val safeArea = 9
    val maxSafeArea = minOf(safeArea, totalCells)
    val maxMines = totalCells - maxSafeArea

    if (maxMines <= 0) {
        println("Grid size is too small for a proper game.")
        println("Enter a new grid size.")
        main() //restart the game
        return 
    }

    //no of mines 
    val numMines = getValidInput(scanner, "Enter number of mines: ", 1, maxMines)

    //generate grid 
    val grid = generateGrid(height, width)

    //array of revealed cells 
    val revealed = Array(height) {Array(width) {false}}
    //array of flags
    val flags = Array(height) {Array(width) {false}}

    //first clicked cell (within grid bounds)
    val firstX = getValidInput(scanner, "Enter row (1 to ${height}): ", 1, height) - 1
    val firstY = getValidInput(scanner, "Enter column (1 to ${width}): ", 1, width) - 1

    //println("Grid size: ${height}x${width}, Mines: $numMines")
    //println("First clicked cell (row, column): (${firstX + 1}, ${firstY + 1})")
    
    //place mines 
    mineLocation(grid, numMines, firstX, firstY)
    //calculate mines
    mineLogic(grid)

    //check first cell is safe 
    val actualMines = coutMines(grid)
    println("Placed $actualMines mines on grid.")

    //check first cell is empty
    firstClick(grid, firstX, firstY, numMines)

    //recalculate mines
    mineLogic(grid)

    //reveal first cell
    revealCell(grid, revealed, firstX, firstY, height, width)

    //chck first move win
    if (checkWin(grid, revealed, flags)) {
        println("Congratulations! You won on the first move!")
        revealAll(grid, revealed, flags)
        printGrid(grid, revealed, flags)
        return
    }

    var gameOver = false 
    while (!gameOver) {
        // println("minesweeper grid")
        printGrid(grid, revealed, flags)

        print("Enter action: R row col (reveal), F row col (flag/unflag): ")
        val input = scanner.nextLine().split(" ")

        if (input.size != 3) {
            print("Invalid input format. Use 'R row col' or 'F row col'.")
            continue
        }

        val action = input[0].uppercase()

        //user input for cell to reveal
        val row = input[1].toIntOrNull()?.minus(1) ?: continue
        val col = input[2].toIntOrNull()?.minus(1) ?: continue

        if (row !in 0 until height || col !in 0 until width) {
            println("Invalid coordinates!")
            continue
        }

        when(action) {
            "R" -> {
                if (flags[row][col]) {
                    println("Cell is flagged. Unflag it first before revealing.")
                } else {
                    gameOver = !revealCell(grid, revealed, row, col, height, width)
                    if (gameOver) {
                        revealAll(grid, revealed, flags)
                        println("Game Over! You hit a mine.")
                        printGrid(grid, revealed, flags)
                        break
                    }
                    if (checkWin(grid, revealed, flags)) {
                        revealAll(grid, revealed, flags)
                        println("Congratulations! You won!")
                        printGrid(grid, revealed, flags)
                        break
                    }
                }
            }
            "F" -> {
                toggleFlag(flags, revealed, row, col)

                //track counts 
                val totalSafeCells = countSafeCells(grid)
                val revealedCells = countRevealedCells(revealed)
                val flaggesMines = countFlaggesMines(grid, flags)

                //check win after each action
                if (checkWin(grid, revealed, flags)) {
                    revealAll(grid, revealed, flags)
                    println("Congratulations! You won!")
                    printGrid(grid, revealed, flags)
                    gameOver = true
                    break
                }
            }
            else -> println("Invalid action. Use 'R' to reveal or 'F' to flag.")
        }
    }
}