package hr.foi.rampu.booklyapprampu.fragments.news

import hr.foi.rampu.booklyapprampu.data.news.News
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.ImageView
import com.bumptech.glide.Glide
import hr.foi.rampu.booklyapprampu.R

class NewsDetailFragment : Fragment() {


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_news_detail, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val news = arguments?.getParcelable<News>("news")


        val imageView = view.findViewById<ImageView>(R.id.detailImageView)
        news?.images?.firstOrNull()?.let { imageUrl ->
            Glide.with(this)
                .load(imageUrl)
                .into(imageView)
        }


        view.findViewById<TextView>(R.id.titleTextView).text = news?.title


        view.findViewById<TextView>(R.id.urlTextView).apply {
            text = news?.url
            paint.isUnderlineText = true
            setOnClickListener {
            }
        }

        view.findViewById<TextView>(R.id.contentTextView).text = news?.content
    }

    companion object {
        fun newInstance(news: News): NewsDetailFragment {
            val fragment = NewsDetailFragment()
            val args = Bundle()
            args.putParcelable("news", news)
            fragment.arguments = args
            return fragment
        }
    }
}
