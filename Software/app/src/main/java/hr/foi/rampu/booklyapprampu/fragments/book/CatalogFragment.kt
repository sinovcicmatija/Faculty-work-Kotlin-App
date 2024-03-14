package hr.foi.rampu.booklyapprampu.fragments.book

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import hr.foi.rampu.booklyapprampu.R
import hr.foi.rampu.booklyapprampu.data.book.Book
import hr.foi.rampu.booklyapprampu.data.book.BookViewModel


class CatalogFragment : Fragment(), BookClickListener {

    private lateinit var bookViewModel: BookViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var bookAdapter: BookAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_catalog, container, false)
        recyclerView = view.findViewById(R.id.recyclerView)

        bookAdapter = BookAdapter(requireContext(), this)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = bookAdapter

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        bookViewModel = ViewModelProvider(this).get(BookViewModel::class.java)


        bookViewModel.pagedBooks.observe(viewLifecycleOwner, Observer {

            bookAdapter.submitList(it)
        })


        bookViewModel.loadDataFromCsv(requireContext())


    }

    override fun onBookClick(book: Book) {
        showBookDetailsFragment(book)
    }

    private fun showBookDetailsFragment(book: Book) {
        Log.d("CatalogFragment", "book.bookYear: ${book.bookYear}")
        Log.d("CatalogFragment", "book.bookAvailability: ${book.bookAvailability}")
        Log.d("CatalogFragment", "book.bookAvailability: ${book.bookGenre}")
        val bookDetailsFragment = BookDetails.newInstance(
            book.bookISBN,
            book.bookImageUrlM,
            book.bookAuthor,
            book.bookYear,
            book.bookPublisher,
            book.bookGenre,
            book.bookAvailability,
            book.bookName,
            book.bookImageUrl
        )
        requireActivity().supportFragmentManager.beginTransaction()

            ?.replace(R.id.fragment_container, bookDetailsFragment)
            ?.addToBackStack(null)
            ?.commit()
    }
}
