package hr.foi.rampu.booklyapprampu.data



import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import hr.foi.rampu.booklyapprampu.data.book.Book
import hr.foi.rampu.booklyapprampu.data.user.User
import hr.foi.rampu.booklyapprampu.data.user.UserDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SessionManager private constructor(context: Context) {

    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    private val editor: SharedPreferences.Editor = sharedPreferences.edit()

    suspend fun saveUser(userDao: UserDao, username: String) {
        withContext(Dispatchers.IO) {
            val user = userDao.getUser(username)
            if (user != null) {
                editor.putInt(KEY_USER_ID, user.id)
                editor.putString(KEY_USERNAME, user.username)
                editor.putString(KEY_EMAIL, user.email)
                editor.apply()
            }
        }
    }

    fun getRandomGenreFromReadBooks(readBooks: MutableList<String>): String? {
        val genreSet = mutableSetOf<String>()

        readBooks.forEach { readBook ->
            val bookAttributes = readBook.split(",")
            if (bookAttributes.size >= 5) {
                val genre = bookAttributes[4]
                if (genre.isNotEmpty()) {
                    genreSet.add(genre)
                }
            }
        }
        if (genreSet.isEmpty()) {
            return null
        }


        val genreList = genreSet.toList()
        return genreList.random()
    }


    fun getReadBooksForUser(userId: Int):MutableList<String>{
        return sharedPreferences.getStringSet("$KEY_READ_BOOKS$userId", setOf())?.toMutableList() ?: mutableListOf()
    }

    fun addReadBook(userId: Int,bookISBN: String,bookName: String, bookAuthor: String, bookImageUrl: String,bookGenre: String ){
        val readBooks = getReadBooksForUser(userId)
        readBooks.add("$bookISBN,$bookName,$bookAuthor,$bookImageUrl,$bookGenre")
        editor.putStringSet("$KEY_READ_BOOKS$userId", readBooks.toSet())
        editor.apply()
    }
    fun getWaitingListForUser(userId: Int): MutableList<String> {
        return sharedPreferences.getStringSet("$KEY_WAITING_LIST$userId", setOf())?.toMutableList() ?: mutableListOf()
    }
    fun addBookToWaitingList(  userId: Int, bookISBN: String, bookName: String, bookAuthor: String, bookImageUrl: String, bookGenre: String, bookAvailability: Int){
        val waitingList = getWaitingListForUser(userId)
        waitingList.add("$bookISBN,$bookName,$bookAuthor,$bookImageUrl,$bookGenre, $bookAvailability")
        editor.putStringSet("$KEY_WAITING_LIST$userId", waitingList.toSet())
        editor.apply()
    }

    fun addReservedBook(userId: Int, bookISBN: String, bookName: String, bookAuthor: String, bookImageUrl: String, bookGenre: String, reservationTimestamp: Long, bookAvailability: Int) {
        val reservedBooks = getReservedBooksForUser(userId)
        reservedBooks.add("$bookISBN,$bookName,$bookAuthor,$bookImageUrl,$bookGenre, $reservationTimestamp, $bookAvailability")
        editor.putStringSet("$KEY_RESERVED_BOOKS$userId", reservedBooks.toSet())
        editor.apply()
    }

    fun getReservedBooksForUser(userId: Int): MutableList<String> {
        return sharedPreferences.getStringSet("$KEY_RESERVED_BOOKS$userId", setOf())?.toMutableList() ?: mutableListOf()
    }

    fun removeReservation(userId: Int, bookISBN: String) {
        val reservedBooks = getReservedBooksForUser(userId)
        reservedBooks.removeAll { it.startsWith("$bookISBN,") }
        updateReservedBooksForUser(userId, reservedBooks)
    }

    fun getUserId(): Int {
        return sharedPreferences.getInt(KEY_USER_ID, -1)
    }
    fun updateReservedBooksForUser(userId: Int, reservedBooks: List<String>) {
        editor.putStringSet("$KEY_RESERVED_BOOKS$userId", reservedBooks.toSet())
        editor.apply()
    }
    fun updateBookAvailability(bookISBN: String, updatedAvailability: Int) {
        val userId = getUserId() // Get the current user ID
        val reservedBooks = getReservedBooksForUser(userId) // Get the reserved books for the user

        reservedBooks.forEachIndexed { index, book ->
            if (book.startsWith("$bookISBN,")) { // Check if the book ISBN matches
                val attributes = book.split(",")
                Log.d("SessionManager", "Velicina: $attributes.size")
                if (attributes.size >= 5) {
                    Log.d("SessionManager", "Old availability:${attributes[6]}  ")
                    Log.d("SessionManager", "New availability: $updatedAvailability")

                    // Update the availability of the book
                     reservedBooks[index] = "${attributes[0]},${attributes[1]},${attributes[2]},${attributes[3]},${attributes[4]},${attributes[5]},$updatedAvailability"
                }else{
                    Log.d("SessionManager", "size nije 7")
                }
            }
        }


        updateReservedBooksForUser(userId, reservedBooks)


        Log.d("SessionManager", "Updated reserved books: $reservedBooks")
    }


    companion object {
        private const val PREF_NAME = "YourAppSession"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_USERNAME = "username"
        private const val KEY_EMAIL = "email"
        private const val KEY_RESERVED_BOOKS = "reserved_books"
        private const val KEY_READ_BOOKS ="read_books"
        private const val KEY_WAITING_LIST = "waiting_list"

        @Volatile
        private var instance: SessionManager? = null

        fun getInstance(context: Context): SessionManager {
            return instance ?: synchronized(this) {
                instance ?: SessionManager(context).also { instance = it }
            }
        }
    }
    fun getUserName(): String {
        return sharedPreferences.getString(KEY_USERNAME, "") ?: ""
    }

    fun getUserEmail(): String {
        return sharedPreferences.getString(KEY_EMAIL, "") ?: ""
    }
}

