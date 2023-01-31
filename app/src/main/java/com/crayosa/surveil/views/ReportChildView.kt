package com.crayosa.surveil.views

import android.annotation.SuppressLint
import android.widget.TextView
import com.crayosa.surveil.R
import com.crayosa.surveil.datamodels.Progress
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.mindorks.placeholderview.annotations.Layout
import com.mindorks.placeholderview.annotations.Resolve
import com.mindorks.placeholderview.annotations.View

@Layout(R.layout.layout_progress_item)
@SuppressLint("NonConstantResourceId")
class ReportChildView(val progress : Progress) {

    @View(R.id.progress_display_name)
    lateinit var name: TextView

    @View(R.id.progress_bar)
    lateinit var progressBar: LinearProgressIndicator

    @Resolve
    fun onResolve(){
        name.text = progress.name
        progressBar.progress = progress.completion.toInt()
    }

}

