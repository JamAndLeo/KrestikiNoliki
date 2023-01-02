package repository

import model.GridItem

interface Repository {
    fun saveInformation(winner:GridItem)

    fun getInformation(): String
}