package pt.saborbrasileiro.app

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class RestaurantePendenteAdapter(
    private val restaurantes: List<Restaurante>,
    private val aoAprovar: (Restaurante) -> Unit,
    private val aoExcluir: (Restaurante) -> Unit
) : RecyclerView.Adapter<RestaurantePendenteAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_restaurante_pendente, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val restaurante = restaurantes[position]
        holder.tvNome.text = restaurante.nome
        holder.tvDetalhe.text = "${restaurante.cidade} • ${restaurante.categoria.ifBlank { "Brasileira" }}"
        holder.tvDescricao.text = restaurante.descricao.ifBlank { "Sem descrição informada." }
        holder.tvOrigem.text = "Enviado por: ${TiposAcesso.nomeLegivel(restaurante.criadoPorTipo)}"
        holder.btnAprovar.setOnClickListener { aoAprovar(restaurante) }
        holder.btnExcluir.setOnClickListener { aoExcluir(restaurante) }
    }

    override fun getItemCount(): Int = restaurantes.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvNome: TextView = itemView.findViewById(R.id.tvNomePendente)
        val tvDetalhe: TextView = itemView.findViewById(R.id.tvDetalhePendente)
        val tvDescricao: TextView = itemView.findViewById(R.id.tvDescricaoPendente)
        val tvOrigem: TextView = itemView.findViewById(R.id.tvOrigemPendente)
        val btnAprovar: Button = itemView.findViewById(R.id.btnAprovarPendente)
        val btnExcluir: Button = itemView.findViewById(R.id.btnExcluirPendente)
    }
}
