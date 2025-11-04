package com.example.listadecomprasapp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.launch

// Nosso "Chef de Cozinha" para a tela de Login
class LoginViewModel : ViewModel() {

    private val repository = AuthRepository

    // Quadro de avisos de SUCESSO DE LOGIN (Sem mudanças)
    private val _loginResult = MutableLiveData<FirebaseUser?>()
    val loginResult: LiveData<FirebaseUser?> = _loginResult

    // Quadro de avisos de ERRO (Sem mudanças)
    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    // --- 1. NOVO QUADRO DE AVISOS ---
    // Para avisar o Garçom (Activity) que o e-mail de recuperação foi enviado
    private val _resetEnviado = MutableLiveData<Boolean>(false)
    val resetEnviado: LiveData<Boolean> = _resetEnviado

    /**
     * O Chef recebe o pedido de LOGIN (Sem mudanças)
     */
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

    // --- 2. NOVA FUNÇÃO ---
    /**
     * O Chef recebe o pedido de RECUPERAÇÃO DE SENHA
     */
    fun recuperarSenha(email: String) {
        viewModelScope.launch {
            try {
                // 1. Pede ao Gerente para enviar o e-mail
                repository.recuperarSenha(email)
                // 2. Avisa no quadro de sucesso
                _resetEnviado.postValue(true)
            } catch (e: Exception) {
                // 3. Avisa no quadro de erro
                _error.postValue("Falha ao enviar e-mail: ${e.message}")
            }
        }
    }
}