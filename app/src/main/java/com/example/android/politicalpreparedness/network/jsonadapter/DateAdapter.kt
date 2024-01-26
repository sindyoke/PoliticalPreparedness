package com.example.android.politicalpreparedness.network.jsonadapter

import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import java.text.SimpleDateFormat
import java.util.*

class DateAdapter {

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    @FromJson
    fun dateFromJson(dateString: String): Date {
        return dateFormat.parse(dateString)
    }

    @ToJson
    fun dateToJson(date: Date): String {
        return dateFormat.format(date)
    }


}
