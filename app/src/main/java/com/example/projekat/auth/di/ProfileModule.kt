package com.example.projekat.auth.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.dataStoreFile
import com.example.projekat.auth.AuthDataSerializer
import com.example.projekat.auth.domain.LoginData
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ProfileModule {

    @Singleton
    @Provides
    fun provideProfileDataStore(
        @ApplicationContext context: Context
    ): DataStore<LoginData> {
        return DataStoreFactory.create(
            produceFile = { context.dataStoreFile(fileName = "auth.json") },
            serializer = AuthDataSerializer(),
        )
    }
}