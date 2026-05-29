package pt.saborbrasileiro.app

data class Utilizador(
    val id: String = "",
    val nome: String = "",
    val email: String = "",
    val tipoAcesso: String = TiposAcesso.UTILIZADOR,
    val fotoUrl: String = "",
    val dataNascimento: String = "",
    val contacto: String = "",
    val nif: String = "",
    val nomeEstabelecimento: String = "",
    val dataFundacao: String = "",
    val nifEmpresa: String = "",
    val rgpdAceite: Boolean = false,
    val rgpdVersao: String = "",
    val rgpdDataAceite: java.util.Date? = null,
    val favoritos: List<String> = emptyList() // Lista com os IDs dos restaurantes favoritos
)
