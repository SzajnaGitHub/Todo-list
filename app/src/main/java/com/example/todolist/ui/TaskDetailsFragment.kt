package com.example.todolist.ui

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProvider
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.todolist.R
import com.example.todolist.databinding.FragmentTaskDetailsBinding
import com.example.todolist.model.TaskModel
import com.example.todolist.utils.Resource
import com.example.todolist.utils.hideKeyboard
import com.example.todolist.utils.input.InputTypeResolver
import com.example.todolist.utils.loadImage
import com.example.todolist.ui.viewmodel.TaskDetailsViewModel
import com.example.todolist.utils.result.MessageResolver
import com.google.android.material.snackbar.Snackbar
import com.jakewharton.rxbinding2.widget.RxTextView
import dagger.android.support.DaggerFragment
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class TaskDetailsFragment : DaggerFragment(R.layout.fragment_task_details) {
    @Inject
    internal lateinit var modelFactory: ViewModelProvider.Factory

    private var _binding: FragmentTaskDetailsBinding? = null
    private val binding get() = _binding!!

    private val viewModel by lazy(LazyThreadSafetyMode.NONE) { modelFactory.create(TaskDetailsViewModel::class.java) }
    private val args: TaskDetailsFragmentArgs? by navArgs()
    private val disposables = CompositeDisposable()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentTaskDetailsBinding.bind(view)
        setupView()
        setupInputChanges()
        setupBindings()
        observeInputValidChanges()
        observeTaskHandled()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        savedInstanceState?.let { inState ->
            inState.getString(TITLE)?.let { viewModel.title = it }
            inState.getString(DESCRIPTION)?.let { viewModel.description = it }
            inState.getString(URL)?.let { viewModel.url = it }
        } ?: setupModel()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    private fun setupView() {
        binding.titleEditText.setText(viewModel.title)
        binding.descriptionEditText.setText(viewModel.description)
        binding.urlInputEditText.setText(viewModel.url)
    }

    private fun setupModel(taskModel: TaskModel? = null) {
        val model: TaskModel? = taskModel ?: args?.taskModel
        viewModel.taskModel = model
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.apply {
            putString(TITLE, viewModel.title)
            putString(DESCRIPTION, viewModel.description)
            putString(URL, viewModel.url)
        }
    }

    private fun setupBindings() {
        binding.background.setOnClickListener {
            hideKeyboard()
        }

        binding.submitButton.setOnClickListener {
            viewModel.validateInput()
        }
    }

    private fun setupInputChanges() {
        RxTextView.textChanges(binding.titleEditText)
            .skipInitialValue()
            .debounce(INPUT_DEBOUNCE, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .map { it.toString().trim() }
            .subscribe {
                viewModel.title = it
                viewModel.isTitleValid(it)
            }
            .let(disposables::add)

        RxTextView.textChanges(binding.descriptionEditText)
            .skipInitialValue()
            .debounce(INPUT_DEBOUNCE, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .map { it.toString().trim() }
            .subscribe {
                viewModel.description = it
            }
            .let(disposables::add)

        RxTextView.textChanges(binding.urlInputEditText)
            .skipInitialValue()
            .debounce(INPUT_DEBOUNCE, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .map { it.toString().trim() }
            .subscribe {
                viewModel.url = it
                binding.imageView.loadImage(it)
            }
            .let(disposables::add)
    }

    private fun observeInputValidChanges() {
        viewModel.titleInputValid.observe(viewLifecycleOwner) {
            binding.textInputTitle.error = InputTypeResolver.resolve(requireContext(), it)
        }
    }

    private fun observeTaskHandled() {
        viewModel.taskAddedOrUpdate.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { resource ->
                when (resource) {
                    is Resource.Success -> {
                        setFragmentResult(FRAGMENT_REQUEST, bundleOf(FRAGMENT_RESULT to resource.successMessageId))
                        findNavController().popBackStack()
                    }
                    is Resource.Failure -> MessageResolver.resolveErrorMessage(requireContext(), resource.errorMessageId)
                    is Resource.Loading -> Unit
                }
            }
        }
    }

    private fun hideKeyboard() {
        requireActivity().hideKeyboard()
    }

    companion object {
        private const val TITLE = "title"
        private const val DESCRIPTION = "description"
        private const val URL = "url"
        private const val INPUT_DEBOUNCE = 500L
        const val FRAGMENT_REQUEST = "TASK_DETAILS_FRAGMENT_REQUEST"
        const val FRAGMENT_RESULT = "TASK_DETAILS_FRAGMENT_RESULT"
    }
}
