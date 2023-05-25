import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.githubtestinterview.Entities.Repo
import com.example.githubtestinterview.Entities.User
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
            // bind user data to UI
        }
    }

    inner class RepoViewHolder(private val itemBinding: RepoItemBinding) : RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(repo: Repo) {
            // bind repo data to UI
        }
    }
}
