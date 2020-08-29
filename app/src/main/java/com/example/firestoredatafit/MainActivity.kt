package com.example.firestoredatafit

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.recyclerview.widget.GridLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_main.*

//이번 대회 이후 데이터 구조 바꾸어야,,,, 로그인하고 그에 따라 데이터 가져오도록 해야 한다.(이름 중복과 같은 이유 때문에)

@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {
    private lateinit var firestore: FirebaseFirestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        var acdatas: MutableList<Acdata> = mutableListOf()
        var firestore = FirebaseFirestore.getInstance()
        var usernamess: MutableList<String> = mutableListOf()
        var n: MutableList<Acdata> = mutableListOf()
        var i =0


        rv_main_list.adapter = MainAdapter(acdatas)
        rv_main_list.layoutManager = GridLayoutManager(this, 2)

        fun createData() {
            acdatas.clear()

            firestore.collection("name").document("usernames")
                .get().addOnSuccessListener {
                    usernamess = it.data?.get("username") as MutableList<String>
                    for (name in usernamess) {

                        firestore.collection("$name").document("userinformation")
                            .get().addOnSuccessListener {
                                var d = it.data
                                acdatas.add(
                                    Acdata(
                                        d?.get("name").toString(),
                                        d?.get("age").toString(),
                                        d?.get("sex").toString(),
                                        d?.get("height").toString(),
                                        d?.get("weight").toString()
                                    )
                                )
                                rv_main_list.adapter = MainAdapter(acdatas)
                                i=0
                            }
                    }
                }
        }

        val plustintent = findViewById<Button>(R.id.plusBtn)
        plustintent.setOnClickListener {
            val intent = Intent(this@MainActivity, PlusActivity::class.java)
            startActivity(intent)
        }

        val docRef = firestore.collection("name").document("usernames")
        docRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                return@addSnapshotListener
            }
            if (snapshot != null && snapshot.exists()) {
                //createdata에 동시에 진입하게 되면 data가 중복적으로 쌓이게 된다. 때문에 하나가 끝나고 하나가 실행되는 구조여야 한다.
                if (i==0) {
                    i+=1
                    Log.d("docRef", "docRef")
                    createData()
                }
            } else {

            }
        }

        entireBtn.setOnClickListener {
            createData()
            searchtext.text.clear()
        }

        searchtext.setOnClickListener {
            var sn = searchtext.text
            if (sn.length > 1) {
                firestore.collection("$sn").document("userinformation")
                    .get().addOnCompleteListener {

                        n = mutableListOf()
                        var d = it.result?.data
                        if (d != null) {
                            n.add(
                                Acdata(
                                    d?.get("name").toString(),
                                    d?.get("age").toString(),
                                    d?.get("sex").toString(),
                                    d?.get("height").toString(),
                                    d?.get("weight").toString()
                                )
                            )
                            acdatas = n
                            rv_main_list.adapter = MainAdapter(acdatas)
                        }
                    }
            }
        }
        restoreBtn.setOnClickListener {
            val intent = Intent(this@MainActivity, RestoreActivity::class.java)
            startActivity(intent)
        }
    }
}
