package com.example.android.politicalpreparedness

import android.content.Context
import com.example.android.politicalpreparedness.database.ElectionDatabase

class AppContainer(applicationContext: Context) {

    private val database = ElectionDatabase.getInstance(applicationContext)

    val repository = Repository(database)
}