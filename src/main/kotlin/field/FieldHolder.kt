package field

import model.GridItem
import view.View
// содержит все операции над полем: создание поле, подмена позиции, проверка ячеек, скан на победителя
class FieldHolder(
    private val view: View
) {
    private var gameField: MutableList<MutableList<GridItem?>> = mutableListOf()

    fun initializeField() {
        val fieldSize = enterSizeField()
        gameField = createGameField(fieldSize)
    }

    private fun enterSizeField(): Int { //ввод размера игрового поля
        view.showText("Для начала давай оперделимся с размером пыточных застенков.\nСторона поля может быть от 3 до 9.\nВведи значение соразмерно своим амбициям:")
        while (true) {
            val x = readLine()
            val validSize = arrayOf("3", "4", "5", "6", "7", "8", "9")
            if (x !in validSize) {
                view.showText("(!) кажется вы ввели недопустимый размер поля (!)")
            } else {
                view.showText("Договорились Малюта Скуратов. Ты будешь драться в казиматах $x на $x!")
                return x!!.toInt()
            }
        }
    }

    private fun createGameField(fieldSize: Int): MutableList<MutableList<GridItem?>> { // создает поле для игры, двухуровневый массив создаваем через конструктор
        val fieldG = MutableList<MutableList<GridItem?>>(fieldSize) {
            MutableList<GridItem?>(fieldSize) { null }
        }
        return fieldG
    }

    fun getField(): MutableList<MutableList<GridItem?>> {
        return gameField
    }

    fun getFieldSize() = gameField.size

    fun isCoordinatesEmpty(first: Int, second: Int): Boolean { // проверяет пустое ли место, наверное можно использовать Pair
        return getField()[second][first] == null
    }

    fun setItem(coordinates: Pair<Int, Int>, player: GridItem) { // подменяет позицию в игровом поле
        getField()[coordinates.second][coordinates.first] = player
    }

    fun getWinner(): GridItem? {
        for (it in gameField.indices) {
            gameField[it].indices.forEach { ind ->
                if (gameField[it][ind] != null) { //проеверяет любую не пусту позицию
                    val isWin = checkAround(it, ind) //можно впринципи вставить в if напрямую, без переменной
                    if (isWin)
                        return gameField[it][ind]
                }
            }
        }
        return null
    }

    private fun checkAround(hor: Int, vert: Int): Boolean {
        val field = gameField //а можно без локальной переменной? напрямую обращаться в условиях к gameField?
        val player = field[hor][vert]
        // по горизонтали
        if (((vert + 3) <= field.size) &&
            (player == field[hor][vert + 1]) &&
            (player == field[hor][vert + 2])
        ) {
            return true
            // по вертикали
        } else if (hor + 3 <= field.size &&
            player == field[hor + 1][vert] &&
            player == field[hor + 2][vert]
        ) {
            return true
            // по диагонали вправо
        } else if (hor + 3 <= field.size &&
            (vert + 3) <= field.size &&
            player == field[hor + 1][vert + 1] &&
            player == field[hor + 2][vert + 2]
        ) {
            return true
            // по диагонали влево
        } else if (hor + 3 <= field.size &&
            vert >= 2 &&
            player == field[hor + 1][vert - 1] &&
            player == field[hor + 2][vert - 2]
        ) {
            return true
        }

        return false
    }
}