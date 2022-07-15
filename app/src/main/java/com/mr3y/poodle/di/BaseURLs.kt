package com.mr3y.poodle.di

import javax.inject.Qualifier

@Qualifier
@MustBeDocumented
@Retention(AnnotationRetention.RUNTIME)
annotation class MavenCentralBaseUrl

@Qualifier
@MustBeDocumented
@Retention(AnnotationRetention.RUNTIME)
annotation class JitPackBaseUrl
