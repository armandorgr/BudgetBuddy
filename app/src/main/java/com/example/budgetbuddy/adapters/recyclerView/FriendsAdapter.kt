package com.example.budgetbuddy.adapters.recyclerView
    import androidx.recyclerview.widget.RecyclerView
    import android.view.LayoutInflater
    import android.view.ViewGroup
    import com.example.budgetbuddy.R
    import com.example.budgetbuddy.model.User
    import com.example.budgetbuddy.viewHolders.FriendsViewHolder
    class FriendsAdapter(private val friendList: List<User>) : RecyclerView.Adapter<FriendsViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendsViewHolder {
            val itemView = LayoutInflater.from(parent.context).inflate(R.layout.friend_iitem, parent, false)
            return FriendsViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: FriendsViewHolder, position: Int) {
            val currentUser = friendList[position]
            holder.bind(currentUser)
        }

        override fun getItemCount(): Int {
            return friendList.size
        }
    }

