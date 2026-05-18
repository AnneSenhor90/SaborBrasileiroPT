package pt.saborbrasileiro.app

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

// O Adapter recebe a lista de restaurantes que vamos puxar do Firestore
class RestauranteAdapter(private val listaRestaurantes: List<Restaurante>) :
    RecyclerView.Adapter<RestauranteAdapter.RestauranteViewHolder>() {

    // 1. Este método cria o "molde" visual de cada linha da lista (infla o XML)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RestauranteViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_restaurante, parent, false)
        return RestauranteViewHolder(view)
    }

    // 2. Este método junta os dados do restaurante real com os componentes do ecrã
    override fun onBindViewHolder(holder: RestauranteViewHolder, position: Int) {
        val restaurante = listaRestaurantes[position]
        holder.tvNome.text = restaurante.nome
        holder.tvCidade.text = restaurante.cidade
        holder.tvCategoria.text = restaurante.categoria
    }

    // 3. Diz ao Android quantos itens a nossa lista tem no total
    override fun getItemCount(): Int {
        return listaRestaurantes.size
    }

    // A classe ViewHolder "encontra" os IDs do XML de cada linha para podermos usar acima
    class RestauranteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvNome: TextView = itemView.findViewById(R.id.tvNomeItem)
        val tvCidade: TextView = itemView.findViewById(R.id.tvCidadeItem)
        val tvCategoria: TextView = itemView.findViewById(R.id.tvCategoriaItem)
    }
}