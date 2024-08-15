package com.example.projekat.leaderBoard.api.di

import com.example.projekat.leaderBoard.api.LeaderBoardApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.create
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LeaderBoardModule {

    @Provides
    @Singleton
    fun provideLeaderBoardApi(@Named("leaderBoard") retrofit: Retrofit): LeaderBoardApi = retrofit.create()
}