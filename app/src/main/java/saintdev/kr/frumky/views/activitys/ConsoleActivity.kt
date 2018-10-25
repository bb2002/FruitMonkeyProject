package saintdev.kr.frumky.views.activitys

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import kotlinx.android.synthetic.main.activity_console.*
import saintdev.kr.frumky.R

class ConsoleActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_console)

        audio_select_1.setOnClickListener { onButtonClicked(it) }
        audio_select_2.setOnClickListener { onButtonClicked(it) }
        audio_select_3.setOnClickListener { onButtonClicked(it) }
        audio_select_4.setOnClickListener { onButtonClicked(it) }
        audio_select_5.setOnClickListener { onButtonClicked(it) }
        audio_select_6.setOnClickListener { onButtonClicked(it) }
    }

    private fun onButtonClicked(view: View) {
        val idx = when(view.id) {
            R.id.audio_select_1 -> 0x1
            R.id.audio_select_2 -> 0x2
            R.id.audio_select_3 -> 0x3
            R.id.audio_select_4 -> 0x4
            R.id.audio_select_5 -> 0x5
            R.id.audio_select_6 -> 0x6
            else -> 0x1
        }

        startActivityForResult(
                Intent.createChooser(Intent(Intent.ACTION_GET_CONTENT).setType("audio/*"),
                        "Choose an Audio"), idx)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)

        if(resultCode == Activity.RESULT_OK) {
            val uri = data.data

            when(requestCode) {
                0x1 -> audio_select_1.text = path
                0x2 -> audio_select_2.text = path
                0x3 -> audio_select_3.text = path
                0x4 -> audio_select_4.text = path
                0x5 -> audio_select_5.text = path
                0x6 -> audio_select_6.text = path
            }
        }
    }
}