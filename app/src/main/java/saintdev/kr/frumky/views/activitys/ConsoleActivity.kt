package saintdev.kr.frumky.views.activitys

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import kotlinx.android.synthetic.main.activity_console.*
import saintdev.kr.frumky.R
import android.provider.MediaStore
import android.R.attr.data
import android.R.attr.data
import android.content.CursorLoader
import android.media.MediaPlayer
import android.widget.Toast
import saintdev.kr.frumky.libs.bluetooth.BlueConnManager
import saintdev.kr.frumky.libs.bluetooth.BlueSocket
import java.io.File
import java.lang.Exception


class ConsoleActivity : AppCompatActivity(), BlueSocket.OnBlueSocketListener {
    private lateinit var player: MediaPlayer
    private val audioSet: HashMap<Int, Uri> = hashMapOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_console)

        audio_select_1.setOnClickListener { onButtonClicked(it) }
        audio_select_2.setOnClickListener { onButtonClicked(it) }
        audio_select_3.setOnClickListener { onButtonClicked(it) }
        audio_select_4.setOnClickListener { onButtonClicked(it) }
        audio_select_5.setOnClickListener { onButtonClicked(it) }
        audio_select_6.setOnClickListener { onButtonClicked(it) }

        BlueConnManager.getSocket().setReceiveListener(this)

        this.player = MediaPlayer()
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

        val intent: Intent
        intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "audio/mpeg"
        startActivityForResult(Intent.createChooser(intent, "Select audio"), idx)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {

        if(resultCode == Activity.RESULT_OK) {
            super.onActivityResult(requestCode, resultCode, data)
            val uri = data.data

            when(requestCode) {
                0x1 -> {
                    audio_select_1.text = uri.encodedPath
                    audioSet[0] = uri
                }
                0x2 -> {
                    audio_select_2.text = uri.encodedPath
                    audioSet[1] = uri
                }
                0x3 -> {
                    audio_select_3.text = uri.encodedPath
                    audioSet[2] = uri
                }
                0x4 -> {
                    audio_select_4.text = uri.encodedPath
                    audioSet[3] = uri
                }
                0x5 -> {
                    audio_select_5.text = uri.encodedPath
                    audioSet[4] = uri
                }
                0x6 -> {
                    audio_select_6.text = uri.encodedPath
                    audioSet[5] = uri
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        player.release()
        finish()
    }

    override fun onReceive(msg: String?) {
        runOnUiThread(Runnable {
            if(msg != null) {
                player.stop()
                player.reset()

                try {
                    val idx = msg.toInt()
                    player.setDataSource(this@ConsoleActivity, audioSet[idx-1])
                    player.prepare()
                    player.start()
                } catch(ex: Exception) {
                    Toast.makeText(this@ConsoleActivity, "No Int exception.", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }
}