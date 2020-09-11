package com.globo.jarvis.sample

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.view_holder_home.view.*

class HomeViewHolder(
    private val view: View,
    private val onClickListener: HomeAdapter.OnClickListener
) :
    RecyclerView.ViewHolder(view), View.OnClickListener {

    init {
        view.text_view_item.setOnClickListener(this)
    }

    fun bind(name: String) {
        view.text_view_item.text = name
    }

    override fun onClick(view: View?) {
        onClickListener.click(view, adapterPosition)
    }
}