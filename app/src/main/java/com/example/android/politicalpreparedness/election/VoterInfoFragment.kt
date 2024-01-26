package com.example.android.politicalpreparedness.election

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.example.android.politicalpreparedness.MyApp
import com.example.android.politicalpreparedness.databinding.FragmentVoterInfoBinding

class VoterInfoFragment : Fragment() {

    private lateinit var viewModel: VoterInfoViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) : View {

        val appContainer = (requireActivity().application as MyApp).appContainer

        val args = navArgs<VoterInfoFragmentArgs>()
        val electionId = args.value.argElectionId
        val division = args.value.argDivision

        val voterInfoViewModelFactory = VoterInfoViewModelFactory(appContainer.repository, electionId, division)
        viewModel = ViewModelProvider(this, voterInfoViewModelFactory)[VoterInfoViewModel::class.java]

        val binding = FragmentVoterInfoBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        return binding.root
    }

}