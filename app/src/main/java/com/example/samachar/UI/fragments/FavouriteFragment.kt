package com.example.samachar.UI.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.samachar.R
import com.example.samachar.UI.NewsActivity
import com.example.samachar.UI.NewsViewModel
import com.example.samachar.adapters.NewsAdapter
import com.example.samachar.databinding.FragmentFavouriteBinding
import com.google.android.material.snackbar.Snackbar


class FavouriteFragment : Fragment(R.layout.fragment_favourite) {
    lateinit var newsViewModel: NewsViewModel
    lateinit var newsAdapter: NewsAdapter
    lateinit var binding: FragmentFavouriteBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentFavouriteBinding.bind(view)

        newsViewModel = (activity as NewsActivity).newsViewModel
        setupFavouriteRecycler()

        newsAdapter.setItemClickListener {
            val bundle = Bundle().apply {
                putSerializable("article", it)
            }

            findNavController().navigate(R.id.action_favouriteFragment_to_articleFragment, bundle)
        }

        val itemTouchHelper = object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val article = newsAdapter.differ.currentList[position]
                newsViewModel.deletearticle(article)
                Snackbar.make(view, "Remove from Favourites ", Snackbar.LENGTH_LONG).apply {
                    setAction("Undo") {
                        newsViewModel.AddtoFavourite(article)
                    }
                    show()
                }
            }

        }

        ItemTouchHelper(itemTouchHelper).apply {
            attachToRecyclerView(binding.RecyclerViewFavourites)
        }


        newsViewModel.getFavouriteNews().observe(viewLifecycleOwner, Observer { articles->
            newsAdapter.differ.submitList(articles)
        })
    }

    private fun setupFavouriteRecycler() {
        newsAdapter = NewsAdapter()
        binding.RecyclerViewFavourites.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)
        }
    }
}