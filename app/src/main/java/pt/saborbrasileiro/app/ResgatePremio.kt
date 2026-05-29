package pt.saborbrasileiro.app

import java.util.Date

data class ResgatePremio(
    val id: String = "",
    val premioId: String = "",
    val premioTitulo: String = "",
    val utilizadorId: String = "",
    val utilizadorNome: String = "",
    val utilizadorEmail: String = "",
    val adminUid: String = "",
    val usado: Boolean = false,
    val dataResgate: Date = Date(),
    val dataUso: Date? = null
)
