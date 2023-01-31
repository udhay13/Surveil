package com.crayosa.surveil.views

import android.annotation.SuppressLint
import android.widget.TextView
import com.crayosa.surveil.R
import com.mindorks.placeholderview.annotations.Layout
import com.mindorks.placeholderview.annotations.Resolve
import com.mindorks.placeholderview.annotations.View
import com.mindorks.placeholderview.annotations.expand.Parent

@SuppressLint("NonConstantResourceId")
@Parent
@Layout(R.layout.layout_report_head)
class ReportHeadView(private val title : String, private val count : Int) {

    @View(R.id.report_header)
    lateinit var textView : TextView

    @View(R.id.report_count)
    lateinit var progressCount : TextView
    @Resolve
    fun onResolve(){
        textView.text = title
        progressCount.text = count.toString()
    }
}