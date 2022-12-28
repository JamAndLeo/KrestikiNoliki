package view

import model.GridItem

interface View {

    fun showText(text: String)

    fun showLine(ln: Int = 20)

    fun showGameField(field: MutableList<MutableList<GridItem?>>)

    fun setSides(firstPlayer: GridItem)
}