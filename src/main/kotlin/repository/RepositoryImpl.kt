package repository

import model.GridItem
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


class RepositoryImpl : Repository {
    private val fileDate = File("dataStorage.txt")
    private val dataFormat = SimpleDateFormat("d MMM yyyy, EEE, HH:mm:ss z")



    override fun saveInformation(winner: GridItem) {
        val winPlayer = if (winner == GridItem.Player) 1 else 0
        if (fileDate.readLines().isEmpty()) {
            fileDate.appendText("1,$winPlayer,${Date().time}")
        } else {
            val info = fileDate.readLines().last().split(",")
            val battles = info[0].toInt() + 1
            val victories = info[1].toInt() + winPlayer
            fileDate.appendText("\n$battles,$victories,${Date().time}")
        }
    }

    override fun getInformation(): String {
        return if (fileDate.createNewFile()) {
            "Это Ваш первый бой!"
        } else if (fileDate.readLines().isEmpty()) {
            "Это Ваш первый бой!"
        } else {
            fileDate.appendText("")
            val info = fileDate.readLines().last().split(",")
            "Всего сражений было проведено - ${info[0]}\nПобед игрока PLAYER - ${info[1]}\nПоследний бой состоялся - ${
                dataFormat.format(info[2].toLong())
            }"
        }
    }
}