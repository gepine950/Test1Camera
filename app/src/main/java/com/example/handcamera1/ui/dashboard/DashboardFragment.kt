package com.example.handcamera1.ui.dashboard

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.handcamera1.Dictionary
import com.example.handcamera1.DictionaryAdapter
import com.example.handcamera1.DictionaryData
import com.example.handcamera1.R
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import cz.msebera.android.httpclient.Header
import org.json.JSONArray

class DashboardFragment : Fragment(R.layout.fragment_dashboard) {
    private lateinit var rvDct: RecyclerView
    private var list: ArrayList<Dictionary> = arrayListOf()
    private lateinit var adapter: DictionaryAdapter

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
        adapter = DictionaryAdapter(list)
        rvDct.setHasFixedSize(true)
        if (DictionaryData.apiCheckBool == false) {
            list.addAll(DictionaryData.listData)
        }
        dataFromApi()
        rvDct.layoutManager = LinearLayoutManager(activity)
        rvDct.adapter = adapter
    }

    private fun dataFromApi() {
        val client = AsyncHttpClient()
        val url = "https://numeric-asset-314318.df.r.appspot.com/"
        client.addHeader("User-Agent", "request")
        client.get(url, object : AsyncHttpResponseHandler() {
            override fun onSuccess(
                statusCode: Int, headers: Array<out Header>?, responseBody: ByteArray
            ) {
                val listDct = ArrayList<Dictionary>()
                val result= String(responseBody)
                Log.d(TAG, result)
                try {
                    val responseObject = JSONArray(result)
                    for (i in 0 until responseObject.length()) {
                        val items = responseObject.getJSONArray(i)
                        val alphaNumA = items[1]
                        val photoA = items[2]
                        val dictionary = Dictionary()
                        dictionary.alphaNumericApi = alphaNumA.toString()
                        dictionary.photoApi = photoA.toString()
                        listDct.add(dictionary)
                    }
                    adapter.setListDict(listDct)
                    DictionaryData.apiCheckBool = true
                    Log.d(TAG, listDct.toString())
                } catch (e: Exception) {
                    e.printStackTrace()
                    Log.d(TAG, "Error ${e.message}")
                    Toast.makeText(context, "No Data", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(statusCode: Int, headers: Array<out Header>?, responseBody: ByteArray?, error: Throwable) {
                val errorMessage = when (statusCode) {
                    401 -> "$statusCode : Bad Request"
                    403 -> "$statusCode : Forbidden"
                    404 -> "$statusCode : Not Found"
                    else -> "$statusCode : ${error.message}"
                }
                Toast.makeText(context, "Connection fail,\nusing local data", Toast.LENGTH_SHORT).show()
                DictionaryData.apiCheckBool = false
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}