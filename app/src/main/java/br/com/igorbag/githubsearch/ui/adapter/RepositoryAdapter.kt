package br.com.igorbag.githubsearch.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import br.com.igorbag.githubsearch.R
import br.com.igorbag.githubsearch.domain.Repository

class RepositoryAdapter(private val repositories: List<Repository>) :
    RecyclerView.Adapter<RepositoryAdapter.ViewHolder>() {

    var gitItemLister: (Repository) -> Unit = {}
    var btnShareLister: (Repository) -> Unit = {}

    // Cria uma nova view
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.repository_item, parent, false)
        return ViewHolder(view)
    }

    // Pega o conteúdo da view e troca pela informação do item de uma lista
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val repository = repositories[position]

        holder.nameRepo.text = repository.name

        holder.itemView.setOnClickListener {
            gitItemLister(repository)
        }

        holder.imageShare.setOnClickListener {
            btnShareLister(repository)
        }
    }

    // Pega a quantidade de repositórios na lista
    override fun getItemCount(): Int {
        return repositories.size
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val card: CardView
        val nameRepo: TextView
        val imageShare: ImageView

        init {
            view.apply {
                card = findViewById(R.id.cv_car)
                nameRepo = findViewById(R.id.name_reposit)
                imageShare = findViewById(R.id.sharing)
            }
        }
    }
}
