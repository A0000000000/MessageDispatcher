package xyz.maoyanluo.messagedispatcher

import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Telephony
import android.text.TextUtils
import java.text.SimpleDateFormat
import java.util.*

class BingoActivity : AppCompatActivity() {

    private val smsBroadcastReceiver = SMSBroadcastReceiver()
    private val larkDispatcher = LarkDispatcher()
    private val simpleDataFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

    private var card1 = "1"
    private var card2 = "2"
    private var webhook = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bingo)
        initParams()
        initLarkWebHooks()
        registerSmsBroadcastReceiver()
    }

    private fun initParams() {
        card1 = intent.getStringExtra("card1") ?: "1"
        card2 = intent.getStringExtra("card2") ?: "2"
        webhook = intent.getStringExtra("webhook") ?: ""
    }

    private fun registerSmsBroadcastReceiver() {
        smsBroadcastReceiver.registerOnSmsMessageReceiver(this::onSmsMessageReceiver)
        val intentFiler = IntentFilter(Telephony.Sms.Intents.SMS_RECEIVED_ACTION)
        intentFiler.priority = Int.MAX_VALUE
        registerReceiver(smsBroadcastReceiver, intentFiler)
    }

    private fun initLarkWebHooks() {
        if (webhook != "") {
            larkDispatcher.addApi(webhook)
        }
    }

    private fun onSmsMessageReceiver(sender: String, content: String, revTimeStamp: Long, card: Int) {
        val sb = StringBuffer()
        sb.append("发信人: $sender\n")
        sb.append("收信人: ${getCard(card)}\n")
        sb.append("收信时间: ${simpleDataFormat.format(Date(revTimeStamp))}\n")
        sb.append("---内容---\n")
        sb.append("$content\n")
        sb.append("---------\n")
        larkDispatcher.postMessage(sb.toString())
    }

    private fun getCard(card: Int): String {
        return when (card) {
            1 -> {
                if (card1 == "") "1" else card1
            }
            2 -> {
                if (card2 == "") "2" else card2
            }
            else -> {
                card.toString()
            }
        }
    }

}