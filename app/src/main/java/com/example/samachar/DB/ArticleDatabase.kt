package com.example.samachar.DB

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.example.samachar.models.Article
import java.util.concurrent.locks.Lock


@Database(entities = [Article::class] , version = 1)

@TypeConverters(Converters::class)
abstract class ArticleDatabase : RoomDatabase() {

    abstract fun getArticle():ArticleDAO

    companion object{
        @Volatile
        private var instance : ArticleDatabase? = null
        private val LOCK = Any()

        operator fun invoke(context: Context) = instance ?: synchronized(LOCK) {

            instance ?: CreateDatabase(context).also {
                instance = it
            }
        }

        private fun CreateDatabase(context: Context) = Room.databaseBuilder(
            context.applicationContext,
            ArticleDatabase::class.java,
            "articles_db.db"
        ).build()
        }
    }
