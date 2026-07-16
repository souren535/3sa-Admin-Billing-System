package com.threesa.billing.di

import com.threesa.billing.data.repository.AuthRepositoryImpl
import com.threesa.billing.data.repository.MockDashboardRepositoryImpl
import com.threesa.billing.domain.repository.AuthRepository
import com.threesa.billing.domain.repository.DashboardRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    abstract fun bindAuthRepository(
        impl: AuthRepositoryImpl
    ): AuthRepository

    @Binds
    abstract fun bindDashboardRepository(impl: MockDashboardRepositoryImpl): DashboardRepository
}