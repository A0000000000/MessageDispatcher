package xyz.maoyanluo.messagedispatcher

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import android.telephony.SmsMessage

class SMSBroadcastReceiver: BroadcastReceiver() {

    private val onSmsMessageReceivers = HashSet<(String, String, Long, Int) -> Unit>()

    fun registerOnSmsMessageReceiver(onSmsMessageReceiver: (String, String, Long, Int) -> Unit) {
        onSmsMessageReceivers.add(onSmsMessageReceiver)
    }

    fun unregisterOnSmsMessageReceiver(onSmsMessageReceiver: (String, String, Long, Int) -> Unit) {
        onSmsMessageReceivers.remove(onSmsMessageReceiver)
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == Telephony.Sms.Intents.SMS_RECEIVED_ACTION) {
            val smsMessages = Telephony.Sms.Intents.getMessagesFromIntent(intent)
            smsMessages?.forEach { smsMessage ->
                smsMessage?.run {
                    val sender = displayOriginatingAddress
                    val content = displayMessageBody
                    val date = timestampMillis
                    val subId = getSubId(this@run)
                    onSmsMessageReceivers.forEach { callback ->
                        callback.invoke(sender, content, date, subId)
                    }
                }
            }
        }
    }

    private fun getSubId(smsMessage: SmsMessage): Int {
        try {
            val method = Class.forName("android.telephony.SmsMessage").getMethod("getSubId")
            return method.invoke(smsMessage)?.toString()?.toInt() ?: -1
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return -1
    }


}