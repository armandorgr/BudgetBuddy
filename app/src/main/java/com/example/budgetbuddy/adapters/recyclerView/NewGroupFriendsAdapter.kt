package com.example.budgetbuddy.adapters.recyclerView

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.budgetbuddy.R
import com.example.budgetbuddy.model.ListItemUiModel
import com.example.budgetbuddy.model.User
import com.example.budgetbuddy.viewHolders.NewGroupFriendViewHolder

class NewGroupFriendsAdapter(
    private val layoutInflater: LayoutInflater,
    private val selectedList : MutableList<User>,
    private val onCheckClickListener: OnCheckClickListener? = null,
    private val onUnCheckClickListener: OnCheckClickListener? = null,
) : RecyclerView.Adapter<NewGroupFriendViewHolder>() {
    private val listData = mutableListOf<ListItemUiModel>()
    private val shownData = mutableListOf<ListItemUiModel>()
    @SuppressLint("NotifyDataSetChanged")
    fun setData(newItems: List<ListItemUiModel>) {
        listData.clear()
        listData.addAll(newItems)
        shownData.clear()
        shownData.addAll(newItems)
        notifyDataSetChanged()
    }

    fun filterData(usernameQuery: String) {
        shownData.clear()
        shownData.addAll(listData.filter { item ->
            require(item is ListItemUiModel.User) {
                "Expected user"
            }
            item.userUiModel.username?.contains(usernameQuery) ?: false
        })
        notifyDataSetChanged()
    }

    fun resetData() {
        shownData.clear()
        shownData.addAll(listData)
        notifyDataSetChanged()
    }

    private val onCheckClickEvent = object : NewGroupFriendViewHolder.OnCheckClickListener {
        override fun onCheckClicked(user: ListItemUiModel.User, position: Int) {
            Log.d("prueba", "Elemento añadido: ${selectedList.add(user.userUiModel)}")
            Log.d("prueba", "selected users: ${selectedList}")
            onCheckClickListener?.onCheckClick(user.userUiModel, position)
            user.selected = true
        }
    }
    private val onUnCheckEvent = object : NewGroupFriendViewHolder.OnCheckClickListener {
        override fun onCheckClicked(user: ListItemUiModel.User, position: Int) {
            Log.d("prueba", "Elemento eleminado: ${selectedList.remove(user.userUiModel)}")
            Log.d("prueba", "selected users: ${selectedList}")
            onUnCheckClickListener?.onCheckClick(user.userUiModel, position)
            user.selected = false
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewGroupFriendViewHolder {
        val view = layoutInflater.inflate(R.layout.new_group_friend_layout, parent, false)
        return NewGroupFriendViewHolder(view, onCheckClickEvent, onUnCheckEvent)
    }

    override fun getItemCount(): Int {
        return shownData.size
    }

    override fun onBindViewHolder(holder: NewGroupFriendViewHolder, position: Int) {
        holder.bindData(shownData[position])
    }

    interface OnCheckClickListener {
        fun onCheckClick(user: User, position: Int)
    }
}