package hr.foi.rampu.booklyapprampu.data.book

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "books")
data class Book(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val bookName: String,
    val bookAuthor: String,
    val bookYear: Int,
    val bookPublisher: String,
    val bookImageUrl: String,
    val bookImageUrlM: String,
    val bookISBN: String,
    val bookGenre: String,
    val bookAvailability: Int,

)




