package com.example.android.politicalpreparedness.representative

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import android.provider.Settings
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.android.politicalpreparedness.BuildConfig
import com.example.android.politicalpreparedness.MyApp
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.databinding.FragmentRepresentativeBinding
import com.example.android.politicalpreparedness.network.models.Address
import com.example.android.politicalpreparedness.representative.adapter.RepresentativeListAdapter
import com.example.android.politicalpreparedness.representative.adapter.setNewValue
import com.example.android.politicalpreparedness.representative.model.Representative
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.snackbar.Snackbar
import timber.log.Timber
import java.util.*

class DetailFragment : Fragment() {

    companion object {
        private const val REQUEST_LOCATION_PERMISSION = 222
        const val MOTION_LAYOUT_STATE_KEY = "MOTION_LAYOUT_STATE_KEY"
        private const val ADDRESS1_KEY = "ADDRESS1_KEY"
        private const val ADDRESS2_KEY = "ADDRESS2_KEY"
        private const val CITY_KEY = "CITY_KEY"
        private const val ZIP_KEY = "ZIP_KEY"
        private const val REP_LIST_KEY = "REP_LIST_KEY"
    }

    private lateinit var viewModel: RepresentativeViewModel
    private lateinit var binding: FragmentRepresentativeBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var address: Address
    private lateinit var representativesAdapter: RepresentativeListAdapter
    private var representativesList: List<Representative>? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // Establish bindings
        val appContainer = (requireActivity().application as MyApp).appContainer
        val representativesViewModelFactory =
            RepresentativeViewModelFactory(appContainer.repository)
        viewModel = ViewModelProvider(
            this,
            representativesViewModelFactory
        ).get(RepresentativeViewModel::class.java)
        binding = FragmentRepresentativeBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        representativesAdapter = RepresentativeListAdapter()
        binding.fragRepRv.adapter = representativesAdapter
        binding.executePendingBindings()

        if(savedInstanceState != null) {
            viewModel.address.value?.line1 ?: savedInstanceState.getString(ADDRESS1_KEY)
            viewModel.address.value?.line2 ?: savedInstanceState.getString(ADDRESS2_KEY)
            viewModel.address.value?.city ?: savedInstanceState.getString(CITY_KEY)
            viewModel.address.value?.zip ?: savedInstanceState.getString(ZIP_KEY)

            binding.motionLayout.transitionToState(savedInstanceState.getInt(MOTION_LAYOUT_STATE_KEY))

            representativesList = savedInstanceState.getParcelableArrayList(REP_LIST_KEY)
            representativesList?.let { viewModel.setRepresentatives(it) }
        }

        viewModel.representatives.observe(viewLifecycleOwner) {
            representativesAdapter.submitList(it)
            representativesList = it
        }

        binding.buttonSearch.setOnClickListener { runFieldSearch() }
        binding.buttonLocation.setOnClickListener { runLocationSearch() }

        fusedLocationClient =
            LocationServices.getFusedLocationProviderClient(requireActivity())

        return binding.root
    }

    override fun onSaveInstanceState(outState: Bundle) {

        outState.putInt(MOTION_LAYOUT_STATE_KEY, binding.motionLayout.currentState)

        outState.putString(ADDRESS1_KEY, binding.addressLine1.text.toString())
        outState.putString(ADDRESS2_KEY, binding.addressLine2.text.toString())
        outState.putString(CITY_KEY, binding.city.text.toString())
        outState.putString(ZIP_KEY, binding.zip.text.toString())

        if(viewModel.representatives.value != null) {
            outState.putParcelableArrayList(
                REP_LIST_KEY,
                viewModel.representatives.value as ArrayList<out Parcelable>
            )
        }

        super.onSaveInstanceState(outState)
    }

    private fun runFieldSearch() {
        hideKeyboard()
        if (isNotValidEntry()) {
            Toast.makeText(
                activity,
                "Fill out obligatory fields: address line 1, city and zip",
                Toast.LENGTH_SHORT
            ).show()
            return
        }
        val stateSelected = binding.state.selectedItem
        viewModel.setState(stateSelected as String)
        viewModel.fetchRepresentatives(viewModel.address.value!!.toFormattedString())
    }

    private fun isNotValidEntry(): Boolean {
        return binding.addressLine1.text.isNullOrBlank() ||
                binding.city.text.isNullOrBlank() ||
                binding.zip.text.isNullOrBlank()
    }

    private fun runLocationSearch() {
        hideKeyboard()
        checkLocationPermissions()
    }

    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (REQUEST_LOCATION_PERMISSION == requestCode) {
            if (grantResults.isNotEmpty() && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                getLocation()
            } else if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                Toast.makeText(
                    requireActivity(),
                    R.string.location_permission_denied_explanation,
                    Toast.LENGTH_LONG
                ).show()
            } else {
                val builder = AlertDialog.Builder(requireContext())
                builder.setTitle(getString(R.string.permission_required_dialog_title))
                    .setMessage(R.string.permission_required_dialog_text)
                    .setPositiveButton(
                        getString(R.string.accept)
                    ) { dialog, _ ->
                        startActivity(Intent().apply {
                            action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                            data = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null)
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        })
                        dialog.dismiss()
                    }
                    .setNegativeButton(
                        R.string.decline
                    ) { dialog, _ ->
                        Snackbar.make(
                            binding.root,
                            "Unable to get your address",
                            Snackbar.LENGTH_LONG
                        ).show()
                        dialog.dismiss()
                    }
                    .show()
            }
        }
    }

    private fun checkLocationPermissions() {
        if (isPermissionGranted()) {
            getLocation()
        } else {
            requestPermissions(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_LOCATION_PERMISSION
            )
        }
    }

    private fun isPermissionGranted(): Boolean {
        return checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    @SuppressLint("MissingPermission")
    private fun getLocation() {

        val locationResult = fusedLocationClient.lastLocation
        locationResult.addOnSuccessListener { location ->
            address = geoCodeLocation(location)
            binding.state.setNewValue(address.state)
            viewModel.getAddressFromGeolocation(address)
            viewModel.fetchRepresentatives(address.toFormattedString())
        }
    }

    private fun geoCodeLocation(location: Location): Address {
        val geocoder = Geocoder(requireContext(), Locale.getDefault())
        return geocoder.getFromLocation(location.latitude, location.longitude, 1)!!
            .map { address ->
                Timber.d("address: $address")
                Address(
                    address.thoroughfare,
                    address.subThoroughfare,
                    address.locality,
                    address.adminArea,
                    address.postalCode
                )
            }
            .first()
    }

    private fun hideKeyboard() {
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(requireView().windowToken, 0)
    }

}