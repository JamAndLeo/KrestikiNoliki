package view

import model.GridItem

interface View {

    fun showText(text: String)

    fun showLine()

    fun showGameField(field: MutableList<MutableList<GridItem>>)
}