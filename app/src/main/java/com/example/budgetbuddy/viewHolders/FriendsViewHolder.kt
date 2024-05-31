package com.example.budgetbuddy.viewHolders
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.budgetbuddy.R
import com.example.budgetbuddy.model.ListItemUiModel
import com.example.budgetbuddy.model.User
import com.example.budgetbuddy.util.ListItemImageLoader

/**
 * Clase ViewHolder responsable de mostrar elementos de amigo en un RecyclerView.
 *
 * @param containerView La vista que representa el diseño del elemento de amigo.
 * @param imageLoader El objeto de carga de imagen utilizado para cargar las fotos de perfil de usuario.
 *
 * @author Álvaro Aparicio
 */
class FriendsViewHolder(
    private val containerView: View,
    private val imageLoader: ListItemImageLoader
) :ListItemViewHolder(containerView) {
    private val imgView: ImageView by lazy {
        containerView.findViewById(R.id.profilePicImageView)
    }
    private val firstName: TextView = itemView.findViewById(R.id.firstNameTextView)
    private val lastName: TextView = itemView.findViewById(R.id.lastNameTextView)
    private val username: TextView = itemView.findViewById(R.id.usernameTextView)
    private val profilePic: ImageView = itemView.findViewById(R.id.profilePicImageView)

    /**
     * Vincula los datos del gasto a los campos de entrada.
     *
     * @param expense El objeto de gasto que contiene los datos a mostrar.
     */
    override fun bindData(listItem: ListItemUiModel) {
        require(listItem is ListItemUiModel.User) {
            "Expected ListItemUiModel.Group $listItem"
        }

        val userData = listItem.userUiModel
        userData.profilePic?.let { imageLoader.loadImage(it, imgView) }
        firstName.text = userData.firstName
        lastName.text = userData.lastName
        username.text = userData.username
        containerView.setOnClickListener {
        }
    }
}
