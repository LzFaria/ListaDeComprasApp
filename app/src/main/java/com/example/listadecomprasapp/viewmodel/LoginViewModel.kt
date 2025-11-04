package com.example.listadecomprasapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.listadecomprasapp.repository.AuthRepository
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {

    private val repository = AuthRepository

    private val _loginResult = MutableLiveData<FirebaseUser?>()
    val loginResult: LiveData<FirebaseUser?> = _loginResult

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    private val _resetEnviado = MutableLiveData<Boolean>(false)
    val resetEnviado: LiveData<Boolean> = _resetEnviado

    fun login(email: String, senha: String) {
        viewModelScope.launch {
            try {
                val user = repository.loginUsuario(email, senha)
                _loginResult.postValue(user)
            } catch (e: Exception) {
                _error.postValue("Falha no login: ${e.message}")
            }
        }
    }

    fun recuperarSenha(email: String) {
        viewModelScope.launch {
            try {
                repository.recuperarSenha(email)
                _resetEnviado.postValue(true)
            } catch (e: Exception) {
                _error.postValue("Falha ao enviar e-mail: ${e.message}")
            }
        }
    }
}