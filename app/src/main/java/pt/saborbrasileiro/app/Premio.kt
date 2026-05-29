package pt.saborbrasileiro.app

import java.util.Date

data class Premio(
    val id: String = "",
    val titulo: String = "",
    val descricao: String = "",
    val criadoPorUid: String = "",
    val nomeEstabelecimento: String = "",
    val ativo: Boolean = true,
    val dataCriacao: Date = Date()
)
