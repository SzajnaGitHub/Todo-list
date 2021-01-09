package com.example.todolist.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.todolist.databinding.ItemTaskBinding
import com.example.todolist.model.TaskModel

class TaskRecyclerAdapter(
    private val itemOnClickListener: (TaskModel) -> Unit,
    private val itemOnLongClickListener: (TaskModel) -> Boolean,

    ) : ListAdapter<TaskModel, TaskRecyclerAdapter.ViewHolder>(ItemDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemTaskBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val item = getItem(position)
        val root = viewHolder.dataBinding.root
        viewHolder.bindModel(item)
        root.setOnClickListener {
            itemOnClickListener.invoke(item)
        }
        root.setOnLongClickListener {
            itemOnLongClickListener.invoke(item)
        }
    }

    class ViewHolder(val dataBinding: ItemTaskBinding) : RecyclerView.ViewHolder(dataBinding.root) {
        fun bindModel(task: TaskModel) {
            dataBinding.model = task
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
