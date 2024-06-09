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
/**
 * Adaptador para mostrar una lista de amigos en un RecyclerView.
 * Este adaptador utiliza el patrón de adaptador común en Android para asociar los datos de los amigos con las vistas de diseño.
 * [RecyclerView](https://developer.android.com/guide/topics/ui/layout/recyclerview)
 * [LayoutInflater](https://developer.android.com/reference/android/view/LayoutInflater)
 * @param layoutInflater Objeto LayoutInflater utilizado para inflar la vista de los elementos.
 * @param imageLoader Objeto ListItemImageLoader utilizado para cargar las imágenes de perfil de los amigos.
 * @author Álvaro Aparicio
 */
    class FriendsAdapter(
        private val layoutInflater: LayoutInflater,
        private val imageLoader: ListItemImageLoader,
    ) : RecyclerView.Adapter<FriendsViewHolder>() {
        private val listUser = mutableListOf<ListItemUiModel>()

    /**
     * Método que crea y devuelve una instancia de `FriendsViewHolder`.
     */
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendsViewHolder {
            val view = layoutInflater.inflate(R.layout.friend_iitem, parent, false)
            return FriendsViewHolder(view, imageLoader)
        }

    /**
     * Método que vincula los datos de un usuario específico con su correspondiente `FriendsViewHolder`.
     */
        override fun onBindViewHolder(holder: FriendsViewHolder, position: Int) {
            holder.bindData(listUser[position])
        }


    /**
     * Método que devuelve el número total de elementos en la lista de amigos.
     */
        override fun getItemCount(): Int {
            return listUser.size
        }

        /**
         * Metodo que sirve para establecer la lista de los amigos cargados y actualizar el [RecyclerView]
         * @param newItems Lista con los amigos a establecer en el [RecyclerView]
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

