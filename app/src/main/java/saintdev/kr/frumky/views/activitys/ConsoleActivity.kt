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
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import saintdev.kr.frumky.libs.bluetooth.BlueConnManager
import saintdev.kr.frumky.libs.bluetooth.BlueSocket
import java.io.File
import java.lang.Exception
import com.jaiselrahman.filepicker.activity.FilePickerActivity
import com.jaiselrahman.filepicker.config.Configurations
import com.jaiselrahman.filepicker.model.MediaFile


class ConsoleActivity : AppCompatActivity(), BlueSocket.OnBlueSocketListener {
    private lateinit var player: MediaPlayer            // 미디어 플레이어
    private val playerQueue = arrayListOf<File>()       // 플레이 큐
    private lateinit var adapter: ArrayAdapter<String>
    private var indexPointer = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_console)
        BlueConnManager.getSocket().setReceiveListener(this)

        this.player = MediaPlayer()
        this.adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1)
        play_quete.adapter = this.adapter

        control_stop.setOnClickListener { onClickStop() }
        control_clear.setOnClickListener { onAddMusic() }
    }

    override fun onReceive(msg: String?) {
        runOnUiThread {
            if(msg != null) {
                player.stop()
                player.reset()

                if(msg == "1" && playerQueue.isNotEmpty()) {
                    // 포인터 조절
                    ++indexPointer
                    if(playerQueue.size == indexPointer) indexPointer = 0

                    val file = playerQueue[indexPointer]
                    player.setDataSource(file.path)
                    player.prepare()
                    player.start()

                    control_now.text = file.name
                }
            }
        }
    }

    private fun onClickStop() {
        this.adapter.clear()
        this.playerQueue.clear()
        control_now.text = ""
        this.player.stop()
        this.player.reset()
        this.adapter.notifyDataSetChanged()
    }

    val FILE_SELECTOR = 0x1
    private fun onAddMusic() {
        val intent = Intent(this, FilePickerActivity::class.java)
        intent.putExtra(FilePickerActivity.CONFIGS, Configurations.Builder()
                .setCheckPermission(true)
                .setShowAudios(true)
                .setShowImages(false)
                .setMaxSelection(10)
                .setSkipZeroSizeFiles(true)
                .build())
        startActivityForResult(intent, FILE_SELECTOR)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == FILE_SELECTOR && resultCode == Activity.RESULT_OK && data != null) {
            val files = data.getParcelableArrayListExtra<MediaFile>(FilePickerActivity.MEDIA_FILES)
            files.forEach {
                playerQueue.add(File(it.path))      // 큐에 포함한다.
            }

            if(!files.isEmpty()) {
                empty_queue.visibility = View.GONE
                play_quete.visibility = View.VISIBLE
            }

            // set adapter
            playerQueue.forEach {
                this.adapter.add(it.name)
            }
            this.adapter.notifyDataSetChanged()
        }
    }
}