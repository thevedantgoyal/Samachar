package com.example.samachar.UI

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.samachar.models.Article
import com.example.samachar.models.NewsResponse
import com.example.samachar.repository.NewsRepository
import com.example.samachar.util.Resource
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException

class NewsViewModel(app : Application , val newRepository : NewsRepository) :  AndroidViewModel(app){
    val headlines : MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    var headlinespagenumber = 1
    var headlineresponse : NewsResponse? = null


    val searchNews : MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    var searchpagingNumber = 1
    var searchresponse : NewsResponse? = null
    var newSearchQuery : String? = null
    var oldSearchQuery : String? = null
    init {
        getheadlines("us")
    }

    fun getheadlines(countryCode: String) = viewModelScope.launch {
        headlineInternet(countryCode)
    }

    fun searchNEWS(searchQuery: String)= viewModelScope.launch {
        searchNewsInternet(searchQuery)
    }


    private fun handleHeadlinesResponse(response : Response<NewsResponse>): Resource<NewsResponse> {
        if (response.isSuccessful){
            response.body()?.let {resultresponse ->
                headlinespagenumber++
                if(headlineresponse == null){
                    headlineresponse = resultresponse
                }
                else{
                    val oldarticles = headlineresponse?.articles
                    val newarticles = resultresponse.articles
                    oldarticles?.addAll(newarticles)
                }
                return Resource.Success(headlineresponse ?:resultresponse)
            }
        }
        return Resource.Error(response.message())
    }

    private fun handleSearchNewsResponse(response : Response<NewsResponse>): Resource<NewsResponse> {
        if (response.isSuccessful){
            response.body()?.let {resultresponse ->
                if(searchresponse == null || newSearchQuery != oldSearchQuery){
                    searchpagingNumber = 1
                    oldSearchQuery = newSearchQuery
                    searchresponse = resultresponse
                }
                else{
                    searchpagingNumber++
                    val oldarticles = searchresponse?.articles
                    val newarticles = resultresponse.articles
                    oldarticles?.addAll(newarticles)
                }
                return Resource.Success(searchresponse ?:resultresponse)
            }
        }
        return Resource.Error(response.message())
    }
    fun AddtoFavourite(article: Article) = viewModelScope.launch {
        newRepository.upsert(article)
    }
    fun getFavouriteNews() = newRepository.getFavouriteNews()

    fun deletearticle(article: Article) = viewModelScope.launch {
        newRepository.deletearticle(article)
    }

    // check for the active internet connection systerm device
    fun InternetConnection(context: Context) : Boolean? {
        (context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager).apply {
             return getNetworkCapabilities(activeNetwork)?.run {
                 when{
                     hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                     hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)->true
                     hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                     else -> false
                 }
             } ?:false
         }
    }
    private suspend fun headlineInternet(countryCode:String){
        headlines.postValue(Resource.Loading())
        try {
            if(InternetConnection(this.getApplication()) == true){
                val response = newRepository.getHeadlines(countryCode , headlinespagenumber)
                headlines.postValue(handleHeadlinesResponse(response))
            }else{
                headlines.postValue(Resource.Error("No internet Connection"))
            }
        }
        catch(t : Throwable){
            when(t){
                is IOException -> headlines.postValue(Resource.Error("Unable to Connect"))
                else -> headlines.postValue(Resource.Error("No signal"))
            }
        }
    }

    private suspend fun searchNewsInternet(searchQuery : String){
        newSearchQuery = searchQuery
        searchNews.postValue(Resource.Loading())
        try {
            if(InternetConnection(this.getApplication()) == true){
                val response = newRepository.searchnews(searchQuery , searchpagingNumber)
                searchNews.postValue(handleSearchNewsResponse(response))
            }else{
                searchNews.postValue(Resource.Error("No internet connection"))
            }
        }
        catch (t : Throwable){
            when(t){
                is IOException -> searchNews.postValue(Resource.Error("Unable to connect"))
                else -> searchNews.postValue(Resource.Error("no signal"))
            }
        }
    }



}