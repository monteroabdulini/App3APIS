package com.example.app3api.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.app3api.data.local.AppDatabase
import com.example.app3api.data.remote.RetrofitClient
import com.example.app3api.data.repository.DemonSlayerRepository
import com.example.app3api.data.repository.DragonBallRepository
import com.example.app3api.data.repository.FavoritesRepository
import com.example.app3api.data.repository.RickAndMortyRepository
import com.example.app3api.domain.ApiSource
import com.example.app3api.domain.CharacterModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed interface UiState {
    object Loading : UiState
    data class Success(
        val characters: List<CharacterModel>,
        val favoriteIds: Set<String> = emptySet()
    ) : UiState
    data class Error(val message: String) : UiState
}

private data class InternalState(
    val query: String = "",
    val onlyFavs: Boolean = false,
    val loading: Boolean = true,
    val netError: String? = null
)

class MainViewModel(application: Application) : AndroidViewModel(application) {

    companion object {
        fun Factory(application: Application): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return MainViewModel(application) as T
            }
        }
    }

    private val db = AppDatabase.getInstance(application)
    private val favoritesRepo = FavoritesRepository(db.favoriteDao())

    private val demonSlayerRepo = DemonSlayerRepository(RetrofitClient.demonSlayerApiService, db.characterCacheDao())
    private val rickAndMortyRepo = RickAndMortyRepository(RetrofitClient.rickAndMortyApiService, db.characterCacheDao())
    private val dragonBallRepo = DragonBallRepository(RetrofitClient.dragonBallApiService, db.characterCacheDao())

    private val _selectedApi = MutableStateFlow(ApiSource.DEMON_SLAYER)
    val selectedApi: StateFlow<ApiSource> = _selectedApi.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _showOnlyFavorites = MutableStateFlow(false)
    val showOnlyFavorites: StateFlow<Boolean> = _showOnlyFavorites.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    private val _networkError = MutableStateFlow<String?>(null)

    private val _selectedCharacter = MutableStateFlow<CharacterModel?>(null)
    val selectedCharacter: StateFlow<CharacterModel?> = _selectedCharacter.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    private val cachedCharactersFlow = _selectedApi.flatMapLatest { apiSource ->
        when (apiSource) {
            ApiSource.DEMON_SLAYER -> demonSlayerRepo.observeCharacters()
            ApiSource.RICK_AND_MORTY -> rickAndMortyRepo.observeCharacters()
            ApiSource.DRAGON_BALL -> dragonBallRepo.observeCharacters()
        }
    }

    private val _internalState = combine(
        _searchQuery,
        _showOnlyFavorites,
        _isLoading,
        _networkError
    ) { query, onlyFavs, loading, netError ->
        InternalState(query, onlyFavs, loading, netError)
    }

    val uiState: StateFlow<UiState> = combine(
        cachedCharactersFlow,
        favoritesRepo.observeFavorites(),
        favoritesRepo.observeFavoriteIds(),
        _internalState
    ) { cachedChars, favChars, favIds, state ->

        val sourceList = if (state.onlyFavs) {
            favChars.filter { it.sourceApi == _selectedApi.value }
        } else {
            cachedChars
        }

        val filteredList = if (state.query.isBlank()) {
            sourceList
        } else {
            sourceList.filter { character ->
                character.name.contains(state.query, ignoreCase = true) ||
                character.statusOrRace.contains(state.query, ignoreCase = true) ||
                character.description.contains(state.query, ignoreCase = true)
            }
        }

        if (filteredList.isEmpty() && state.loading) {
            UiState.Loading
        } else if (filteredList.isEmpty() && state.netError != null && !state.onlyFavs) {
            UiState.Error(state.netError)
        } else {
            UiState.Success(
                characters = filteredList,
                favoriteIds = favIds
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = UiState.Loading
    )

    init {
        loadCharactersForSource(_selectedApi.value)
    }

    fun selectApi(apiSource: ApiSource) {
        if (_selectedApi.value != apiSource) {
            _selectedApi.value = apiSource
            _searchQuery.value = ""
            loadCharactersForSource(apiSource)
        }
    }

    fun toggleShowOnlyFavorites() {
        _showOnlyFavorites.value = !_showOnlyFavorites.value
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

    fun toggleFavorite(character: CharacterModel) {
        viewModelScope.launch {
            try {
                val currentFavIds = (uiState.value as? UiState.Success)?.favoriteIds ?: emptySet()
                if (character.id in currentFavIds) {
                    favoritesRepo.removeFavorite(character.id)
                } else {
                    favoritesRepo.addFavorite(character)
                }
            } catch (_: Exception) {}
        }
    }

    private fun loadCharactersForSource(apiSource: ApiSource) {
        viewModelScope.launch {
            _isLoading.value = true
            _networkError.value = null

            val hasCache = try {
                when (apiSource) {
                    ApiSource.DEMON_SLAYER -> demonSlayerRepo.hasCachedCharacters()
                    ApiSource.RICK_AND_MORTY -> rickAndMortyRepo.hasCachedCharacters()
                    ApiSource.DRAGON_BALL -> dragonBallRepo.hasCachedCharacters()
                }
            } catch (_: Exception) {
                false
            }

            val result = when (apiSource) {
                ApiSource.DEMON_SLAYER -> demonSlayerRepo.fetchAndCacheCharacters()
                ApiSource.RICK_AND_MORTY -> rickAndMortyRepo.fetchAndCacheCharacters()
                ApiSource.DRAGON_BALL -> dragonBallRepo.fetchAndCacheCharacters()
            }

            _isLoading.value = false

            result.onFailure { error ->
                if (!hasCache) {
                    _networkError.value = error.localizedMessage ?: "Error al obtener los datos."
                }
            }
        }
    }
}
