package com.example.phase1.vm

import androidx.lifecycle.ViewModel
import com.example.phase1.data.repository.ImageRepository
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val imageRepository: ImageRepository
) : ViewModel() {
    suspend fun login(email: String, password: String) : String?
    {
        return imageRepository.login(email, password)
    }
    suspend fun createUser(email: String, password: String) : String?
    {
        return imageRepository.createUser(email, password)
    }
    fun getUser() : FirebaseUser?
    {
        return imageRepository.getUser()
    }
    suspend fun logout() : String?
    {
        return imageRepository.logout()
    }

}