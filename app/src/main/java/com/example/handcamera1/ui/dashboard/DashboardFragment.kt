package com.example.handcamera1.ui.dashboard

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
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

class DashboardFragment : Fragment(R.layout.fragment_dashboard) {
    private lateinit var rvDct: RecyclerView
    private var list: ArrayList<Dictionary> = arrayListOf()

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_dashboard, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rvDct = view.findViewById(R.id.rv_kamus)
        rvDct.setHasFixedSize(true)

        list.addAll(DictionaryData.listData)
        rvDct.layoutManager = LinearLayoutManager(activity)
        val adapter = DictionaryAdapter(list)
        rvDct.adapter = adapter
        Log.d(TAG, list.toString())
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}