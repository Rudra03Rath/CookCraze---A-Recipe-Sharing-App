package com.example.cookcraze.Activity

import Recipe
import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.MediaController
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.cookcraze.R
import java.util.*

class InstructionsActivity : AppCompatActivity() {

    private lateinit var videoView: VideoView
    private lateinit var progressBar: ProgressBar
    private lateinit var btnFullScreen: Button
    private lateinit var tvInstructions: TextView
    private lateinit var btnAddTimer: Button
    private lateinit var btnFinish: Button

    private val REQUEST_NOTIFICATION_PERMISSION = 1
    private var isFullScreen = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_instructions)

        videoView = findViewById(R.id.videoView)
        progressBar = findViewById(R.id.progressBar)
        btnFullScreen = findViewById(R.id.btnFullScreen)
        tvInstructions = findViewById(R.id.tvInstructions)
        btnAddTimer = findViewById(R.id.btnAddTimer)
        btnFinish = findViewById(R.id.btnFinish)

        val recipe = intent.getParcelableExtra<Recipe>("recipe")
        recipe?.let {
            loadVideo(it.videoUrl)
            tvInstructions.text = it.instructions
        }

        val mediaController = MediaController(this)
        videoView.setMediaController(mediaController)
        mediaController.setAnchorView(videoView)

        videoView.setOnPreparedListener { mp ->
            progressBar.visibility = View.GONE
            mp.start()
            mediaController.show()
        }

        videoView.setOnErrorListener { _, _, _ ->
            progressBar.visibility = View.GONE
            Toast.makeText(this, "Error loading video", Toast.LENGTH_SHORT).show()
            true
        }

        btnFullScreen.setOnClickListener {
            if (isFullScreen) {
                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            } else {
                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            }
            isFullScreen = !isFullScreen
        }

        btnAddTimer.setOnClickListener { showTimerDialog() }
        btnFinish.setOnClickListener {
            val intent = Intent(this, FinishActivity::class.java)
            intent.putExtra("recipe", recipe)
            startActivity(intent)
        }
    }

    private fun loadVideo(videoUrl: String?) {
        progressBar.visibility = View.VISIBLE
        if (videoUrl != null) {
            val uri = Uri.parse(videoUrl)
            videoView.setVideoURI(uri)
        } else {
            progressBar.visibility = View.GONE
            Toast.makeText(this, "Video URL is not available", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showTimerDialog() {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(
            this,
            { _, hourOfDay, minute ->
                val currentTime = System.currentTimeMillis()
                val selectedTime = Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY, hourOfDay)
                    set(Calendar.MINUTE, minute)
                }.timeInMillis

                if (selectedTime <= currentTime) {
                    Toast.makeText(this, "Please select a future time", Toast.LENGTH_SHORT).show()
                } else {
                    setTimer(selectedTime - currentTime)
                }
            },
            hour,
            minute,
            true
        )
        timePickerDialog.show()
    }

    private fun setTimer(duration: Long) {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "timer_channel"
        val channelName = "Timer Notification"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(channel)
        }

        val intent = Intent(this, InstructionsActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_app_logo)
            .setContentTitle("Timer Finished")
            .setContentText("The timer you set has finished.")
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        /*val notificationManagerCompat = NotificationManagerCompat.from(this)
        notificationManagerCompat.notify(1, notificationBuilder.build())*/

        val timer = Timer()
        timer.schedule(object : TimerTask() {
            override fun run() {
                notificationManager.notify(1, notificationBuilder.build())
            }
        }, duration)
    }
}
