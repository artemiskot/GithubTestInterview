package com.example.githubtestinterview.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.githubtestinterview.Entities.Content
import com.example.githubtestinterview.R
import com.example.githubtestinterview.databinding.RepoContentItemBinding

class RepoContentAdapter(
    private val onFolderClick: (String) -> Unit,
    private val onFileClick: (String) -> Unit
) : RecyclerView.Adapter<RepoContentAdapter.RepoContentViewHolder>() {

    val contents = mutableListOf<Content>()

    fun setContents(contents: List<Content>) {
        this.contents.clear()
        this.contents.addAll(contents)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RepoContentViewHolder {
        val itemBinding = RepoContentItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RepoContentViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: RepoContentViewHolder, position: Int) {
        val content = contents[position]
        holder.bind(content)
    }


    override fun getItemCount(): Int = contents.size


    inner class RepoContentViewHolder(private val binding: RepoContentItemBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            itemView.setOnClickListener {
                val content = contents[adapterPosition]
                if (content.type == "dir") {
                    onFolderClick(content.path)
                } else if (content.type == "file") {
                    onFileClick(content.path)
                }
            }
        }

        fun bind(content: Content) {
            binding.nameTextView.text = content.name
            if(content.type == "dir") {
                binding.iconImageView.setImageResource(R.drawable.ic_folder) // set your folder icon
            } else {
                binding.iconImageView.setImageResource(R.drawable.ic_file) // set your file icon
            }
        }
    }
}

