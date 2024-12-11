package com.example.samachar.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.samachar.R
import com.example.samachar.models.Article
import okhttp3.internal.http2.Http2Connection

class NewsAdapter : RecyclerView.Adapter<NewsAdapter.ArticleViewHolder>(){
    inner class ArticleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    lateinit var articleImage : ImageView
    lateinit var articleSource : TextView
    lateinit var articleTitle : TextView
    lateinit var articleDescription : TextView
    lateinit var articleDateTime : TextView

    val diffUtil = object : DiffUtil.ItemCallback<Article>(){
        override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem.url == newItem.url
        }

        override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
           return oldItem == newItem
        }

    }
    val differ = AsyncListDiffer(this , diffUtil)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        return ArticleViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_news , parent , false))
    }

    override fun getItemCount(): Int {
       return differ.currentList.size
    }

    private var ItemclickListener : ((Article) -> Unit)? = null

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {

        val article = differ.currentList[position]

        articleImage = holder.itemView.findViewById(R.id.articleImage)
        articleSource = holder.itemView.findViewById(R.id.articleSource)
        articleTitle = holder.itemView.findViewById(R.id.articleTitle)
        articleDescription = holder.itemView.findViewById(R.id.articleDescription)
        articleDateTime = holder.itemView.findViewById(R.id.articleDateTime)

        holder.itemView.apply {
            Glide.with(this).load(article.urlToImage).into(articleImage)
            articleSource.text = article.source?.name
            articleTitle.text = article.title
            articleDescription.text = article.description
            articleDateTime.text= article.publishedAt

            setOnClickListener {
                ItemclickListener?.let {
                 it(article)
               }
            }
        }
    }

   fun setItemClickListener(listener: (Article) -> Unit){
       ItemclickListener = listener
   }
}