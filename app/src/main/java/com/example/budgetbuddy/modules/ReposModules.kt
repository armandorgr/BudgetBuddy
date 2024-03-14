package com.example.budgetbuddy.modules

import com.example.budgetbuddy.repositories.GroupRepository
import com.example.budgetbuddy.repositories.InvitationsRepository
import com.example.budgetbuddy.repositories.UsersRepository
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent


/**
 * Modulo que sirve para implementar inyeccion de dependencias al viewmodel de RegisterViewModel
 * Sirve para inyectar el repositorio de usuarios necesario para realizar las operaciones relacionados
 * con ellos.
 * */
@Module
@InstallIn(SingletonComponent::class)
class ReposModules {
    @Provides
    fun provideUsersRepo(): UsersRepository = UsersRepository()

    @Provides
    fun provideInvitationsRepo(): InvitationsRepository = InvitationsRepository()

    @Provides
    fun provideGroupRepo(): GroupRepository = GroupRepository()
}