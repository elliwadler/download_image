package at.interactivecuriosity.imagedownload

import android.app.IntentService
import android.content.Intent
import java.io.File
import java.io.FileOutputStream
import java.net.URL

class DownloadService : IntentService("DownloadService") {

    override fun onHandleIntent(intent: Intent?) {
        val url = intent?.getStringExtra("url")
        val fileName = intent?.getStringExtra("fileName")

        if (!url.isNullOrEmpty() && !fileName.isNullOrEmpty()) {
            try {
                val urlConnection = URL(url).openConnection()
                val inputStream = urlConnection.getInputStream()
                val file = File(getExternalFilesDir(null), fileName)

                FileOutputStream(file).use { output ->
                    inputStream.copyTo(output)
                }

                // Broadcast, send download status to activity
                val broadcastIntent = Intent("download_complete")
                broadcastIntent.putExtra("status", "success")
                sendBroadcast(broadcastIntent)

            } catch (e: Exception) {
                e.printStackTrace()

                val broadcastIntent = Intent("download_complete")
                broadcastIntent.putExtra("status", "error")
                sendBroadcast(broadcastIntent)
            }
        }
    }
}
