package com.spotato.bluetoothcontroller

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.speech.RecognizerIntent
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import org.w3c.dom.Text
import java.io.InputStream
import java.io.OutputStream
import java.util.*

class ControllerActivity : AppCompatActivity() {
    private val uuid: String = "00001101-0000-1000-8000-00805F9B34FB"
    private val upCommand: Array<String> = arrayOf("tiến lên", "lên")
    private val downCommand: Array<String> = arrayOf("lùi xuống", "xuống")
    private val leftCommand: Array<String> = arrayOf("rẽ trái", "trái")
    private val rightCommand: Array<String> = arrayOf("rẽ phải", "phải")
    private val stopCommand: Array<String> = arrayOf("dừng lại", "dừng")
    private val autoCommand: Array<String> = arrayOf("tự động", "auto")
    private lateinit var socket: BluetoothSocket
    private lateinit var outputStream: OutputStream
    private lateinit var inputStream: InputStream

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_controller)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val upButton: Button = findViewById(R.id.up_button)
        val downButton: Button = findViewById(R.id.down_button)
        val leftButton: Button = findViewById(R.id.left_button)
        val rightButton: Button = findViewById(R.id.right_button)
        val autoButton: Button = findViewById(R.id.auto_button)
        val stopButton: Button = findViewById(R.id.stop_button)
        val voiceButton: ImageButton = findViewById(R.id.voice_button)


        upButton.setOnClickListener {
            try {
                outputStream.write("2".toByteArray())
            } catch (e: Exception) {
                toastError()
                Log.d("Bluetooth", "$e")
            }
        }

        downButton.setOnClickListener {
            try {
                outputStream.write("5".toByteArray())
            } catch (e: Exception) {
                toastError()
                Log.d("Bluetooth", "$e")
            }
        }

        leftButton.setOnClickListener {
            try {
                outputStream.write("3".toByteArray())
            } catch (e: Exception) {
                toastError()
                Log.d("Bluetooth", "$e")
            }
        }

        rightButton.setOnClickListener {
            try {
                outputStream.write("4".toByteArray())
            } catch (e: Exception) {
                toastError()
                Log.d("Bluetooth", "$e")
            }
        }

        autoButton.setOnClickListener {
            try {
                outputStream.write("1".toByteArray())
            } catch (e: Exception) {
                toastError()
                Log.d("Bluetooth", "$e")
            }
        }

        stopButton.setOnClickListener {
            try {
                outputStream.write("0".toByteArray())
            } catch (e: Exception) {
                toastError()
                Log.d("Bluetooth", "$e")
            }
        }

        voiceButton.setOnClickListener {
            promptSpeechInput()
        }

        try {
            var device: BluetoothDevice =
                intent.getParcelableExtra<BluetoothDevice>("DEVICE") as BluetoothDevice
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.BLUETOOTH_CONNECT
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    return
                }
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                title = device.alias
            } else {
                title = device.name
            }
            socket = device.createRfcommSocketToServiceRecord(UUID.fromString(uuid))
            socket.connect()
            inputStream = socket.inputStream
            outputStream = socket.outputStream
        } catch (e: Exception) {
            toastError()
            Log.d("Bluetooth", "onCreate: $e")
        }
    }

    private fun toastError() {
        Toast.makeText(this, "Connection error!", Toast.LENGTH_SHORT).show()
    }


    private fun promptSpeechInput() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "vi-VN")
        intent.putExtra(
            RecognizerIntent.EXTRA_PROMPT,
            getString(R.string.speech_prompt)
        )
        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
        try {
            talkLauncher.launch(intent)
        } catch (a: ActivityNotFoundException) {
            Toast.makeText(
                applicationContext,
                getString(R.string.speech_not_supported),
                Toast.LENGTH_SHORT
            ).show()
        }
    }


    private var talkLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                try {
                    val resultArray =
                        result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                    textToCommand(resultArray!![0])
                } catch (e: Exception) {
                    Log.d("Exception", e.toString())
                }
            }
        }

    private fun textToCommand(inText: String) {
        val textView: TextView = findViewById(R.id.voice_text)
        textView.text = inText
        try {
            val text = inText.lowercase()
            if (text in upCommand) {
                textView.text = getString(R.string.command_success, textView.text, "UP")
                outputStream.write("2".toByteArray())
            } else if (text in downCommand) {
                textView.text = getString(R.string.command_success, textView.text, "DOWN")
                outputStream.write("5".toByteArray())
            } else if (text in leftCommand) {
                textView.text = getString(R.string.command_success, textView.text, "LEFT")
                outputStream.write("3".toByteArray())
            } else if (text in rightCommand) {
                textView.text = getString(R.string.command_success, textView.text, "RIGHT")
                outputStream.write("4".toByteArray())
            } else if (text in stopCommand) {
                textView.text = getString(R.string.command_success, textView.text, "STOP")
                outputStream.write("0".toByteArray())
            } else if (text in autoCommand) {
                textView.text = getString(R.string.command_success, textView.text, "AUTO")
                outputStream.write("1".toByteArray())
            }
        } catch (e: Exception) {
            toastError()
            Log.d("Bluetooth", "$e")
        }
    }
}