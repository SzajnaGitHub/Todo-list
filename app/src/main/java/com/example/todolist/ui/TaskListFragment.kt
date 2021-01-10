package com.example.todolist.ui

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.todolist.R
import com.example.todolist.databinding.FragmentTaskListBinding
import com.example.todolist.model.TaskModel
import com.example.todolist.ui.adapters.TaskLoadStateAdapter
import com.example.todolist.ui.adapters.TaskRecyclerAdapter
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentTaskListBinding.bind(view)
        val taskAdapter = TaskRecyclerAdapter(itemOnClickListener = ::itemClickHandler, itemOnLongClickListener = ::itemLongClickHandler)
        setupRecycler(taskAdapter)
        observeTaskDeleted()
        setupBindings()
        setupLayoutRefresh(taskAdapter)
        observeTaskItems(taskAdapter)
        observeTasksChanged(taskAdapter)
        checkForResults()
    }

    override fun onResume() {
        super.onResume()
        binding.refreshLayout.isEnabled = true
    }

    override fun onPause() {
        binding.refreshLayout.isEnabled = false
        super.onPause()
    }

    override fun onDestroyView() {
        binding.itemRecycler.adapter = null
        _binding = null
        super.onDestroyView()
    }

    private fun observeTaskItems(taskAdapter: TaskRecyclerAdapter) {
        viewModel.tasks.observe(viewLifecycleOwner) {
            it.peekContent().let { resource ->
                when (resource) {
                    is Resource.Loading -> Unit
                    is Resource.Failure -> showSafeSnackbar(binding.root, resource.exception.localizedMessage)
                    is Resource.Success -> {
                        taskAdapter.submitData(viewLifecycleOwner.lifecycle, resource.data)
                        binding.refreshLayout.isRefreshing = false
                        if (taskAdapter.itemCount == 0) {
                            runDelayedRecyclerAnimation()
                        }
                    }
                }
            }
        }
    }

    private fun runDelayedRecyclerAnimation() {
        Handler(Looper.getMainLooper()).postDelayed({
            binding.itemRecycler.scheduleLayoutAnimation()
        }, RECYCLER_ANIM_DELAY)
    }

    private fun observeTaskDeleted() {
        viewModel.taskDeleted.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { resource ->
                when (resource) {
                    is Resource.Loading -> Unit
                    is Resource.Failure -> showSafeSnackbar(binding.root, MessageResolver.resolveErrorMessage(requireContext(), resource.errorMessageId))
                    is Resource.Success -> showSafeSnackbar(binding.root, MessageResolver.resolveSuccessMessage(requireContext(), resource.successMessageId))
                }
            }
        }
    }

    private fun observeTasksChanged(taskAdapter: TaskRecyclerAdapter) {
        viewModel.tasksChanged.observe(viewLifecycleOwner) {
            it.peekContent().let { resource ->
                if (resource is Resource.Success) {
                    taskAdapter.refresh()
                }
            }
        }
    }

    private fun setupBindings() {
        binding.addTaskButton.setOnClickListener {
            navigateToDetailsFragment(title = getString(R.string.add_item_title))
        }
        binding.viewEmptyList.viewAddItem.setOnClickListener {
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

    private fun setupRecycler(taskAdapter: TaskRecyclerAdapter) {
        val itemTouchHelper = ItemTouchHelper(SwipeItemCallback(requireContext()) { position ->
            taskAdapter.getItemAtPosition(position)?.let {
                viewModel.deleteTask(it)
            }
        })

        binding.itemRecycler.apply {
            taskAdapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
                override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                    if (positionStart == 0) {
                        (layoutManager as LinearLayoutManager).scrollToPositionWithOffset(positionStart, 0)
                    }
                }
            })

            setHasFixedSize(true)
            adapter = taskAdapter.withLoadStateHeaderAndFooter(
                header = TaskLoadStateAdapter { taskAdapter.retry() },
                footer = TaskLoadStateAdapter { taskAdapter.retry() }
            )

            itemTouchHelper.attachToRecyclerView(this)
        }

        taskAdapter.addLoadStateListener { loadState ->
            binding.apply {
                binding.viewLoading.isVisible = loadState.source.refresh is LoadState.Loading && taskAdapter.itemCount < 1
                binding.viewError.root.isVisible = loadState.source.refresh is LoadState.Error
                binding.viewEmptyList.root.isVisible = loadState.source.refresh is LoadState.NotLoading && loadState.append.endOfPaginationReached && taskAdapter.itemCount < 1

                val errorState = loadState.source.append as? LoadState.Error
                    ?: loadState.source.prepend as? LoadState.Error
                    ?: loadState.append as? LoadState.Error
                    ?: loadState.prepend as? LoadState.Error

                errorState?.let { showSafeSnackbar(binding.root, it.error.localizedMessage) }
            }
        }
    }

    private fun setupLayoutRefresh(taskAdapter: TaskRecyclerAdapter) {
        binding.refreshLayout.setOnRefreshListener {
            taskAdapter.refresh()
        }
    }

    private fun checkForResults() {
        setFragmentResultListener(TaskDetailsFragment.FRAGMENT_REQUEST) { _, bundle ->
            val result = bundle.getSerializable(TaskDetailsFragment.FRAGMENT_RESULT) as? SuccessMessageId
            result?.let {
                showSafeSnackbar(binding.root, MessageResolver.resolveSuccessMessage(requireContext(), result))
            }
        }
    }

    companion object {
        private const val RECYCLER_ANIM_DELAY = 300L
    }
}
