package com.example.firestoredatafit

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_plusactivity.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

//회원추가
class PlusActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_plusactivity)

        var firestore = FirebaseFirestore.getInstance()
        var usernamedata: MutableList<String> = mutableListOf()

        val sdf = SimpleDateFormat("yyyy.M.dd")
        val currentDate = sdf.format(Date())
        var date: String = "$currentDate"

        fun createData() {
            var userdata = Acdata(
                uname.text.toString(),
                uage.text.toString(),
                usex.text.toString(),
                uheight.text.toString(),
                uweight.text.toString()
            )
            var umuscle = umuscle.text.toString()
            var hmomuscle = hashMapOf<String, String>(date to umuscle)
            var ufat = ufat.text.toString()
            var hmoufat = hashMapOf<String, String>(date to ufat)
            var inbodydata = hashMapOf<String, ArrayList<HashMap<String, String>>>(
                "muscle" to arrayListOf<HashMap<String, String>>(hmomuscle),
                "fat" to arrayListOf<HashMap<String, String>>(hmoufat)
            )
            firestore?.collection("name")?.document("usernames")?.get().addOnSuccessListener {
                usernamedata = it.data?.get("username") as MutableList<String>
                if (usernamedata.remove(uname.text.toString())) {
                    uname.setText("중복 다른 이름을 입력하시오.")
                } else {
                    usernamedata.add("${userdata.name}")
                    firestore?.collection("name").document("usernames")
                        .update("username", usernamedata)
                    firestore?.collection("${userdata.name}")?.document("userinformation")
                        ?.set(userdata)
                    firestore?.collection("${userdata.name}")?.document("userinformation")?.update(
                        inbodydata as Map<String, Any>
                    )
                    finish()
                }

            }
        }

        plususerdataBtn.setOnClickListener {
            createData()
        }
    }
}