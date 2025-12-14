package com.example.habittracker.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.habittracker.data.model.User
import com.example.habittracker.data.repository.UserRepository
import kotlinx.coroutines.launch

class AuthViewModel(private val repository: UserRepository) : ViewModel() {

    // Trạng thái đăng ký
    private val _registerResult = MutableLiveData<Result<Long>>()
    val registerResult: LiveData<Result<Long>> = _registerResult

    // Trạng thái đăng nhập
    private val _loginResult = MutableLiveData<Result<User>>()
    val loginResult: LiveData<Result<User>> = _loginResult

    fun register(name: String, email: String, pass: String) {
        viewModelScope.launch {
            val newUser = User(name = name, email = email, password = pass)
            _registerResult.value = repository.registerUser(newUser)
        }
    }

    fun login(email: String, pass: String) {
        viewModelScope.launch {
            _loginResult.value = repository.loginUser(email, pass)
        }
    }
}