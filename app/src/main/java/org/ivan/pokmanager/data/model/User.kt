package org.ivan.pokmanager.data.model

import java.time.LocalDate

data class User(
    val name: String,
    val surname: String,
    val email: String,
    val birthDay: LocalDate,
    val password: String
)
