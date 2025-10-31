package com.example.listadecomprasapp

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

// O "Chef de Cozinha" da tela AdicionarListaActivity
class AdicionarListaViewModel : ViewModel() {

    // O Chef conhece o Gerente
    private val repository = ListasRepository

    // "Quadro de avisos" para avisar o Garçom (Activity) que terminamos
    private val _concluido = MutableLiveData<Boolean>(false)
    val concluido: LiveData<Boolean> = _concluido

    // "Quadro de avisos" de erros
    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    // "Quadro de avisos" de carregamento (Loading)
    private val _loading = MutableLiveData<Boolean>(false)
    val loading: LiveData<Boolean> = _loading

    /**
     * O Garçom (Activity) chama esta função para criar uma nova lista
     */
    fun salvarLista(nome: String, uriLocal: Uri?) {
        // 1. Mostra o "Carregando..."
        _loading.postValue(true)

        viewModelScope.launch {
            try {
                // 2. Pede ao Gerente para salvar (e fazer upload se necessário)
                repository.adicionarLista(nome, uriLocal)
                // 3. Avisa no quadro que terminou com sucesso
                _concluido.postValue(true)
            } catch (e: Exception) {
                // 4. Avisa no quadro de erro
                _error.postValue(e.message)
            } finally {
                // 5. Esconde o "Carregando..."
                _loading.postValue(false)
            }
        }
    }

    // TODO: Função de Editar
}