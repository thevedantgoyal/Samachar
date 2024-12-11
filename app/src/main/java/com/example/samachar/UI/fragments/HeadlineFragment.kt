package com.example.samachar.UI.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.samachar.R
import com.example.samachar.UI.NewsActivity
import com.example.samachar.UI.NewsViewModel
import com.example.samachar.adapters.NewsAdapter
import com.example.samachar.databinding.FragmentHeadlineBinding
import com.example.samachar.util.Constants
import com.example.samachar.util.Resource

class HeadlineFragment : Fragment(R.layout.fragment_headline) {
   lateinit var newsViewModel: NewsViewModel
   lateinit var newsAdapter: NewsAdapter
   lateinit var retryButton : Button
   lateinit var errortext : TextView
   lateinit var itemHeadlinesError : CardView
   lateinit var binding: FragmentHeadlineBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding = FragmentHeadlineBinding.bind(view)
        super.onViewCreated(view, savedInstanceState)


        itemHeadlinesError = view.findViewById(R.id.itemHeadlinesError)

        val inflator = requireContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view : View = inflator.inflate(R.layout.item_error , null)

        retryButton = view.findViewById(R.id.retryButton)
        errortext = view.findViewById(R.id.errorText)


        newsViewModel = (activity as NewsActivity).newsViewModel
        setUprecyclerHeadlines()

        newsAdapter.setItemClickListener {
            val bundle = Bundle().apply {
                putSerializable("article" , it)
            }

            findNavController().navigate(R.id.action_headlineFragment_to_articleFragment , bundle)
        }

        newsViewModel.headlines.observe(viewLifecycleOwner, Observer { response ->
            when(response){
                is Resource.Success<*> -> {
                   hideProgressBar()
                    hideErrorMessages()
                    response.data?.let { newsresponse ->
                        newsAdapter.differ.submitList(newsresponse.articles.toList())
                        val totalpage = newsresponse.totalResults / Constants.QUERY_PAGE_SIZE+2
                        isLastPage = newsViewModel.headlinespagenumber == totalpage
                        if (isLastPage){
                            binding.RecyclerViewHeadlines.setPadding(0,0,0,0)
                        }
                    }
                }

                is Resource.Error<*> -> {
                    hideProgressBar()
                    response.message?.let {message ->
                        Toast.makeText(activity , "Sorry Error : ${message}",Toast.LENGTH_SHORT).show()
                        ShowErrorMessages(message)
                    }
                }

                is Resource.Loading<*> -> {
                        showProgressBar()
                }
            }
        })

        retryButton.setOnClickListener {
            newsViewModel.getheadlines("us")
        }

    }
    var iserror = false
    var isloading = false
    var isLastPage = false
    var isScrolling = false

    private fun hideProgressBar(){
        binding.paginationProgressBar.visibility = View.INVISIBLE
        isloading = false
    }

    private fun showProgressBar(){
        binding.paginationProgressBar.visibility = View.VISIBLE
        isloading = true
    }

    private fun hideErrorMessages(){
        itemHeadlinesError.visibility = View.INVISIBLE
        iserror = false
    }
    private fun ShowErrorMessages(message : String){
        itemHeadlinesError.visibility = View.VISIBLE
        errortext.text = message
        iserror = true
    }

    val scrollListener = object  : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            val layoutManager = recyclerView.layoutManager as LinearLayoutManager
            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
            val visibleItemcount = layoutManager.childCount
            val TotalItemCount = layoutManager.itemCount

            val isNoerror = !iserror
            val isNotloadingANDlastpage = !isloading && !isLastPage
            val isAtlastItem = firstVisibleItemPosition + visibleItemcount >= TotalItemCount
            val isNotAtBeginning = firstVisibleItemPosition >= 0
            val isTotalMorethanVisible = TotalItemCount >= Constants.QUERY_PAGE_SIZE
            val shouldPaginate =
                isNoerror && isNotloadingANDlastpage && isAtlastItem && isNotAtBeginning && isTotalMorethanVisible && isScrolling
            if (shouldPaginate) {
                newsViewModel.getheadlines("us")
                isScrolling = false
            }
        }

        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)

            if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                isScrolling = true
            }
        }
    }
        private fun setUprecyclerHeadlines() {
            newsAdapter = NewsAdapter()
            binding.RecyclerViewHeadlines.apply {
                adapter = newsAdapter
                layoutManager = LinearLayoutManager(activity)
                addOnScrollListener(this@HeadlineFragment.scrollListener)
            }
        }
}