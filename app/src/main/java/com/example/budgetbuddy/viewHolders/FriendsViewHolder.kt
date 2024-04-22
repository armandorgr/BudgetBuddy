package com.example.budgetbuddy.viewHolders
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.budgetbuddy.R
import com.example.budgetbuddy.model.ListItemUiModel
import com.example.budgetbuddy.model.User
import com.example.budgetbuddy.util.ListItemImageLoader

class FriendsViewHolder(
    itemView: View,
    private val imageLoader: ListItemImageLoader
) : RecyclerView.ViewHolder(itemView) {
    private val firstName: TextView = itemView.findViewById(R.id.firstNameTextView)
    private val lastName: TextView = itemView.findViewById(R.id.lastNameTextView)
    private val username: TextView = itemView.findViewById(R.id.usernameTextView)
    private val profilePic: ImageView = itemView.findViewById(R.id.profilePicImageView)


    fun bind(user: User,listItem: ListItemUiModel) {
        require(listItem is ListItemUiModel.User) {
            "Expected ListItemUiModel.User $listItem"
        }
        firstName.text = user.firstName
        lastName.text = user.lastName
        username.text = user.username

        val userData = listItem.userUiModel
        userData.profilePic?.let { path -> imageLoader.loadImage(path, profilePic)
        }
    }
}
