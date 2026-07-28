package com.threesa.billing.di

import com.threesa.billing.data.repository.AuthRepositoryImpl
import com.threesa.billing.data.repository.DashboardRepositoryImpl
import com.threesa.billing.data.repository.InventoryRepositoryImpl
import com.threesa.billing.data.repository.PettyCashRepositoryImpl
import com.threesa.billing.data.repository.UtilsRepositoryImpl
import com.threesa.billing.domain.repository.AuthRepository
import com.threesa.billing.domain.repository.DashboardRepository
import com.threesa.billing.domain.repository.InventoryRepository
import com.threesa.billing.domain.repository.PettyCashRepository
import com.threesa.billing.domain.repository.UtilsRepository
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
    abstract fun bindDashboardRepository(impl: DashboardRepositoryImpl): DashboardRepository
    @Binds
    abstract fun bindPettyCashRepository(impl: PettyCashRepositoryImpl): PettyCashRepository

    @Binds
    abstract fun bindUtilsRepository(impl: UtilsRepositoryImpl): UtilsRepository

    @Binds
    abstract fun bindInventoryRepository(impl: InventoryRepositoryImpl): InventoryRepository

    @Binds
    abstract fun bindReportsRepository(impl: com.threesa.billing.data.repository.ReportsRepositoryImpl): com.threesa.billing.domain.repository.ReportsRepository
}
