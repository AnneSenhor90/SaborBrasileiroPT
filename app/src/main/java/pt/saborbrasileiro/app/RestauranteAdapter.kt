package pt.saborbrasileiro.app

import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import java.util.Locale

class RestauranteAdapter(
    private val listaRestaurantes: List<Restaurante>,
    private val aoAbrirDetalhes: (Restaurante) -> Unit
) : RecyclerView.Adapter<RestauranteAdapter.RestauranteViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RestauranteViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_restaurante, parent, false)
        return RestauranteViewHolder(view)
    }

    override fun onBindViewHolder(holder: RestauranteViewHolder, position: Int) {
        val restaurante = listaRestaurantes[position]
        val context = holder.itemView.context

        holder.tvNome.text = restaurante.nome
        holder.tvCidade.text = restaurante.cidade.ifBlank { "Portugal" }
        holder.tvCategoria.text = restaurante.categoria.ifBlank { "Brasileira" }
        holder.tvDescricao.text = restaurante.descricao.ifBlank { "Sabores brasileiros perto de si." }

        val avaliacao = restaurante.avaliacaoMedia
        holder.tvAvaliacao.text = if (avaliacao > 0.0) {
            String.format(Locale.forLanguageTag("pt-PT"), "%.1f", avaliacao)
        } else {
            "Novo"
        }

        val (statusTexto, statusCor) = when {
            avaliacao >= 4.0 -> "Excelente" to R.color.rating_excellent
            avaliacao >= 2.5 -> "Média" to R.color.rating_medium
            avaliacao > 0.0 -> "Fraca" to R.color.rating_weak
            else -> "Novo" to R.color.sabor_green
        }

        holder.tvRatingStatus.text = statusTexto
        val statusBackground = holder.tvRatingStatus.background.mutate() as GradientDrawable
        statusBackground.setColor(ContextCompat.getColor(context, statusCor))
        holder.tvRatingStatus.background = statusBackground

        holder.tvDistancia.text = if (restaurante.latitude != 0.0 || restaurante.longitude != 0.0) {
            "Ver no mapa"
        } else {
            "Perto de si"
        }

        val erroImagem = if (restaurante.nome.contains("Maria Pitanga", ignoreCase = true)) {
            R.drawable.logo_maria_pitanga
        } else {
            R.drawable.ic_sabor_pin_fork
        }

        Glide.with(context)
            .load(restaurante.imagemUrl.takeIf { it.isNotBlank() })
            .placeholder(R.drawable.bg_food_placeholder)
            .error(erroImagem)
            .centerCrop()
            .into(holder.ivRestaurante)

        holder.btnDetalhes.setOnClickListener { aoAbrirDetalhes(restaurante) }
        holder.itemView.setOnClickListener { aoAbrirDetalhes(restaurante) }
    }

    override fun getItemCount(): Int = listaRestaurantes.size

    class RestauranteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivRestaurante: ImageView = itemView.findViewById(R.id.ivRestauranteItem)
        val btnDetalhes: View = itemView.findViewById(R.id.btnAvaliarItem)
        val tvNome: TextView = itemView.findViewById(R.id.tvNomeItem)
        val tvCidade: TextView = itemView.findViewById(R.id.tvCidadeItem)
        val tvCategoria: TextView = itemView.findViewById(R.id.tvCategoriaItem)
        val tvDescricao: TextView = itemView.findViewById(R.id.tvDescricaoItem)
        val tvAvaliacao: TextView = itemView.findViewById(R.id.tvAvaliacaoItem)
        val tvDistancia: TextView = itemView.findViewById(R.id.tvDistanciaItem)
        val tvRatingStatus: TextView = itemView.findViewById(R.id.tvRatingStatus)
    }
}
