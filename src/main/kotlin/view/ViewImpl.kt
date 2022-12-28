package view

import model.GridItem

class ViewImpl : View {
    private var PLAYER_GRID = "X";
    private var COMPUTER_GRID = "O";

    override fun showText(text: String) {
        println(text)
    }

    override fun showLine(ln: Int) {
        repeat(ln) {
            print("-")
        }
        println("-")
    }

    override fun showGameField(field: MutableList<MutableList<GridItem?>>) {
        for (n in 1..field.size) { // выводит верхнюю линию
            print(" $n")
        }
        println()

        for (n in field.indices) { // печать игровой зоны
            field[n].forEach {
                print("|")
                printSide(it)
            }.also { println("|${n + 1}") }
        }
    }

    private fun printSide(item: GridItem?) {
        when (item) {
            GridItem.Computer -> print(COMPUTER_GRID)
            GridItem.Player -> print(PLAYER_GRID)
            else -> print(" ")
        }
    }

    override fun setSides(firstPlayer: GridItem) {
        if (firstPlayer == GridItem.Player) {
            PLAYER_GRID = "X"
            COMPUTER_GRID = "O"
        } else {
            PLAYER_GRID = "O"
            COMPUTER_GRID = "X"
        }
    }
}