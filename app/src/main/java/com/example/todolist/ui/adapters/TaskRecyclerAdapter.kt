package com.example.todolist.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.todolist.databinding.ItemTaskBinding
import com.example.todolist.model.TaskModel
import com.example.todolist.utils.loadImage

class TaskRecyclerAdapter(
    private val itemOnClickListener: (TaskModel) -> Unit,
    private val itemOnLongClickListener: (TaskModel) -> Boolean
) : PagingDataAdapter<TaskModel, TaskRecyclerAdapter.ViewHolder>(ItemDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemTaskBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        getItem(position)?.let { model -> viewHolder.bindModel(model) }
    }

    fun getItemAtPosition(position: Int) = getItem(position)

    inner class ViewHolder(private val dataBinding: ItemTaskBinding) : RecyclerView.ViewHolder(dataBinding.root) {

        fun bindModel(task: TaskModel) {
            dataBinding.model = task
            dataBinding.apply {
                root.setOnClickListener { itemOnClickListener.invoke(task) }
                root.setOnLongClickListener { itemOnLongClickListener.invoke(task) }
            }
            dataBinding.imageView.loadImage(task.iconUrl)
        }
    }

    class ItemDiffCallback : DiffUtil.ItemCallback<TaskModel>() {
        override fun areItemsTheSame(oldItem: TaskModel, newItem: TaskModel): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: TaskModel, newItem: TaskModel): Boolean {
            return oldItem == newItem
        }
    }
}
