package com.example.android.politicalpreparedness.network.models

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class VoterInfoResponse (
    val election: Election,
    @Transient val pollingLocations: String? = null,
    @Transient val contests: String? = null,
    val state: List<State>?,
    val electionElectionOfficials: List<ElectionOfficial>? = null
)