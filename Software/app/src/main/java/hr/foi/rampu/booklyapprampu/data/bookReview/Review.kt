package hr.foi.rampu.booklyapprampu.data.bookReview

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Review")
data class Review(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: String,
    val isbn: String,
    val rating: Int,
    val comment: String,
    val userName: String
)

