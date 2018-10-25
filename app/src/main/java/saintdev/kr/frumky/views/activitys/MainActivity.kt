package saintdev.kr.frumky.views.activitys

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.ListView
import saintdev.kr.frumky.R
import saintdev.kr.frumky.libs.bluetooth.BlueConnManager

class MainActivity : AppCompatActivity() {
    private lateinit var listView: ListView

    private lateinit var blueConnManager: BlueConnManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 블루투스 연결
        this.blueConnManager = BlueConnManager(this)
        this.blueConnManager.openConnectableList {
            if(blueConnManager.tryConnectDevice(it)) {
                startActivity(Intent(this@MainActivity, ConsoleActivity::class.java))
            } else {
                finish()
            }
        }
    }
}
