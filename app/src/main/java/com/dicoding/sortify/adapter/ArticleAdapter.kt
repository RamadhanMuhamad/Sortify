package com.dicoding.sortify.adapter

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.sortify.R
import com.dicoding.sortify.data.remote.response.ArticlesItem
import com.dicoding.sortify.databinding.ItemArticleBinding
import com.dicoding.sortify.ui.article.DetailArticleActivity

class ArticleAdapter : ListAdapter<ArticlesItem, ArticleAdapter.MyViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemArticleBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val news = getItem(position)
        holder.bind(news)
    }

    class MyViewHolder(private val binding: ItemArticleBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(news: ArticlesItem) {
            binding.tvHeadlineArticle.text = news.title
            binding.tvDescription.text = news.content
            Glide.with(itemView.context)
                .load(news.image)
                .error(R.drawable.ic_no_image)
                .into(binding.imageView)

            itemView.setOnClickListener {
//                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(news.link))
//                itemView.context.startActivity(intent)
                val intent = Intent(itemView.context, DetailArticleActivity::class.java)
                intent.putExtra("content", news.content)
                intent.putExtra("title", news.title)
                intent.putExtra("image", news.image)
                itemView.context.startActivity(intent)
            }
        }
    }

    companion object {
        val DIFF_CALLBACK: DiffUtil.ItemCallback<ArticlesItem> =
            object : DiffUtil.ItemCallback<ArticlesItem>() {
                override fun areItemsTheSame(oldItem: ArticlesItem, newItem: ArticlesItem): Boolean {
                    return oldItem.title == newItem.title
                }

                @SuppressLint("DiffUtilEquals")
                override fun areContentsTheSame(oldItem: ArticlesItem, newItem: ArticlesItem): Boolean {
                    return oldItem == newItem
                }
            }
    }
}
