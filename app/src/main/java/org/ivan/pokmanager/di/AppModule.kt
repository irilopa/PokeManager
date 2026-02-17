package org.ivan.pokmanager.di

import com.google.firebase.firestore.FirebaseFirestore
import org.ivan.pokmanager.domain.repository.PokemonFirestoreRepository
import org.ivan.pokmanager.domain.usecase.DeletePokemonUseCase
import org.ivan.pokmanager.domain.usecase.GetPokemonUseCase
import org.ivan.pokmanager.domain.usecase.SavePokemonUseCase
import org.ivan.pokmanager.presentation.viewmodel.AddPokemonScreenViewModel
import org.ivan.pokmanager.presentation.viewmodel.LoginScreenViewModel
import org.ivan.pokmanager.presentation.viewmodel.PokemonListViewModel
import org.ivan.pokmanager.presentation.viewmodel.RegisterScreenViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    // Singleton del FirebaseFirestore
    single { FirebaseFirestore.getInstance() }

    // Repositorio Firestore
    single { PokemonFirestoreRepository(get()) }

    // UseCases
    factory { GetPokemonUseCase(get()) }
    factory { DeletePokemonUseCase(get()) }
    factory { SavePokemonUseCase(get()) }

    // ViewModels
    viewModel { AddPokemonScreenViewModel(get()) }
    viewModel { PokemonListViewModel(get(), get()) }

    viewModel { LoginScreenViewModel() }
    viewModel { RegisterScreenViewModel() }

}