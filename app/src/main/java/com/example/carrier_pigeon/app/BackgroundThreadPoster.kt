package com.example.carrier_pigeon.app

import dagger.hilt.android.scopes.ViewModelScoped
import java.util.concurrent.Callable
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import javax.inject.Inject

@ViewModelScoped
class BackgroundThreadPoster @Inject constructor() {
    private val backgroundHandler: ExecutorService = Executors.newSingleThreadExecutor()

    fun <S> submit(callable: Callable<S>): S {
        return backgroundHandler.submit(callable).get()
    }
}
