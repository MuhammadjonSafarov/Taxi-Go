package uz.xia.taxi.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import uz.xia.taxi.R
import uz.xia.taxi.ui.MainActivity
import kotlin.random.Random

private const val TAG = "MyFirebaseMessagingService"
private val UPDATE_CHANNEL_NAME: CharSequence = "UPDATE_CHANNEL_NAME_VENDOR"
private const val UPDATE_CHANNEL_ID: String = "UPDATE_CHANNEL_ID_VENDOR"
private const val NOTIFICATION: String = "NOTIFICATION"

@AndroidEntryPoint
class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {

        Timber.tag(TAG).d("From: " + remoteMessage.from!!)

        // applyLocale()

        if (remoteMessage.data.isNotEmpty()) {
            createNotification(remoteMessage.data)
            Timber.d("$TAG Message data payload: " + remoteMessage.data)
        }

        if (remoteMessage.notification != null) {
            Timber.d("$TAG Message Notification Body: " + remoteMessage.notification!!.body!!)
        }
    }

    override fun onNewToken(token: String) {
        /*FirebaseInstanceId.getInstance().instanceId.addOnCompleteListener(object :
            OnCompleteListener<InstanceIdResult> {
            override fun onComplete(p0: Task<InstanceIdResult>) {
                if (p0.isSuccessful) {
                    Log.d("TTT", "getInstanceFailed ${p0.exception}")
                    return
                }
                Log.d("TTT", "token: " + p0.result!!.token)
            }
        })*/
    }

    private fun createNotification(data: Map<String?, String?>) {
        if (data["version"] != null) {
           // preference.setVersionControl(data["version"]?.toInt() ?: 1)
        }

        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(
                UPDATE_CHANNEL_ID,
                UPDATE_CHANNEL_NAME,
                importance)
            manager.createNotificationChannel(channel)
        }

        val builder = NotificationCompat.Builder(this,UPDATE_CHANNEL_ID)
            .setContentTitle(data["title"])
            .setContentText(data["subtitle"])
            .setSubText(data["body"])
            .setAutoCancel(true)
            .setContentIntent(getIntent(data["type"]?:"new_order"))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setSmallIcon(R.mipmap.ic_launcher_foreground)
            .setWhen(System.currentTimeMillis())
            .setDefaults(Notification.DEFAULT_LIGHTS or Notification.DEFAULT_VIBRATE)
            .setStyle(NotificationCompat.BigTextStyle())
        manager.notify(Random.nextInt(), builder.build())
    }

    private fun getIntent(type: String): PendingIntent? {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra(NOTIFICATION, type)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        return PendingIntent.getActivity(
            this, 0,
            intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }
}
