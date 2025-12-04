package com.example.listadecomprasapp.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.listadecomprasapp.auth.AuthRepository
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.launch

class CadastroViewModel : ViewModel() {

    private val repository = AuthRepository

    private val _cadastroResult = MutableLiveData<FirebaseUser?>()
    val cadastroResult: LiveData<FirebaseUser?> = _cadastroResult

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    private val _loading = MutableLiveData<Boolean>(false)
    val loading: LiveData<Boolean> = _loading

    fun cadastrar(nome: String, email: String, senha: String) {
        _loading.postValue(true)
        viewModelScope.launch {
            try {
                val user = repository.cadastrarUsuario(email, senha)

                if (user != null) {
                    repository.salvarNomeUsuario(user, nome)

                    _cadastroResult.postValue(user)
                } else {
                    _error.postValue("Erro ao cadastrar usuário.")
                }
            } catch (e: Exception) {
                _error.postValue("Falha no cadastro: ${e.message}")
            } finally {
                _loading.postValue(false)
            }
        }
    }
}