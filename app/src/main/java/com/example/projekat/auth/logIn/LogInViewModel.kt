package com.example.projekat.auth.logIn

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projekat.auth.AuthStore
import com.example.projekat.auth.domain.LoginData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class LogInViewModel @Inject constructor(
    private val authStore: AuthStore
) : ViewModel() {

    private val _state = MutableStateFlow(LogInContract.LoginState())
    val state = _state.asStateFlow()
    private fun setState(reducer: LogInContract.LoginState.() -> LogInContract.LoginState) = _state.update(reducer)

    private val events = MutableSharedFlow<LogInContract.LoginEvent>()
    fun setEvent(event: LogInContract.LoginEvent) = viewModelScope.launch { events.emit(event) }

    init {
        observeEvents()
    }

    private fun observeEvents() {
        viewModelScope.launch {
            events.collect { event ->
                when (event) {
                    is LogInContract.LoginEvent.OnNameChange -> {
                        setState { copy(name = event.name, isNameValid = event.name != "") }
                    }
                    is LogInContract.LoginEvent.OnNicknameChange -> {
                        setState { copy(nickname = event.nickname, isNicknameValid = event.nickname.matches(Regex("^[a-zA-Z0-9_]*$"))) }
                    }
                    is LogInContract.LoginEvent.OnEmailChange -> {
                        setState { copy(email = event.email, isEmailValid = android.util.Patterns.EMAIL_ADDRESS.matcher(event.email).matches()) }
                    }
                    LogInContract.LoginEvent.OnCreateProfile -> {
                        if (state.value.isNameValid && state.value.isNicknameValid && state.value.isEmailValid) {
                            val newProfileData = LoginData(
                                fullName = state.value.name,
                                nickname = state.value.nickname,
                                email = state.value.email
                            )
                            authStore.updateProfileData(newProfileData)
                            setState { copy(isProfileCreated = true) }
                        }
                    }
                }
            }
        }
    }
}