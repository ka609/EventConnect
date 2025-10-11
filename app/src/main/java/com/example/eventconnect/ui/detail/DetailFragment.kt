package com.example.eventconnect.ui.detail

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.example.eventconnect.databinding.FragmentDetailBinding
import com.example.eventconnect.model.Event
import com.example.eventconnect.utils.loadImage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class DetailFragment : Fragment() {

    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!
    private val viewModel: DetailViewModel by viewModels()
    private val args: DetailFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val event = args.event
        setupUI(event)
        setupObservers(event)
    }

    private fun setupUI(event: Event) {
        binding.apply {
            textEventTitle.text = event.title
            textEventDescription.text = event.description
            textEventDate.text =
                SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(event.date)
            textEventLocation.text = event.location
            textEventCategory.text = event.category
            textEventOrganizer.text = event.organizer

            if (event.imageUrl.isNotEmpty()) {
                imageEvent.loadImage(event.imageUrl)
            }

            buttonParticipate.setOnClickListener {
                viewModel.toggleParticipation(event.id)
            }

            buttonShare.setOnClickListener {
                shareEvent(event)
            }

            buttonOpenMap.setOnClickListener {
                openMap(event.latitude, event.longitude)
            }
        }
    }

    private fun setupObservers(event: Event) {
        // Collecte du StateFlow dans le cycle de vie du fragment
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.isParticipating.collectLatest { isParticipating ->
                binding.buttonParticipate.text = if (isParticipating) "Annuler" else "Participer"
            }
        }

        viewModel.checkParticipation(event.id)
    }

    private fun shareEvent(event: Event) {
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, "Rejoins-moi à ${event.title} le ${event.date} !")
            type = "text/plain"
        }
        startActivity(Intent.createChooser(shareIntent, "Partager l'événement"))
    }

    private fun openMap(lat: Double, lon: Double) {
        val uri = "geo:$lat,$lon?q=$lat,$lon"
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
