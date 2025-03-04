package com.example.ggconnect.ui.chatrooms

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ChatroomsViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is chatrooms Fragment"
    }
    val text: LiveData<String> = _text
}