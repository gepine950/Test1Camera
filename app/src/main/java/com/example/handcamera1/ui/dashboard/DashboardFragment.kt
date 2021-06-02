package com.example.handcamera1.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.handcamera1.Dictionary
import com.example.handcamera1.DictionaryAdapter
import com.example.handcamera1.DictionaryData
import com.example.handcamera1.R
import com.example.handcamera1.databinding.FragmentDashboardBinding

class DashboardFragment : Fragment(R.layout.fragment_dashboard) {
    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!
    private lateinit var rvDct: RecyclerView
    private var list: ArrayList<Dictionary> = arrayListOf()
    private lateinit var adapter: DictionaryAdapter

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentDashboardBinding.bind(view)

        adapter = DictionaryAdapter()
//        adapter.notifyDataSetChanged()

        rvDct = binding.rvKamus
        rvDct.setHasFixedSize(true)
        rvDct.layoutManager = LinearLayoutManager(activity)
        rvDct.adapter = adapter
        list.addAll(DictionaryData.listData)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}