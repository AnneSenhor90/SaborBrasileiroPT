package pt.saborbrasileiro.app

object TiposAcesso {
    const val UTILIZADOR = "utilizador"
    const val ADMIN_RESTAURANTE = "admin_restaurante"
    const val ADMIN_MASTER = "admin_master"

    private val emailsMasterIniciais = setOf(
        "anne.kelly.senhor@gmail.com"
    )

    fun tipoParaEmail(email: String, tipoEscolhido: String): String {
        return if (ehMasterInicial(email)) {
            ADMIN_MASTER
        } else {
            tipoEscolhido
        }
    }

    fun ehMasterInicial(email: String): Boolean {
        return email.lowercase().trim() in emailsMasterIniciais
    }

    fun nomeLegivel(tipo: String): String {
        return when (tipo) {
            ADMIN_MASTER -> "Administrador master"
            ADMIN_RESTAURANTE -> "Administrador de restaurante"
            else -> "Utilizador"
        }
    }
}
