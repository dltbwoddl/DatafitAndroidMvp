package com.example.firestoredatafit

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_userinformationupdate.*
import kotlinx.android.synthetic.main.activity_userinformationupdate.deleteBtn
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


//골격근, 체지방에서 values값만 가져오기.

class UserInformationUpdateActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_userinformationupdate)
        var name = intent.getStringExtra("name")

        val sdf = SimpleDateFormat("yyyy.M.dd")
        val currentDate = sdf.format(Date())
        var date: String = "$currentDate"


        var firestore = FirebaseFirestore.getInstance()
        firestore.collection("$name")?.document("userinformation").get()
            .addOnSuccessListener {
                var dk = it.data
                var dks = dk?.get("muscle") as ArrayList<HashMap<String, String>>
                var dkss = dks.size - 1
                var dkf = dk?.get("fat") as ArrayList<HashMap<String, String>>
                var dkfs = dkf.size - 1

                updatesex.setText(dk?.get("sex").toString())
                updateage.setText(dk?.get("age").toString())
                updateheight.setText(dk?.get("height").toString())
                updateweight.setText(dk?.get("weight").toString())
                updatemuscle.setText(dks[dkss].values.toMutableList()[0])
                updatefat.setText(dkf[dkfs].values.toMutableList()[0])

            }

        plususerupdateBtn.setOnClickListener {
            var hmo = hashMapOf<String, String>(
                "sex" to updatesex.text.toString(),
                "age" to updateage.text.toString(),
                "height" to updateheight.text.toString(),
                "weight" to updateweight.text.toString()
            )
            var hmom = hashMapOf(date to updatemuscle.text.toString())
            var hmof = hashMapOf(date to updatefat.text.toString())

            var mdata: ArrayList<HashMap<String, String>>
            var fdata: ArrayList<HashMap<String, String>>

            firestore.collection("$name")?.document("userinformation").update(
                hmo as Map<String, String>
            ).addOnSuccessListener {
                firestore.collection("$name")?.document("userinformation").get()
                    .addOnSuccessListener {
                        mdata = it.data?.get("muscle") as ArrayList<HashMap<String, String>>
                        fdata = it.data?.get("fat") as ArrayList<HashMap<String, String>>

                        var mdatasize = mdata.size - 1
                        var mdatakey: MutableList<String> = mutableListOf()

                        var k = 1

                        for (i in 0..mdatasize) {
                            mdatakey.add(mdata[i].keys.toMutableList()[0])
                            if (mdatakey.remove(date)) {
                                mdata.remove(mdata[i])
                                mdata.add(hmom)
                                fdata.remove(fdata[i])
                                fdata.add(hmof)
                                k += 1
                                break
                            }
                        }
                        if (k == 1) {
                            mdata.add(hmom)
                            fdata.add(hmof)
                        }


                        var newmf = hashMapOf<String, ArrayList<HashMap<String, String>>>(
                            "muscle" to mdata,
                            "fat" to fdata
                        )

                        firestore.collection("$name")?.document("userinformation").update(
                            newmf as Map<String, Any>
                        )

                        finish()
                    }

            }

        }

        var usernames: MutableList<String>
        var delusersnames: MutableList<String>

        deleteBtn.setOnClickListener {
            firestore.collection("name").document("usernames").get().addOnSuccessListener {
                usernames = it.data?.get("username") as MutableList<String>
                usernames.remove("$name")
                firestore?.collection("name").document("usernames").update("username", usernames)
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
            firestore.collection("name").document("usernames").get().addOnSuccessListener {
                delusersnames = it.data?.get("deletename") as MutableList<String>
                delusersnames.add("$name")
                firestore?.collection("name").document("usernames")
                    .update("deletename", delusersnames)
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
        }

        inbodyBtn.setOnClickListener {
            val intent = Intent(this, InbodyChartActivity::class.java)
            intent.putExtra("name", "$name")
            startActivity(intent)
        }
    }
}