package com.example.samachar.util

sealed class Resource<T>(

    // it represents the result data of the operation that could be success or error
    val data : T? = null,

    // it is variable used to store an error message in case of error
    val message : String? = null

){

    // generic parameter type T and holds the datatype of T and is nullable

    class Success<T>(data: T):Resource<T>(data)
    // subclass of resource representing the successState it has a constructor
    // that takes the data hat is the result data and passes into the superclass Resource

    // subclass of resource representing the errorState it has a constructor
    // that takes the data that is the error and  result data and passes into the superclass Resource
    class Error<T>(message : String , data: T? = null):Resource<T>(data , message)

    // that the data still is being fetched
    class Loading<T> : Resource<T>()


}