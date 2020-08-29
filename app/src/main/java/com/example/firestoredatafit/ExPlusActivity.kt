package com.example.firestoredatafit

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.annotation.RequiresApi
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_explusac.*
import kotlinx.android.synthetic.main.activity_plusactivity.*
import java.lang.Exception
import kotlin.math.log

//운동추가
class ExPlusActivity : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_explusac)
        //캘린더뷰로부터 intent객체를 통해 받은 name,date값
        var name = intent.extras?.get("name")
        var date = intent.extras?.get("date")

        var firestore = FirebaseFirestore.getInstance()
        var ula: String = " "
        var aexercise: MutableList<String>
        var exitems: MutableList<String> = mutableListOf()
        var ex: String = " "
        var part: String = " "
        var plusex: MutableList<String>

        fun exdata(ula: String) {
            firestore.collection("exercisedata").document("exdata/body/exd").get()
                .addOnSuccessListener {
                    exitems.clear()
                    exitems.add("exercise choice")
                    var exd = it.data?.get("$ula") as MutableList<String>
                    for (i in exd) {
                        exitems.add(i)
                    }
                    val exAdapter =
                        ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, exitems)
                    exdataspinner.adapter = exAdapter
                    exdataspinner.onItemSelectedListener =
                        object : AdapterView.OnItemSelectedListener {
                            override fun onItemSelected(
                                parent: AdapterView<*>,
                                view: View,
                                position: Int,
                                id: Long
                            ) {
                                ex = exdataspinner.selectedItem.toString()
                                if (ex != "exercise choice") {
                                    newextext2.visibility = View.INVISIBLE
                                } else {
                                    newextext2.visibility = View.VISIBLE
                                    newextext2.hint = "새로운 운동을 추가하시오."
                                }
                            }

                            override fun onNothingSelected(parent: AdapterView<*>) {

                            }
                        }
                }
        }

        var partlist: MutableList<String> = mutableListOf()
        firestore.collection("exercisedata").document("exdata").get()
            .addOnSuccessListener {
                partlist = it.data?.get("part") as MutableList<String>
                val items = partlist
                val myAdapter =
                    ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, items)
                ulaspinner.adapter = myAdapter
                ulaspinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>,
                        view: View,
                        position: Int,
                        id: Long
                    ) {
                        ula = ulaspinner.selectedItem.toString()
                        if (ula != "part choice") {
                            newparttext.visibility = View.INVISIBLE
                            newextext.visibility = View.INVISIBLE
                        } else {
                            newparttext.visibility = View.VISIBLE
                            newextext.visibility = View.VISIBLE
                            newparttext.hint = "새로운 부위를 추가하시오."
                        }
                        exdata(ula)
                    }

                    override fun onNothingSelected(parent: AdapterView<*>) {

                    }
                }
            }
        var plist: MutableList<String> = mutableListOf()

        ulaBtn.setOnClickListener {

            if (ula == "part choice") {
                firestore.collection("exercisedata").document("exdata").get()
                    .addOnSuccessListener {
                        var npl = newparttext.text.toString()
                        ula = npl
                        var net = newextext.text.toString()
                        plist = it.data?.get("part") as MutableList<String>
                        if (plist.remove(npl)) {
                            newparttext.setText("이미 존재하는 부위입니다.")
                            plist.add(npl)
                        } else {
                            plist.add(npl)
                            firestore.collection("exercisedata").document("exdata").update(
                                "part", plist
                            ).addOnSuccessListener {
                                firestore.collection("exercisedata").document("exdata/body/exd")
                                    .update(
                                        npl, arrayListOf(net)
                                    ).addOnSuccessListener {
                                        firestore.collection("$name")
                                            .document("userinformation/exerciseinformation/$date")
                                            .get()
                                            .addOnSuccessListener {
                                                if (it.data != null) {
                                                    firestore.collection("$name")
                                                        .document("userinformation/exerciseinformation/$date")
                                                        .update(
                                                            npl,
                                                            arrayListOf("$net : ${exdesedittext.text}")
                                                        )
                                                } else {

                                                    firestore.collection("exercisedata")
                                                        .document("exdata")
                                                        .get().addOnSuccessListener {
                                                            var part =
                                                                it.data?.get("part") as MutableList<String>
                                                            var hmo =
                                                                hashMapOf<String, ArrayList<String>>()
                                                            for (i in part) {
                                                                hmo[i] = arrayListOf<String>()
                                                            }
                                                            hmo[ula] =
                                                                arrayListOf("$net" + " : " + "${exdesedittext.text.toString()}")
                                                            hmo["thisdate"] =
                                                                arrayListOf<String>(date.toString())
                                                            firestore.collection("$name")
                                                                .document("userinformation/exerciseinformation/$date")
                                                                .set(
                                                                    hmo
                                                                )
                                                        }
                                                }
                                            }
                                    }
                            }
                            finish()
                        }


                    }
            } else {

                if (ex == "exercise choice") {
                    ex = newextext2.text.toString()
                    firestore.collection("exercisedata").document("exdata/body/exd").get()
                        .addOnSuccessListener {
                            plusex = it.data?.get("$ula") as MutableList<String>
                            if (plusex.remove(ex)) {
                                plusex.add(ex)
                            } else {
                                plusex.add(ex)
                                firestore.collection("exercisedata").document("exdata/body/exd")
                                    .update(
                                        "$ula", plusex
                                    )
                            }
                        }
                }

                firestore?.collection("$name")
                    ?.document("userinformation/exerciseinformation/$date")
                    ?.get().addOnSuccessListener {

                        if (it.data != null) {
                            var dataexkeydata = it.data?.keys
                            if (dataexkeydata!!.remove(ula)) {
                                aexercise = it.data?.get("$ula") as MutableList<String>
                                aexercise.add(ex + " : " + exdesedittext.text.toString())
                                firestore?.collection("$name")
                                    ?.document("userinformation/exerciseinformation/$date")
                                    ?.update("$ula", aexercise)
                            } else {
                                firestore.collection("$name")
                                    .document("userinformation/exerciseinformation/$date").update(
                                        ula, ArrayList<String>()
                                    ).addOnSuccessListener {
                                        firestore?.collection("$name")
                                            ?.document("userinformation/exerciseinformation/$date")
                                            ?.get().addOnSuccessListener {
                                                aexercise =
                                                    it.data?.get("$ula") as MutableList<String>
                                                aexercise.add(ex + " : " + exdesedittext.text.toString())
                                                firestore?.collection("$name")
                                                    ?.document("userinformation/exerciseinformation/$date")
                                                    ?.update("$ula", aexercise)
                                            }
                                    }
                            }
                        } else {

                            firestore.collection("exercisedata").document("exdata")
                                .get().addOnSuccessListener {
                                    var part =
                                        it.data?.get("part") as MutableList<String>
                                    var hmo = hashMapOf<String, ArrayList<String>>()
                                    for (i in part) {
                                        hmo[i] = arrayListOf<String>()
                                    }
                                    hmo[ula] =
                                        arrayListOf<String>(ex + " : " + exdesedittext.text.toString())
                                    hmo["thisdate"] = arrayListOf<String>(date.toString())
                                    firestore.collection("$name")
                                        .document("userinformation/exerciseinformation/$date")
                                        .set(
                                            hmo
                                        )

                                }
                        }
                    }
                finish()
            }

        }
    }
}