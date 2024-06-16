package com.example.volcanoseason3.ui.home

import android.os.Bundle
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

//        Log.d("HomeFragment", "LOGGED")
//        Log.d("HomeFragment", getString(R.string.mt_adams_link))

        val mountainLinks = arrayListOf(
            MountainLink(
                getString(R.string.link_name_adams),
                getString(R.string.link_adams)
            ),
            MountainLink(
                getString(R.string.link_name_bachelor),
                getString(R.string.link_bachelor)
            ),
            MountainLink(
                getString(R.string.link_name_broken_top),
                getString(R.string.link_broken_top)
            ),
            MountainLink(
                getString(R.string.link_name_diamond),
                getString(R.string.link_diamond)
            ),
            MountainLink(
                getString(R.string.link_name_hood),
                getString(R.string.link_hood)
            ),
            MountainLink(
                getString(R.string.link_name_lassen),
                getString(R.string.link_lassen)
            ),
            MountainLink(
                getString(R.string.link_name_mcloughlin),
                getString(R.string.link_mcloughlin)
            ),
            MountainLink(
                getString(R.string.link_name_shasta),
                getString(R.string.link_shasta)
            ),
            MountainLink(
                getString(R.string.link_name_helens),
                getString(R.string.link_helens)
            ),
            MountainLink(
                getString(R.string.link_name_thielsen),
                getString(R.string.link_thielsen)
            ),
            MountainLink(
                getString(R.string.link_name_sisters),
                getString(R.string.link_sisters)
            ),
            MountainLink(
                getString(R.string.link_name_washington),
                getString(R.string.link_washington)
            ),
            MountainLink(
                getString(R.string.link_name_bend_noaa),
                getString(R.string.link_bend_noaa)
            )
        )

        val adapter = MountainLinkAdapter(requireContext(), mountainLinks)
        binding.lvForecastList.adapter = adapter

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}