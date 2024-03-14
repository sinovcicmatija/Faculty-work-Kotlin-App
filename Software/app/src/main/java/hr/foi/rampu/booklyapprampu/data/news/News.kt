package hr.foi.rampu.booklyapprampu.data.news

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "news")
data class News(
    @PrimaryKey val _id: String,
    val url: String,
    val title: String,
    val epoch_time: Long,
    val website: String,
    val author: String,
    val raw_content: String,
    val content: String,
    val news_post_date: String,
    val category: String,
    val sub_category: String,
    val language: String,
    val news_post_date_in_seconds: String?,
    val header_image: String,
    val images: List<String>,
    val news_sub_header: String
) : Parcelable