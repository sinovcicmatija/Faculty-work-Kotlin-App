package hr.foi.rampu.booklyapprampu.data.user

import android.widget.EditText
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_data")
 data class User(
   @PrimaryKey(autoGenerate = true)

   val id: Int,
   val username: String,
   val password: String,
   val email: String
)