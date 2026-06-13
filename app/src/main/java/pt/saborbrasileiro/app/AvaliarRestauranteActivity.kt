package pt.saborbrasileiro.app

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AvaliarRestauranteActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var rvComentarios: RecyclerView
    private lateinit var tvSemComentarios: TextView
    private val listaComentarios = mutableListOf<Avaliacao>()
    private lateinit var comentariosAdapter: ComentariosAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_avaliar_restaurante)

        findViewById<android.widget.ImageButton>(R.id.btnVoltar).setOnClickListener {
            finish()
        }

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        val restauranteId = intent.getStringExtra(EXTRA_RESTAURANTE_ID).orEmpty()
        val restauranteNome = intent.getStringExtra(EXTRA_RESTAURANTE_NOME).orEmpty()

        if (restauranteId.isBlank()) {
            Toast.makeText(this, "Restaurante inválido.", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        findViewById<TextView>(R.id.tvRestauranteAvaliar).text = restauranteNome.ifBlank { "Restaurante" }
        carregarDetalhesRestaurante(restauranteId, restauranteNome)

        val ratingRestaurante = findViewById<RatingBar>(R.id.ratingRestaurante)
        val etComentario = findViewById<EditText>(R.id.etComentarioAvaliacao)
        val btnEnviar = findViewById<Button>(R.id.btnEnviarAvaliacao)

        rvComentarios = findViewById(R.id.rvComentariosRestaurante)
        tvSemComentarios = findViewById(R.id.tvSemComentarios)

        rvComentarios.layoutManager = LinearLayoutManager(this)
        comentariosAdapter = ComentariosAdapter(listaComentarios)
        rvComentarios.adapter = comentariosAdapter

        escutarComentarios(restauranteId)

        btnEnviar.setOnClickListener {
            val utilizadorAtual = auth.currentUser
            if (utilizadorAtual == null) {
                Toast.makeText(this, "Entre na sua conta para enviar um relato.", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            val comentario = etComentario.text.toString().trim()
            if (comentario.length < 10) {
                Toast.makeText(this, "Escreva um relato com pelo menos 10 caracteres.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            db.collection("utilizadores").document(utilizadorAtual.uid).get()
                .addOnSuccessListener { documentoUtilizador ->
                    val nomeUtilizador = documentoUtilizador.getString("nome").orEmpty().ifBlank { "Utilizador" }
                    enviarAvaliacao(
                        restauranteId = restauranteId,
                        restauranteNome = restauranteNome,
                        nomeUtilizador = nomeUtilizador,
                        emailUtilizador = utilizadorAtual.email.orEmpty(),
                        nota = ratingRestaurante.rating.toDouble(),
                        comentario = comentario
                    )
                }
                .addOnFailureListener {
                    enviarAvaliacao(
                        restauranteId = restauranteId,
                        restauranteNome = restauranteNome,
                        nomeUtilizador = "Utilizador",
                        emailUtilizador = utilizadorAtual.email.orEmpty(),
                        nota = ratingRestaurante.rating.toDouble(),
                        comentario = comentario
                    )
                }
        }
    }

    private fun carregarDetalhesRestaurante(restauranteId: String, restauranteNomeFallback: String) {
        db.collection("restaurantes").document(restauranteId).get()
            .addOnSuccessListener { documento ->
                val restauranteFirestore = documento.toObject(Restaurante::class.java)
                if (restauranteFirestore == null) {
                    findViewById<TextView>(R.id.tvRestauranteAvaliar).text =
                        restauranteNomeFallback.ifBlank { "Restaurante" }
                    return@addOnSuccessListener
                }
                val restaurante = restauranteFirestore.copy(
                    id = restauranteFirestore.id.ifBlank { documento.id }
                )

                findViewById<TextView>(R.id.tvRestauranteAvaliar).text = restaurante.nome
                findViewById<TextView>(R.id.tvCategoriaDetalhe).text =
                    restaurante.categoria.ifBlank { "Brasileira" }
                findViewById<TextView>(R.id.tvMoradaDetalhe).text = when {
                    restaurante.morada.isNotBlank() -> restaurante.morada
                    restaurante.cidade.isNotBlank() -> restaurante.cidade
                    else -> "Localidade por confirmar"
                }
                findViewById<TextView>(R.id.tvContactoDetalhe).text =
                    restaurante.contacto.ifBlank { "Contacto por confirmar" }
                findViewById<TextView>(R.id.tvDescricaoDetalhe).text =
                    restaurante.descricao.ifBlank { "Sabores brasileiros perto de si." }

                val erroImagem = if (restaurante.nome.contains("Maria Pitanga", ignoreCase = true)) {
                    R.drawable.logo_maria_pitanga
                } else {
                    R.drawable.ic_sabor_pin_fork
                }

                Glide.with(this)
                    .load(restaurante.imagemUrl.takeIf { it.isNotBlank() })
                    .placeholder(R.drawable.bg_food_placeholder)
                    .error(erroImagem)
                    .centerCrop()
                    .into(findViewById<ImageView>(R.id.ivRestauranteDetalhe))
            }
            .addOnFailureListener {
                findViewById<TextView>(R.id.tvRestauranteAvaliar).text =
                    restauranteNomeFallback.ifBlank { "Restaurante" }
            }
    }

    private fun escutarComentarios(restauranteId: String) {
        db.collection("avaliacoes")
            .whereEqualTo("restauranteId", restauranteId)
            .orderBy("data", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, erro ->
                if (erro != null) {
                    // Fallback local se o índice do Firestore ainda não estiver pronto
                    db.collection("avaliacoes")
                        .whereEqualTo("restauranteId", restauranteId)
                        .addSnapshotListener { snap, err ->
                            if (err != null) return@addSnapshotListener
                            atualizarLista(snap?.documents)
                        }
                    return@addSnapshotListener
                }
                atualizarLista(snapshot?.documents)
            }
    }

    private fun atualizarLista(documentos: List<com.google.firebase.firestore.DocumentSnapshot>?) {
        listaComentarios.clear()
        documentos?.forEach { doc ->
            doc.toObject(Avaliacao::class.java)?.let { avaliacao ->
                listaComentarios.add(avaliacao)
            }
        }
        listaComentarios.sortByDescending { it.data }

        if (listaComentarios.isEmpty()) {
            tvSemComentarios.visibility = View.VISIBLE
            rvComentarios.visibility = View.GONE
        } else {
            tvSemComentarios.visibility = View.GONE
            rvComentarios.visibility = View.VISIBLE
        }
        comentariosAdapter.notifyDataSetChanged()
    }

    private fun enviarAvaliacao(
        restauranteId: String,
        restauranteNome: String,
        nomeUtilizador: String,
        emailUtilizador: String,
        nota: Double,
        comentario: String
    ) {
        val documento = db.collection("avaliacoes_pendentes").document()
        val avaliacao = Avaliacao(
            id = documento.id,
            restauranteId = restauranteId,
            restauranteNome = restauranteNome,
            utilizadorId = auth.currentUser?.uid.orEmpty(),
            nomeUtilizador = nomeUtilizador,
            emailUtilizador = emailUtilizador,
            nota = nota,
            comentario = comentario,
            estado = "pendente",
            data = Date()
        )

        documento.set(avaliacao)
            .addOnSuccessListener {
                Toast.makeText(this, "Obrigado. O seu relato ficou pendente de verificação.", Toast.LENGTH_LONG).show()
                findViewById<EditText>(R.id.etComentarioAvaliacao).text.clear()
                findViewById<RatingBar>(R.id.ratingRestaurante).rating = 5.0f
            }
            .addOnFailureListener { erro ->
                Toast.makeText(this, "Erro ao enviar relato: ${erro.message}", Toast.LENGTH_LONG).show()
            }
    }

    private fun atualizarMediaRestaurante(restauranteId: String) {
        db.collection("avaliacoes")
            .whereEqualTo("restauranteId", restauranteId)
            .get()
            .addOnSuccessListener { snapshot ->
                val docs = snapshot?.documents ?: return@addOnSuccessListener
                if (docs.isEmpty()) return@addOnSuccessListener

                var soma = 0.0
                var total = 0
                for (doc in docs) {
                    val nota = doc.getDouble("nota")
                    if (nota != null) {
                        soma += nota
                        total++
                    }
                }

                if (total > 0) {
                    val novaMedia = soma / total
                    db.collection("restaurantes").document(restauranteId)
                        .update("avaliacaoMedia", java.lang.Double.parseDouble(String.format(Locale.US, "%.1f", novaMedia)))
                }
            }
    }

    class ComentariosAdapter(private val lista: List<Avaliacao>) :
        RecyclerView.Adapter<ComentariosAdapter.ComentarioViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ComentarioViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_comentario, parent, false)
            return ComentarioViewHolder(view)
        }

        override fun onBindViewHolder(holder: ComentarioViewHolder, position: Int) {
            val item = lista[position]
            holder.tvNome.text = item.nomeUtilizador.ifBlank { "Utilizador" }
            holder.rating.rating = item.nota.toFloat()
            holder.tvTexto.text = item.comentario

            val df = SimpleDateFormat("dd 'de' MMMM 'de' yyyy", Locale.forLanguageTag("pt-PT"))
            holder.tvData.text = df.format(item.data)
        }

        override fun getItemCount(): Int = lista.size

        class ComentarioViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val tvNome: TextView = view.findViewById(R.id.tvNomeAutor)
            val rating: RatingBar = view.findViewById(R.id.ratingComentario)
            val tvData: TextView = view.findViewById(R.id.tvDataComentario)
            val tvTexto: TextView = view.findViewById(R.id.tvTextoComentario)
        }
    }

    companion object {
        const val EXTRA_RESTAURANTE_ID = "extra_restaurante_id"
        const val EXTRA_RESTAURANTE_NOME = "extra_restaurante_nome"
    }
}
