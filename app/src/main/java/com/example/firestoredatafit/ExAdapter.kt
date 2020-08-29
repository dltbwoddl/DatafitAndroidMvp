package com.example.firestoredatafit

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.exercisedatarecyclerview.view.*

private lateinit var firestore: FirebaseFirestore

class exAdapter(
    private val items: MutableList<String>,
    private val uname: String,
    private val udate: String
) :
    RecyclerView.Adapter<exAdapter.MainViewHolder>() {
    private var firestore = FirebaseFirestore.getInstance()

    interface ItemCheck {
        fun onClick(view: View, position: Int)
    }

    var itemCheck: ItemCheck? = null


    override fun onCreateViewHolder(parent: ViewGroup, p1: Int) = MainViewHolder(parent)


    override fun getItemCount(): Int = items.size


    override fun onBindViewHolder(holer: MainViewHolder, position: Int) {
        items[position].let { item ->
            with(holer) {
                exview.text = item
            }
            holer.exview.setOnClickListener {
                var deldate = holer.exview.text
                firestore.collection(uname).document("userinformation/exerciseinformation/$udate")
                    .get()
                    .addOnCompleteListener {
                        var d = it.result?.data
                        firestore.collection("exercisedata").document("exdata").get()
                            .addOnSuccessListener {
                                it.data?.get("part")
                                var partlist = it.data?.get("part") as MutableList<String>
                                for (p in partlist) {
                                    if (d?.get(p) != null) {
                                        var w = d?.get(p) as MutableList<String>
                                        if (w.remove(deldate)) {
                                            firestore.collection(uname)
                                                .document("userinformation/exerciseinformation/$udate")
                                                .update(
                                                    p, w
                                                )
                                        }
                                    }
                                }
                            }
                    }

            }

        }
    }

    inner class MainViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context)
            .inflate(R.layout.exercisedatarecyclerview, parent, false)
    ) {
        val exview = itemView.excheckbox

    }

}
