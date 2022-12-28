import field.FieldHolder
import game.Game
import game.GameImpl
import view.View
import view.ViewImpl

fun main() {
    val view: View = ViewImpl()
    view.showText("Привествую тебя, Отважный, в жестокой битве между мастерами\nпыток на ХХ-дыбе и искусных в деле ОО-колесования!\nСражения идет до тех пор пока кто-то не выставит три своих\nорудия пытки в ряд по вертикали/горизонтали/диагонали.")
    val fieldHolder = FieldHolder(view)
    val game: Game = GameImpl(view, fieldHolder)

    while (game.play()) {
    }
}