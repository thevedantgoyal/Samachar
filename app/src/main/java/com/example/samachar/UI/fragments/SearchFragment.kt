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
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.samachar.R
import com.example.samachar.UI.NewsActivity
import com.example.samachar.UI.NewsViewModel
import com.example.samachar.adapters.NewsAdapter
import com.example.samachar.databinding.FragmentSearchBinding
import com.example.samachar.util.Constants
import com.example.samachar.util.Constants.Companion.SEARCH_NEWS_TIME_DELAY
import com.example.samachar.util.Resource
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.w3c.dom.Text

class SearchFragment : Fragment(R.layout.fragment_search) {
    lateinit var newsViewModel: NewsViewModel
    lateinit var newsAdapter: NewsAdapter
    lateinit var retryButton : Button
    lateinit var errorText : TextView
    lateinit var itemSearchError : CardView
    lateinit var binding: FragmentSearchBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding = FragmentSearchBinding.bind(view)
        super.onViewCreated(view, savedInstanceState)

        itemSearchError = view.findViewById(R.id.itemSearchError)

        val inflator = requireContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view : View = inflator.inflate(R.layout.item_error , null)

        retryButton = view.findViewById(R.id.retryButton)
        errorText = view.findViewById(R.id.errorText)


        newsViewModel = (activity as NewsActivity).newsViewModel
        setUpsearchRecycler()

        newsAdapter.setItemClickListener {
            val bundle = Bundle().apply {
                putSerializable("article" , it)
            }

            findNavController().navigate(R.id.action_headlineFragment_to_articleFragment , bundle)
        }


        var job : Job? = null
       binding.searchEditTxt.addTextChangedListener(){editable ->
           job?.cancel()
           job = MainScope().launch {
               delay(SEARCH_NEWS_TIME_DELAY)
               editable?.let {
                   if(editable.toString().isNotEmpty()){
                       newsViewModel.searchNEWS(editable.toString())
                   }
               }
           }
       }

        newsViewModel.searchNews.observe(viewLifecycleOwner, Observer { response ->
            when(response){
                is Resource.Success<*> -> {
                    hideProgressBar()
                    hideErrorMessages()
                    response.data?.let { newsresponse ->
                        newsAdapter.differ.submitList(newsresponse.articles.toList())
                        val totalpage = newsresponse.totalResults / Constants.QUERY_PAGE_SIZE+2
                        isLastPage = newsViewModel.searchpagingNumber == totalpage
                        if (isLastPage){
                            binding.recyclerSearch.setPadding(0,0,0,0)
                        }
                    }
                }

                is Resource.Error<*> -> {
                    hideProgressBar()
                    response.message?.let {message ->
                        Toast.makeText(activity , "Sorry Error : ${message}", Toast.LENGTH_SHORT).show()
                        ShowErrorMessages(message)
                    }
                }

                is Resource.Loading<*> -> {
                    showProgressBar()
                }
            }
        })

       retryButton.setOnClickListener {
           if(binding.searchEditTxt.text.toString().isNotEmpty()){
               newsViewModel.searchNEWS(binding.searchEditTxt.text.toString())
           }else{
               hideErrorMessages()
           }
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
        itemSearchError.visibility = View.INVISIBLE
        iserror = false
    }
    private fun ShowErrorMessages(message : String){
        itemSearchError.visibility = View.VISIBLE
        errorText.text = message
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
                newsViewModel.searchNEWS(binding.searchEditTxt.text.toString())
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

    private fun setUpsearchRecycler(){
        newsAdapter = NewsAdapter()
        binding.recyclerSearch.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)
            addOnScrollListener(this@SearchFragment.scrollListener)
        }
    }
}