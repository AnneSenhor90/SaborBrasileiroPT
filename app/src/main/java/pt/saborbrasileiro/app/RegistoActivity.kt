package pt.saborbrasileiro.app

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RegistoActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registo)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        val etNome = findViewById<EditText>(R.id.etNomeRegisto)
        val etEmail = findViewById<EditText>(R.id.etEmailRegisto)
        val etSenha = findViewById<EditText>(R.id.etSenhaRegisto)
        val btnSubmeter = findViewById<Button>(R.id.btnSubmeterRegisto)

        btnSubmeter.setOnClickListener {
            val nome = etNome.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val senha = etSenha.text.toString().trim()

            if (nome.isEmpty() || email.isEmpty() || senha.isEmpty()) {
                Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (senha.length < 6) {
                Toast.makeText(this, "A senha deve ter pelo menos 6 caracteres", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 1. Criar o utilizador no Firebase Authentication
            auth.createUserWithEmailAndPassword(email, senha)
                .addOnCompleteListener(this) { tarefa ->
                    if (tarefa.isSuccessful) {
                        val uid = auth.currentUser?.uid ?: ""

                        // 2. Criar o objeto Utilizador para guardar no Firestore
                        val novoUtilizador = Utilizador(
                            id = uid,
                            nome = nome,
                            email = email,
                            favoritos = emptyList()
                        )

                        // 3. Guardar no Firestore na coleção "utilizadores"
                        db.collection("utilizadores").document(uid)
                            .set(novoUtilizador)
                            .addOnSuccessListener {
                                Toast.makeText(this, "Conta criada com sucesso!", Toast.LENGTH_SHORT).show()
                                // Redirecionar para a Home
                                val intent = Intent(this, MainActivity::class.java)
                                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                startActivity(intent)
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(this, "Erro ao guardar dados: ${e.message}", Toast.LENGTH_LONG).show()
                            }

                    } else {
                        Toast.makeText(this, "Erro no registo: ${tarefa.exception?.message}", Toast.LENGTH_LONG).show()
                    }
                }
        }
    }
}