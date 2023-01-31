package com.crayosa.surveil.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.crayosa.surveil.R
import com.crayosa.surveil.databinding.LayoutProgressItemBinding
import com.crayosa.surveil.datamodels.Progress

class ProgressListAdapter : ListAdapter<Progress, ProgressViewHolder>(MyDiffUtilCallback()){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProgressViewHolder {
        val binding = DataBindingUtil.inflate<LayoutProgressItemBinding>(
            LayoutInflater.from(parent.context), R.layout.layout_progress_item, parent, false
        )
        return ProgressViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProgressViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}
class MyDiffUtilCallback : DiffUtil.ItemCallback<Progress>(){
    override fun areItemsTheSame(oldItem: Progress, newItem: Progress): Boolean {
        return oldItem === newItem
    }

    override fun areContentsTheSame(oldItem: Progress, newItem: Progress): Boolean {
        return oldItem == newItem
    }
}

class ProgressViewHolder(val binding : LayoutProgressItemBinding) : RecyclerView.ViewHolder(binding.root){
    fun bind(progress : Progress){
        binding.progressBar.progress = progress.completion.toInt()
        binding.progressDisplayName.text = progress.name
    }

}


