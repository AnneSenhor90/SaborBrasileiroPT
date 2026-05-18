package pt.saborbrasileiro.app

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Inicializar o Firebase Auth
        auth = FirebaseAuth.getInstance()

        // Verificar se o utilizador já está logado. Se sim, vai direto para a MainActivity
        if (auth.currentUser != null) {
            irParaHome()
        }

        val etEmail = findViewById<EditText>(R.id.etEmailLogin)
        val etSenha = findViewById<EditText>(R.id.etSenhaLogin)
        val btnEntrar = findViewById<Button>(R.id.btnEntrar)
        val tvIrParaRegisto = findViewById<TextView>(R.id.tvIrParaRegisto)

        btnEntrar.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val senha = etSenha.text.toString().trim()

            if (email.isEmpty() || senha.isEmpty()) {
                Toast.makeText(this, "Por favor, preencha todos os campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Fazer login no Firebase
            auth.signInWithEmailAndPassword(email, senha)
                .addOnCompleteListener(this) { tarefa ->
                    if (tarefa.isSuccessful) {
                        Toast.makeText(this, "Bem-vindo de volta!", Toast.LENGTH_SHORT).show()
                        irParaHome()
                    } else {
                        Toast.makeText(this, "Erro ao entrar: ${tarefa.exception?.message}", Toast.LENGTH_LONG).show()
                    }
                }
        }

        // Navegação para a tela de registo
        tvIrParaRegisto.setOnClickListener {
            val intent = Intent(this, RegistoActivity::class.java) // Se der erro de digitação, use RegistoActivity::class.java
            startActivity(intent)
        }
    }

    private fun irParaHome() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish() // Fecha a tela de login para o utilizador não voltar para trás ao clicar no botão "Back"
    }
}