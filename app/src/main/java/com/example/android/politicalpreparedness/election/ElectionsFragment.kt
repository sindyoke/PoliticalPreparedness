package com.example.android.politicalpreparedness.election

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.android.politicalpreparedness.MyApp
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.databinding.FragmentElectionBinding
import com.example.android.politicalpreparedness.election.adapter.ElectionListAdapter
import com.example.android.politicalpreparedness.election.adapter.ElectionListener
import com.example.android.politicalpreparedness.network.jsonadapter.ElectionAdapter
import timber.log.Timber

class ElectionsFragment : Fragment() {

    private lateinit var binding: FragmentElectionBinding
    private lateinit var viewModel: ElectionsViewModel
    private lateinit var savedAdapter: ElectionListAdapter

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val appContainer = (requireActivity().application as MyApp).appContainer
        val electionsViewModelFactory = ElectionsViewModelFactory(appContainer.repository)
        viewModel =
            ViewModelProvider(this, electionsViewModelFactory).get(ElectionsViewModel::class.java)

        binding = FragmentElectionBinding.inflate(inflater, container, false)
        binding.apply {
            lifecycleOwner = this@ElectionsFragment
            electionsViewModel = viewModel
        }

        val upcomingAdapter = ElectionListAdapter(ElectionListener { election ->
            viewModel.displayVoterInfo(election)
        })
        savedAdapter = ElectionListAdapter(ElectionListener { election ->
            viewModel.displayVoterInfo(election)
        })

        binding.fragelecRvUpcoming.adapter = upcomingAdapter
        binding.fragelecRvUpcoming.setHasFixedSize(true)
        binding.fragelecRvSaved.adapter = savedAdapter
        binding.fragelecRvSaved.setHasFixedSize(true)


        viewModel.upcomingElections.observe(viewLifecycleOwner) { elections ->
            elections?.let {
                upcomingAdapter.submitList(elections)
            }
        }

        viewModel.savedElections.observe(viewLifecycleOwner) { elections ->
            elections?.let {
                savedAdapter.submitList(elections)
                savedAdapter.notifyDataSetChanged()
            }
        }

        viewModel.navigateToVoterInfo.observe(viewLifecycleOwner) {
            if (null != it) {
                findNavController().navigate(
                    ElectionsFragmentDirections.actionElectionsFragmentToVoterInfoFragment(
                        it.id,
                        it.division
                    )
                )
                viewModel.onElectionNavigated()
            }
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.lifecycleOwner = null
        binding.unbind()
    }

}