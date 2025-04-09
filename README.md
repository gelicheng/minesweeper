# Minesweeper Game

This is a simple implementation of the **Minesweeper** game in Kotlin. It allows users to play the classic game with a custom grid size and number of mines.

## Prerequisites

Before running this project, you need to have the following installed:

- **Kotlin**: [Install Kotlin](https://kotlinlang.org/docs/tutorials/command-line.html)
- **JDK**: Java Development Kit (JDK 11 or above)
  - You can install it from [AdoptOpenJDK](https://adoptopenjdk.net/).

## Setup Instructions

1. **Clone the repository** to your local machine:
   ```bash
   git clone https://github.com/gelicheng/minesweeper.git
   
2. **Navigate to your project folder**
   ```bash
   cd your-repository-name
   
3. **Compile and run the project using Kotlin**
   ```bash
   kotlinc Main.kt -include-runtime -d Minesweeper.jar
   java -jar Minesweeper.jar

## How to Play
1. The game will prompt you to enter the grid width, heignt, and number of mines.
2. Once the grid is generated, choose to reveal or flag the remaining cells. Use the format:
   - R row col to reveal a cell at the specified row and column.
   - F row col to flag a cell. 
3. Numbers revealed in the cell indicate how many mines are in its neighboring cells. 
4. The goal is to avoid revealing on the cels that contain mines. The game ends when all non-mine cells are revealed, or whne you reveal a mine.

## Additional Notes 
- The game provides a command-line interface, so you will interact with it through your terminal or command prompt. 
