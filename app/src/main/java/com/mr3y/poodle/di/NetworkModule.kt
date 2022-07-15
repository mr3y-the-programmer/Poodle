package com.mr3y.poodle.di

import com.mr3y.poodle.network.datasources.JitPack
import com.mr3y.poodle.network.datasources.JitPackImpl
import com.mr3y.poodle.network.datasources.MavenCentral
import com.mr3y.poodle.network.datasources.MavenCentralImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.logging.ANDROID
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class NetworkModule {

    @Singleton
    @Provides
    fun provideHttpClient(): HttpClient {
        return HttpClient(OkHttp) {
            install(Logging) {
                logger = Logger.ANDROID
                level = LogLevel.HEADERS
            }
            engine {
                config {
                    // empty for now...
                }
            }
        }
    }

    @MavenCentralBaseUrl
    @Provides
    fun provideMavenCentralBaseUrl(): String = "https://search.maven.org/solrsearch/select"

    @JitPackBaseUrl
    @Provides
    fun provideJitPackBaseUrl(): String = "https://jitpack.io/api"

    @Binds
    abstract fun provideMavenCentralDataSource(mavenCentralImpl: MavenCentralImpl): MavenCentral

    @Binds
    abstract fun provideJitPackDataSource(jitPackImpl: JitPackImpl): JitPack
}
