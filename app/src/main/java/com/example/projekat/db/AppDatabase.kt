package com.example.projekat.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.projekat.Kviz.dp.Kviz
import com.example.projekat.Kviz.dp.KvizDao
import com.example.projekat.cats.db.Cat
import com.example.projekat.cats.db.CatDao
import com.example.projekat.photos.db.Album
import com.example.projekat.photos.db.AlbumDao

@Database(
    entities = [
        Cat::class,
        Album::class,
        Kviz::class,

    ],
    version = 6,
    exportSchema = true,
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun catDao(): CatDao

    abstract fun albumDao() : AlbumDao
    abstract fun kvizDao() : KvizDao

}