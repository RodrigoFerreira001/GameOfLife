package di

import org.koin.dsl.module

object Modules {
    val modules by lazy {
        listOf(coreModule)
    }

    private val coreModule = module {

    }
}