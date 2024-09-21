package com.xcvi.firebasesample

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val authService: AuthenticationService,
    private val dataRepository: DataRepository
) : ViewModel() {

    var state = MutableStateFlow(UiState())

    data class UiState(
        val data: List<DataModel> = emptyList(),
        val errorMessage: String = ""
    )

    init {
        viewModelScope.launch {
            dataRepository.observeData().collect{ result ->
                result.onSuccess { data ->
                    state.update {
                        it.copy(data = data)
                    }
                }.onFailure { error ->
                    state.update {
                        it.copy(errorMessage = error.message.toString())
                    }
                }
            }
        }
    }

    fun saveData(onSuccess: () -> Unit){
        viewModelScope.launch {
            dataRepository.saveData(
                "Data ${Instant.now()}"
            ){
                onSuccess()
            }
        }
    }

    fun getData(onSuccess: ()-> Unit, onFailure: (Exception) -> Unit){
//        viewModelScope.launch{
//            dataRepository.getData(
//                onSuccess = {
//                    data.value = it
//                    onSuccess()
//                },
//                onFailure = {
//                    onFailure(it)
//                }
//            )
//        }
    }

    fun logout(){
        viewModelScope.launch {
            authService.logout()
        }
    }

}





















