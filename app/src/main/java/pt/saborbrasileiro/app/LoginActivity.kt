package pt.saborbrasileiro.app

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        if (auth.currentUser != null) {
            prepararPerfilDepoisDoLogin()
        }

        val etEmail = findViewById<EditText>(R.id.etEmailLogin)
        val etSenha = findViewById<EditText>(R.id.etSenhaLogin)
        val btnEntrar = findViewById<Button>(R.id.btnEntrar)
        val tvIrParaRegisto = findViewById<TextView>(R.id.tvIrParaRegisto)
        val tvEsqueceuSenha = findViewById<TextView>(R.id.tvEsqueceuSenha)

        tvEsqueceuSenha.setOnClickListener {
            val email = etEmail.text.toString().trim()
            if (email.isEmpty()) {
                Toast.makeText(this, "Introduza o seu e-mail no campo acima para recuperar a senha.", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            auth.sendPasswordResetEmail(email)
                .addOnSuccessListener {
                    Toast.makeText(this, "Enviámos um e-mail para recuperar a senha.", Toast.LENGTH_LONG).show()
                }
                .addOnFailureListener { erro ->
                    Toast.makeText(this, "Erro ao enviar e-mail: ${erro.message}", Toast.LENGTH_LONG).show()
                }
        }

        btnEntrar.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val senha = etSenha.text.toString().trim()

            if (email.isEmpty() || senha.isEmpty()) {
                Toast.makeText(this, "Por favor, preencha todos os campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            auth.signInWithEmailAndPassword(email, senha)
                .addOnCompleteListener(this) { tarefa ->
                    if (tarefa.isSuccessful) {
                        Toast.makeText(this, "Bem-vindo de volta!", Toast.LENGTH_SHORT).show()
                        prepararPerfilDepoisDoLogin()
                    } else {
                        Toast.makeText(this, "Erro ao entrar: ${tarefa.exception?.message}", Toast.LENGTH_LONG).show()
                    }
                }
        }

        tvIrParaRegisto.setOnClickListener {
            startActivity(Intent(this, RegistoActivity::class.java))
        }
    }

    private fun prepararPerfilDepoisDoLogin() {
        val utilizadorAtual = auth.currentUser
        if (utilizadorAtual == null) {
            irParaHome()
            return
        }

        val email = utilizadorAtual.email.orEmpty()
        val documento = db.collection("utilizadores").document(utilizadorAtual.uid)

        documento.get()
            .addOnSuccessListener { snapshot ->
                val nome = snapshot.getString("nome").orEmpty().ifBlank { email.substringBefore("@") }
                val tipoAtual = snapshot.getString("tipoAcesso") ?: TiposAcesso.UTILIZADOR
                resolverTipoAcesso(email, tipoAtual) { tipoFinal ->
                    val dados = mapOf(
                        "id" to utilizadorAtual.uid,
                        "nome" to nome,
                        "email" to email,
                        "tipoAcesso" to tipoFinal
                    )

                    documento.set(dados, SetOptions.merge())
                        .addOnCompleteListener { irParaHome() }
                }
            }
            .addOnFailureListener {
                irParaHome()
            }
    }

    private fun resolverTipoAcesso(email: String, tipoAtual: String, aoResolver: (String) -> Unit) {
        if (TiposAcesso.ehMasterInicial(email)) {
            aoResolver(TiposAcesso.ADMIN_MASTER)
            return
        }

        db.collection("admin_masters").document(email.lowercase().trim()).get()
            .addOnSuccessListener { documento ->
                aoResolver(if (documento.exists()) TiposAcesso.ADMIN_MASTER else tipoAtual)
            }
            .addOnFailureListener {
                aoResolver(tipoAtual)
            }
    }

    private fun irParaHome() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}
