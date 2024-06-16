package com.example.volcanoseason3.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
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

        val mountainLinks = arrayListOf(
            MountainLink(
                "\uD83C\uDF0B Mt Adams",
                "https://www.mountain-forecast.com/peaks/Mount-Adams/forecasts/3741"
            ),
            MountainLink(
                "\uD83C\uDF0B Mount Bachelor",
                "https://www.mountain-forecast.com/peaks/Mount-Bachelor/forecasts/2764"
            ),
            MountainLink(
                "\uD83C\uDF0B Broken Top",
                "https://www.mountain-forecast.com/peaks/Broken-Top/forecasts/2797"
            ),
            MountainLink(
                "\uD83C\uDF0B Diamond Peak",
                "https://www.mountain-forecast.com/peaks/Diamond-Peak-Oregon/forecasts/2665"
            ),
            MountainLink(
                "\uD83C\uDF0B Mt Hood",
                "https://www.mountain-forecast.com/peaks/Mount-Hood/forecasts/3426"
            ),
            MountainLink(
                "\uD83C\uDF0B Lassen Peak",
                "https://www.mountain-forecast.com/peaks/Lassen-Peak/forecasts/3187"
            ),
            MountainLink(
                "\uD83C\uDF0B Mount McLoughlin",
                "https://www.mountain-forecast.com/peaks/Mount-McLoughlin/forecasts/2894"
            ),
            MountainLink(
                "\uD83C\uDF0B Mt Shasta",
                "https://www.mountain-forecast.com/peaks/Mount-Shasta/forecasts/4317"
            ),
            MountainLink(
                "\uD83C\uDF0B Mt St Helens",
                "https://www.mountain-forecast.com/peaks/Mount-Saint-Helens/forecasts/2549"
            ),
            MountainLink(
                "\uD83C\uDF0B Mt Thielsen",
                "https://www.mountain-forecast.com/peaks/Mount-Thielsen/forecasts/2797"
            ),
            MountainLink(
                "\uD83C\uDF0B Three Sisters",
                "https://www.mountain-forecast.com/peaks/Three-Sisters/forecasts/3157"
            ),
            MountainLink(
                "\uD83C\uDF0B Mt Washington",
                "https://www.mountain-forecast.com/peaks/Mount-Washington-3/forecasts/2376"
            ),
            MountainLink(
                "\uD83C\uDF24️  Bend NOAA",
                "https://forecast.weather.gov/MapClick.php?lat=44.06&lon=-121.3#.YsZ5b-zMLeo"
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