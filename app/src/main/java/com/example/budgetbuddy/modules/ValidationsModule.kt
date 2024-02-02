package com.example.budgetbuddy.modules

import com.example.budgetbuddy.validations.validators.NameValidator
import com.example.budgetbuddy.validations.validators.EmailValidator
import com.example.budgetbuddy.validations.validators.PasswordValidator
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent


@Module
@InstallIn(SingletonComponent::class)
class ValidationsModule {
    @Provides
    fun provideNamesValidator(): NameValidator {
        return NameValidator
    }

    @Provides
    fun provideEmailValidator(): EmailValidator {
        return EmailValidator
    }

    @Provides
    fun providePasswordValidator(): PasswordValidator {
        return PasswordValidator
    }


}