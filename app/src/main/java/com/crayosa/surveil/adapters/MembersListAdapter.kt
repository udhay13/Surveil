package com.crayosa.surveil.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.crayosa.surveil.R
import com.crayosa.surveil.databinding.LayoutItemHeaderBinding
import com.crayosa.surveil.databinding.LayoutMembersBinding
import com.crayosa.surveil.fragments.MembersFragment

class MembersListAdapter : ListAdapter<String, RecyclerView.ViewHolder>(MemberDiffUtil()){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType){
            0 -> MemberViewHolder.from(parent)
            else -> MemberHeaderViewHolder.from(parent, viewType)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder){
            is MemberViewHolder -> holder.bind(getItem(position))
            is MemberHeaderViewHolder -> holder.bind()
        }

    }

    override fun getItemViewType(position: Int): Int {
        super.getItemViewType(position)
        if(getItem(position) == MembersFragment.HEADER_ADMIN){
               return 1
        }else if(getItem(position) == MembersFragment.HEADER_STUDENT){
            return 2
        }
        return 0
    }
}

private class MemberDiffUtil : DiffUtil.ItemCallback<String>(){
    override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
        return oldItem == newItem
    }

}


class MemberViewHolder(private val binding : LayoutMembersBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bind(name: String) {
        binding.memberName.text = name
    }
    companion object{
        fun from(parent : ViewGroup) : MemberViewHolder{
            return MemberViewHolder(
                DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.layout_members,parent,false)
            )
        }
    }
}

class MemberHeaderViewHolder(private val binding: LayoutItemHeaderBinding, private val text : String) : RecyclerView.ViewHolder(binding.root){
    fun bind(){
        binding.membersHeader.text = text
    }
    companion object{
        fun from(parent : ViewGroup, type : Int) : MemberHeaderViewHolder{
            return MemberHeaderViewHolder(
                DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.layout_item_header,parent,false),
                when(type){
                    1->"Admin"
                    else -> "Roommates"
                }
            )
        }
    }
}