package com.example.todolist.ui

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import com.example.todolist.R
import com.example.todolist.databinding.FragmentTaskListBinding
import com.example.todolist.model.TaskModel
import com.example.todolist.ui.viewmodel.TaskListViewModel
import com.example.todolist.utils.Resource
import com.example.todolist.utils.SwipeItemCallback
import com.example.todolist.utils.result.MessageResolver
import com.example.todolist.utils.result.SuccessMessageId
import com.example.todolist.utils.showSafeSnackbar
import dagger.android.support.DaggerFragment
import javax.inject.Inject

class TaskListFragment : DaggerFragment(R.layout.fragment_task_list) {
    @Inject
    internal lateinit var modelFactory: ViewModelProvider.Factory

    private var _binding: FragmentTaskListBinding? = null
    private val binding get() = _binding!!

    private val viewModel by lazy { modelFactory.create(TaskListViewModel::class.java) }
    private val taskAdapter by lazy(LazyThreadSafetyMode.NONE) { TaskRecyclerAdapter(itemOnClickListener = ::itemClickHandler, itemOnLongClickListener = ::itemLongClickHandler) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentTaskListBinding.bind(view)
        setupRecycler()
        observeTaskItems()
        observeTaskDeleted()
        setupBindings()
        checkForResults()
    }

    override fun onDestroyView() {
        binding.itemRecycler.adapter = null
        _binding = null
        super.onDestroyView()
    }

    private fun observeTaskItems() {
        viewModel.tasks.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { resource ->
                when (resource) {
                    is Resource.Loading -> println("TEKST LOADING")
                    is Resource.Failure -> println("TEKST FAILURE")
                    is Resource.Success -> {
                        println("TEKST SUCCESS")
                        taskAdapter.submitList(resource.data)
                    }
                }
            }
        }
    }

    private fun observeTaskDeleted() {
        viewModel.taskDeleted.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { resource ->
                when (resource) {
                    is Resource.Loading -> println("TEKST LOADING")
                    is Resource.Failure -> showSafeSnackbar(binding.root, MessageResolver.resolveErrorMessage(requireContext(), resource.errorMessageId))
                    is Resource.Success -> showSafeSnackbar(binding.root, MessageResolver.resolveSuccessMessage(requireContext(), resource.successMessageId))
                }
            }
        }
    }

    private fun setupBindings() {
        binding.addTaskButton.setOnClickListener {
            navigateToDetailsFragment(title = getString(R.string.add_item_title))
        }
    }

    private fun itemClickHandler(model: TaskModel) {
        navigateToDetailsFragment(model = model, getString(R.string.edit_item_title))
    }

    private fun navigateToDetailsFragment(model: TaskModel? = null, title: String) {
        findNavController().navigate(TaskListFragmentDirections.actionListFragmentToDetailsFragment(model, title))
    }

    private fun itemLongClickHandler(model: TaskModel): Boolean {
        showConfirmationDialog(model)
        return true
    }

    private fun showConfirmationDialog(taskModel: TaskModel) = AlertDialog.Builder(requireContext())
        .setTitle(getString(R.string.title_remove))
        .setMessage(getString(R.string.remove_confirmation_text))
        .setPositiveButton(getString(R.string.action_delete)) { _, _ -> viewModel.deleteTask(taskModel) }
        .setNegativeButton(getString(R.string.action_cancel)) { _, _ -> }
        .show()

    private fun setupRecycler() {
        val itemTouchHelper = ItemTouchHelper(SwipeItemCallback(requireContext(), ::setupItemSwiped))

        binding.itemRecycler.apply {
            adapter = taskAdapter
            itemTouchHelper.attachToRecyclerView(this)
        }
    }

    private fun setupItemSwiped(position: Int) {

    }

    private fun checkForResults() {
        setFragmentResultListener(TaskDetailsFragment.FRAGMENT_REQUEST) { _, bundle ->
            val result = bundle.getSerializable(TaskDetailsFragment.FRAGMENT_RESULT) as? SuccessMessageId
            result?.let { showSafeSnackbar(binding.root, MessageResolver.resolveSuccessMessage(requireContext(), result)) }
        }
    }
}
