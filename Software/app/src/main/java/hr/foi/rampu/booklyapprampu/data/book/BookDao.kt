package hr.foi.rampu.booklyapprampu.data.book

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface BookDao {
    @Query("SELECT * FROM books")
    fun getAllBooks(): LiveData<List<Book>>

    @Query("SELECT * FROM books")
    fun getBooksPaged(): DataSource.Factory<Int, Book>

    @Insert
    fun insertBook(book: Book)

    @Delete
    fun deleteBook(book: Book)

    @Query("DELETE FROM books")
    suspend fun deleteAllBooks()

}
