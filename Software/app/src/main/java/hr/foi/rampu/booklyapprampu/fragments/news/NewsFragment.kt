package hr.foi.rampu.booklyapprampu.fragments.news



import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import hr.foi.rampu.booklyapprampu.R
import hr.foi.rampu.booklyapprampu.data.news.NewsViewModel

class NewsFragment : Fragment() {

    private lateinit var viewModel: NewsViewModel
    private lateinit var newsAdapter: NewsAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_news, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this).get(NewsViewModel::class.java)
        newsAdapter = NewsAdapter { news ->
            val fragment = NewsDetailFragment.newInstance(news)
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit()
        }

        view.findViewById<RecyclerView>(R.id.recycler_view_news).apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        viewModel.allNews.observe(viewLifecycleOwner) { news ->
            newsAdapter.setNews(news)
        }

        viewModel.loadNewsFromJson(requireContext())
    }
}

