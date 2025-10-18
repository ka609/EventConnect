package com.example.eventconnect.ui.create

import android.widget.AdapterView
import androidx.core.widget.addTextChangedListener
import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.eventconnect.databinding.FragmentCreateEventBinding
import com.example.eventconnect.utils.ImagePickerHelper
import com.example.eventconnect.ui.create.CreateEventViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class CreateEventFragment : Fragment(), OnMapReadyCallback {

    private var _binding: FragmentCreateEventBinding? = null
    private val binding get() = _binding!!
    private val viewModel: CreateEventViewModel by viewModels()
    private lateinit var imagePickerHelper: ImagePickerHelper
    private var googleMap: GoogleMap? = null

    private val requestGalleryPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) pickImageFromGallery()
    }

    private val requestCameraPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) takePhotoWithCamera()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreateEventBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        imagePickerHelper = ImagePickerHelper(this)
        setupUI()
        setupObservers()
        setupMap()
    }

    private fun setupUI() {
        // Spinner catégorie
        val categories = listOf("SPORT", "MUSIC", "OTHER")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, categories)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerCategory.adapter = adapter

        // Date
        binding.editTextDate.setOnClickListener { showDatePicker() }

        // Image
        binding.buttonAddImage.setOnClickListener { showImageSourceDialog() }

        // Créer événement
        binding.buttonCreateEvent.setOnClickListener { viewModel.createEvent() }

        binding.etTitle.addTextChangedListener {text->
            viewModel.updateTitle(text.toString())
        }
        binding.editTextDescription.addTextChangedListener { text->
            viewModel.updateDescription(text.toString())
        }
        binding.editTextLocation.addTextChangedListener {text ->
            viewModel.updateLocation(text.toString())
        }
        binding.spinnerCategory.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                val category = parent.getItemAtPosition(position).toString()
                viewModel.updateCategory(category)
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

    }

    private fun showImageSourceDialog() {
        val options = arrayOf("Galerie", "Caméra")
        android.app.AlertDialog.Builder(requireContext())
            .setTitle("Ajouter une image")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> checkGalleryPermission()
                    1 -> checkCameraPermission()
                }
            }
            .show()
    }

    private fun setupObservers() {
        viewModel.uiState.observe(viewLifecycleOwner) { state ->
            binding.etTitle.setText(state.title)
            binding.editTextDescription.setText(state.description)
            binding.editTextLocation.setText(state.location)
            binding.editTextDate.setText(
                SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(state.date)
            )
        }

        viewModel.isCreated.observe(viewLifecycleOwner) { isCreated ->
            if (isCreated) {
                findNavController().popBackStack()
                viewModel.resetCreationState()
            }
        }
    }

    private fun setupMap() {
        val mapFragment = childFragmentManager.findFragmentById(binding.mapContainer.id) as SupportMapFragment?
        mapFragment?.getMapAsync(this)
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        android.app.DatePickerDialog(
            requireContext(),
            { _, year, month, day ->
                val selectedDate = Calendar.getInstance().apply { set(year, month, day) }
                viewModel.updateDate(selectedDate.time)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun checkGalleryPermission() {
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            Manifest.permission.READ_MEDIA_IMAGES
        else
            Manifest.permission.READ_EXTERNAL_STORAGE

        if (ContextCompat.checkSelfPermission(requireContext(), permission) == PackageManager.PERMISSION_GRANTED) {
            pickImageFromGallery()
        } else requestGalleryPermissionLauncher.launch(permission)
    }

    private fun checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            takePhotoWithCamera()
        } else requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA)
    }

    private fun pickImageFromGallery() {
        imagePickerHelper.pickImageFromGallery { uri ->
            uri?.let {
                viewModel.updateImageUrl(it.toString())
                binding.imageViewEvent.setImageURI(it)
            }
        }
    }

    private fun takePhotoWithCamera() {
        imagePickerHelper.takePhotoWithCamera { uri ->
            uri?.let {
                viewModel.updateImageUrl(it.toString())
                binding.imageViewEvent.setImageURI(it)
            }
        }
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        map.uiSettings.isZoomControlsEnabled = true

        map.setOnMapClickListener { latLng ->
            map.clear()
            map.addMarker(MarkerOptions().position(latLng).title("Event Location"))
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
            viewModel.updateCoordinates(latLng.latitude, latLng.longitude)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
