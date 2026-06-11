package pt.saborbrasileiro.app

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var googleSignInClient: GoogleSignInClient

    private val googleLoginLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { resultado ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(resultado.data)
        try {
            val conta = task.getResult(ApiException::class.java)
            val idToken = conta.idToken
            if (idToken.isNullOrBlank()) {
                Toast.makeText(
                    this,
                    "Não foi possível validar a conta Google. Verifique a configuração Firebase.",
                    Toast.LENGTH_LONG
                ).show()
                return@registerForActivityResult
            }
            autenticarFirebaseComGoogle(idToken)
        } catch (erro: ApiException) {
            Toast.makeText(this, "Login Google cancelado ou indisponível.", Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        configurarLoginGoogle()

        if (auth.currentUser != null) {
            prepararPerfilDepoisDoLogin()
        }

        val etEmail = findViewById<EditText>(R.id.etEmailLogin)
        val etSenha = findViewById<EditText>(R.id.etSenhaLogin)
        val btnEntrarGoogle = findViewById<Button>(R.id.btnEntrarGoogle)
        val btnEntrar = findViewById<Button>(R.id.btnEntrar)
        val tvIrParaRegisto = findViewById<TextView>(R.id.tvIrParaRegisto)
        val tvEsqueceuSenha = findViewById<TextView>(R.id.tvEsqueceuSenha)

        btnEntrarGoogle.setOnClickListener {
            iniciarLoginGoogle()
        }

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

    private fun configurarLoginGoogle() {
        val webClientId = getString(R.string.google_web_client_id).trim()
        val opcoes = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .apply {
                if (webClientId.isNotBlank()) {
                    requestIdToken(webClientId)
                }
            }
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, opcoes)
    }

    private fun iniciarLoginGoogle() {
        val webClientId = getString(R.string.google_web_client_id).trim()
        if (webClientId.isBlank()) {
            Toast.makeText(
                this,
                "Para ativar o login Google, configure o Web Client ID no Firebase e atualize o app.",
                Toast.LENGTH_LONG
            ).show()
            return
        }

        googleSignInClient.signOut().addOnCompleteListener {
            googleLoginLauncher.launch(googleSignInClient.signInIntent)
        }
    }

    private fun autenticarFirebaseComGoogle(idToken: String) {
        val credencial = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credencial)
            .addOnSuccessListener {
                Toast.makeText(this, "Bem-vindo ao Sabor Brasileiro PT!", Toast.LENGTH_SHORT).show()
                prepararPerfilDepoisDoLogin()
            }
            .addOnFailureListener { erro ->
                Toast.makeText(this, "Erro no login Google: ${erro.message}", Toast.LENGTH_LONG).show()
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
                val nome = snapshot.getString("nome").orEmpty().ifBlank {
                    utilizadorAtual.displayName ?: email.substringBefore("@")
                }
                val tipoAtual = snapshot.getString("tipoAcesso") ?: TiposAcesso.UTILIZADOR
                resolverTipoAcesso(email, tipoAtual) { tipoFinal ->
                    val dados = mapOf(
                        "id" to utilizadorAtual.uid,
                        "nome" to nome,
                        "email" to email,
                        "fotoUrl" to (utilizadorAtual.photoUrl?.toString() ?: snapshot.getString("fotoUrl").orEmpty()),
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
