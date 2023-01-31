package com.crayosa.surveil.listener

import com.crayosa.surveil.datamodels.ClassRoom
import com.crayosa.surveil.datamodels.Lecture

abstract class OnItemClickListener {
    open fun onClick(classroom : ClassRoom){}
    open fun onClick(lecture: Lecture, position : Int){}
}