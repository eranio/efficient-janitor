package com.eranio.efficientjanitor.ui.main

import android.os.Build
import android.os.Bundle
import android.view.WindowInsets
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.eranio.efficientjanitor.R
import com.eranio.efficientjanitor.databinding.ActivityMainBinding
import com.eranio.efficientjanitor.util.hideKeyboard
import com.eranio.efficientjanitor.ui.result.ResultFragment
import com.eranio.efficientjanitor.viewmodel.JanitorViewModel
import com.eranio.efficientjanitor.viewmodel.UiEvent
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: JanitorViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            WindowCompat.setDecorFitsSystemWindows(window, false)
        }

        val adapter = BagRecyclerAdapter { bag ->
            viewModel.onRemoveBag(bag)
        }

        binding.bagRecyclerView.adapter = adapter
        binding.bagRecyclerView.layoutManager = LinearLayoutManager(this)

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiEvent.collect { event ->
                    when (event) {
                        is UiEvent.ShowSnackbar -> {
                            val message = getString(event.messageRes, *event.args.toTypedArray())
                            Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
                        }

                        is UiEvent.ClearSuccess -> {
                            Toast.makeText(this@MainActivity, getString(R.string.list_was_cleared), Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }



        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    adapter.submitList(state.bags)

                    binding.calculateButton.isEnabled = state.bags.isNotEmpty()
                    binding.clearButton.isEnabled = state.bags.isNotEmpty()
                }
            }
        }


        binding.addButton.setOnClickListener {
            val input = binding.weightInputEditText.text.toString()
            viewModel.onAddBagClicked(input)
            binding.weightInputEditText.text?.clear()
            binding.weightInputEditText.hideKeyboard()
        }

        binding.addRandomBags.setOnClickListener {
            viewModel.onAddRandomBagsClicked()
        }

        binding.weightInputEditText.doAfterTextChanged { text ->
            val isValid = text?.toString()?.toDoubleOrNull() != null
            binding.addButton.isEnabled = isValid
        }

        binding.clearButton.setOnClickListener {
            viewModel.onClearBagsClicked()
            binding.weightInputEditText.hideKeyboard()
        }

        binding.calculateButton.setOnClickListener {
            viewModel.onCalculateClicked()
            supportFragmentManager.beginTransaction()
                .setCustomAnimations(
                    R.anim.slide_in_right,
                    R.anim.slide_out_right,
                    R.anim.slide_in_right, // for popEnter
                    R.anim.slide_out_right  // for popExit
                )
                .replace(android.R.id.content, ResultFragment())
                .addToBackStack(null)
                .commit()
        }
    }
}
