package com.example.eventconnect.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.eventconnect.databinding.FragmentSettingsBinding
import com.example.eventconnect.model.Category
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SettingsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUI()
        setupObservers()
    }

    private fun setupUI() {
        binding.switchDarkTheme.setOnCheckedChangeListener { _, isChecked ->
            viewModel.toggleDarkTheme(isChecked)
            updateTheme(isChecked)
        }

        binding.buttonClearData.setOnClickListener {
            viewModel.clearAllData()
        }

        setupCategoryPreferences()
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collectLatest { state ->
                binding.switchDarkTheme.isChecked = state.isDarkTheme
                updateCategorySelections(state.favoriteCategories)
            }
        }
    }

    private fun updateTheme(isDarkTheme: Boolean) {
        AppCompatDelegate.setDefaultNightMode(
            if (isDarkTheme) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
        )
    }

    private fun setupCategoryPreferences() {
        // TODO: Ajouter les cases à cocher ou un MultiSelectSpinner pour les catégories
    }

    private fun updateCategorySelections(categories: Set<Category>) {
        // TODO: mettre à jour l’UI selon les catégories préférées
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
