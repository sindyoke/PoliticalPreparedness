package com.example.android.politicalpreparedness.election

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.Repository
import com.example.android.politicalpreparedness.network.models.AdministrationBody
import com.example.android.politicalpreparedness.network.models.Division
import com.example.android.politicalpreparedness.network.models.Election
import com.example.android.politicalpreparedness.network.models.VoterInfoResponse
import kotlinx.coroutines.launch
import timber.log.Timber

class VoterInfoViewModel(private val repository: Repository, private val electionId: Int, private val division: Division) : ViewModel() {

    private val _voterInfoResponse = MutableLiveData<VoterInfoResponse>()
    val voterInfoResponse: LiveData<VoterInfoResponse>
        get() = _voterInfoResponse

    private val _election = MutableLiveData<Election>()
    val election: LiveData<Election>
        get() = _election

    private val _stateElectionAdministrationBody = MutableLiveData<AdministrationBody>()
    val stateElectionAdministrationBody: LiveData<AdministrationBody>
        get() = _stateElectionAdministrationBody

    private val _electionIsSaved = MutableLiveData<Boolean>()
    val electionIsSaved: LiveData<Boolean>
        get() = _electionIsSaved

    private fun getAddress(): String {
        if(division.state.isNotEmpty()) {
            return "${division.country},${division.state}"
        }
        return "USA,CA"
    }

    init {
        checkIfElectionSaved()
        getVoterInfo()
    }

    fun getVoterInfo() = viewModelScope.launch {
        _electionIsSaved.value = repository.checkIfElectionIsSaved(electionId).value
        try {
            _voterInfoResponse.value = repository.getVoterInfo(getAddress(), electionId)
            _election.value = voterInfoResponse.value?.election
            _stateElectionAdministrationBody.value = voterInfoResponse.value?.state?.first()?.electionAdministrationBody

            Timber.d("address: ${getAddress()}, electionId: $electionId")
            Timber.d("voterInfoResponse is: ${voterInfoResponse.value.toString()}")
            Timber.d("election: ${voterInfoResponse.value?.election.toString()}")
            Timber.d("locationUrl: ${stateElectionAdministrationBody.value?.votingLocationFinderUrl.toString()}")
            Timber.d("ballotUrl: ${stateElectionAdministrationBody.value?.ballotInfoUrl.toString()}")
            Timber.d("correspondenceAddress: ${stateElectionAdministrationBody.value?.correspondenceAddress.toString()}")
        } catch (exception: java.lang.Exception) {
            Timber.e(exception)
        }
    }

    fun checkIfElectionSaved() {
        viewModelScope.launch {
            val electionFromDatabase: Election? = repository.getElectionByIdFromDb(electionId)
            if(electionFromDatabase == null) {
                Timber.d("election from database is null")
                _electionIsSaved.value = false
            } else {
                Timber.d("electionId from database is ${electionFromDatabase.id}")
                _electionIsSaved.value = true
            }
        }
    }

    fun onButtonClick() = viewModelScope.launch {
        if(electionIsSaved.value == true) {
            election.value?.let { repository.deleteElection(it.id) }
            _electionIsSaved.value = false
        } else {
            repository.insertElection(election.value!!)
            _electionIsSaved.value = true
        }
    }
}