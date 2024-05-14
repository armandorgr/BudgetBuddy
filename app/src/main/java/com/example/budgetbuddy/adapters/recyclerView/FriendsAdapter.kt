package com.example.budgetbuddy.adapters.recyclerView
    import android.annotation.SuppressLint
    import androidx.recyclerview.widget.RecyclerView
    import android.view.LayoutInflater
    import android.view.ViewGroup
    import com.example.budgetbuddy.R
    import com.example.budgetbuddy.model.ListItemUiModel
    import com.example.budgetbuddy.model.User
    import com.example.budgetbuddy.util.ListItemImageLoader
    import com.example.budgetbuddy.viewHolders.FriendsViewHolder
    class FriendsAdapter(
        private val layoutInflater: LayoutInflater,
        private val imageLoader: ListItemImageLoader,
    ) : RecyclerView.Adapter<FriendsViewHolder>() {
        private val listUser = mutableListOf<ListItemUiModel>()
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendsViewHolder {
            val view = layoutInflater.inflate(R.layout.friend_iitem, parent, false)
            return FriendsViewHolder(view, imageLoader)
        }

        override fun onBindViewHolder(holder: FriendsViewHolder, position: Int) {
            holder.bindData(listUser[position])
        }

        override fun getItemCount(): Int {
            return listUser.size
        }

        /**
         * Metodo que sirve para establecer la lista de los grupos cargados y actualizar el [RecyclerView]
         * @param newItems Lista con los nuevos grupos a establecer en el [RecyclerView]
         * */
        @SuppressLint("NotifyDataSetChanged")
        fun setData(newItems: List<ListItemUiModel>) {
            listUser.clear()
            listUser.addAll(newItems)
            notifyDataSetChanged()
        }

        /**
         * Metodo que sirve para filtar la busqueda concreta de amigos a traves del SearchView a partir del texto que introduzca
         * el usuario.
         * @param String texto que contiene el nombre de usuario a buscar
         * */
        fun filter(nombreUsuario: String) {
            val listaFiltrada = if (nombreUsuario.isBlank()) {
                // Si el texto de búsqueda está vacío, se muestran todos los amigos
                listUser.toList()
            } else {
                // Se filtra la lista de amigos basándose en el texto de búsqueda
                listUser.filter {
                    it is ListItemUiModel.User && it.userUiModel.username?.contains(nombreUsuario, true) ?: false

                }
            }
            setData(listaFiltrada)
        }

    }

