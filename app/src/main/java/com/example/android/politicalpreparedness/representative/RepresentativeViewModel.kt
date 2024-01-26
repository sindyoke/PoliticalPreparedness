package com.example.android.politicalpreparedness.representative

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.Repository
import com.example.android.politicalpreparedness.network.models.Address
import com.example.android.politicalpreparedness.representative.model.Representative
import kotlinx.coroutines.launch
import timber.log.Timber
class RepresentativeViewModel(private val repository: Repository) : ViewModel() {

    private val _representatives = MutableLiveData<List<Representative>?>()
    val representatives: LiveData<List<Representative>?>
        get() = _representatives

    private val _address = MutableLiveData<Address>()
    val address: LiveData<Address>
        get() = _address

    init {
        _address.value = Address("", "", "", "", "")
    }

    fun fetchRepresentatives(addressString: String) {
        viewModelScope.launch {
            try {
                val (offices, officials) = repository.getRepresentatives(addressString)
                _representatives.value =
                    offices.flatMap { office -> office.getRepresentatives(officials) }
            } catch (e: Exception) {
                _representatives.value = null
            }
        }
    }

    fun getAddressFromGeolocation (address: Address) {
        _address.value = address
    }

    fun setState(stateSelected: String) {
        _address.value!!.state = stateSelected
    }

// DONE: Create function to get address from individual fields
// - no need, everything is set from two way binding, just state from code

}
