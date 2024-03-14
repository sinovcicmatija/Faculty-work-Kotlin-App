package hr.foi.rampu.booklyapprampu.fragments.book
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import hr.foi.rampu.booklyapprampu.data.bookReview.Review
import hr.foi.rampu.booklyapprampu.databinding.ItemReviewBinding

class ReviewAdapter(private var reviews: List<Review>) : RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
        val binding = ItemReviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ReviewViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
        val review = reviews[position]
        holder.binding.userName.text = review.userName
        holder.binding.ratingBar.rating = review.rating.toFloat() / 2
        holder.binding.tvComment.text = review.comment
    }
    fun submitList(newReviews: List<Review>) {
        reviews = newReviews
        notifyDataSetChanged()
    }
    override fun getItemCount() = reviews.size

    class ReviewViewHolder(val binding: ItemReviewBinding) : RecyclerView.ViewHolder(binding.root){

    }
}