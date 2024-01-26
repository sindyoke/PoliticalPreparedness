package com.example.android.politicalpreparedness.election

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.android.politicalpreparedness.Repository
import com.example.android.politicalpreparedness.network.models.Division

class VoterInfoViewModelFactory(
    private val repository: Repository,
    private val electionId: Int,
    private val division: Division) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(VoterInfoViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return VoterInfoViewModel(repository, electionId, division) as T
        }
        throw IllegalArgumentException("Unable to construct VoterInfoViewModel class")
    }
}