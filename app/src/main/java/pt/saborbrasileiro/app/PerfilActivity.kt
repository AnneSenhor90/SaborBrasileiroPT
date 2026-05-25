package pt.saborbrasileiro.app

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class PerfilActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

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

        val tvNomePerfil = findViewById<TextView>(R.id.tvNomePerfil)
        val tvEmailPerfil = findViewById<TextView>(R.id.tvEmailPerfil)
        val tvTipoAcessoPerfil = findViewById<TextView>(R.id.tvTipoAcessoPerfil)
        val btnAlterarSenha = findViewById<Button>(R.id.btnAlterarSenha)
        val btnValidacaoMaster = findViewById<Button>(R.id.btnValidacaoMaster)
        val btnSair = findViewById<Button>(R.id.btnSair)

        findViewById<android.widget.ImageButton>(R.id.btnVoltar).setOnClickListener {
            finish()
        }

        tvEmailPerfil.text = utilizadorAtual.email.orEmpty()

        db.collection("utilizadores").document(utilizadorAtual.uid).get()
            .addOnSuccessListener { documento ->
                val nome = documento.getString("nome").orEmpty()
                val tipoAcesso = documento.getString("tipoAcesso") ?: TiposAcesso.UTILIZADOR
                tvNomePerfil.text = nome.ifBlank { "Utilizador" }
                tvTipoAcessoPerfil.text = TiposAcesso.nomeLegivel(tipoAcesso)
                btnValidacaoMaster.visibility = if (tipoAcesso == TiposAcesso.ADMIN_MASTER) {
                    View.VISIBLE
                } else {
                    View.GONE
                }
            }
            .addOnFailureListener {
                tvNomePerfil.text = "Utilizador"
                tvTipoAcessoPerfil.text = TiposAcesso.nomeLegivel(TiposAcesso.UTILIZADOR)
            }

        btnAlterarSenha.setOnClickListener {
            val email = utilizadorAtual.email
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

        btnSair.setOnClickListener {
            auth.signOut()
            irParaLogin()
        }

        btnValidacaoMaster.setOnClickListener {
            startActivity(Intent(this, AdminMasterActivity::class.java))
        }
    }

    private fun irParaLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }
}
