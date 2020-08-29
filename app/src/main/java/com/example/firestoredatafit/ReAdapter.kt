package com.example.firestoredatafit

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.exercisedatarecyclerview.view.*
import kotlinx.android.synthetic.main.restoreitem.view.*
import java.net.MalformedURLException

class ReAdapter(
    private val items: MutableList<String>
) :
    RecyclerView.Adapter<ReAdapter.ReViewHolder>() {
    private var firestore = FirebaseFirestore.getInstance()

    interface ItemCheck {
        fun onClick(view: View, position: Int)
    }

    var itemCheck: ItemCheck? = null


    override fun onCreateViewHolder(parent: ViewGroup, p1: Int) = ReViewHolder(parent)


    override fun getItemCount(): Int = items.size


    override fun onBindViewHolder(holer: ReViewHolder, position: Int) {
        items[position].let { item ->
            with(holer) {
                restoreCb.text = item
            }
            holer.restoreCb.setOnClickListener {
                var delname = holer.restoreCb.text
                firestore.collection("name").document("usernames")
                    .get()
                    .addOnCompleteListener {
                        var d = it.result?.data?.get("deletename") as MutableList<String>
                        d.remove(delname)
                        firestore.collection("name").document("usernames")
                            .update(
                                "deletename", d
                            )
                    }
                firestore.collection("name").document("usernames").get().addOnSuccessListener {
                    var un = it.data?.get("username") as MutableList<String>
                    un.add("$delname")
                    firestore.collection("name").document("usernames").update(
                        "username", un
                    )
                }

            }

        }
    }

    inner class ReViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context)
            .inflate(R.layout.restoreitem, parent, false)
    ) {
        val restoreCb = itemView.restoreCb

    }


}