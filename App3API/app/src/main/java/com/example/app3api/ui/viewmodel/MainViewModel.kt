package com.example.app3api.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app3api.data.remote.RetrofitClient
import com.example.app3api.data.repository.DemonSlayerRepository
import com.example.app3api.data.repository.DragonBallRepository
import com.example.app3api.data.repository.RickAndMortyRepository
import com.example.app3api.domain.ApiSource
import com.example.app3api.domain.CharacterModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

sealed interface UiState {
    object Loading : UiState
    data class Success(val characters: List<CharacterModel>) : UiState
    data class Error(val message: String) : UiState
}

class MainViewModel(
    private val demonSlayerRepo: DemonSlayerRepository = DemonSlayerRepository(RetrofitClient.demonSlayerApiService),
    private val rickAndMortyRepo: RickAndMortyRepository = RickAndMortyRepository(RetrofitClient.rickAndMortyApiService),
    private val dragonBallRepo: DragonBallRepository = DragonBallRepository(RetrofitClient.dragonBallApiService)
) : ViewModel() {

    private val _selectedApi = MutableStateFlow(ApiSource.DEMON_SLAYER)
    val selectedApi: StateFlow<ApiSource> = _selectedApi.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _allCharacters = MutableStateFlow<List<CharacterModel>>(emptyList())
    
    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val _selectedCharacter = MutableStateFlow<CharacterModel?>(null)
    val selectedCharacter: StateFlow<CharacterModel?> = _selectedCharacter.asStateFlow()

    init {
        // Combinar la lista completa de personajes con el query de búsqueda
        viewModelScope.launch {
            combine(_allCharacters, _searchQuery) { characters, query ->
                if (query.isBlank()) {
                    characters
                } else {
                    characters.filter { character ->
                        character.name.contains(query, ignoreCase = true) ||
                        character.statusOrRace.contains(query, ignoreCase = true) ||
                        character.description.contains(query, ignoreCase = true)
                    }
                }
            }.collect { filteredList ->
                if (_uiState.value !is UiState.Loading && _uiState.value !is UiState.Error) {
                    _uiState.value = UiState.Success(filteredList)
                }
            }
        }

        loadCharactersForSource(ApiSource.DEMON_SLAYER)
    }

    fun selectApi(apiSource: ApiSource) {
        if (_selectedApi.value != apiSource) {
            _selectedApi.value = apiSource
            _searchQuery.value = ""
            loadCharactersForSource(apiSource)
        }
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun refresh() {
        loadCharactersForSource(_selectedApi.value)
    }

    fun selectCharacter(character: CharacterModel?) {
        _selectedCharacter.value = character
    }

    private fun loadCharactersForSource(apiSource: ApiSource) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            val result = when (apiSource) {
                ApiSource.DEMON_SLAYER -> demonSlayerRepo.getCharacters()
                ApiSource.RICK_AND_MORTY -> rickAndMortyRepo.getCharacters()
                ApiSource.DRAGON_BALL -> dragonBallRepo.getCharacters()
            }

            result.fold(
                onSuccess = { characters ->
                    _allCharacters.value = characters
                    val query = _searchQuery.value
                    val filtered = if (query.isBlank()) characters else characters.filter {
                        it.name.contains(query, ignoreCase = true) ||
                        it.statusOrRace.contains(query, ignoreCase = true) ||
                        it.description.contains(query, ignoreCase = true)
                    }
                    _uiState.value = UiState.Success(filtered)
                },
                onFailure = { error ->
                    _uiState.value = UiState.Error(
                        error.localizedMessage ?: "Error desconocido al cargar los datos."
                    )
                }
            )
        }
    }
}
