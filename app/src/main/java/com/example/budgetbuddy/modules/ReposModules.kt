package com.example.budgetbuddy.modules

import com.example.budgetbuddy.repositories.UsersRepository
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
}