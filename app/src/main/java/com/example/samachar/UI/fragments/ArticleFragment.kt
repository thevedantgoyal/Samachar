package com.example.samachar.UI.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebViewClient
import androidx.navigation.fragment.navArgs
import com.example.samachar.R
import com.example.samachar.UI.NewsActivity
import com.example.samachar.UI.NewsViewModel
import com.example.samachar.databinding.FragmentArticleBinding
import com.google.android.material.snackbar.Snackbar

class ArticleFragment : Fragment(R.layout.fragment_article) {

    lateinit var newsViewModel: NewsViewModel
    val args : ArticleFragmentArgs by navArgs()
    lateinit var binding: FragmentArticleBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentArticleBinding.bind(view)

        // for initialising web view so we first initailise the vies model
        newsViewModel = (activity as NewsActivity).newsViewModel
        val article = args.article

        // web view is responsible for various handling various events in the web view such as when
        // NEW URL is located
        binding.webView.apply {
            webViewClient = WebViewClient()
            article.url?.let {
                loadUrl(it)
            }
        }

        binding.FavoriteBtn.setOnClickListener {
           newsViewModel.AddtoFavourite(article)
            Snackbar.make(view , "Added to Favourites", Snackbar.LENGTH_SHORT).show()
        }
    }

}