package com.crayosa.surveil.utils

import java.lang.StringBuilder


class YouTubeUtils{
    companion object{
        fun getId(url : String) : String{
            return StringBuilder(url).substring(17,28)
        }
    }
}