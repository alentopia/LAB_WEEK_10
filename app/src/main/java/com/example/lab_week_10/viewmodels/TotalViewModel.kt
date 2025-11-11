package com.example.lab_week_10.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class TotalViewModel: ViewModel() {
    val total = MutableLiveData<Int>().apply { value = 0 }
    fun incrementTotal() {
        val current = total.value ?: 0
        total.value = current + 1
    }
}