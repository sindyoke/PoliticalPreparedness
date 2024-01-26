package com.example.android.politicalpreparedness.election.adapter

import android.content.Intent
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.election.WebViewActivity
import com.example.android.politicalpreparedness.network.models.Election
import timber.log.Timber

@BindingAdapter("android:listData")
fun bindListToRecycler(recyclerView: RecyclerView, list: List<Election>?) {
    val adapter = recyclerView.adapter as ElectionListAdapter
    if(list != null) {
        adapter.elections = list
    }
}

@BindingAdapter("clickableLink")
fun bindClickableText(textView: TextView, url: String?) {
    if (url.isNullOrEmpty()) return
    val spannableString = SpannableString(url)
    val clickableSpan: ClickableSpan = object : ClickableSpan() {
        override fun onClick(view: View) {
            val intent = Intent(textView.context, WebViewActivity::class.java)
            intent.putExtra("URL", url)
            textView.context.startActivity(intent)
        }
    }

    spannableString.setSpan(clickableSpan, 0, url.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
    textView.setText(spannableString, TextView.BufferType.SPANNABLE)
    textView.movementMethod = LinkMovementMethod.getInstance()
}