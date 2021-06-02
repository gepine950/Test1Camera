package com.example.handcamera1

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.handcamera1.databinding.ItemRowBinding

class DictionaryAdapter : RecyclerView.Adapter<DictionaryAdapter.AViewHolder>() {
    private val listDct = ArrayList<Dictionary>()

    private lateinit var binding: ItemRowBinding

    inner class AViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvAlphaNum: TextView = binding.textView
        var imgPhoto: ImageView = binding.imageView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.item_row, parent, false)
        return AViewHolder(view)
    }

    override fun onBindViewHolder(holder: AViewHolder, position: Int) {
        val dictionary = listDct[position]

        Glide.with(holder.itemView.context)
            .load(dictionary.photo)
            .into(holder.imgPhoto)

        holder.tvAlphaNum.text = dictionary.alphaNumeric
    }

    override fun getItemCount(): Int {
        return listDct.size
    }

}