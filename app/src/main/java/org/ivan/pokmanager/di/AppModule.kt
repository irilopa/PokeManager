package org.ivan.pokmanager.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import org.ivan.pokmanager.domain.repository.AuthRepository
import org.ivan.pokmanager.domain.repository.PokemonFirestoreRepository
import org.ivan.pokmanager.domain.repository.UserRepository
import org.ivan.pokmanager.domain.usecase.DeletePokemonUseCase
import org.ivan.pokmanager.domain.usecase.GetPokemonUseCase
import org.ivan.pokmanager.domain.usecase.SavePokemonUseCase
import org.ivan.pokmanager.presentation.viewmodel.AddPokemonScreenViewModel
import org.ivan.pokmanager.presentation.viewmodel.LoginScreenViewModel
import org.ivan.pokmanager.presentation.viewmodel.PokemonListViewModel
import org.ivan.pokmanager.presentation.viewmodel.RegisterScreenViewModel
import org.ivan.pokmanager.presentation.viewmodel.PokemonDetailViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

/**
 * Modulo Koin con dependencias de Firebase, repositorios, use cases y view models.
 */
val appModule = module {
    single { FirebaseFirestore.getInstance() }
    single { FirebaseAuth.getInstance() }

    single { PokemonFirestoreRepository(get()) }
    single { AuthRepository(get()) }
    single { UserRepository(get()) }

    factory { GetPokemonUseCase(get()) }
    factory { DeletePokemonUseCase(get()) }
    factory { SavePokemonUseCase(get()) }

    viewModel { AddPokemonScreenViewModel(get(), get(), get()) }
    viewModel { PokemonListViewModel(get(), get(),get()) }

    viewModel { LoginScreenViewModel(get()) }
    viewModel { RegisterScreenViewModel(get(), get()) }
    viewModel { (pokemonId: Int) -> PokemonDetailViewModel(pokemonId, get(), get()) }

}