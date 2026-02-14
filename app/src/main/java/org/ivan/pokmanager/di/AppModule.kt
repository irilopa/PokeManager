package org.ivan.pokmanager.di

import com.google.firebase.firestore.FirebaseFirestore
import org.ivan.pokmanager.presentation.viewmodel.AddPokemonScreenViewModel
import org.ivan.pokmanager.presentation.viewmodel.LoginScreenViewModel
import org.ivan.pokmanager.presentation.viewmodel.PokemonListViewModel
import org.ivan.pokmanager.presentation.viewmodel.RegisterScreenViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    // Singleton del FirebaseFirestore
    single { FirebaseFirestore.getInstance() }
//    // Singleton del respositorio de usuarios, se le inyecta el FirebaseFirestore creado en la sección anterior
//    single { UserFirestoreRepository(get()) }
//    // Usamos factory para que proporcione una instancia del UseCase cada vez que se solicite
//    factory { GetUsersUseCase(get()) }
//    // Usamos factory para que proporcione una instancia del UseCase cada vez que se solicite
//    factory { DeleteUserUseCase(get()) }
    // Crea el viewModel con las dependencias que tenga definidas
    viewModel { AddPokemonScreenViewModel() }
    viewModel { LoginScreenViewModel() }
    viewModel { PokemonListViewModel() }
    viewModel { RegisterScreenViewModel() }

}