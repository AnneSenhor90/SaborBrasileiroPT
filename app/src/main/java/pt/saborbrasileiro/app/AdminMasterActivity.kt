package pt.saborbrasileiro.app

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Date

class AdminMasterActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var adapter: RestaurantePendenteAdapter
    private lateinit var tvResumo: TextView
    private val pendentes = mutableListOf<Restaurante>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_master)

        findViewById<android.widget.ImageButton>(R.id.btnVoltar).setOnClickListener {
            finish()
        }

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        tvResumo = findViewById(R.id.tvResumoPendentes)

        verificarAcessoMaster {
            configurarNovoMaster()
            val recyclerView = findViewById<RecyclerView>(R.id.rvRestaurantesPendentes)
            recyclerView.layoutManager = LinearLayoutManager(this)
            adapter = RestaurantePendenteAdapter(
                restaurantes = pendentes,
                aoAprovar = { restaurante -> aprovarRestaurante(restaurante) },
                aoExcluir = { restaurante -> excluirRestaurante(restaurante) }
            )
            recyclerView.adapter = adapter
            escutarPendentes()
        }
    }

    private fun configurarNovoMaster() {
        val etEmailNovoMaster = findViewById<EditText>(R.id.etEmailNovoMaster)
        val btnAdicionarMaster = findViewById<Button>(R.id.btnAdicionarMaster)

        btnAdicionarMaster.setOnClickListener {
            val email = etEmailNovoMaster.text.toString().lowercase().trim()
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Informe um e-mail válido.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val dados = mapOf(
                "email" to email,
                "tipoAcesso" to TiposAcesso.ADMIN_MASTER,
                "criadoPorUid" to auth.currentUser?.uid.orEmpty(),
                "dataCriacao" to Date()
            )

            db.collection("admin_masters").document(email).set(dados)
                .addOnSuccessListener {
                    etEmailNovoMaster.text.clear()
                    Toast.makeText(this, "$email agora será master no próximo login.", Toast.LENGTH_LONG).show()
                }
                .addOnFailureListener { erro ->
                    Toast.makeText(this, "Erro ao adicionar master: ${erro.message}", Toast.LENGTH_LONG).show()
                }
        }
    }

    private fun verificarAcessoMaster(aoValidar: () -> Unit) {
        val uid = auth.currentUser?.uid
        if (uid == null) {
            finalizarSemPermissao()
            return
        }

        db.collection("utilizadores").document(uid).get()
            .addOnSuccessListener { documento ->
                val tipoAcesso = documento.getString("tipoAcesso") ?: TiposAcesso.UTILIZADOR
                if (tipoAcesso == TiposAcesso.ADMIN_MASTER) {
                    aoValidar()
                } else {
                    finalizarSemPermissao()
                }
            }
            .addOnFailureListener {
                finalizarSemPermissao()
            }
    }

    private fun escutarPendentes() {
        db.collection("restaurantes_pendentes")
            .addSnapshotListener { snapshot, erro ->
                if (erro != null) {
                    Toast.makeText(this, "Erro ao carregar pendentes: ${erro.message}", Toast.LENGTH_LONG).show()
                    return@addSnapshotListener
                }

                pendentes.clear()
                snapshot?.documents?.forEach { documento ->
                    documento.toObject(Restaurante::class.java)?.let { restaurante ->
                        pendentes.add(restaurante)
                    }
                }

                tvResumo.text = if (pendentes.isEmpty()) {
                    "Nenhum restaurante pendente."
                } else {
                    "${pendentes.size} restaurantes pendentes de aprovação."
                }
                adapter.notifyDataSetChanged()
            }
    }

    private fun aprovarRestaurante(restaurante: Restaurante) {
        val masterUid = auth.currentUser?.uid.orEmpty()
        val aprovado = restaurante.copy(
            estado = "aprovado",
            aprovadoPorUid = masterUid,
            dataAprovacao = Date()
        )

        val batch = db.batch()
        val aprovadoRef = db.collection("restaurantes").document(restaurante.id)
        val pendenteRef = db.collection("restaurantes_pendentes").document(restaurante.id)
        batch.set(aprovadoRef, aprovado)
        batch.delete(pendenteRef)
        batch.commit()
            .addOnSuccessListener {
                Toast.makeText(this, "Restaurante aprovado e publicado.", Toast.LENGTH_LONG).show()
            }
            .addOnFailureListener { erro ->
                Toast.makeText(this, "Erro ao aprovar: ${erro.message}", Toast.LENGTH_LONG).show()
            }
    }

    private fun excluirRestaurante(restaurante: Restaurante) {
        db.collection("restaurantes_pendentes").document(restaurante.id).delete()
            .addOnSuccessListener {
                Toast.makeText(this, "Restaurante pendente excluído.", Toast.LENGTH_LONG).show()
            }
            .addOnFailureListener { erro ->
                Toast.makeText(this, "Erro ao excluir: ${erro.message}", Toast.LENGTH_LONG).show()
            }
    }

    private fun finalizarSemPermissao() {
        Toast.makeText(this, "Acesso permitido apenas ao administrador master.", Toast.LENGTH_LONG).show()
        finish()
    }
}
