package com.example.firestoredatafit

import android.graphics.Color
import android.graphics.Color.*
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis.AxisDependency
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_inbodychart.*


class InbodyChartActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inbodychart)
        var firestore = FirebaseFirestore.getInstance()
        var name = intent.getStringExtra("name")
        firestore.collection("$name").document("userinformation").get()
            .addOnSuccessListener {

                val muscledata = it.data?.get("muscle") as ArrayList<HashMap<String, String>>
                val fatdata = it.data?.get("fat") as ArrayList<HashMap<String, String>>


                val musclegraphdata: ArrayList<Entry> = arrayListOf()
                val fatgraphdata: ArrayList<Entry> = arrayListOf()

                var ms = muscledata.size - 1
                val xlabelkey: ArrayList<String> = arrayListOf()


                for (i in 0..ms) {
                    xlabelkey.add(muscledata[i].keys.toMutableList()[0])
                }

                for (i in 0..ms) {
                    var m = muscledata[i].values.toMutableList()
                    var f = fatdata[i].values.toMutableList()
                    if (m[0].toFloatOrNull() != null) {
                        musclegraphdata.add(Entry(i.toFloat(), m[0].toFloat()))
                    }
                    if (f[0].toFloatOrNull() != null) {
                        fatgraphdata.add(Entry(i.toFloat(), f[0].toFloat()))
                    }
                }

                val muscle = LineDataSet(musclegraphdata, "muscle")
                muscle.axisDependency = AxisDependency.LEFT
                muscle.color = Color.RED
                muscle.valueTextSize = 15f
                muscle.setCircleColor(Color.RED)
                muscle.lineWidth = 7f

                val fat = LineDataSet(fatgraphdata, "fat")
                fat.axisDependency = AxisDependency.LEFT
                fat.color = Color.BLUE
                fat.valueTextSize = 15f
                fat.setCircleColor(Color.BLUE)
                fat.lineWidth = 7f

                val dataSets: MutableList<ILineDataSet> = ArrayList()
                dataSets.add(muscle)
                dataSets.add(fat)
                val data = LineData(dataSets)
                inbodychartv.data = data
                inbodychartv.setDrawGridBackground(true)
                inbodychartv.isAutoScaleMinMaxEnabled = true
                inbodychartv.setVisibleXRangeMaximum(4f)
                inbodychartv.invalidate() // refresh
                inbodychartv.setBackgroundColor(Color.WHITE)
                inbodychartv.setGridBackgroundColor(Color.WHITE)
                val inbodydes = inbodychartv.description
                inbodydes.text = ""

                val Legendx = inbodychartv.legend
                Legendx.formSize = 11f
                Legendx.textSize = 11f
                Legendx.form = com.github.mikephil.charting.components.Legend.LegendForm.CIRCLE

                val quarters = xlabelkey
                val formatter: ValueFormatter =
                    object : ValueFormatter() {
                        override fun getAxisLabel(value: Float, axis: AxisBase): String {
                            return quarters[value.toInt()]
                        }
                    }
                val oneformatter: ValueFormatter =
                    object : ValueFormatter() {
                        override fun getAxisLabel(value: Float, axis: AxisBase): String {
                            return quarters[0]
                        }
                    }

                val xAxis: XAxis = inbodychartv.xAxis
                xAxis.position = XAxis.XAxisPosition.BOTTOM
                xAxis.textSize = 13f
                xAxis.granularity = 1f
                if (xlabelkey.size == 1) {
                    xAxis.valueFormatter = oneformatter
                } else {
                    xAxis.valueFormatter = formatter
                }
            }


    }
}

