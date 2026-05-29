package pt.saborbrasileiro.app

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import java.util.Date

class PerfilActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var utilizadorId: String
    private var tipoAcessoAtual = TiposAcesso.UTILIZADOR

    private lateinit var ivFotoPerfil: ImageView
    private lateinit var etFotoPerfilUrl: EditText
    private lateinit var etNomePerfil: EditText
    private lateinit var etDataNascimentoPerfil: EditText
    private lateinit var etEmailPerfil: EditText
    private lateinit var etContatoPerfil: EditText
    private lateinit var etNifPerfil: EditText
    private lateinit var cbRgpdPerfil: CheckBox
    private lateinit var tvTipoAcessoPerfil: TextView
    private lateinit var secaoAdmin: LinearLayout

    private val premiosAdmin = mutableListOf<Premio>()
    private val premiosResgatados = mutableListOf<ResgatePremio>()
    private val resgatesAdmin = mutableListOf<ResgatePremio>()
    private val comentariosPendentes = mutableListOf<Avaliacao>()

    private lateinit var premiosAdminAdapter: PremioAdapter
    private lateinit var premiosResgatadosAdapter: ResgateAdapter
    private lateinit var resgatesAdminAdapter: ResgateAdapter
    private lateinit var comentariosPendentesAdapter: ComentarioPendenteAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_perfil)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        val utilizadorAtual = auth.currentUser
        if (utilizadorAtual == null) {
            irParaLogin()
            return
        }
        utilizadorId = utilizadorAtual.uid

        mapearViews()
        configurarListas()
        configurarAcoes()

        etEmailPerfil.setText(utilizadorAtual.email.orEmpty())
        carregarPerfil()
        carregarPremiosResgatados()
    }

    private fun mapearViews() {
        ivFotoPerfil = findViewById(R.id.ivFotoPerfil)
        etFotoPerfilUrl = findViewById(R.id.etFotoPerfilUrl)
        etNomePerfil = findViewById(R.id.etNomePerfil)
        etDataNascimentoPerfil = findViewById(R.id.etDataNascimentoPerfil)
        etEmailPerfil = findViewById(R.id.etEmailPerfil)
        etContatoPerfil = findViewById(R.id.etContatoPerfil)
        etNifPerfil = findViewById(R.id.etNifPerfil)
        cbRgpdPerfil = findViewById(R.id.cbRgpdPerfil)
        tvTipoAcessoPerfil = findViewById(R.id.tvTipoAcessoPerfil)
        secaoAdmin = findViewById(R.id.secaoAdmin)
    }

    private fun configurarListas() {
        premiosAdminAdapter = PremioAdapter(premiosAdmin)
        premiosResgatadosAdapter = ResgateAdapter(premiosResgatados, false) {}
        resgatesAdminAdapter = ResgateAdapter(resgatesAdmin, true) { resgate ->
            marcarPremioComoUsado(resgate)
        }
        comentariosPendentesAdapter = ComentarioPendenteAdapter(
            comentariosPendentes,
            aoAprovar = { avaliacao -> aprovarComentario(avaliacao) },
            aoReprovar = { avaliacao -> reprovarComentario(avaliacao, false) },
            aoMaldoso = { avaliacao -> reprovarComentario(avaliacao, true) }
        )

        findViewById<RecyclerView>(R.id.rvPremiosResgatados).apply {
            layoutManager = LinearLayoutManager(this@PerfilActivity)
            adapter = premiosResgatadosAdapter
        }
        findViewById<RecyclerView>(R.id.rvPremiosAdmin).apply {
            layoutManager = LinearLayoutManager(this@PerfilActivity)
            adapter = premiosAdminAdapter
        }
        findViewById<RecyclerView>(R.id.rvResgatesAdmin).apply {
            layoutManager = LinearLayoutManager(this@PerfilActivity)
            adapter = resgatesAdminAdapter
        }
        findViewById<RecyclerView>(R.id.rvComentariosPendentes).apply {
            layoutManager = LinearLayoutManager(this@PerfilActivity)
            adapter = comentariosPendentesAdapter
        }
    }

    private fun configurarAcoes() {
        findViewById<android.widget.ImageButton>(R.id.btnVoltar).setOnClickListener { finish() }
        findViewById<Button>(R.id.btnSalvarPerfil).setOnClickListener { salvarPerfilGeral() }
        findViewById<Button>(R.id.btnGuardarRgpd).setOnClickListener { guardarConsentimentoRgpd() }
        findViewById<Button>(R.id.btnSolicitarApagarDados).setOnClickListener { solicitarApagamentoDados() }
        findViewById<Button>(R.id.btnSalvarAdmin).setOnClickListener { salvarPerfilAdmin() }
        findViewById<Button>(R.id.btnCriarPremio).setOnClickListener { criarPremio() }

        findViewById<Button>(R.id.btnAlterarSenha).setOnClickListener {
            val email = auth.currentUser?.email
            if (email.isNullOrBlank()) {
                Toast.makeText(this, "Não foi possível encontrar o e-mail da conta.", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            auth.sendPasswordResetEmail(email)
                .addOnSuccessListener {
                    Toast.makeText(this, "Enviámos um e-mail para alterar a senha.", Toast.LENGTH_LONG).show()
                }
                .addOnFailureListener { erro ->
                    Toast.makeText(this, "Erro ao enviar e-mail: ${erro.message}", Toast.LENGTH_LONG).show()
                }
        }

        findViewById<Button>(R.id.btnSair).setOnClickListener {
            auth.signOut()
            irParaLogin()
        }

        findViewById<Button>(R.id.btnValidacaoMaster).setOnClickListener {
            startActivity(Intent(this, AdminMasterActivity::class.java))
        }
    }

    private fun carregarPerfil() {
        db.collection("utilizadores").document(utilizadorId).get()
            .addOnSuccessListener { doc ->
                tipoAcessoAtual = doc.getString("tipoAcesso") ?: TiposAcesso.UTILIZADOR
                tvTipoAcessoPerfil.text = TiposAcesso.nomeLegivel(tipoAcessoAtual)
                etFotoPerfilUrl.setText(doc.getString("fotoUrl").orEmpty())
                etNomePerfil.setText(doc.getString("nome").orEmpty())
                etDataNascimentoPerfil.setText(doc.getString("dataNascimento").orEmpty())
                etContatoPerfil.setText(doc.getString("contacto").orEmpty())
                etNifPerfil.setText(doc.getString("nif").orEmpty())
                cbRgpdPerfil.isChecked = doc.getBoolean("rgpdAceite") ?: false

                findViewById<EditText>(R.id.etNomeEstabelecimento)
                    .setText(doc.getString("nomeEstabelecimento").orEmpty())
                findViewById<EditText>(R.id.etDataFundacao)
                    .setText(doc.getString("dataFundacao").orEmpty())
                findViewById<EditText>(R.id.etNifEmpresa)
                    .setText(doc.getString("nifEmpresa").orEmpty())

                atualizarFoto(doc.getString("fotoUrl").orEmpty())
                configurarVisibilidadePorTipo()
            }
    }

    private fun configurarVisibilidadePorTipo() {
        val eAdmin = tipoAcessoAtual == TiposAcesso.ADMIN_RESTAURANTE ||
            tipoAcessoAtual == TiposAcesso.ADMIN_MASTER
        secaoAdmin.visibility = if (eAdmin) View.VISIBLE else View.GONE
        findViewById<Button>(R.id.btnValidacaoMaster).visibility =
            if (tipoAcessoAtual == TiposAcesso.ADMIN_MASTER) View.VISIBLE else View.GONE

        if (eAdmin) {
            carregarPremiosAdmin()
            carregarResgatesAdmin()
            carregarComentariosPendentes()
        }
    }

    private fun atualizarFoto(url: String) {
        if (url.isBlank()) {
            ivFotoPerfil.setImageResource(R.drawable.ic_profile)
            return
        }
        Glide.with(this)
            .load(url)
            .placeholder(R.drawable.bg_food_placeholder)
            .error(R.drawable.ic_profile)
            .centerCrop()
            .into(ivFotoPerfil)
    }

    private fun salvarPerfilGeral() {
        val fotoUrl = etFotoPerfilUrl.text.toString().trim()
        val dados = mapOf(
            "fotoUrl" to fotoUrl,
            "nome" to etNomePerfil.text.toString().trim(),
            "dataNascimento" to etDataNascimentoPerfil.text.toString().trim(),
            "contacto" to etContatoPerfil.text.toString().trim(),
            "nif" to etNifPerfil.text.toString().trim()
        )

        db.collection("utilizadores").document(utilizadorId).set(dados, SetOptions.merge())
            .addOnSuccessListener {
                atualizarFoto(fotoUrl)
                Toast.makeText(this, "Perfil guardado.", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { erro ->
                Toast.makeText(this, "Erro ao guardar perfil: ${erro.message}", Toast.LENGTH_LONG).show()
            }
    }

    private fun guardarConsentimentoRgpd() {
        val dados = mapOf(
            "rgpdAceite" to cbRgpdPerfil.isChecked,
            "rgpdVersao" to "rgpd-v1-2026-05-29",
            "rgpdDataAceite" to Date()
        )

        db.collection("utilizadores").document(utilizadorId).set(dados, SetOptions.merge())
            .addOnSuccessListener {
                Toast.makeText(this, "Preferência RGPD guardada.", Toast.LENGTH_LONG).show()
            }
    }

    private fun solicitarApagamentoDados() {
        val dados = mapOf(
            "utilizadorId" to utilizadorId,
            "email" to etEmailPerfil.text.toString(),
            "estado" to "pendente",
            "tipoPedido" to "apagamento_dados",
            "dataPedido" to Date()
        )
        db.collection("pedidos_rgpd").document().set(dados)
            .addOnSuccessListener {
                Toast.makeText(this, "Pedido RGPD enviado para análise.", Toast.LENGTH_LONG).show()
            }
    }

    private fun salvarPerfilAdmin() {
        val dados = mapOf(
            "nomeEstabelecimento" to findViewById<EditText>(R.id.etNomeEstabelecimento).text.toString().trim(),
            "dataFundacao" to findViewById<EditText>(R.id.etDataFundacao).text.toString().trim(),
            "nifEmpresa" to findViewById<EditText>(R.id.etNifEmpresa).text.toString().trim()
        )

        db.collection("utilizadores").document(utilizadorId).set(dados, SetOptions.merge())
            .addOnSuccessListener {
                Toast.makeText(this, "Perfil do estabelecimento guardado.", Toast.LENGTH_SHORT).show()
            }
    }

    private fun criarPremio() {
        val titulo = findViewById<EditText>(R.id.etTituloPremio).text.toString().trim()
        val descricao = findViewById<EditText>(R.id.etDescricaoPremio).text.toString().trim()
        if (titulo.isBlank() || descricao.isBlank()) {
            Toast.makeText(this, "Preencha o título e a descrição do prémio.", Toast.LENGTH_SHORT).show()
            return
        }

        val doc = db.collection("premios").document()
        val premio = Premio(
            id = doc.id,
            titulo = titulo,
            descricao = descricao,
            criadoPorUid = utilizadorId,
            nomeEstabelecimento = findViewById<EditText>(R.id.etNomeEstabelecimento).text.toString().trim(),
            dataCriacao = Date()
        )
        doc.set(premio).addOnSuccessListener {
            findViewById<EditText>(R.id.etTituloPremio).text.clear()
            findViewById<EditText>(R.id.etDescricaoPremio).text.clear()
            Toast.makeText(this, "Prémio criado.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun carregarPremiosAdmin() {
        db.collection("premios").whereEqualTo("criadoPorUid", utilizadorId)
            .addSnapshotListener { snapshot, _ ->
                premiosAdmin.clear()
                snapshot?.documents?.forEach { doc ->
                    doc.toObject(Premio::class.java)?.let { premiosAdmin.add(it) }
                }
                premiosAdminAdapter.notifyDataSetChanged()
            }
    }

    private fun carregarPremiosResgatados() {
        db.collection("resgates_premios").whereEqualTo("utilizadorId", utilizadorId)
            .addSnapshotListener { snapshot, _ ->
                premiosResgatados.clear()
                snapshot?.documents?.forEach { doc ->
                    doc.toObject(ResgatePremio::class.java)?.let { premiosResgatados.add(it) }
                }
                premiosResgatadosAdapter.notifyDataSetChanged()
            }
    }

    private fun carregarResgatesAdmin() {
        db.collection("resgates_premios").whereEqualTo("adminUid", utilizadorId)
            .addSnapshotListener { snapshot, _ ->
                resgatesAdmin.clear()
                snapshot?.documents?.forEach { doc ->
                    doc.toObject(ResgatePremio::class.java)?.let { resgatesAdmin.add(it) }
                }
                resgatesAdminAdapter.notifyDataSetChanged()
            }
    }

    private fun carregarComentariosPendentes() {
        db.collection("avaliacoes_pendentes")
            .addSnapshotListener { snapshot, _ ->
                comentariosPendentes.clear()
                snapshot?.documents?.forEach { doc ->
                    doc.toObject(Avaliacao::class.java)?.let { comentariosPendentes.add(it) }
                }
                comentariosPendentesAdapter.notifyDataSetChanged()
            }
    }

    private fun aprovarComentario(avaliacao: Avaliacao) {
        val aprovado = avaliacao.copy(
            estado = "aprovado",
            moderadoPorUid = utilizadorId,
            dataModeracao = Date()
        )
        val batch = db.batch()
        batch.set(db.collection("avaliacoes").document(avaliacao.id), aprovado)
        batch.delete(db.collection("avaliacoes_pendentes").document(avaliacao.id))
        batch.commit().addOnSuccessListener {
            Toast.makeText(this, "Comentário aprovado.", Toast.LENGTH_SHORT).show()
            atualizarMediaRestaurante(avaliacao.restauranteId)
        }
    }

    private fun reprovarComentario(avaliacao: Avaliacao, maldoso: Boolean) {
        val estado = if (maldoso) "maldoso" else "reprovado"
        val reprovado = avaliacao.copy(
            estado = estado,
            motivoReprovacao = if (maldoso) "Comunicação direta ao master" else "Reprovado pelo administrador",
            moderadoPorUid = utilizadorId,
            dataModeracao = Date()
        )
        val batch = db.batch()
        batch.set(db.collection("avaliacoes_reprovadas").document(avaliacao.id), reprovado)
        batch.delete(db.collection("avaliacoes_pendentes").document(avaliacao.id))
        if (maldoso) {
            batch.set(db.collection("comunicacoes_master").document(avaliacao.id), reprovado)
        }
        batch.commit().addOnSuccessListener {
            Toast.makeText(this, "Comentário tratado.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun atualizarMediaRestaurante(restauranteId: String) {
        if (restauranteId.isBlank()) return
        db.collection("avaliacoes").whereEqualTo("restauranteId", restauranteId)
            .get()
            .addOnSuccessListener { snapshot ->
                val notas = snapshot.documents.mapNotNull { it.getDouble("nota") }
                if (notas.isEmpty()) return@addOnSuccessListener
                val media = notas.sum() / notas.size
                db.collection("restaurantes").document(restauranteId)
                    .update("avaliacaoMedia", kotlin.math.round(media * 10) / 10)
            }
    }

    private fun marcarPremioComoUsado(resgate: ResgatePremio) {
        db.collection("resgates_premios").document(resgate.id)
            .set(mapOf("usado" to true, "dataUso" to Date()), SetOptions.merge())
            .addOnSuccessListener {
                Toast.makeText(this, "Prémio marcado como usado.", Toast.LENGTH_SHORT).show()
            }
    }

    private fun irParaLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    class PremioAdapter(private val lista: List<Premio>) :
        RecyclerView.Adapter<PremioAdapter.ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_premio, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val premio = lista[position]
            holder.titulo.text = premio.titulo
            holder.descricao.text = premio.descricao
        }

        override fun getItemCount(): Int = lista.size

        class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val titulo: TextView = view.findViewById(R.id.tvTituloPremio)
            val descricao: TextView = view.findViewById(R.id.tvDescricaoPremio)
        }
    }

    class ResgateAdapter(
        private val lista: List<ResgatePremio>,
        private val mostrarAcaoAdmin: Boolean,
        private val aoMarcarUsado: (ResgatePremio) -> Unit
    ) : RecyclerView.Adapter<ResgateAdapter.ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_resgate_premio, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val resgate = lista[position]
            holder.titulo.text = resgate.premioTitulo
            holder.detalhe.text = if (mostrarAcaoAdmin) {
                "${resgate.utilizadorNome} • ${if (resgate.usado) "usado" else "por usar"}"
            } else {
                if (resgate.usado) "Usado" else "Disponível para usar"
            }
            holder.botao.visibility = if (mostrarAcaoAdmin && !resgate.usado) View.VISIBLE else View.GONE
            holder.botao.setOnClickListener { aoMarcarUsado(resgate) }
        }

        override fun getItemCount(): Int = lista.size

        class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val titulo: TextView = view.findViewById(R.id.tvTituloResgate)
            val detalhe: TextView = view.findViewById(R.id.tvDetalheResgate)
            val botao: Button = view.findViewById(R.id.btnMarcarUsado)
        }
    }

    class ComentarioPendenteAdapter(
        private val lista: List<Avaliacao>,
        private val aoAprovar: (Avaliacao) -> Unit,
        private val aoReprovar: (Avaliacao) -> Unit,
        private val aoMaldoso: (Avaliacao) -> Unit
    ) : RecyclerView.Adapter<ComentarioPendenteAdapter.ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_comentario_pendente, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val avaliacao = lista[position]
            holder.restaurante.text = avaliacao.restauranteNome.ifBlank { "Restaurante" }
            holder.texto.text = "${avaliacao.nomeUtilizador}: ${avaliacao.comentario}"
            holder.aprovar.setOnClickListener { aoAprovar(avaliacao) }
            holder.reprovar.setOnClickListener { aoReprovar(avaliacao) }
            holder.maldoso.setOnClickListener { aoMaldoso(avaliacao) }
        }

        override fun getItemCount(): Int = lista.size

        class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val restaurante: TextView = view.findViewById(R.id.tvComentarioRestaurante)
            val texto: TextView = view.findViewById(R.id.tvComentarioTexto)
            val aprovar: Button = view.findViewById(R.id.btnAprovarComentario)
            val reprovar: Button = view.findViewById(R.id.btnReprovarComentario)
            val maldoso: Button = view.findViewById(R.id.btnMaldosoComentario)
        }
    }
}
