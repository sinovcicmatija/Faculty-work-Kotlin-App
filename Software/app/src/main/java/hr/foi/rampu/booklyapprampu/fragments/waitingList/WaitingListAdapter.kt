package hr.foi.rampu.booklyapprampu.fragments.waitingList
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import hr.foi.rampu.booklyapprampu.R
import hr.foi.rampu.booklyapprampu.data.book.Book

class WaitingListAdapter: ListAdapter<Book, WaitingListAdapter.WaitingListViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WaitingListViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_book, parent, false)
        return WaitingListViewHolder(view)
    }

    override fun onBindViewHolder(holder: WaitingListViewHolder, position: Int) {
        val book = getItem(position)
        holder.titleTextView.text = book.bookName
        holder.authorTextView.text = book.bookAuthor
        holder.genreTextView.text = book.bookGenre

        Glide.with(holder.itemView.context)
            .load(book.bookImageUrl)
            .placeholder(R.drawable.placeholder_book)
            .error(R.drawable.default_book_cover)
            .into(holder.imageViewBook)


    }

    inner class WaitingListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.textViewTitle)
        val authorTextView: TextView = itemView.findViewById(R.id.textViewAuthor)
        val genreTextView: TextView = itemView.findViewById(R.id.textViewGenre)
        val imageViewBook: ImageView = itemView.findViewById(R.id.imageViewBook)
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<Book>() {
            override fun areItemsTheSame(oldItem: Book, newItem: Book): Boolean {
                return oldItem.bookISBN == newItem.bookISBN
            }

            override fun areContentsTheSame(oldItem: Book, newItem: Book): Boolean {
                return oldItem == newItem
            }
        }
    }
}