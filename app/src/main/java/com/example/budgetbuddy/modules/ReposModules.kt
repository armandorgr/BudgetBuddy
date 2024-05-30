package com.example.budgetbuddy.modules

import com.example.budgetbuddy.repositories.BalanceRepository
import com.example.budgetbuddy.repositories.ExpenseRepository
import com.example.budgetbuddy.repositories.GroupRepository
import com.example.budgetbuddy.repositories.InvitationsRepository
import com.example.budgetbuddy.repositories.MessageRepository
import com.example.budgetbuddy.repositories.StorageRepository
import com.example.budgetbuddy.repositories.UsersRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent


/**
 * Modulo que sirve para implementar inyeccion de dependencias al viewmodel de RegisterViewModel
 * Sirve para inyectar el repositorio de usuarios necesario para realizar las operaciones relacionados
 * con ellos.
 * Uso de la inyección de dependencias consultado aquí:
 * https://www.packtpub.com/product/how-to-build-android-apps-with-kotlin-second-edition/9781837634934
 * capítulo Dependency Injection with Dagger, Hilt, and Koin
 * @author Armando Guzmán
 * */
@Module
@InstallIn(SingletonComponent::class)
class ReposModules {
    /**
     * Método que sirve para inyecctar un objeto del repositorio [UsersRepository]
     * @return Objeto del repositorio de usuarios.
     * */
    @Provides
    fun provideUsersRepo(): UsersRepository = UsersRepository()

    /**
     * Método que sirve para inyecctar un objeto del repositorio [InvitationsRepository]
     * @return Objeto del repositorio de invitaciones.
     * */
    @Provides
    fun provideInvitationsRepo(): InvitationsRepository = InvitationsRepository()

    /**
     * Método que sirve para inyecctar un objeto del repositorio [GroupRepository]
     * @return Objeto del repositorio de grupos.
     * */
    @Provides
    fun provideGroupRepo(): GroupRepository = GroupRepository()


    /**
     * Método que sirve para inyecctar un objeto del repositorio [StorageRepository]
     * @return Objeto del repositorio de Firebase Storage.
     * */
    @Provides
    fun provideStorageRepository(): StorageRepository = StorageRepository()

    /**
     * Método que sirve para inyecctar un objeto del repositorio [MessageRepository]
     * @return Objeto del repositorio de mensajes.
     * */
    @Provides
    fun provideMessagesRepository(): MessageRepository = MessageRepository()

    @Provides
    fun provideExpenseRepository(): ExpenseRepository = ExpenseRepository()

    @Provides
    fun provideBalanceRepository(): BalanceRepository = BalanceRepository()
}