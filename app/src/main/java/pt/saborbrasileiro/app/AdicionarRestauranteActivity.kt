package pt.saborbrasileiro.app

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Date

class AdicionarRestauranteActivity : AppCompatActivity() {

    // Criamos a variável que vai gerir a ligação com a base de dados Firestore
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_adicionar_restaurante)

        // Inicializamos a instância do Firestore
        db = FirebaseFirestore.getInstance()

        // Mapeamos o botão do layout XML para o nosso código Kotlin
        val btnSalvar = findViewById<Button>(R.id.btnSalvarRestaurante)

        // Configuramos o clique do botão
        btnSalvar.setOnClickListener {
            executarEnvioFirestore()
        }
    } // Aqui fecha apenas o onCreate

    private fun executarEnvioFirestore() {
        // 1. Capturamos os textos digitados nas caixas
        val nome = findViewById<EditText>(R.id.etNomeRestaurante).text.toString().trim()
        val cidade = findViewById<EditText>(R.id.etCidadeRestaurante).text.toString().trim()
        val categoria = findViewById<EditText>(R.id.etCategoriaRestaurante).text.toString().trim()
        val urlFoto = findViewById<EditText>(R.id.etLinkImagem).text.toString().trim()
        val descricao = findViewById<EditText>(R.id.etDescricaoRestaurante).text.toString().trim()

        // 2. Validação de segurança
        if (nome.isEmpty() || cidade.isEmpty()) {
            Toast.makeText(this, "Por favor, preencha o Nome e a Cidade!", Toast.LENGTH_SHORT).show()
            return
        }

        // 3. Pedimos ao Firestore para gerar um documento vazio e criar um ID único
        val documentoReferencia = db.collection("restaurantes").document()
        val idGerado = documentoReferencia.id

        // 4. Montamos o objeto utilizando o modelo "Restaurante"
        val novoRestaurante = Restaurante(
            id = idGerado,
            nome = nome,
            cidade = cidade,
            categoria = categoria,
            descricao = descricao,
            avaliacaoMedia = 0.0,
            imagemUrl = urlFoto,
            latitude = 0.0,
            longitude = 0.0,
            contacto = "",
            dataCriacao = Date()
        )

        // 5. Enviamos os dados para a nuvem
        documentoReferencia.set(novoRestaurante)
            .addOnSuccessListener {
                Toast.makeText(this, "Restaurante guardado com sucesso!", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener { erro ->
                Toast.makeText(this, "Erro ao gravar: ${erro.message}", Toast.LENGTH_LONG).show()
            }
    } // Aqui fecha a função executarEnvioFirestore
} // Aqui fecha a classe AdicionarRestauranteActivity (ÚLTIMA CHAVE DO ARQUIVO)