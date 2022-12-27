package view

import model.GridItem

class ViewImpl : View {
    override fun showText(text: String) {
        println(text)
    }

    override fun showLine(ln: Int) {
        repeat(ln) {
            print("-")
        }
        println("-")
    }

    override fun showGameField(field: MutableList<MutableList<GridItem>>, chooseSides: Map<String, GridItem>) {
        for (n in 1..field.size) { // выводит верхнюю линию
            print(" $n")
        }
        println()

        for (n in field.indices) { // печать игровой зоны
            field[n].forEach {
                print(
                    "|${
                        when (it) {
                            GridItem.Computer -> if (chooseSides["X"] == GridItem.Computer) "X" else "O"
                            GridItem.Empty -> " "
                            GridItem.Player -> if (chooseSides["X"] == GridItem.Player) "X" else "O"
                        }
                    }"
                )
            }.also { println("|${n + 1}") }
        }
    }
}