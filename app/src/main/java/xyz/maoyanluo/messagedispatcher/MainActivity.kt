package xyz.maoyanluo.messagedispatcher

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {

    companion object {
        const val PERMISSION_REQUEST_CODE = 1113
    }

    private var card1: TextView? = null
    private var card2: TextView? = null
    private var larkWebhook: TextView? = null
    private var go: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        checkPermissions()
        initView()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        var needQuit = false
        if (requestCode == PERMISSION_REQUEST_CODE) {
            for (index in grantResults.indices) {
                if (grantResults[index] != PackageManager.PERMISSION_GRANTED) {
                    needQuit = true
                    Toast.makeText(this, "缺少权限${permissions[index]}!", Toast.LENGTH_SHORT).show()
                }
            }
        }
        if (needQuit) {
            finish()
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun checkPermissions() {
        val permissions = ArrayList<String>()
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.SEND_SMS)
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.RECEIVE_SMS)
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.READ_SMS)
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.INTERNET)
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_WIFI_STATE) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.ACCESS_WIFI_STATE)
        }
        if (permissions.isNotEmpty()) {
            requestPermissions(permissions.toTypedArray(), PERMISSION_REQUEST_CODE)
        }
    }

    private fun initView() {
        card1 = findViewById(R.id.card1)
        card2 = findViewById(R.id.card2)
        larkWebhook = findViewById(R.id.lark_webhook)
        go = findViewById(R.id.go)
        val sp = getSharedPreferences("base-message", MODE_PRIVATE)
        val card1Str = sp.getString("card1", "")
        val card2Str = sp.getString("card2", "")
        val webhook = sp.getString("webhook", "")
        if (card1Str != "") {
            card1?.text = card1Str
        }
        if (card2Str != "") {
            card2?.text = card2Str
        }
        if (webhook != "") {
            larkWebhook?.text = webhook
        }
        go?.setOnClickListener {
            val intent = Intent(this@MainActivity, BingoActivity::class.java)
            val c1 = card1?.text?.toString() ?: ""
            val c2 = card2?.text?.toString() ?: ""
            val wh = larkWebhook?.text?.toString() ?: ""

            sp.edit()?.let {
                if (c1 != "") {
                    it.putString("card1", c1)
                }
                if (c2 != "") {
                    it.putString("card2", c2)
                }
                if (wh != "") {
                    it.putString("webhook", wh)
                }
                it.commit()
            }

            if (wh == "") {
                Toast.makeText(this@MainActivity, "webhook不能为空!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            intent.putExtra("card1", c1)
            intent.putExtra("card2", c2)
            intent.putExtra("webhook", wh)

            startActivity(intent)
        }
    }

}