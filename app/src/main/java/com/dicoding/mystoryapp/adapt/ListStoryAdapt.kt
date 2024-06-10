package com.dicoding.mystoryapp.adapt

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.mystoryapp.R
import com.dicoding.mystoryapp.databinding.ItemPostBinding
import com.dicoding.mystoryapp.db.DetailPostStory
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

@Suppress("DEPRECATION")
class ListStoryAdapt :
    PagingDataAdapter<DetailPostStory, ListStoryAdapt.ListVH>(StoryDetailDiffCallback()) {

    private lateinit var onItemClickCallback: OnItemClickCallback

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    interface OnItemClickCallback {
        fun onItemClicked(detail: DetailPostStory)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListVH {
        val binding =
            ItemPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListVH(binding)
    }

    override fun onBindViewHolder(holder: ListVH, itemIndex: Int) {
        getItem(itemIndex)?.let { holder.bind(it) }
        holder.itemView.setOnClickListener {
            getItem(itemIndex)?.let { i -> onItemClickCallback.onItemClicked(i) }
        }
    }

    class ListVH(private var binding: ItemPostBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(detail: DetailPostStory) {
            binding.authorStory.text = detail.name
            binding.uploadedOn.text = formatDateToString(detail.createdAt.toString())
            Glide.with(itemView.context)
                .load(detail.photoUrl)
                .placeholder(R.drawable.loading)
                .error(R.drawable.loading)
                .fallback(R.drawable.loading)
                .into(binding.imageStory)
        }
    }


    class StoryDetailDiffCallback : DiffUtil.ItemCallback<DetailPostStory>() {
        override fun areItemsTheSame(
            prevItem: DetailPostStory,
            currItem: DetailPostStory
        ): Boolean {
            return prevItem.id == currItem.id
        }

        override fun areContentsTheSame(
            prevItem: DetailPostStory,
            currItem: DetailPostStory
        ): Boolean {
            return prevItem == currItem
        }
    }

    companion object {
        @JvmStatic
        fun formatDateToString(dateString: String): String {
            val inputDateFormat =
                SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
            inputDateFormat.timeZone = TimeZone.getTimeZone("UTC")

            return try {
                val date = inputDateFormat.parse(dateString)
                if (date != null) {
                    val currentLocale = Locale.getDefault()
                    val dateTimeFormat = DateFormat.getDateTimeInstance(
                        DateFormat.DEFAULT, DateFormat.SHORT, currentLocale
                    )
                    dateTimeFormat.format(date)
                } else {
                    ""
                }
            } catch (e: ParseException) {
                e.printStackTrace()
                ""
            }
        }
    }
}
