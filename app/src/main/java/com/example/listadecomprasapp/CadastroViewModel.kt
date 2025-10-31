package com.example.listadecomprasapp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.launch

// Nosso "Chef de Cozinha" para a tela de Cadastro
class CadastroViewModel : ViewModel() {

    // O Chef tem o contato do "Gerente da Cozinha"
    private val repository = AuthRepository

    // O "quadro de avisos" de SUCESSO
    private val _cadastroResult = MutableLiveData<FirebaseUser?>()
    val cadastroResult: LiveData<FirebaseUser?> = _cadastroResult

    // O "quadro de avisos" de ERRO
    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    /**
     * O Chef recebe o pedido do Garçom (CadastroActivity)
     */
    fun cadastrar(nome: String, email: String, senha: String) {
        // Começa a "cozinhar" (trabalho assíncrono)
        viewModelScope.launch {
            try {
                // 1. Pede ao Gerente para cadastrar no Auth (RF002 - Parte 1)
                val user = repository.cadastrarUsuario(email, senha)

                if (user != null) {
                    // 2. Pede ao Gerente para salvar o nome no Firestore (RF002 - Parte 2)
                    repository.salvarNomeUsuario(user, nome)

                    // 3. Avisa no quadro de SUCESSO
                    _cadastroResult.postValue(user)
                } else {
                    _error.postValue("Erro ao cadastrar usuário.")
                }
            } catch (e: Exception) {
                // 3. Avisa no quadro de ERRO
                _error.postValue("Falha no cadastro: ${e.message}")
            }
        }
    }
}