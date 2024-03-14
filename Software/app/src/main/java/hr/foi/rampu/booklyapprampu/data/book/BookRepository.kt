package hr.foi.rampu.booklyapprampu.data.book


import android.content.Context
import androidx.lifecycle.LiveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import hr.foi.rampu.booklyapprampu.R
import java.io.BufferedReader
import java.io.InputStreamReader


class BookRepository(private val bookDao: BookDao) {

    val allBooks: LiveData<List<Book>> = bookDao.getAllBooks()

    fun insert(book: Book) {
        bookDao.insertBook(book)
    }

    fun delete(book: Book) {
        bookDao.deleteBook(book)
    }

    suspend fun deleteAllBooks() {
        bookDao.deleteAllBooks()
    }


    fun isDatabaseEmpty(): Boolean {
        return bookDao.getAllBooks().value.isNullOrEmpty()
    }

    fun getBooksPaged(): LiveData<PagedList<Book>> {
        val config = PagedList.Config.Builder()
            .setEnablePlaceholders(false)
            .setPageSize(10)
            .build()

        return LivePagedListBuilder(bookDao.getBooksPaged(), config).build()
    }


    fun insertBooksFromCsv(context: Context, maxBooksToLoad: Int) {
        val books = readCsvData(context, maxBooksToLoad)
        for (book in books) {
            bookDao.insertBook(book)
        }
    }


    private fun readCsvData(context: Context, maxBooksToLoad: Int): List<Book> {
        val inputStream = context.resources.openRawResource(R.raw.books)
        val reader = BufferedReader(InputStreamReader(inputStream))
        val books = mutableListOf<Book>()

        var line: String?
        var isFirstLine = true
        var loadedBooks = 0
        while (reader.readLine().also { line = it } != null && loadedBooks < maxBooksToLoad) {

            if (isFirstLine) {
                isFirstLine = false
                continue
            }
            val tokens = line?.split(";")

            if (tokens?.size == 10) {
                val book = Book(
                    bookISBN = tokens[0],
                    bookName = tokens[1],
                    bookAuthor = tokens[2],
                    bookYear = tokens[3].toIntOrNull() ?: 0,
                    bookPublisher = tokens[4],
                    bookImageUrl = tokens[5],
                    bookGenre = tokens[8],
                    bookAvailability = tokens[9].toIntOrNull() ?: 0,
                    bookImageUrlM = tokens[6]
                )
                books.add(book)
                loadedBooks++
            }
        }

        reader.close()
        return books
    }

}

