package org.ivan.pokmanager.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import org.ivan.pokmanager.data.repository.FirebaseAuthRepository
import org.ivan.pokmanager.data.repository.FirestoreUserRepository
import org.ivan.pokmanager.data.repository.PokemonFirestoreRepository
import org.ivan.pokmanager.domain.repository.AuthRepository
import org.ivan.pokmanager.domain.repository.PokemonRepository
import org.ivan.pokmanager.domain.repository.UserRepository
import org.ivan.pokmanager.domain.usecase.DeleteAccountUseCase
import org.ivan.pokmanager.domain.usecase.DeletePokemonUseCase
import org.ivan.pokmanager.domain.usecase.GetPokemonByIdUseCase
import org.ivan.pokmanager.domain.usecase.GetPokemonUseCase
import org.ivan.pokmanager.domain.usecase.SavePokemonUseCase
import org.ivan.pokmanager.domain.usecase.UpdatePokemonUseCase
import org.ivan.pokmanager.presentation.viewmodel.AddPokemonScreenViewModel
import org.ivan.pokmanager.presentation.viewmodel.DeleteAccountViewModel
import org.ivan.pokmanager.presentation.viewmodel.ForgotPasswordViewModel
import org.ivan.pokmanager.presentation.viewmodel.LoginScreenViewModel
import org.ivan.pokmanager.presentation.viewmodel.PokemonDetailViewModel
import org.ivan.pokmanager.presentation.viewmodel.PokemonListViewModel
import org.ivan.pokmanager.presentation.viewmodel.RegisterScreenViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

/**
 * Modulo Koin con dependencias de Firebase, repositorios, use cases y view models.
 */
val appModule = module {
    single { FirebaseFirestore.getInstance() }
    single { FirebaseAuth.getInstance() }

    single<PokemonRepository> { PokemonFirestoreRepository(get()) }
    single<AuthRepository> { FirebaseAuthRepository(get()) }
    single<UserRepository> { FirestoreUserRepository(get()) }

    factory { GetPokemonUseCase(get()) }
    factory { GetPokemonByIdUseCase(get()) }
    factory { DeletePokemonUseCase(get()) }
    factory { SavePokemonUseCase(get()) }
    factory { UpdatePokemonUseCase(get()) }
    factory { DeleteAccountUseCase(get(), get(), get()) }

    viewModel { (pokemonId: String) -> AddPokemonScreenViewModel(get(), get(), get(), get(), get(), pokemonId) }
    viewModel { PokemonListViewModel(get(), get(), get()) }
    viewModel { DeleteAccountViewModel(get()) }
    viewModel { ForgotPasswordViewModel(get()) }
    viewModel { LoginScreenViewModel(get()) }
    viewModel { RegisterScreenViewModel(get(), get()) }
    viewModel { (pokemonId: String) -> PokemonDetailViewModel(pokemonId, get(), get()) }
}
