package com.example.composeuitestsample.data

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Inject
import javax.inject.Qualifier
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

@Entity(tableName = "bird")
data class Bird(
    @PrimaryKey val id: String,
    val name: String,
    val color: String,
)

@Dao
interface BirdDao {
    @Query("SELECT * FROM bird")
    suspend fun getAll(): List<Bird>

    @Query("SELECT * FROM bird")
    fun getAllFlow(): Flow<List<Bird>>

    @Insert
    suspend fun insertAll(vararg birds: Bird)
}

@Database(
    entities = [Bird::class],
    version = 1,
    exportSchema = false,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun birdDao(): BirdDao
}

@Qualifier
public annotation class IoDispatcher

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Singleton
    @Provides
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context, AppDatabase::class.java, "bird_database"
        )
            .build()
    }
}

@Module
@InstallIn(SingletonComponent::class)
class DaoModule {
    @Singleton
    @Provides
    fun provideBirdDao(appDatabase: AppDatabase): BirdDao {
        return appDatabase.birdDao()
    }
}

@Module
@InstallIn(SingletonComponent::class)
class DispatcherModule {
    @IoDispatcher
    @Provides
    fun provideIoDispatcher(): CoroutineDispatcher = Dispatchers.IO
}

class BirdRepository @Inject constructor(
    private val birdDao: BirdDao,
    @IoDispatcher private val coroutineDispatcher: CoroutineDispatcher
) {
    suspend fun getAllBirds(): List<Bird> = withContext(coroutineDispatcher) {
        birdDao.getAll()
    }

    fun getAllBirdsFlow(): Flow<List<Bird>> = birdDao.getAllFlow()


    suspend fun insertBirds(vararg birds: Bird) = withContext(coroutineDispatcher) {
        birdDao.insertAll(*birds)
    }
}
