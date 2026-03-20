package com.qrmobil.admin.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.qrmobil.admin.data.Feedback
import com.qrmobil.admin.data.MockDatabase

class FeedbacksViewModel : ViewModel() {
    private val _feedbacks = mutableStateListOf<Feedback>()
    val feedbacks: List<Feedback> get() = _feedbacks

    init {
        _feedbacks.addAll(MockDatabase.feedbacks)
    }
}
