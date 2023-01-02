import field.FieldHolder
import game.Game
import game.GameImpl
import repository.RepositoryImpl
import view.View
import view.ViewImpl

fun main() {
    val repository=RepositoryImpl()
    val view: View = ViewImpl() //создали вью, что бы пользоваться отображением
    view.showLine()
    view.showText(repository.getInformation())
    view.showLine()
    view.showText("Привествую тебя, Отважный, в жестокой битве между мастерами\nпыток на ХХ-дыбе и искусных в деле ОО-колесования!\nСражения идет до тех пор пока кто-то не выставит три своих\nорудия пытки в ряд по вертикали/горизонтали/диагонали.")
    val fieldHolder = FieldHolder(view) //создали игровое поле
    val game: Game = GameImpl(view, fieldHolder, repository) //создали игру

    while (game.play()) { //цикл матча
    }
}