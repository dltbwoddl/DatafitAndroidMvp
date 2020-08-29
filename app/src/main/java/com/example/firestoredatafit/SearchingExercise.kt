package com.example.firestoredatafit

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_searching_exercise.*

class SearchingExercise : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_searching_exercise)

        var ula: String

        var name = intent.extras?.get("name")

        var firestore = FirebaseFirestore.getInstance()

        firestore.collection("exercisedata").document("exdata").get().addOnSuccessListener {
            val items = it.data?.get("part") as ArrayList<String>
            val myAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, items)
            part.adapter = myAdapter
            part.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View,
                    position: Int,
                    id: Long
                ) {
                    ula = part.selectedItem.toString()
                    exdata.text = " "
                    if (ula == "part choice") {
                        exdata.text = "파트를 선택하시오."
                    } else {
                        firestore.collection("$name/userinformation/exerciseinformation")
                            .addSnapshotListener { value, error ->
                                if (value?.documents?.size!! > 0) {
                                    var n: Int = value?.documents?.size!! - 1
                                    for (i in 0..n) {
                                        var td =
                                            value?.documents?.get(i)
                                                ?.get("thisdate") as ArrayList<String>
                                        var ul =
                                            value?.documents?.get(i)?.get("$ula")
                                        if (ul != null) {
                                            ul = ul as ArrayList<String>
                                            var uls = ul.size - 1
                                            if (uls > 0) {
                                                exdata.append("\n" + td[0])
                                                for (i in 0..uls) {
                                                    exdata.append(
                                                        "\n" + ul[i]
                                                    )
                                                }
                                            } else if (uls == 0) {
                                                exdata.append(
                                                    "\n" + td[0] + "  " + ul[0]
                                                )
                                            }
                                        }
                                    }
                                } else {
                                    exdata.append("해당 회원의 운동데이터가 없습니다.")
                                }
                            }
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>) {

                }
            }
        }
    }
}