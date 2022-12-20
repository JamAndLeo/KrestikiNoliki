import java.lang.Thread.sleep

/*

Начинаем учиться разбивать на файлы)
Абсолютно в всех языках программирования необходимо делить свое приложение (программу) на так называемые слои.
Их обычно три:
1) Слой отображения (View). Все что связано с показать пользователю
2) Слой логики (в андроиде называется обычно presenter). Там где лежит так называемая бизнес-логика, это основной код твоем программы.
Здесь для простоты я назвал это Game
3) Слой данных (Repository). Это для работы с данными, запросить что-то в интернете, сохранить в память, это все репозиторий.

Ты будешь встречать разные названия, но смысл будет оставаться тем же.
И еще важный момент - чем меньше слои знают о внутренней реализации друг друга тем лучше.
Я создал два интерфейса Game и View.
Рядом с ними тебе надо создать GameImpl и ViewImpl и сделать чтобы они наследовались от Game и View соответственно.
Интересный факт который пригодится только на собеседовании:
Классы наследуются, а интерфейсы имплементируются. Так говорить правильно, но в работе абсолютно похуй.

-----view-----
у нас в вью будут лежать какбы команды по отрисовке, то есть все, что ты хочешь показать через print или println должно быть только в вью.
Хороший тон в таких классах писать человеским языком, ты какбы просишь покажи текст через метод showText.
Создаешь в папке view класс ViewImpl и имплементируешь интерфейс View. Будет вот так class ViewImpl : View
По программистки это будет звучать: ViewImpl это реализация интерфейса View
class ViewImpl должен будет подчеркнуться красной линией, так как тебе надо реализовать все методы описанные в View.
например, первый метод будет выглядеть так
    override fun showText(text: String) {
        println(text)
    }
Обрати внимание, я создал енам GridItem и в вью есть метод showGameField(field: MutableList<MutableList<GridItem>>)
То есть массив только с элементами поля, никто кроме вью не должен знать как это поле будет отрисовываться, поэтому в нем не может быть
элементов типа |, только информация о состоянии поля

-----game-----
Это главный класс в твоем приложении. main.kt и в нем метод main это просто стартовая точка, все основная движуха должна быть в классе Game.
Как было описано выше реализуешь интерфейс Game через класс GameImpl.
Я сделал по тупому и метод play возращает булеан значение. то есть если игрок в конце сказал что хочет поиграть еще,
то возращаем true, если не хочет то false.
На твоем месте первым делом я бы перенес все из main.kt в GameImpl, а потом только брался за view.
Когда сделаешь view то в GameImpl
println("Наш боец PLAYER в кровавом углу ринга сделал свой ход")
у тебя должен превратиться
view.showText("Наш боец PLAYER в кровавом углу ринга сделал свой ход")

-----еще важный моментик-----
Пока тебе не надо знать почему, но пока возьми за строгое правило что слои нельзя создавать в классах других слоев.
Так что конструкор GameImpl должен ожидать что в него положат view : View.
И все классы тебе надо создать в main, собрать в кучу и после этого грубо говоря стартовать программу.
Это называется внедрение зависимостей (dependency injection)
В итоге у тебя весь main.kt должен выглядеть как-то так:

fun main() {
    val view: View = ViewImpl()
    val game: Game = GameImpl(view)
    var isRunning:Boolean = true
    while (isRunning) {
        isRunning = game.play()
    }
}*/

fun main() {
    println("Привествую тебя, Отважный, в жестокой битве между мастерами\nпыток на ХХ-дыбе и искусных в деле ОО-колесования!\nСражения идет до тех пор пока кто-то не выставит три своих\nорудия пытки в ряд по вертикали/горизонтали/диагонали.")
    printHorLine(20)
    // здесь будет начло (!) цикла игры
    while (true) {
        val fieldSize = enterSizeField()
        var gameField = createGameField(fieldSize) // главное хранилище данных
        printGameField(gameField)
        rulesOfGame()
        // далее рандомный выбор старта
        val chooseSides = randomSideSelection()
        val sidePlayer = chooseSides["Player"]
        val sideComputer = chooseSides["Computer"]
        var switch: Int = if (sidePlayer == "X") {
            1
        } else {
            0
        } // счетчик очередности хода, если 1 то ходит PLAYER, 0 - ходит компьютер
        var gameProgress = 0
        // первый ход
        printHorLine(20)
        if (switch == 1) {
            gameField = playerMove(gameField, fieldSize, sidePlayer!!)
            printGameField(gameField)
            println("Наш боец PLAYER в кровавом углу ринга сделал свой ход")
            printHorLine(20)
            switch = 0
            gameProgress = 1
        } else {
            gameField = computerFirstMove(gameField, fieldSize, sideComputer!!)
            printGameField(gameField)
            println("COMPUTER сделал свой ход!")
            printHorLine(20)
            switch = 1
            gameProgress = 1
        }
        // далее (!) цикл матча
        while (true) {
            if (switch == 1) {
                gameField = playerMove(gameField, fieldSize, sidePlayer!!)
                printGameField(gameField)
                println("Наш боец PLAYER в кровавом углу ринга сделал свой ход")
                printHorLine(20)
                gameProgress++
                if (checkWin(gameField)) {
                    println("В непримеримой борьбе за объем выпущенной крови на ходу №$gameProgress побеждает PLAYER!\nПобедитель, заберите свои золотые щепцы для выдирания ногтей. ")
                    break
                }
                switch = 0
            } else {
                gameField = computerMove(gameField, sideComputer!!, sidePlayer!!)
                printGameField(gameField)
                println("COMPUTER сделал свой ход!")
                printHorLine(20)
                gameProgress++
                if (checkWin(gameField)) {
                    println("На ходу №$gameProgress победу одерживает искусственный интеллект COMPUTER!")
                    break
                }
                switch = 1
            }
        }
        //выбор еще одной игры
        printHorLine(20)
        println("Может желаете ещё кого-нибудь выпотрошить? да/нет")

        val gameElse = readLine()?.trim()?.replaceFirstChar { it.lowercase() }
        if (gameElse == "да") {
            true
        } else if (gameElse == "нет") {
            println("Ну что же, как подлечитесь обязательно возвращайтесь!")
            break
        } else {
            println("Вы говорите что-то совершенно не внятное, кажется вам выбили зубы в схватке, возвращатесь когда подлечитесь!")
            break
        }
    }
}

private fun computerMove(
    field: MutableList<MutableList<String>>, sideComputer: String, sidePlayer: String,
): MutableList<MutableList<String>> {
    val coordinatesImpact: MutableSet<String> = mutableSetOf() // key - строка, value - столбец
    val coordinatesPlayer: MutableSet<String> = mutableSetOf()
    val critImpactComp: MutableSet<String> = mutableSetOf()
    val critImpactPlayer: MutableSet<String> = mutableSetOf()
    // пока фишки стоят по одному
    for (it in field) {
        it.indices.forEach { ind ->
            if (it[ind] == sideComputer) {
                coordinatesImpact.addAll(scanEmptyAI(field, field.indexOf(it), ind))
            } else if (it[ind] == sidePlayer) {
                coordinatesPlayer.addAll(scanEmptyAI(field, field.indexOf(it), ind))
            }
        }
    }
    // выявлем критическии позиции, когда уже по двое
    for (it in field) {
        it.indices.forEach { ind ->
            if (it[ind] == sideComputer) {
                critImpactComp.addAll(scanCritEmptyAI(field, field.indexOf(it), ind))
            } else if (it[ind] == sidePlayer) {
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
    }] = sideComputer
    return field
}

// собиарает свободные поля среди спареннх позиций, что можно еще прописать в будущем: 1) выявить и законтрить позицию между двумя позициями противника
private fun scanCritEmptyAI(field: MutableList<MutableList<String>>, hor: Int, vert: Int): MutableSet<String> {
    val coordinates: MutableSet<String> = mutableSetOf()

    // по горизонтали
    if ((((vert + 1) / 2 + 2) <= field.size) &&
        field[hor][vert] == field[hor][vert + 2] &&
        field[hor][vert + 4] == " "
    ) coordinates.add((hor).toString() + (vert + 4).toString())

    if ((((vert + 1) / 2 + 1) <= field.size) && vert > 1 &&
        field[hor][vert] == field[hor][vert + 2] &&
        field[hor][vert - 2] == " "
    ) coordinates.add((hor).toString() + (vert - 2).toString())

    // по вертикали
    if (hor + 3 <= field.size &&
        field[hor][vert] == field[hor + 1][vert] &&
        field[hor + 2][vert] == " "
    ) coordinates.add((hor + 2).toString() + (vert).toString())

    if (hor + 2 <= field.size && hor > 0 &&
        field[hor][vert] == field[hor + 1][vert] &&
        field[hor - 1][vert] == " "
    ) coordinates.add((hor - 1).toString() + (vert).toString())

    // по диагонали вправо
    if (hor + 3 <= field.size &&
        ((vert + 1) / 2 + 2) <= field.size &&
        field[hor][vert] == field[hor + 1][vert + 2] &&
        field[hor + 2][vert + 4] == " "
    ) coordinates.add((hor + 2).toString() + (vert + 4).toString())

    if (hor + 2 <= field.size &&
        ((vert + 1) / 2 + 1) <= field.size && hor > 0 && vert > 1 &&
        field[hor][vert] == field[hor + 1][vert + 2] &&
        field[hor - 1][vert - 2] == " "
    ) coordinates.add((hor - 1).toString() + (vert - 2).toString())

    // по диагонали влево
    if (hor + 3 <= field.size &&
        vert >= 5 &&
        field[hor][vert] == field[hor + 1][vert - 2] &&
        field[hor + 2][vert - 4] == " "
    ) coordinates.add((hor + 2).toString() + (vert - 4).toString())

    if (hor > 0 && ((vert + 1) / 2 + 1) <= field.size && hor + 2 <= field.size &&
        vert >= 3 &&
        field[hor][vert] == field[hor + 1][vert - 2] &&
        field[hor - 1][vert + 2] == " "
    ) coordinates.add((hor - 1).toString() + (vert + 2).toString())
    return coordinates
}

private fun scanEmptyAI(
    //собирает все свободные поля
    field: MutableList<MutableList<String>>,
    hor: Int,
    ver: Int,
): MutableSet<String> { //согласен, выглядет безобразно
    val coordinates: MutableSet<String> = mutableSetOf()
    if (hor != 0) {         //вверх
        if (field[hor - 1][ver] == " ") coordinates.add((hor - 1).toString() + ver.toString())
        if (ver > 1 && field[hor - 1][ver - 2] == " ") coordinates.add((hor - 1).toString() + (ver - 2).toString())
        if ((ver + 1) / 2 < field.size && field[hor - 1][ver + 2] == " ") coordinates.add((hor - 1).toString() + (ver + 2).toString())
    }
    if (hor + 1 < field.size) {        //вниз
        if (field[hor + 1][ver] == " ") coordinates.add((hor + 1).toString() + ver.toString())
        if (ver > 1 && field[hor + 1][ver - 2] == " ") coordinates.add((hor + 1).toString() + (ver - 2).toString())
        if ((ver + 1) / 2 < field.size && field[hor + 1][ver + 2] == " ") coordinates.add((hor + 1).toString() + (ver + 2).toString())
    }
    if (ver > 1) coordinates.add(hor.toString() + (ver - 2).toString())     //влево
    if ((ver + 1) / 2 < field.size) coordinates.add(hor.toString() + (ver + 2).toString())  //вправо
    return coordinates
}

private fun computerFirstMove(
    field: MutableList<MutableList<String>>,
    fieldSize: Int,
    sideComputer: String,
): MutableList<MutableList<String>> {
    val range = 1..fieldSize
    field[range.random() - 1][range.random() * 2 - 1] = sideComputer
    return field
}

private fun printHorLine(x: Int) { // просто горизонтальная линия
    var quantity = x * 2
    repeat(quantity) {
        print("-")
    }
    println("-")
}

private fun createGameField(fieldSize: Int): MutableList<MutableList<String>> { // создает поле для игры
    val fieldG = mutableListOf<MutableList<String>>()
    for (n in 1..fieldSize) {
        val workLine = mutableListOf<String>()
        repeat(fieldSize) {
            workLine.add("|")
            workLine.add(" ")
        }
        workLine.add("|")
        workLine.add(n.toString())
        fieldG.add(workLine)
    }
    return fieldG
}

private fun printGameField(field: MutableList<MutableList<String>>) { // печать поля
    for (n in 1..field.size) { // выводит верхнюю линию
        print(" $n")
    }
    println()
    for (n in field) { // печать игровой зоны
        n.forEach { print(it) }.also { println() }
    }
}

private fun enterSizeField(): Int { //ввод размера игрового поля
    println("Для начала давай оперделимся с размером пыточных застенков.\nСторона поля может быть от 3 до 9.\nВведи значение соразмерно своим амбициям:")
    while (true) {
        val x = readLine()
        val validSize = arrayOf("3", "4", "5", "6", "7", "8", "9")
        if (x !in validSize) {
            println("(!) кажется вы ввели недопустимый размер поля (!)")
        } else {
            println("Договорились Малюта Скуратов. Ты будешь драться в казиматах $x на $x!")
            return x!!.toInt()
        }
    }
}

private fun rulesOfGame() { // просто правила игры
    println("Давай немного расскажу о правилах игры. Как ты можешь видеть на нашем поле \nстолбцы и строки пронумерованы, когда придёт время твоего хода введи \nкоординаты установки пыточного оборудования в виде двузначного числа: \n(!)первая цифра - столбец \n(!) вторая цифра - строка\nПервым ходит всегда игрок на ХХ-дыбах. \nКому какая достанется сторона мы сейчас выберем случайно.")
}

private fun pause() = sleep(1500) // пауза если вдруг понядобиться
private fun randomSideSelection(): Map<String, String> { //рандомный выбор стороны - позиция [0]-X, [1]-O
    println("КРИБЛИ-КРАБЛИ-…(кто-нибудь, уберите отработанный материал с гильотины)..-БУМС:")
    val players = arrayOf("X", "O")
    val sidePlayer = players.random()
    val sideComputer = if (sidePlayer == "X") {
        "O"
    } else {
        "X"
    }
    println(
        "На дыбах ХХ сегодня сражается и ходит первым: ${
            if (sidePlayer == "X") {
                "PLAYER"
            } else {
                "COMPUTER"
            }
        }\nА аппонирует ему на устройствах для колесования ОО: ${
            if (sidePlayer == "O") {
                "PLAYER"
            } else {
                "COMPUTER"
            }
        }"
    )
    println("Да начнуться пытки!!! Делайте свой первый ход!")
    return mapOf("Player" to sidePlayer, "Computer" to sideComputer)
}

private fun playerMove(
    field: MutableList<MutableList<String>>,
    size: Int,
    sidePlayer: String,
): MutableList<MutableList<String>> {
    println("PLAYER твой выбор:")
    val playerEnterWORK = mutableListOf<Int>(0, 0)
    while (true) {                             //проверка что допустимые символы
        val playerEnter: MutableList<String> = readln().toCharArray().map { it.toString() }.toMutableList()
        if (checkEnter(playerEnter, size)) {
            playerEnter.mapIndexed { ind, x -> playerEnterWORK[ind] = x.toInt() }
            if (checkValidEnter(playerEnterWORK, field)) break
        }
    }
    field[playerEnterWORK[1] - 1][playerEnterWORK[0] * 2 - 1] = sidePlayer // ставим ход и подменяем позицию на поле
    return field
}

private fun checkEnter(move: List<String>, size: Int): Boolean {
    if (move.size !== 2) {
        println("(!) Введено недопустимое количество символов (!)\nПовторите ввод:")
        return false
    }
    val validValue: MutableList<String> = mutableListOf()
    for (x in 1..size) {
        validValue.add("$x")
    }
    move.forEach {
        if (it !in validValue) {
            println("(!) Введены недопустимые символы (!)\nПовторите ввод:")
            return false
        }
    }
    return true
}

private fun checkValidEnter(move: List<Int>, field: MutableList<MutableList<String>>): Boolean {
    if (field[move[1] - 1][move[0] * 2 - 1] !== " ") {
        println("(!) УПС... Кажется тут уже стоит какой-то пыточный аппарат...\nПовторите ввод:")
        return false
    }
    return true
}

private fun checkWin(field: MutableList<MutableList<String>>): Boolean {
    for (it in field) {
        it.indices.forEach { ind ->
            if (it[ind] == "X" || it[ind] == "O") {
                if (checkAround(field, field.indexOf(it), ind)) return true
            }
        }
    }
    return false
}


private fun checkAround(field: MutableList<MutableList<String>>, hor: Int, vert: Int): Boolean {
    // по горизонтали
    if ((((vert + 1) / 2 + 2) <= field.size) &&
        (field[hor][vert] == field[hor][vert + 2]) &&
        (field[hor][vert + 2] == field[hor][vert + 4])
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
        ((vert + 1) / 2 + 2) <= field.size &&
        field[hor][vert] == field[hor + 1][vert + 2] &&
        field[hor][vert] == field[hor + 2][vert + 4]
    ) {
        return true
        // по диагонали влево
    } else return hor + 3 <= field.size &&
            vert >= 5 &&
            field[hor][vert] == field[hor + 1][vert - 2] &&
            field[hor][vert] == field[hor + 2][vert - 4]
}

// 1 2 3 4
//----------
//0123456789
//|x| | | |1
//----------
//|x|o| | |2
//----------
//| | | | |3
//----------
//| | | | |4
//----------