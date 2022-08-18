package com.mr3y.poodle.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import kotlin.coroutines.CoroutineContext

@Module
@InstallIn(SingletonComponent::class)
object CoroutineContextModule {
    @ImmediateDispatcher
    @Provides
    fun provideImmediateDispatcher(): CoroutineContext = Dispatchers.Main.immediate
}
