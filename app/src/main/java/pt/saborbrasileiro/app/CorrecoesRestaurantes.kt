package pt.saborbrasileiro.app

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import java.util.Date
import java.util.Locale

object CorrecoesRestaurantes {

    private val dadosMariaPitanga = mapOf(
        "nome" to "Maria Pitanga Matosinhos",
        "cidade" to "Matosinhos",
        "categoria" to "Açaí",
        "descricao" to "Açaí brasileiro, sorbets, doces, bebidas e salgados como coxinha, bolinha de queijo e empada.",
        "avaliacaoMedia" to 4.5,
        "imagemUrl" to "https://ugc.production.linktr.ee/1fe67b5c-f7a8-45e6-88ce-59409ab30feb_AVATAR-MATOSINHOS-BRANCO.png?io=true&size=avatar-v3_0",
        "contacto" to "",
        "morada" to "Av. da Liberdade 155, 4450-396 Matosinhos",
        "fontePesquisa" to "Uber Eats / Glovo / Google",
        "fonteUrl" to "https://www.ubereats.com/pt-en/store/maria-pitanga-matosinhos/aFESm18WQOieNdmkc3p9Ww",
        "dataAtualizacao" to Date()
    )

    fun sincronizarMariaPitanga(db: FirebaseFirestore) {
        listOf("restaurantes", "restaurantes_pendentes").forEach { colecao ->
            db.collection(colecao).get()
                .addOnSuccessListener { snapshot ->
                    snapshot.documents.forEach { documento ->
                        val nome = documento.getString("nome").orEmpty()
                        if (normalizar(nome).contains("maria pitanga")) {
                            documento.reference.set(dadosMariaPitanga, SetOptions.merge())
                        }
                    }
                }
        }
    }

    private fun normalizar(valor: String): String {
        return valor.lowercase(Locale.ROOT).trim()
    }
}
