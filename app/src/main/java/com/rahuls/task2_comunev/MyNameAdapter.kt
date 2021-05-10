package com.rahuls.task2_comunev

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MyNameAdapter(private var arrayList: List<FullName>) :
    RecyclerView.Adapter<MyNameAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.recyclerview_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val repo: FullName = arrayList[position]

        holder.title.text = repo.title
        holder.first.text = repo.first
        holder.last.text = repo.last
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var title: TextView = itemView.findViewById(R.id.title_name)
        var first: TextView = itemView.findViewById(R.id.first_name)
        var last: TextView = itemView.findViewById(R.id.last_name)

    }
}