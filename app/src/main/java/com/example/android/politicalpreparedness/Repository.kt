package com.example.android.politicalpreparedness

import androidx.lifecycle.LiveData
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.network.CivicsApi
import com.example.android.politicalpreparedness.network.models.Address
import com.example.android.politicalpreparedness.network.models.Election
import com.example.android.politicalpreparedness.network.models.RepresentativeResponse
import com.example.android.politicalpreparedness.network.models.VoterInfoResponse
import com.example.android.politicalpreparedness.representative.model.Representative
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

class Repository(private val database: ElectionDatabase) {

    suspend fun getUpcomingElections(): List<Election> {
        val upcomingElections: List<Election>
        withContext(Dispatchers.IO) {
            upcomingElections = CivicsApi.retrofitService.getElections().elections
        }
        return upcomingElections
    }

    fun getSavedElections(): LiveData<List<Election>> {
        return database.electionDao.getElections()
    }

    suspend fun getVoterInfo(address: String, electionId: Int): VoterInfoResponse {
        val voterInfoResponse: VoterInfoResponse
        withContext(Dispatchers.IO) {
            voterInfoResponse = CivicsApi.retrofitService.getVoterInfo(address, electionId)
        }
        return voterInfoResponse
    }

    suspend fun getElectionByIdFromDb(electionId: Int): Election  {
        return withContext(Dispatchers.IO) {
            database.electionDao.getElectionById(electionId)
        }
    }

    fun checkIfElectionIsSaved(electionId: Int): LiveData<Boolean> {
        return database.electionDao.checkElectionIdExistsInDb(electionId)
    }

    suspend fun deleteElection(electionId: Int) {
        withContext(Dispatchers.IO) {
            database.electionDao.deleteElectionById(electionId)
        }
    }

    suspend fun insertElection(election: Election) = withContext(Dispatchers.IO) {
        database.electionDao.saveElection(election)
    }

    suspend fun getRepresentatives(addressString: String): RepresentativeResponse {
        val representativeResponse: RepresentativeResponse
        withContext(Dispatchers.IO) {
            representativeResponse = CivicsApi.retrofitService.getRepresentativesInfoByAddress(addressString)
        }
        Timber.d("representativeResponse: $representativeResponse")
        return representativeResponse
    }


}