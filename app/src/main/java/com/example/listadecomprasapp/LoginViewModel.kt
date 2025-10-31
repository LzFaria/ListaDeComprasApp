package com.example.listadecomprasapp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.launch

// Nosso "Chef de Cozinha" para a tela de Login
class LoginViewModel : ViewModel() {

    // O Chef tem o contato do "Gerente da Cozinha"
    private val repository = AuthRepository

    // Este é o "quadro de avisos" que o Garçom (Activity) vai observar.
    // O '_' no início indica que esta é a versão interna, que só o Chef pode alterar.
    private val _loginResult = MutableLiveData<FirebaseUser?>()

    // Esta é a versão pública do quadro de avisos, que o Garçom só pode LER.
    val loginResult: LiveData<FirebaseUser?> = _loginResult

    // Um quadro de avisos separado para mensagens de erro
    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    /**
     * O Chef recebe o pedido do Garçom e começa a "cozinhar" (a lógica).
     * viewModelScope.launch é a "cozinha" onde o trabalho demorado acontece
     * sem atrapalhar o salão (a interface do app).
     */
    fun login(email: String, senha: String) {
        viewModelScope.launch {
            try {
                // 1. Pede para o Gerente buscar o ingrediente (fazer o login)
                val user = repository.loginUsuario(email, senha)
                // 2. Se deu certo, avisa no "quadro de avisos" de sucesso
                _loginResult.postValue(user)
            } catch (e: Exception) {
                // 3. Se deu errado, avisa no "quadro de avisos" de erro
                _error.postValue("Falha no login: ${e.message}")
            }
        }
    }
}