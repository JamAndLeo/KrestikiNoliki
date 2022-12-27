package game

import model.GridItem
import view.View

class GameImpl(private val view: View) : Game {

    override fun play(): Boolean {
        val fieldSize = enterSizeField()
        var gameField = createGameField(fieldSize) // главное хранилище данных
        val chooseSides: MutableMap<String, GridItem> = mutableMapOf()
        view.showGameField(gameField, chooseSides)
        rulesOfGame()
        // далее рандомный выбор старта
        chooseSides.putAll(randomSideSelection())
        var switch: Int = if (chooseSides["X"] == GridItem.Player) 1 else 0
        // счетчик очередности хода, если 1 то ходит PLAYER, 0 - ходит компьютер
        var gameProgress = 0

        // первый ход
        if (switch == 1) {
            gameField = playerMove(gameField)
            view.showGameField(gameField, chooseSides)
            view.showText("Наш боец PLAYER в кровавом углу ринга сделал свой ход")
            view.showLine()
            switch = 0
            gameProgress = 1
        } else {
            gameField = computerFirstMove(gameField)
            view.showGameField(gameField, chooseSides)
            view.showText("COMPUTER сделал свой ход!")
            view.showLine()
            switch = 1
            gameProgress = 1
        }

        // далее (!) цикл матча
        while (true) {
            if (switch == 1) {
                gameField = playerMove(gameField)
                view.showGameField(gameField, chooseSides)
                view.showText("Наш боец PLAYER в кровавом углу ринга сделал свой ход")
                view.showLine()
                gameProgress++
                if (checkWin(gameField)) {
                    view.showText("В непримеримой борьбе за объем выпущенной крови на ходу №$gameProgress побеждает PLAYER!\nПобедитель, заберите свои золотые щепцы для выдирания ногтей. ")
                    break
                }
                switch = 0
            } else {
                gameField = computerMove(gameField)
                view.showGameField(gameField, chooseSides)
                view.showText("COMPUTER сделал свой ход!")
                view.showLine()
                gameProgress++
                if (checkWin(gameField)) {
                    view.showText("На ходу №$gameProgress победу одерживает искусственный интеллект COMPUTER!")
                    break
                }
                switch = 1
            }
        }
        //выбор еще одной игры
        view.showLine()
        view.showText("Может желаете ещё кого-нибудь выпотрошить? да/нет")

        return when (readLine()?.trim()?.replaceFirstChar { it.lowercase() }) {
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


    private fun computerMove(
        field: MutableList<MutableList<GridItem>>,
    ): MutableList<MutableList<GridItem>> {
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
        } else coordinatesPlayer.random()

        //ход
        field[impact[0].toString().toInt()][if (impact.length == 2) {
            impact[1].toString().toInt()
        } else {
            (impact[1].toString() + impact[2].toString()).toInt()
        }] = GridItem.Computer
        return field
    }

    // собиарает свободные поля среди спареннх позиций, что можно еще прописать в будущем: 1) выявить и законтрить позицию между двумя позициями противника
    private fun scanCritEmptyAI(field: MutableList<MutableList<GridItem>>, hor: Int, vert: Int): MutableSet<String> {
        val coordinates: MutableSet<String> = mutableSetOf()

        // по горизонтали
        if (((vert + 3) <= field.size) &&
            field[hor][vert] == field[hor][vert + 1] &&
            field[hor][vert + 2] == GridItem.Empty
        ) coordinates.add(hor.toString() + (vert + 2).toString())

        if (((vert + 2) <= field.size) && vert > 0 &&
            field[hor][vert] == field[hor][vert + 1] &&
            field[hor][vert - 1] == GridItem.Empty
        ) coordinates.add(hor.toString() + (vert - 1).toString())

        // по вертикали
        if (hor + 3 <= field.size &&
            field[hor][vert] == field[hor + 1][vert] &&
            field[hor + 2][vert] == GridItem.Empty
        ) coordinates.add((hor + 2).toString() + (vert).toString())

        if (hor + 2 <= field.size && hor > 0 &&
            field[hor][vert] == field[hor + 1][vert] &&
            field[hor - 1][vert] == GridItem.Empty
        ) coordinates.add((hor - 1).toString() + (vert).toString())

        // по диагонали вправо
        if (hor + 3 <= field.size &&
            (vert + 3) <= field.size &&
            field[hor][vert] == field[hor + 1][vert + 1] &&
            field[hor + 2][vert + 2] == GridItem.Empty
        ) coordinates.add((hor + 2).toString() + (vert + 2).toString())

        if (hor + 2 <= field.size &&
            (vert + 2) <= field.size && hor > 0 && vert > 0 &&
            field[hor][vert] == field[hor + 1][vert + 1] &&
            field[hor - 1][vert - 1] == GridItem.Empty
        ) coordinates.add((hor - 1).toString() + (vert - 1).toString())

        // по диагонали влево
        if (hor + 3 <= field.size &&
            vert >= 2 &&
            field[hor][vert] == field[hor + 1][vert - 1] &&
            field[hor + 2][vert - 2] == GridItem.Empty
        ) coordinates.add((hor + 2).toString() + (vert - 2).toString())

        if (hor > 0 && (vert + 2) <= field.size && hor + 2 <= field.size &&
            vert >= 1 &&
            field[hor][vert] == field[hor + 1][vert - 1] &&
            field[hor - 1][vert + 1] == GridItem.Empty
        ) coordinates.add((hor - 1).toString() + (vert + 1).toString())
        return coordinates
    }

    private fun scanEmptyAI(
        //собирает все свободные поля
        field: MutableList<MutableList<GridItem>>,
        hor: Int,
        ver: Int,
    ): MutableSet<String> { //согласен, выглядет безобразно
        val coordinates: MutableSet<String> = mutableSetOf()
        if (hor != 0) {         //вверх
            if (field[hor - 1][ver] == GridItem.Empty) coordinates.add((hor - 1).toString() + ver.toString())
            if (ver > 0 && field[hor - 1][ver - 1] == GridItem.Empty) coordinates.add((hor - 1).toString() + (ver - 1).toString())
            if (ver + 1 < field.size && field[hor - 1][ver + 1] == GridItem.Empty) coordinates.add((hor - 1).toString() + (ver + 1).toString())
        }
        if (hor + 1 < field.size) {        //вниз
            if (field[hor + 1][ver] == GridItem.Empty) coordinates.add((hor + 1).toString() + ver.toString())
            if (ver > 0 && field[hor + 1][ver - 1] == GridItem.Empty) coordinates.add((hor + 1).toString() + (ver - 1).toString())
            if (ver + 1 < field.size && field[hor + 1][ver + 1] == GridItem.Empty) coordinates.add((hor + 1).toString() + (ver + 1).toString())
        }
        if (ver > 0 && field[ver - 1][hor] == GridItem.Empty) coordinates.add(hor.toString() + (ver - 1).toString())     //влево
        if (ver + 1 < field.size && field[ver + 1][hor] == GridItem.Empty) coordinates.add(hor.toString() + (ver + 2).toString())  //вправо
        return coordinates
    }

    private fun computerFirstMove(field: MutableList<MutableList<GridItem>>): MutableList<MutableList<GridItem>> {
        val range = 1..field.size
        field[range.random() - 1][range.random() - 1] = GridItem.Computer
        return field
    }

    private fun createGameField(fieldSize: Int): MutableList<MutableList<GridItem>> { // создает поле для игры
        val fieldG = mutableListOf<MutableList<GridItem>>()
        for (n in 1..fieldSize) {
            val workLine = mutableListOf<GridItem>()
            repeat(fieldSize) {
                workLine.add(GridItem.Empty)
            }
            fieldG.add(workLine)
        }
        return fieldG
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

    private fun rulesOfGame() { // просто правила игры
        view.showText("Давай немного расскажу о правилах игры. Как ты можешь видеть на нашем поле \nстолбцы и строки пронумерованы, когда придёт время твоего хода введи \nкоординаты установки пыточного оборудования в виде двузначного числа: \n(!)первая цифра - столбец \n(!) вторая цифра - строка\nПервым ходит всегда игрок на ХХ-дыбах. \nКому какая достанется сторона мы сейчас выберем случайно.")
    }

    private fun pause() = Thread.sleep(1500) // пауза если вдруг понядобиться

    private fun randomSideSelection(): MutableMap<String, GridItem> {
        view.showText("КРИБЛИ-КРАБЛИ-…(кто-нибудь, уберите отработанный материал с гильотины)..-БУМС:")
        val players = arrayOf(GridItem.Player, GridItem.Computer)
        val sideXX = players.random()
        val sideOO = if (sideXX == GridItem.Player) {
            GridItem.Computer
        } else {
            GridItem.Player
        }
        view.showText(
            "На дыбах ХХ сегодня сражается и ходит первым: ${
                if (sideXX == GridItem.Player) {
                    "PLAYER"
                } else {
                    "COMPUTER"
                }
            }\nА аппонирует ему на устройствах для колесования ОО: ${
                if (sideOO == GridItem.Player) {
                    "PLAYER"
                } else {
                    "COMPUTER"
                }
            }"
        )
        view.showText("Да начнуться пытки!!! Делайте свой первый ход!")
        return mutableMapOf("X" to sideXX, "O" to sideOO)
    }

    private fun playerMove(field: MutableList<MutableList<GridItem>>): MutableList<MutableList<GridItem>> {
        view.showText("PLAYER твой выбор:")
        val playerEnterWORK = mutableListOf<Int>(0, 0)
        while (true) {                             //проверка что допустимые символы
            val playerEnter: MutableList<String> = readln().toCharArray().map { it.toString() }.toMutableList()
            if (checkEnter(playerEnter, field.size)) {
                playerEnter.mapIndexed { ind, x -> playerEnterWORK[ind] = x.toInt() }
                if (checkValidEnter(playerEnterWORK, field)) break
            }
        }
        field[playerEnterWORK[1] - 1][playerEnterWORK[0] - 1] = GridItem.Player// ставим ход и подменяем позицию на поле
        return field
    }

    private fun checkEnter(move: List<String>, size: Int): Boolean {
        if (move.size !== 2) {
            view.showText("(!) Введено недопустимое количество символов (!)\nПовторите ввод:")
            return false
        }
        val validValue: MutableList<String> = mutableListOf()
        for (x in 1..size) {
            validValue.add("$x")
        }
        move.forEach {
            if (it !in validValue) {
                view.showText("(!) Введены недопустимые символы (!)\nПовторите ввод:")
                return false
            }
        }
        return true
    }

    private fun checkValidEnter(move: List<Int>, field: MutableList<MutableList<GridItem>>): Boolean {
        if (field[move[1] - 1][move[0] - 1] !== GridItem.Empty) {
            view.showText("(!) УПС... Кажется тут уже стоит какой-то пыточный аппарат...\nПовторите ввод:")
            return false
        }
        return true
    }

    private fun checkWin(field: MutableList<MutableList<GridItem>>): Boolean {
        for (it in field.indices) {
            field[it].indices.forEach { ind ->
                if (field[it][ind] == GridItem.Computer || field[it][ind] == GridItem.Player) {
                    if (checkAround(field, it, ind)) return true
                }
            }
        }
        return false
    }

    private fun checkAround(field: MutableList<MutableList<GridItem>>, hor: Int, vert: Int): Boolean {
        // по горизонтали
        if (((vert + 3) <= field.size) &&
            (field[hor][vert] == field[hor][vert + 1]) &&
            (field[hor][vert] == field[hor][vert + 2])
        ) {
            return true
            // по вертикали
        } else if (hor + 3 <= field.size &&
            field[hor][vert] == field[hor + 1][vert] &&
            field[hor][vert] == field[hor + 2][vert]
        ) {
            return true
            // по диагонали вправо
        } else if (hor + 3 <= field.size &&
            (vert + 3) <= field.size &&
            field[hor][vert] == field[hor + 1][vert + 1] &&
            field[hor][vert] == field[hor + 2][vert + 2]
        ) {
            return true
            // по диагонали влево
        } else if (hor + 3 <= field.size &&
            vert >= 2 &&
            field[hor][vert] == field[hor + 1][vert - 1] &&
            field[hor][vert] == field[hor + 2][vert - 2]
        ) {
            return true
        }

        return false
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

