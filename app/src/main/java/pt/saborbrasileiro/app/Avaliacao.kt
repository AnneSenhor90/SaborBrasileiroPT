package pt.saborbrasileiro.app

import java.util.Date

data class Avaliacao(
    val id: String = "",
    val restauranteId: String = "",
    val restauranteNome: String = "",
    val utilizadorId: String = "",
    val nomeUtilizador: String = "", // Facilita mostrar quem comentou sem nova consulta
    val emailUtilizador: String = "",
    val nota: Double = 0.0, // Ex: 4.5
    val comentario: String = "",
    val estado: String = "pendente",
    val data: Date = Date()
)
