package com.example.budgetbuddy.modules

import com.example.budgetbuddy.validations.validators.NameValidator
import com.example.budgetbuddy.validations.validators.EmailValidator
import com.example.budgetbuddy.validations.validators.PasswordValidator
import com.example.budgetbuddy.validations.validators.UsernameValidator
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/**
 * Modulo que sirve para implementar inyección de dependencias al ViewModel de RegisterViewModel
 * Sirve para inyectar los validadores necesarios para validar los datos del formulario de registro.
 * */
@Module
@InstallIn(SingletonComponent::class)
class ValidationsModule {
    @Provides
    fun provideNamesValidator(): NameValidator = NameValidator

    @Provides
    fun provideEmailValidator(): EmailValidator = EmailValidator

    @Provides
    fun providePasswordValidator(): PasswordValidator = PasswordValidator

    @Provides
    fun provideUsernameValidator(): UsernameValidator = UsernameValidator


}