package game

import field.FieldHolder
import model.GridItem
import repository.RepositoryImpl
import view.View
import java.lang.Exception
import kotlin.random.Random

class GameImpl(
    private val view: View,
    private val fieldHolder: FieldHolder,
    private val repositoryImpl: RepositoryImpl,
) : Game {

    override fun play(): Boolean {
        fieldHolder.initializeField()// главное хранилище данных
        showGameField() //пропечатали поле
        showRulesOfGame() // пропечатали правило
        val firstPlayer: GridItem = getFirstPlayer() //получили кто на Х
        view.setSides(firstPlayer) //отпрвили во вью кто Х а кто О

        return startGame(firstPlayer) //цикл матча
    }

    private fun startGame(firstPlayer: GridItem): Boolean {
        // счетчик очередности хода, если 1 то ходит PLAYER, 0 - ходит компьютер
        // избавились от счетчика очередности хода
        var gameProgress = 0

        // далее (!) цикл матча
        while (true) {
            makeTurn(firstPlayer)
            gameProgress++
            var winner = fieldHolder.getWinner()
            if (winner != null) {
                showWinnerText(winner, gameProgress)
                repositoryImpl.saveInformation(winner)
                break
            }

            if (firstPlayer == GridItem.Computer)
                makeTurn(GridItem.Player)
            else makeTurn(GridItem.Computer)
            gameProgress++
            winner = fieldHolder.getWinner()
            if (winner != null) {
                showWinnerText(winner, gameProgress)
                repositoryImpl.saveInformation(winner)
                break
            }
        }


        //выбор еще одной игры
        view.showLine()
        view.showText("Может желаете ещё кого-нибудь выпотрошить? да/нет")

        return when (readLine()?.trim()?.replaceFirstChar
        { it.lowercase() }) {
            "да" -> {
                true
            }

            "нет" -> {
                view.showText("Ну что же, как подлечитесь обязательно возвращайтесь!")
                false
            }

            else -> {
                view.showText("Вы говорите что-то совершенно не внятное, кажется вам выбили зубы в схватке, возвращатесь когда подлечитесь!")
                false
            }
        }
    }

    private fun showWinnerText(player: GridItem, gameProgress: Int) {
        if (player == GridItem.Player)
            view.showText("В непримеримой борьбе за объем выпущенной крови на ходу №$gameProgress побеждает PLAYER!\nПобедитель, заберите свои золотые щепцы для выдирания ногтей. ")
        else view.showText("На ходу №$gameProgress победу одерживает искусственный интеллект COMPUTER!")
    }

    private fun makeTurn(player: GridItem) {
        if (player == GridItem.Player)
            playerMove()
        else computerMove()
        showGameField()
        view.showLine()
    }

    private fun showGameField() {
        view.showGameField(fieldHolder.getField())
    }

    private fun getFirstPlayer(): GridItem {
        view.showText("КРИБЛИ-КРАБЛИ-…(кто-нибудь, уберите отработанный материал с гильотины)..-БУМС:")
        val first = if (Random.nextBoolean())
            GridItem.Player
        else GridItem.Computer

        view.showText("На дыбах ХХ сегодня сражается и ходит первым: $first")
        view.showText("Да начнуться пытки!!! Делайте свой первый ход!")
        return first
    }

    private fun computerMove() {
        val field = fieldHolder.getField()

        val coordinatesImpact: MutableSet<String> = mutableSetOf() // key - строка, value - столбец
        val coordinatesPlayer: MutableSet<String> = mutableSetOf()
        val critImpactComp: MutableSet<String> = mutableSetOf()
        val critImpactPlayer: MutableSet<String> = mutableSetOf()
        // пока фишки стоят по одному
        for (it in field) {
            it.indices.forEach { ind ->
                if (it[ind] == GridItem.Computer) {
                    coordinatesImpact.addAll(scanEmptyAI(field, field.indexOf(it), ind))
                } else if (it[ind] == GridItem.Player) {
                    coordinatesPlayer.addAll(scanEmptyAI(field, field.indexOf(it), ind))
                }
            }
        }
        // выявлем критическии позиции, когда уже по двое
        for (it in field) {
            it.indices.forEach { ind ->
                if (it[ind] == GridItem.Computer) {
                    critImpactComp.addAll(scanCritEmptyAI(field, field.indexOf(it), ind))
                } else if (it[ind] == GridItem.Player) {
                    critImpactPlayer.addAll(scanCritEmptyAI(field, field.indexOf(it), ind))
                }
            }
        }
        //координаты удара
        val impact = if (critImpactComp.size != 0) {
            critImpactComp.random()
        } else if (critImpactPlayer.size != 0) {
            critImpactPlayer.random()
        } else if (coordinatesImpact.size != 0) {
            coordinatesImpact.random()
        } else if (coordinatesPlayer.isNotEmpty()) {
            coordinatesPlayer.random()
        } else {
            Random.nextInt(fieldHolder.getFieldSize()).toString() + Random.nextInt(fieldHolder.getFieldSize())
                .toString()
        }

        val first = impact[0].toString().toInt()
        val second = if (impact.length == 2) {
            impact[1].toString().toInt()
        } else {
            (impact[1].toString() + impact[2].toString()).toInt()
        }
        fieldHolder.setItem(second to first, GridItem.Computer)
        view.showText("COMPUTER сделал свой ход!")
    }

    // собиарает свободные поля среди спареннх позиций, что можно еще прописать в будущем: 1) выявить и законтрить позицию между двумя позициями противника
    private fun scanCritEmptyAI(field: MutableList<MutableList<GridItem?>>, hor: Int, vert: Int): MutableSet<String> {
        val coordinates: MutableSet<String> = mutableSetOf()

        // по горизонтали
        if (((vert + 3) <= field.size) &&
            field[hor][vert] == field[hor][vert + 1] &&
            field[hor][vert + 2] == null
        ) coordinates.add(hor.toString() + (vert + 2).toString())

        if (((vert + 2) <= field.size) && vert > 0 &&
            field[hor][vert] == field[hor][vert + 1] &&
            field[hor][vert - 1] == null
        ) coordinates.add(hor.toString() + (vert - 1).toString())

        // по вертикали
        if (hor + 3 <= field.size &&
            field[hor][vert] == field[hor + 1][vert] &&
            field[hor + 2][vert] == null
        ) coordinates.add((hor + 2).toString() + (vert).toString())

        if (hor + 2 <= field.size && hor > 0 &&
            field[hor][vert] == field[hor + 1][vert] &&
            field[hor - 1][vert] == null
        ) coordinates.add((hor - 1).toString() + (vert).toString())

        // по диагонали вправо
        if (hor + 3 <= field.size &&
            (vert + 3) <= field.size &&
            field[hor][vert] == field[hor + 1][vert + 1] &&
            field[hor + 2][vert + 2] == null
        ) coordinates.add((hor + 2).toString() + (vert + 2).toString())

        if (hor + 2 <= field.size &&
            (vert + 2) <= field.size && hor > 0 && vert > 0 &&
            field[hor][vert] == field[hor + 1][vert + 1] &&
            field[hor - 1][vert - 1] == null
        ) coordinates.add((hor - 1).toString() + (vert - 1).toString())

        // по диагонали влево
        if (hor + 3 <= field.size &&
            vert >= 2 &&
            field[hor][vert] == field[hor + 1][vert - 1] &&
            field[hor + 2][vert - 2] == null
        ) coordinates.add((hor + 2).toString() + (vert - 2).toString())

        if (hor > 0 && (vert + 2) <= field.size && hor + 2 <= field.size &&
            vert >= 1 &&
            field[hor][vert] == field[hor + 1][vert - 1] &&
            field[hor - 1][vert + 1] == null
        ) coordinates.add((hor - 1).toString() + (vert + 1).toString())
        return coordinates
    }

    private fun scanEmptyAI(
        //собирает все свободные поля
        field: MutableList<MutableList<GridItem?>>,
        hor: Int,
        ver: Int,
    ): MutableSet<String> { //согласен, выглядет безобразно
        val coordinates: MutableSet<String> = mutableSetOf()
        if (hor != 0) {         //вверх
            if (field[hor - 1][ver] == null) coordinates.add((hor - 1).toString() + ver.toString())
            if (ver > 0 && field[hor - 1][ver - 1] == null) coordinates.add((hor - 1).toString() + (ver - 1).toString())
            if (ver + 1 < field.size && field[hor - 1][ver + 1] == null) coordinates.add((hor - 1).toString() + (ver + 1).toString())
        }
        if (hor + 1 < field.size) {        //вниз
            if (field[hor + 1][ver] == null) coordinates.add((hor + 1).toString() + ver.toString())
            if (ver > 0 && field[hor + 1][ver - 1] == null) coordinates.add((hor + 1).toString() + (ver - 1).toString())
            if (ver + 1 < field.size && field[hor + 1][ver + 1] == null) coordinates.add((hor + 1).toString() + (ver + 1).toString())
        }
        if (ver > 0 && field[ver - 1][hor] == null) coordinates.add(hor.toString() + (ver - 1).toString())     //влево
        if (ver + 1 < field.size && field[ver + 1][hor] == null) coordinates.add(hor.toString() + (ver + 2).toString())  //вправо
        return coordinates
    }

    private fun showRulesOfGame() { // просто правила игры
        view.showText("Давай немного расскажу о правилах игры. Как ты можешь видеть на нашем поле \nстолбцы и строки пронумерованы, когда придёт время твоего хода введи \nкоординаты установки пыточного оборудования в виде двузначного числа: \n(!)первая цифра - столбец \n(!) вторая цифра - строка\nПервым ходит всегда игрок на ХХ-дыбах. \nКому какая достанется сторона мы сейчас выберем случайно.")
    }

    private fun pause() = Thread.sleep(1500) // пауза если вдруг понядобиться

    private fun playerMove() {
        view.showText("PLAYER твой выбор:")

        val coordinates = getCoordinatesFromPlayer()
        fieldHolder.setItem(coordinates, GridItem.Player)
        view.showText("Наш боец PLAYER в кровавом углу ринга сделал свой ход")
    }

    private fun getCoordinatesFromPlayer(): Pair<Int, Int> {
        while (true) {
            val playerText = readln().replace(" ", "")
            if (playerText.length != 2) {
                view.showText("(!) Введено недопустимое количество символов (!)\nПовторите ввод:")
                continue
            }
            try {
                val first = playerText[0].toString().toInt() - 1
                val second = playerText[1].toString().toInt() - 1
                if (first > fieldHolder.getFieldSize() || second > fieldHolder.getFieldSize()) {
                    view.showText("(!) Введены недопустимые символы (!)\nПовторите ввод:")
                } else if (fieldHolder.isCoordinatesEmpty(first, second)) {
                    return first to second
                } else {
                    view.showText("(!) УПС... Кажется тут уже стоит какой-то пыточный аппарат...\nПовторите ввод:")
                }

            } catch (e: Exception) {
                view.showText("(!) Введены недопустимые символы (!)\nПовторите ввод:")
            }
        }
    }

// 1 2 3 4
// 0 1 2 3
//0123456789
//|x| | | |1  0
//----------
//|x|o| | |2  1
//----------
//| | | | |3  2
//----------
//| | | | |4  3
//----------
}