package pt.saborbrasileiro.app

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {

    // Declaração das variáveis globais para a Base de Dados e para a Lista
    private lateinit var db: FirebaseFirestore
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: RestauranteAdapter
    private val listaRestaurantes = mutableListOf<Restaurante>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 1. Inicializar o Firebase Firestore
        db = FirebaseFirestore.getInstance()

        // 2. Configurar a RecyclerView do XML
        recyclerView = findViewById(R.id.rvRestaurantes)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // 3. Inicializar o Adapter passando a nossa lista vazia por enquanto
        adapter = RestauranteAdapter(listaRestaurantes)
        recyclerView.setAdapter(adapter)

        // 4. Configurar o botão "+ Adicionar" para abrir o formulário
        val btnIrAdicionar = findViewById<Button>(R.id.btnIrAdicionar)
        btnIrAdicionar.setOnClickListener {
            val intent = Intent(this, AdicionarRestauranteActivity::class.java)
            startActivity(intent)
        }

        // 5. Chamar a função que vai carregar os dados em tempo real
        escutarDadosFirestore()
    }

    private fun escutarDadosFirestore() {
        // Acedemos à coleção "restaurantes" no Firestore
        db.collection("restaurantes")
            // O addSnapshotListener fica "ouvindo" a nuvem. Se um dado mudar lá, ele atualiza a tela na hora!
            .addSnapshotListener { snapshot, erro ->
                if (erro != null) {
                    Toast.makeText(this, "Erro ao carregar dados: ${erro.message}", Toast.LENGTH_LONG).show()
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    // Limpamos a lista antiga para não duplicar os dados na tela
                    listaRestaurantes.clear()

                    // Transformamos cada documento do Firestore num objeto "Restaurante" automaticamente
                    for (documento in snapshot.documents) {
                        val restaurante = documento.toObject(Restaurante::class.java)
                        if (restaurante != null) {
                            listaRestaurantes.add(restaurante)
                        }
                    }

                    // Avisamos o Adapter de que há novos dados para ele redesenhar no ecrã
                    adapter.notifyDataSetChanged()
                }
            }
    }
}