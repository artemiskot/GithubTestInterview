import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.githubtestinterview.Entities.Repo
import com.example.githubtestinterview.Entities.User
import com.example.githubtestinterview.R
import com.example.githubtestinterview.RepoContentActivity
import com.example.githubtestinterview.databinding.UserItemBinding
import com.example.githubtestinterview.databinding.RepoItemBinding

class CombinedAdapter(private val items: List<Any>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_USER = 0
        private const val TYPE_REPO = 1
    }

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is User -> TYPE_USER
            is Repo -> TYPE_REPO
            else -> throw IllegalArgumentException("Invalid type of data " + position)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_USER -> {
                val itemBinding = UserItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                UserViewHolder(itemBinding)
            }
            TYPE_REPO -> {
                val itemBinding = RepoItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                RepoViewHolder(itemBinding)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = items[position]
        when (holder) {
            is UserViewHolder -> holder.bind(item as User)
            is RepoViewHolder -> holder.bind(item as Repo)
        }
    }

    override fun getItemCount(): Int = items.size

    inner class UserViewHolder(private val itemBinding: UserItemBinding) : RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(user: User) {
            itemBinding.apply {
                usernameTextView.text = user.login
                scoreTextView.text = user.score.toString()

                Glide.with(itemView.context)
                    .load(user.avatar_url)
                    .placeholder(R.drawable.ic_placeholder)
                    .into(avatarImageView)
                itemView.setOnClickListener {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(user.html_url))
                    itemView.context.startActivity(intent)
                }
            }
        }
    }

    inner class RepoViewHolder(private val itemBinding: RepoItemBinding) : RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(repo: Repo) {
            itemBinding.apply {
                repoNameTextView.text = repo.name
                if(repo.forks_count!=1) {
                    forksCountTextView.text = repo.forks_count.toString() + " forks"
                } else{
                    forksCountTextView.text =repo.forks_count.toString() + " fork"
                }
                repoDescriptionTextView.text = repo.description

                itemView.setOnClickListener {
                    val intent = Intent(itemView.context, RepoContentActivity::class.java).apply {
                        putExtra("repo_url", "https://api.github.com/repos/${repo.owner.login}/${repo.name}/contents")
                    }
                    itemView.context.startActivity(intent)
                }
            }
        }
    }


}
