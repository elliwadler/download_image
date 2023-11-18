package at.interactivecuriosity.imagedownload

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.io.File

class MainActivity : AppCompatActivity() {

    private lateinit var imageView: ImageView
    private lateinit var downloadButton: Button
    private lateinit var deleteButton: Button
    //Add loading indicator for a better user experience
    private lateinit var loadingIndicator: ProgressBar
    private val imageUrl = "https://www.markusmaurer.at/fhj/eyecatcher.jpg"
    private val fileName = "downloadedImage.jpg"
    private val downloadReceiver = DownloadReceiver()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        imageView = findViewById(R.id.imageView)
        downloadButton = findViewById(R.id.downloadButton)
        deleteButton = findViewById(R.id.deleteButton)
        loadingIndicator = findViewById(R.id.loadingIndicator)

        downloadButton.setOnClickListener {
            showLoadingIndicator(true)
            downloadImage(imageUrl, fileName)
        }

        deleteButton.setOnClickListener {
            deleteImage(fileName)
        }

        val filter = IntentFilter("download_complete")
        registerReceiver(downloadReceiver, filter)
    }

    private fun downloadImage(urlString: String, fileName: String) {
        val downloadIntent = Intent(this, DownloadService::class.java)
        downloadIntent.putExtra("url", urlString)
        downloadIntent.putExtra("fileName", fileName)
        startService(downloadIntent)
    }

    private fun deleteImage(fileName: String) {
        val file = File(getExternalFilesDir(null), fileName)
        if (file.exists()) {
            file.delete()
            imageView.setImageBitmap(null)
            Toast.makeText(this, "Bild gelöscht", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showLoadingIndicator(show: Boolean) {
        if (show) {
            loadingIndicator.visibility = View.VISIBLE
            imageView.visibility = View.GONE
        } else {
            loadingIndicator.visibility = View.GONE
            imageView.visibility = View.VISIBLE
        }
    }
    override fun onDestroy() {
        unregisterReceiver(downloadReceiver)
        super.onDestroy()
    }

    inner class DownloadReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val status = intent?.getStringExtra("status")

            when (status) {
                "success" -> updateUIForSuccess()
                "error" -> updateUIForError()
            }
        }

        private fun updateUIForSuccess() {
            // Update UI for successful download - show picture
            val file = File(getExternalFilesDir(null), fileName)
            val bitmap = BitmapFactory.decodeFile(file.absolutePath)
            imageView.setImageBitmap(bitmap)
            Toast.makeText(this@MainActivity, "Bild heruntergeladen", Toast.LENGTH_SHORT).show()
            showLoadingIndicator(false)
        }

        private fun updateUIForError() {
            // Update UI for download error
            Toast.makeText(this@MainActivity, "Fehler beim Herunterladen", Toast.LENGTH_LONG).show()
        }
    }
}
