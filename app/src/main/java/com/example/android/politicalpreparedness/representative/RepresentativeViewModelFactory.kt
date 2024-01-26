package com.example.android.politicalpreparedness.representative

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.android.politicalpreparedness.Repository

class RepresentativeViewModelFactory(private val repository: Repository): ViewModelProvider.Factory {

        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if(modelClass.isAssignableFrom(RepresentativeViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return RepresentativeViewModel(repository) as T
            }
            throw IllegalArgumentException("Unable to construct RepresentativeViewModel class")
        }

}