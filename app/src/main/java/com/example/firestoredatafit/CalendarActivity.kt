package com.example.firestoredatafit

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.size
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_calendaractivity.*
import java.text.SimpleDateFormat
import java.util.*


class CalendarActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calendaractivity)
        var name = intent.getStringExtra("name")
        userexi.text = name
        var excaldata: MutableList<String> = mutableListOf()

        val redata: MutableList<String> = mutableListOf()
        var i = 0
        val sdf = SimpleDateFormat("yyyy.M.dd")
        val currentDate = sdf.format(Date())
        var date: String = "$currentDate"
        var fdate: String? = null
        exrecyclerview.adapter = name?.let { exAdapter(excaldata, it, date) }
        exrecyclerview.layoutManager = LinearLayoutManager(this)

        var usernames: MutableList<String> = mutableListOf()


        var firestore = FirebaseFirestore.getInstance()


        fun readData(name: String, date: String) {
            redata.clear()
            firestore?.collection("$name")
                .document("userinformation/exerciseinformation/$date")
                .get()
                .addOnCompleteListener {
                    var td = it.result?.data?.get("thisdate")
                    var values = it.result?.data?.values
                    if (values != null) {
                        for (value in values) {
                            if (value != td) {
                                redata += value as MutableList<String>

                            }
                        }
                    }
                    var excount = (exrecyclerview.adapter as exAdapter).itemCount

                    (exrecyclerview.adapter as exAdapter?)?.notifyItemRangeRemoved(0, excount)
                    exrecyclerview.adapter = exAdapter(redata, name, date)
                }
            i = 0
        }

        var docRef =
            firestore.collection("$name").document("userinformation/exerciseinformation/$date")
        docRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                return@addSnapshotListener
            }

            if (snapshot != null && snapshot.exists()) {
                if (i == 0) {
                    Log.d("exdoc", "exdocexdocexdocexdoc")
                    if (name != null) {
                        i += 1
                        readData(name, date)
                    }
                }
            } else {

            }
        }

        calendarView.setOnDateChangeListener { _, year, month, day ->
            fdate = "$year.${month + 1}.$day"
            Log.d("날짜 바뀜", "$fdate")
            if (date != fdate) {
                if (name != null) {
                    var docRef =
                        firestore.collection("$name")
                            .document("userinformation/exerciseinformation/$fdate")
                    docRef.addSnapshotListener { snapshot, e ->
                        if (e != null) {
                            return@addSnapshotListener
                        }
                        if (snapshot != null && snapshot.exists()) {
                            if (i == 0) {
                                Log.d("exdoc2", "exdocexdocexdocexdoc")
                                i += 1
                                if (name != null) {
                                    readData(name, fdate!!)
                                }
                            }
                        } else {
                            readData(name, fdate!!)
                        }
                    }
                }
            } else {
                if (name != null) {
                    readData(name, date)
                }
            }
        }


        userinformationupdateBtn.setOnClickListener {
            val intent = Intent(this, UserInformationUpdateActivity::class.java)
            intent.putExtra("name", name)
            startActivity(intent)
        }

        explusBtn.setOnClickListener {
            val explustintent = findViewById<Button>(R.id.explusBtn)
            explustintent.setOnClickListener {
                val intent = Intent(this, ExPlusActivity::class.java)
                intent.putExtra("name", name)
                if (fdate != null) {
                    intent.putExtra("date", fdate)
                } else {
                    intent.putExtra("date", date)
                }
                startActivity(intent)
            }
        }

        searchexBtn.setOnClickListener {
            val intent = Intent(this, SearchingExercise::class.java)
            intent.putExtra("name", name)
            startActivity(intent)
        }

    }
}
