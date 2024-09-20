package com.xcvi.firebasesample

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.Instant
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val authService: AuthenticationService,
    private val dataRepository: DataRepository
) : ViewModel() {

    val id = "12345678"

    fun saveData(onSuccess: () -> Unit){
        viewModelScope.launch {
            dataRepository.saveData(
                "Data ${Instant.now()}"
            ){
                onSuccess()
            }
        }
    }

    fun getData(onSuccess: (DataModel)-> Unit){
        viewModelScope.launch{
            dataRepository.getDataById(id = id) {
                onSuccess(it)
            }
        }
    }

    fun logout(){
        viewModelScope.launch {
            authService.logout()
        }
    }

}





















