package hr.foi.rampu.booklyapprampu.fragments.news

import hr.foi.rampu.booklyapprampu.data.news.News



import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import hr.foi.rampu.booklyapprampu.R


class NewsAdapter(private val onClick: (News) -> Unit) : RecyclerView.Adapter<NewsAdapter.NewsViewHolder>() {

    private var newsList = emptyList<News>()

    class NewsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(news: News, onClick: (News) -> Unit) {
            itemView.findViewById<TextView>(R.id.titleTextView).text = news.title
            itemView.findViewById<TextView>(R.id.descriptionTextView).text = news.news_sub_header
            val imageView = itemView.findViewById<ImageView>(R.id.imageView)


            if (news.images.isNotEmpty()) {
                Glide.with(itemView.context)
                    .load(news.images[0])
                    .into(imageView)
            } else {

                imageView.setImageResource(R.drawable.ic_launcher_background)
            }

            itemView.setOnClickListener { onClick(news) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.news_item, parent, false)
        return NewsViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        holder.bind(newsList[position], onClick)
    }

    override fun getItemCount() = newsList.size

    fun setNews(news: List<News>) {
        this.newsList = news
        notifyDataSetChanged()
    }
}



