package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface SignalDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(signal: SignalEntity): Long

    @Update
    suspend fun update(signal: SignalEntity)

    @Query("SELECT * FROM signals ORDER BY period DESC")
    fun getAllSignals(): Flow<List<SignalEntity>>

    @Query("SELECT * FROM signals ORDER BY period DESC LIMIT :limit")
    fun getRecentSignals(limit: Int = 50): Flow<List<SignalEntity>>

    @Query("SELECT * FROM signals WHERE period = :period LIMIT 1")
    suspend fun getSignalByPeriod(period: Long): SignalEntity?

    @Query("SELECT * FROM signals ORDER BY period DESC LIMIT 1")
    suspend fun getLatestSignal(): SignalEntity?

    @Query("DELETE FROM signals")
    suspend fun clearAll()
}
