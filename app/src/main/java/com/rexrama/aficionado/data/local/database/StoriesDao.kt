package com.rexrama.aficionado.data.local.database

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import com.rexrama.aficionado.data.remote.response.ListStoryItem


@Dao
interface StoriesDao {

    @Query("SELECT * FROM story")
    fun getAllStory() : PagingSource<Int, ListStoryItem>



}