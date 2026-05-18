package pt.saborbrasileiro.app

data class Utilizador(
    val id: String = "",
    val nome: String = "",
    val email: String = "",
    val favoritos: List<String> = emptyList() // Lista com os IDs dos restaurantes favoritos
)