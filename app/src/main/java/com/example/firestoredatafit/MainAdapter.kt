package com.example.firestoredatafit

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.recyclerview_item.view.*

//경우의 수 나눠서 예비 사용해보기.


class MainAdapter(private val items: MutableList<Acdata>) :
    RecyclerView.Adapter<MainAdapter.MainViewHolder>() {

    var firestore = FirebaseFirestore.getInstance()

    interface ItemClick {
        fun onClick(view: View, position: Int)
    }

    var itemClick: ItemClick? = null

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int) = MainViewHolder(parent)


    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holer: MainViewHolder, position: Int) {
        items[position].let { item ->
            with(holer) {
                nametext.text = item.name
                agetext.text = "나이" + " : " + item.age
                sextext.text = "성별 : " + item.sex
                heighttext.text = "키" + " : " + item.height + "(cm)"
                weighttext.text = "몸무게" + " : " + item.weight + "(kg)"
            }
            holer.nametext.setOnClickListener {
                val context = holer.nametext.context

                val intent = Intent(context, CalendarActivity::class.java)
                intent.putExtra("name", holer.nametext.text)
                context.startActivity(intent)
            }

        }

    }

    inner class MainViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.recyclerview_item, parent, false)
    ) {
        val nametext = itemView.namev
        val agetext = itemView.agev
        val sextext = itemView.sexv
        val heighttext = itemView.heightv
        val weighttext = itemView.weightv
    }

}
