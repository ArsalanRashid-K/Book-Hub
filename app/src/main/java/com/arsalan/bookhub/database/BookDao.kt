package com.arsalan.bookhub.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert


@Dao()
interface BookDao {

    @Insert
    fun insertBook(bookEntity: BookEntity)

    @Delete
    fun deleteBook(bookEntity: BookEntity)
    fun getAllBooks():List<BookEntity>

}