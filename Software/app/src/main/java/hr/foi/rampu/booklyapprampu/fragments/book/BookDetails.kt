package hr.foi.rampu.booklyapprampu.fragments.book

import WaitingListFragment
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import kotlinx.coroutines.*
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import hr.foi.rampu.booklyapprampu.R
import hr.foi.rampu.booklyapprampu.data.ReservationService
import hr.foi.rampu.booklyapprampu.data.bookReview.AppDatabase
import hr.foi.rampu.booklyapprampu.data.bookReview.Review
import hr.foi.rampu.booklyapprampu.data.SessionManager

class BookDetails : Fragment() {
    private lateinit var progressBar: ProgressBar
    private lateinit var rvReviews: RecyclerView
    private lateinit var reviewAdapter: ReviewAdapter
    private lateinit var sessionManager: SessionManager
    private var bookIsbn: String? = null
    private var bookAuthor: String? = null
    private var bookYear: Int? = null
    private var bookPublisher: String? = null
    private var bookGenre: String? = null
    private var bookName: String? = null
    private var bookImageUrlM: String? = null
    private var bookAvailability: Int? = null
    private var bookimageUrl: String? = null
    private lateinit var database: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bookIsbn = arguments?.getString("ISBN")
        bookAuthor = arguments?.getString("Author")
        bookYear = arguments?.getInt("Year")
        bookPublisher = arguments?.getString("Publisher")
        bookGenre = arguments?.getString("Genre")
        bookName = arguments?.getString("Name")
        bookImageUrlM = arguments?.getString("ImageUrl")
        bookAvailability = arguments?.getInt("Availability")
        bookimageUrl = arguments?.getString("ImageUrlS")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sessionManager = SessionManager.getInstance(requireContext())
        database = AppDatabase.getDatabase(requireContext())
        progressBar = view.findViewById(R.id.progressBar)
        progressBar.visibility = View.VISIBLE


        bookName?.let { name ->
            view.findViewById<TextView>(R.id.textImeKnjige).text = "Ime knjige: \n$name"
        }

        bookAuthor?.let { author ->
            view.findViewById<TextView>(R.id.textAutorKnjige).text = "Autor knjige: \n$author"
        }

        bookYear?.let { year ->
            Log.d(TAG, "onViewCreated - Setting text for year: $year")
            view.findViewById<TextView>(R.id.textGodinaIzdanja).text =
                "Godina izdanja knjige: \n${year}"
        }


        bookPublisher?.let { publisher ->
            view.findViewById<TextView>(R.id.textIzdavac).text = "Izdavač: \n$publisher"
        }

        bookImageUrlM?.let { imageUrl ->
            val slikaKnjige = view.findViewById<ImageView>(R.id.SlikaKnjigeM)
            Glide.with(requireContext())
                .load(imageUrl)
                .placeholder(R.drawable.placeholder_book)
                .error(R.drawable.default_book_cover)
                .into(slikaKnjige)
        }


        val btnReservation = view.findViewById<Button>(R.id.btnReservation)
        btnReservation.setOnClickListener {

            val bookISBN = bookIsbn
            val bookName = bookName
            val bookAuthor = bookAuthor
            val bookImageUrl = bookimageUrl
            val genre = bookGenre
            val bookAvailability = bookAvailability

            if (bookISBN != null && bookName != null && bookAuthor != null && bookImageUrl != null && genre != null) {
                val userId = sessionManager.getUserId()

                if (bookAvailability != null) {
                    Log.d("Broj knjiga raspoloživih", "$bookAvailability")
                    if (bookAvailability >= 1) {
                        val reservationTimestamp = System.currentTimeMillis()
                        val alarmManager =
                            requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
                        val intent = Intent(requireContext(), ReservationService::class.java)
                        intent.putExtra("userId", userId)
                        intent.putExtra("bookISBN", bookISBN)

                        val pendingIntent = PendingIntent.getService(
                            requireContext(),
                            0,
                            intent,
                            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                        )
                        val oneWeekInMillis = 7 * 24 * 60 * 60 * 1000
                        alarmManager.set(
                            AlarmManager.RTC_WAKEUP,
                            reservationTimestamp + oneWeekInMillis,
                            pendingIntent
                        )
                        sessionManager.addReservedBook(
                            userId,
                            bookISBN,
                            bookName,
                            bookAuthor,
                            bookImageUrl,
                            genre,
                            reservationTimestamp,
                            bookAvailability
                        )
                        Toast.makeText(requireContext(), "Knjiga rezervirana!", Toast.LENGTH_SHORT)
                            .show()

                        val updatedAvailability = bookAvailability - 1

                        sessionManager.updateBookAvailability(bookISBN, updatedAvailability)

                    } else {
                        Snackbar.make(view, "Knjiga nije dostupna!", Snackbar.LENGTH_SHORT).show()
                        sessionManager.addBookToWaitingList(
                            userId,
                            bookISBN,
                            bookName,
                            bookAuthor,
                            bookImageUrl,
                            genre,
                            bookAvailability
                        )
                        val waitingListFragment = WaitingListFragment()
                        parentFragmentManager.beginTransaction()
                            .replace(R.id.fragment_container, waitingListFragment)
                            .addToBackStack(null)
                            .commit()
                    }
                }

            } else {

                Toast.makeText(
                    requireContext(),
                    "Greška pri rezervaciji knjige.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }


        val btnAddReview = view.findViewById<Button>(R.id.btnAdd)
        btnAddReview.visibility = View.GONE

        bookIsbn?.let { isbn ->
            loadReviewsForBook(isbn, progressBar, btnAddReview)
        }
    }

    private fun loadReviewsForBook(isbn: String, progressBar: ProgressBar, btnAddReview: Button) {
        CoroutineScope(Dispatchers.IO).launch {
            val reviewsForBook = database.reviewDao().getReviewsForBook(isbn)
            withContext(Dispatchers.Main) {
                reviewAdapter.submitList(reviewsForBook)
                progressBar.visibility = View.GONE
                btnAddReview.visibility = View.VISIBLE
                updateAverageRating(calculateAverageRating(reviewsForBook))
            }
        }
    }

    private fun calculateAverageRating(reviews: List<Review>): Float {
        return if (reviews.isNotEmpty()) {
            reviews.map { it.rating.toFloat() / 2 }.average().toFloat()
        } else {
            0f
        }
    }

    private fun updateAverageRating(averageRating: Float) {
        val averageRatingText = view?.findViewById<TextView>(R.id.averageRatingTextView)
        averageRatingText?.text = String.format(
            "Ocjena: " +
                    " %.2f", averageRating
        )
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_book_details, container, false)

        setupRecyclerView(view)
        setupAddReviewButton(view)
        return view
    }

    private fun setupRecyclerView(view: View) {
        rvReviews = view.findViewById(R.id.rvReviews)
        rvReviews.layoutManager = LinearLayoutManager(context)
        reviewAdapter = ReviewAdapter(emptyList())
        rvReviews.adapter = reviewAdapter
    }

    private fun setupAddReviewButton(view: View) {
        val btnAddReview = view.findViewById<Button>(R.id.btnAdd)
        btnAddReview.setOnClickListener {
            showAddReviewPopup()
        }
    }

    private fun showAddReviewPopup() {
        val sessionManager = SessionManager.getInstance(requireContext())
        val userId = sessionManager.getUserId().toString()
        val userName = sessionManager.getUserName()


        if (userId == null) {
            Toast.makeText(context, "Error s ID.", Toast.LENGTH_SHORT).show()
            return
        }

        val popupView = LayoutInflater.from(context).inflate(R.layout.add_review, null)
        val popupWindow = PopupWindow(
            popupView,
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        ).apply {
            isFocusable = true
            setBackgroundDrawable(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.popup_background
                )
            )
            elevation = 10f
            showAtLocation(view, Gravity.CENTER, 0, 0)
            softInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING

        }

        val ratingBar = popupView.findViewById<RatingBar>(R.id.ratingBarAdd)
        val commentEditText = popupView.findViewById<EditText>(R.id.Comment)
        val btnSubmitReview = popupView.findViewById<Button>(R.id.btnSubmitReview)

        btnSubmitReview.setOnClickListener {
            Log.d(TAG, "Submit kliknut")
            val rating = (ratingBar.rating * 2).toInt()
            val comment = commentEditText.text.toString()

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    Log.d(TAG, "Provjera")
                    val userHasReviewed = database.reviewDao()
                        .getUserReviewForBook(userId.toInt(), bookIsbn ?: "") != null
                    withContext(Dispatchers.Main) {
                        if (userHasReviewed) {
                            Log.d(TAG, "već ocjenio")
                            Toast.makeText(context, "Već ste ocijenili knjigu", Toast.LENGTH_SHORT)
                                .show()
                        } else {
                            Log.d(TAG, "Dodavanje")
                            val newReview = Review(0, userId, bookIsbn!!, rating, comment, userName)
                            database.reviewDao().insertReview(newReview)
                            Log.d(TAG, "New review added, refreshing the list.")
                            displayReviewsForBook(bookIsbn!!, progressBar, btnSubmitReview)
                            popupWindow.dismiss()
                        }
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error dodavanja", e)
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "Fail: ${e.message}", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }


        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0)
    }

    private fun displayReviewsForBook(
        isbn: String,
        progressBar: ProgressBar,
        btnAddReview: Button
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            val reviewsForBook = database.reviewDao().getReviewsForBook(isbn)
            val averageRating = calculateAverageRating(reviewsForBook)

            withContext(Dispatchers.Main) {
                reviewAdapter.submitList(reviewsForBook)
                progressBar.visibility = View.GONE
                btnAddReview.visibility = View.VISIBLE
                updateAverageRating(averageRating)
            }
        }
    }


    companion object {
        fun newInstance(
            bookISBN: String,
            bookImageUrlM: String,
            bookAuthor: String,
            bookYear: Int,
            bookPublisher: String,
            bookGenre: String,
            bookAvailability: Int,
            bookName: String,
            bookImageUrl: String
        ): BookDetails {
            val fragment = BookDetails()

            val bundle = Bundle().apply {
                putString("ISBN", bookISBN)
                putString("ImageUrl", bookImageUrlM)
                putString("Author", bookAuthor)
                putInt("Year", bookYear)
                putString("Publisher", bookPublisher)
                putString("Genre", bookGenre)
                putInt("Availability", bookAvailability)
                putString("Name", bookName)
                putString("ImageUrlS", bookImageUrl)
            }
            fragment.arguments = bundle
            return fragment
        }
    }
}
