package com.example.volcanoseason3.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.volcanoseason3.R
import com.example.volcanoseason3.data.gallery.MountainLink
import com.example.volcanoseason3.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val linkNames : Array<String> = resources.getStringArray(R.array.forecast_link_names)
        val links : Array<String> = resources.getStringArray(R.array.forecast_links)

        val mountainLinks = ArrayList(
            linkNames.zip(links) { name, link -> MountainLink(name, link) }.toList()
        )

        val adapter = MountainLinkAdapter(requireContext(), mountainLinks)
        binding.lvForecastList.adapter = adapter

        return root
    }

    fun addLink(name: String, link: String) {
        Log.d("HomeFragment", "Adding link for mountain: $name, $link")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}