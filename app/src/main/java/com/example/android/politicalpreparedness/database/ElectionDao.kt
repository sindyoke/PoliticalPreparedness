package com.example.android.politicalpreparedness.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.android.politicalpreparedness.network.models.Election

@Dao
interface ElectionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveElection(election: Election)

    @Query("SELECT * FROM election_table ORDER BY electionDay DESC")
    fun getElections(): LiveData<List<Election>>

    @Query("SELECT * FROM election_table WHERE id = :electionId")
    fun getElectionById(electionId: Int): Election

    @Query("SELECT EXISTS (SELECT * FROM election_table WHERE id = :electionId)")
    fun checkElectionIdExistsInDb(electionId: Int): LiveData<Boolean>

    @Query("DELETE FROM election_table WHERE id = :electionId")
    fun deleteElectionById(electionId: Int)

    @Query("DELETE FROM election_table")
    fun clearElectionsTable()

}