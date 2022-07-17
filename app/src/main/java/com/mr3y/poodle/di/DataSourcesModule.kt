package com.mr3y.poodle.di

import com.mr3y.poodle.network.datasources.JitPack
import com.mr3y.poodle.network.datasources.JitPackImpl
import com.mr3y.poodle.network.datasources.MavenCentral
import com.mr3y.poodle.network.datasources.MavenCentralImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class DataSourcesModule {
    @Binds
    abstract fun provideMavenCentralDataSource(mavenCentralImpl: MavenCentralImpl): MavenCentral

    @Binds
    abstract fun provideJitPackDataSource(jitPackImpl: JitPackImpl): JitPack
}
