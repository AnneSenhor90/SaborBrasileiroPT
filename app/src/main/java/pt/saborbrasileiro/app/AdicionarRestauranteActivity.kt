package pt.saborbrasileiro.app

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Date

class AdicionarRestauranteActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private var tipoAcessoAtual: String = TiposAcesso.UTILIZADOR

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_adicionar_restaurante)

        findViewById<android.widget.ImageButton>(R.id.btnVoltar).setOnClickListener {
            finish()
        }

        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        carregarTipoAcesso()

        val btnSalvar = findViewById<Button>(R.id.btnSalvarRestaurante)
        btnSalvar.setOnClickListener {
            executarEnvioFirestore()
        }
    }

    private fun executarEnvioFirestore() {
        val utilizadorAtual = auth.currentUser
        if (utilizadorAtual == null) {
            Toast.makeText(this, "Entre na sua conta para cadastrar um restaurante.", Toast.LENGTH_LONG).show()
            return
        }

        val nome = findViewById<EditText>(R.id.etNomeRestaurante).text.toString().trim()
        val cidade = findViewById<EditText>(R.id.etCidadeRestaurante).text.toString().trim()
        val morada = findViewById<EditText>(R.id.etMoradaRestaurante).text.toString().trim()
        val categoria = findViewById<EditText>(R.id.etCategoriaRestaurante).text.toString().trim()
        val contacto = findViewById<EditText>(R.id.etContactoRestaurante).text.toString().trim()
        val urlFoto = findViewById<EditText>(R.id.etLinkImagem).text.toString().trim()
        val descricao = findViewById<EditText>(R.id.etDescricaoRestaurante).text.toString().trim()

        if (nome.isEmpty() || cidade.isEmpty()) {
            Toast.makeText(this, "Por favor, preencha o nome e a cidade.", Toast.LENGTH_SHORT).show()
            return
        }

        val publicarDireto = tipoAcessoAtual == TiposAcesso.ADMIN_MASTER
        val colecaoDestino = if (publicarDireto) "restaurantes" else "restaurantes_pendentes"
        val documentoReferencia = db.collection(colecaoDestino).document()

        val novoRestaurante = Restaurante(
            id = documentoReferencia.id,
            nome = nome,
            cidade = cidade,
            categoria = categoria,
            descricao = descricao,
            avaliacaoMedia = 0.0,
            imagemUrl = urlFoto,
            latitude = 0.0,
            longitude = 0.0,
            contacto = contacto,
            morada = morada,
            estado = if (publicarDireto) "aprovado" else "pendente",
            criadoPorUid = utilizadorAtual.uid,
            criadoPorTipo = tipoAcessoAtual,
            aprovadoPorUid = if (publicarDireto) utilizadorAtual.uid else "",
            dataAprovacao = if (publicarDireto) Date() else null,
            dataCriacao = Date()
        )

        documentoReferencia.set(novoRestaurante)
            .addOnSuccessListener {
                val mensagem = if (publicarDireto) {
                    "Restaurante publicado com sucesso!"
                } else {
                    "Restaurante enviado para aprovação do administrador."
                }
                Toast.makeText(this, mensagem, Toast.LENGTH_LONG).show()
                finish()
            }
            .addOnFailureListener { erro ->
                Toast.makeText(this, "Erro ao gravar: ${erro.message}", Toast.LENGTH_LONG).show()
            }
    }

    private fun carregarTipoAcesso() {
        val uid = auth.currentUser?.uid ?: return
        db.collection("utilizadores").document(uid).get()
            .addOnSuccessListener { documento ->
                tipoAcessoAtual = documento.getString("tipoAcesso") ?: TiposAcesso.UTILIZADOR
                findViewById<Button>(R.id.btnSalvarRestaurante).text =
                    if (tipoAcessoAtual == TiposAcesso.ADMIN_MASTER) {
                        "Publicar restaurante"
                    } else {
                        "Enviar para validação"
                    }
            }
    }
}
