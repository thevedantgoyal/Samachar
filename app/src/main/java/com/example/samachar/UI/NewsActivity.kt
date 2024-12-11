package com.example.samachar.UI

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.samachar.DB.ArticleDatabase
import com.example.samachar.R
import com.example.samachar.databinding.ActivityNewsBinding
import com.example.samachar.repository.NewsRepository

class NewsActivity : AppCompatActivity() {
    lateinit var newsViewModel: NewsViewModel
    lateinit var binding: ActivityNewsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewsBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val newsRepository = NewsRepository(ArticleDatabase(this))
        val viewModelProviderFactory = NewsViewModelProviderFactory(application , newsRepository)
        newsViewModel = ViewModelProvider(this , viewModelProviderFactory).get(NewsViewModel::class.java)


        val navHostFragment = supportFragmentManager.findFragmentById(R.id.newsNavHostFragment) as NavHostFragment
        val navcontroller = navHostFragment.findNavController()

        // 1 change findnavcontroller <- navcontroller

        binding.bottomNavigationView.setupWithNavController(navcontroller)

    }
}