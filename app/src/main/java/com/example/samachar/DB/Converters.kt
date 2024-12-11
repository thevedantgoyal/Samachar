package com.example.samachar.DB

import androidx.room.TypeConverter
import com.example.samachar.models.Source

class Converters {

   @TypeConverter
   fun fromSource(source: Source):String{
       return source.name
   }

    @TypeConverter
    fun toString(name : String) : Source{
        return Source(name,name)
    }
}