package com.custom.app.ui.createData.analytics.utils

import android.os.Bundle
import androidx.fragment.app.Fragment

abstract class DemoBaseFragment : Fragment() {
    protected val months = arrayOf(
            "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Okt", "Nov", "Dec"
    )
    protected val parties = arrayOf(
            "CC 1", "CC 2", "CC 3", "CC 4", "CC 5", "CC 6", "CC 7", "CC 8",
            "CC 9", "CC 10", "CC 11", "CC 12", "CC 13", "CC 14", "CC 15", "CC 16",
            "CC 17", "CC 18", "CC 19", "CC 20", "CC 21", "CC 22", "CC 23", "CC 24",
            "CC 25", "CC 26"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    protected fun getRandom(range: Float, start: Float): Float {
        return (Math.random() * range).toFloat() + start
    }
}
