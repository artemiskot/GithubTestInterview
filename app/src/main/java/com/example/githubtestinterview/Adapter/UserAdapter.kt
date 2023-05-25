package com.example.githubtestinterview.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.githubtestinterview.Entities.User
import com.example.githubtestinterview.R

class UserAdapter(private val users: List<User>) : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    inner class UserViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val avatarImageView: ImageView = view.findViewById(R.id.avatarImageView)
        val usernameTextView: TextView = view.findViewById(R.id.usernameTextView)
        val scoreTextView: TextView = view.findViewById(R.id.scoreTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.user_item, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = users[position]

        holder.itemView.setOnClickListener {
            // Handle click event
        }

        // Bind data to views
        holder.usernameTextView.text = user.login
        holder.scoreTextView.text = user.score.toString()

    }

    override fun getItemCount() = users.size
}

