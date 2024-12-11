package com.example.samachar.repository

import com.example.samachar.DB.ArticleDatabase
import com.example.samachar.api.RetrofitInstance
import com.example.samachar.models.Article

class NewsRepository(val db : ArticleDatabase) {

    suspend fun getHeadlines(countrycode : String , pagenumber : Int) =
        RetrofitInstance.api.getHeadlines(countrycode , pagenumber)

    suspend fun searchnews(searchQuery: String , pagenumber: Int) =
        RetrofitInstance.api.SearchforNews(searchQuery , pagenumber)

     suspend fun upsert(article : Article) = db.getArticle().upsert(article)

     suspend fun deletearticle(article: Article) = db.getArticle().deleteArticle(article)

     fun getFavouriteNews() = db.getArticle().getAllArticles()

}