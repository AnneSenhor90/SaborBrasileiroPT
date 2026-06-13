package pt.saborbrasileiro.app

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import java.text.Normalizer
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: RestauranteAdapter
    private lateinit var etBuscarRestaurante: EditText
    private lateinit var tvSectionTitle: TextView
    private lateinit var chipsCategoria: List<TextView>

    private val listaRestaurantes = mutableListOf<Restaurante>()
    private val listaRestaurantesFiltrada = mutableListOf<Restaurante>()
    private var categoriaSelecionada = "Todos"
    private var seedInicialSolicitado = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        db = FirebaseFirestore.getInstance()
        CorrecoesRestaurantes.sincronizarMariaPitanga(db)

        recyclerView = findViewById(R.id.rvRestaurantes)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = RestauranteAdapter(listaRestaurantesFiltrada) { restaurante ->
            val intent = Intent(this, AvaliarRestauranteActivity::class.java)
            intent.putExtra(AvaliarRestauranteActivity.EXTRA_RESTAURANTE_ID, restaurante.id)
            intent.putExtra(AvaliarRestauranteActivity.EXTRA_RESTAURANTE_NOME, restaurante.nome)
            startActivity(intent)
        }
        recyclerView.adapter = adapter

        configurarBuscaEFiltros()

        findViewById<android.widget.ImageButton>(R.id.btnPerfil).setOnClickListener {
            startActivity(Intent(this, PerfilActivity::class.java))
        }

        val btnIrAdicionar = findViewById<Button>(R.id.btnIrAdicionar)
        btnIrAdicionar.setOnClickListener {
            val intent = Intent(this, AdicionarRestauranteActivity::class.java)
            startActivity(intent)
        }

        escutarDadosFirestore()
    }

    private fun configurarBuscaEFiltros() {
        etBuscarRestaurante = findViewById(R.id.etBuscarRestaurante)
        tvSectionTitle = findViewById(R.id.tvSectionTitle)
        val btnLimparBusca = findViewById<TextView>(R.id.btnLimparBusca)

        chipsCategoria = listOf(
            findViewById(R.id.chipTodos),
            findViewById(R.id.chipChurrasco),
            findViewById(R.id.chipFeijoada),
            findViewById(R.id.chipLanches),
            findViewById(R.id.chipDoces),
            findViewById(R.id.chipBebidas)
        )

        etBuscarRestaurante.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                aplicarFiltros()
            }

            override fun afterTextChanged(s: Editable?) = Unit
        })

        btnLimparBusca.setOnClickListener {
            etBuscarRestaurante.text.clear()
            categoriaSelecionada = "Todos"
            atualizarEstadoChips()
            aplicarFiltros()
        }

        chipsCategoria.forEach { chip ->
            chip.setOnClickListener {
                categoriaSelecionada = chip.text.toString()
                atualizarEstadoChips()
                aplicarFiltros()
            }
        }

        atualizarEstadoChips()
    }

    private fun escutarDadosFirestore() {
        db.collection("restaurantes")
            .addSnapshotListener { snapshot, erro ->
                if (erro != null) {
                    Toast.makeText(this, "Erro ao carregar dados: ${erro.message}", Toast.LENGTH_LONG).show()
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val idsExistentes = snapshot.documents.map { documento -> documento.id }.toSet()
                    val idsSeed = DadosRestaurantesPorto.idsSeed()
                    val algumSeedSemImagem = snapshot.documents.any { documento ->
                        documento.id in idsSeed && documento.getString("imagemUrl").isNullOrBlank()
                    }

                    if (!seedInicialSolicitado && (DadosRestaurantesPorto.temRestaurantesAusentes(idsExistentes) || algumSeedSemImagem)) {
                        seedInicialSolicitado = true
                        DadosRestaurantesPorto.sincronizarNoFirestore(
                            db = db,
                            aoConcluir = { total ->
                                if (total > 0) {
                                    Toast.makeText(
                                        this,
                                        "$total restaurantes do Porto sincronizados com imagens.",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            },
                            aoFalhar = { erro ->
                                Toast.makeText(
                                    this,
                                    "Erro ao gerar restaurantes iniciais: ${erro.message}",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        )
                    }

                    listaRestaurantes.clear()

                    for (documento in snapshot.documents) {
                        val restaurante = documento.toObject(Restaurante::class.java)
                        if (restaurante != null) {
                            listaRestaurantes.add(
                                restaurante.copy(
                                    id = restaurante.id.ifBlank { documento.id }
                                )
                            )
                        }
                    }

                    aplicarFiltros()
                }
            }
    }

    private fun aplicarFiltros() {
        val termoBusca = normalizar(etBuscarRestaurante.text.toString())

        listaRestaurantesFiltrada.clear()
        listaRestaurantesFiltrada.addAll(
            listaRestaurantes.filter { restaurante ->
                val correspondeBusca = termoBusca.isBlank() || listOf(
                    restaurante.nome,
                    restaurante.cidade,
                    restaurante.categoria,
                    restaurante.descricao
                ).any { normalizar(it).contains(termoBusca) }

                correspondeBusca && correspondeCategoria(restaurante)
            }
        )

        atualizarTituloSecao()
        adapter.notifyDataSetChanged()
    }

    private fun correspondeCategoria(restaurante: Restaurante): Boolean {
        if (categoriaSelecionada == "Todos") return true

        val textoRestaurante = listOf(restaurante.categoria, restaurante.descricao, restaurante.nome)
            .joinToString(separator = " ")
            .let { normalizar(it) }

        val palavrasChave = when (categoriaSelecionada) {
            "Churrasco" -> listOf("churrasco", "churrascaria", "picanha", "rodizio")
            "Feijoada" -> listOf("feijoada", "feijao")
            "Lanches" -> listOf("lanche", "lanches", "hamburguer", "sanduiche", "pastel", "acai")
            "Doces" -> listOf("doce", "doces", "brigadeiro", "sobremesa", "pastelaria")
            "Bebidas" -> listOf("bebida", "bebidas", "bar", "caipirinha", "sumo")
            else -> listOf(normalizar(categoriaSelecionada))
        }

        return palavrasChave.any { textoRestaurante.contains(it) }
    }

    private fun atualizarEstadoChips() {
        chipsCategoria.forEach { chip ->
            val selecionado = chip.text.toString() == categoriaSelecionada
            chip.setBackgroundResource(if (selecionado) R.drawable.bg_chip_selected else R.drawable.bg_chip)
            chip.setTextColor(
                ContextCompat.getColor(
                    this,
                    if (selecionado) R.color.white else R.color.sabor_black_soft
                )
            )
        }
    }

    private fun atualizarTituloSecao() {
        tvSectionTitle.text = when {
            listaRestaurantesFiltrada.isEmpty() -> "Nenhum restaurante encontrado"
            categoriaSelecionada == "Todos" && etBuscarRestaurante.text.isBlank() -> "Mais bem avaliados"
            else -> "${listaRestaurantesFiltrada.size} restaurantes encontrados"
        }
    }

    private fun normalizar(valor: String): String {
        val textoSemAcentos = Normalizer.normalize(valor, Normalizer.Form.NFD)
            .replace("\\p{Mn}+".toRegex(), "")
        return textoSemAcentos.lowercase(Locale.ROOT).trim()
    }
}
