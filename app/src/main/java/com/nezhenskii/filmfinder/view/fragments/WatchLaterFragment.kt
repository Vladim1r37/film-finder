package com.nezhenskii.filmfinder.view.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.nezhenskii.filmfinder.databinding.FragmentWatchLaterBinding
import com.nezhenskii.filmfinder.utils.AnimationHelper


class WatchLaterFragment : Fragment() {
    private var _binding: FragmentWatchLaterBinding? = null
    private val binding: FragmentWatchLaterBinding
        get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWatchLaterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        AnimationHelper.performFragmentCircularRevealAnimation(binding.root, requireActivity(), 3)
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }
}