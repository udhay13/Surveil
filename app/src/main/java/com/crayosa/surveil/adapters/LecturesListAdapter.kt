package com.crayosa.surveil.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.crayosa.surveil.R
import com.crayosa.surveil.databinding.LayoutLectureBinding
import com.crayosa.surveil.datamodels.Lecture
import com.crayosa.surveil.listener.OnItemClickListener

class LecturesListAdapter(val listener : OnItemClickListener) : ListAdapter<Lecture, LectureViewHolder>(MyDiffUtil()){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LectureViewHolder {
        val binding = DataBindingUtil.inflate<LayoutLectureBinding>(
            LayoutInflater.from(parent.context), R.layout.layout_lecture, parent, false
        )
        return LectureViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LectureViewHolder, position: Int) {
        holder.bind(getItem(position))
        holder.itemView.setOnClickListener {
            listener.onClick(getItem(position), position)
        }
    }

}
class MyDiffUtil : DiffUtil.ItemCallback<Lecture>(){
    override fun areItemsTheSame(oldItem: Lecture, newItem: Lecture): Boolean {
        return oldItem === newItem
    }

    override fun areContentsTheSame(oldItem: Lecture, newItem: Lecture): Boolean {
        return oldItem == newItem
    }

}
class LectureViewHolder(val binding : LayoutLectureBinding) : RecyclerView.ViewHolder(binding.root){
    fun bind(lecture : Lecture){
        binding.lectureTitle.text = lecture.name
    }
}