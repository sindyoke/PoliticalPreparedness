package com.example.android.politicalpreparedness.election

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.Repository
import com.example.android.politicalpreparedness.network.CivicsApi
import com.example.android.politicalpreparedness.network.models.Election
import kotlinx.coroutines.launch
import timber.log.Timber

class ElectionsViewModel(private val repository: Repository) : ViewModel() {

    private val _upcomingElections = MutableLiveData<List<Election>>()
    val upcomingElections: LiveData<List<Election>>
        get() = _upcomingElections

    val savedElections: LiveData<List<Election>> = repository.getSavedElections()

    private val _navigateToVoterInfo = MutableLiveData<Election>()
    val navigateToVoterInfo: MutableLiveData<Election>
        get() = _navigateToVoterInfo

    init {
        getUpcomingElections()
    }

    private fun getUpcomingElections() = viewModelScope.launch {
        try {
            _upcomingElections.value = repository.getUpcomingElections()
        } catch (exception: java.lang.Exception) {
            Timber.e(exception)
        }
    }

    fun onElectionNavigated() {
        null.also {_navigateToVoterInfo.value = it }
    }

    fun displayVoterInfo(election: Election) {
        _navigateToVoterInfo.value = election
    }

}