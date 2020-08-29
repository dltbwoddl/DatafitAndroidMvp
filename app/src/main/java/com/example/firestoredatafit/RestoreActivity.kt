package com.example.firestoredatafit

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_restore.*

class RestoreActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_restore)
        var firestore = FirebaseFirestore.getInstance()
        var delusersname: MutableList<String>

        firestore.collection("name").document("usernames").get().addOnSuccessListener {
            delusersname = it.data?.get("deletename") as MutableList<String>
            if (delusersname.size != 0) {
                ReStoreRecyclerview.adapter = ReAdapter(delusersname)
                ReStoreRecyclerview.layoutManager = LinearLayoutManager(this)
            } else {
                restoreTextview.text = "복원할 수 있는 회원 없음."
            }
        }
        finishBtn.setOnClickListener {
            finish()
        }
    }
}