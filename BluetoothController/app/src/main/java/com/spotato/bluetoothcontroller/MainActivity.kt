package com.spotato.bluetoothcontroller

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.ActionBar
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {

    private lateinit var btAdapter: BluetoothAdapter
    private var devices: Set<BluetoothDevice> = emptySet()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        createListView()
    }

    override fun onResume() {
        super.onResume()

        createListView()
    }

    private fun createListView() {
        val text: TextView = findViewById(R.id.textfield)

        val btManager = this.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager?

        if (btManager == null) {
            text.text = R.string.bluetooth_not_supported.toString()
            return
        }
        btAdapter = btManager.adapter

        if (btAdapter.state == BluetoothAdapter.STATE_OFF) {
            val intent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            bluetoothLauncher.launch(intent)
        }

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.BLUETOOTH_CONNECT
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                requestBluetoothPermission.launch(Manifest.permission.BLUETOOTH_CONNECT)
            }
        }

        devices = btAdapter.bondedDevices
        if (devices.isEmpty()) {
            text.visibility = View.VISIBLE
            text.text = getString(R.string.bluetooth_device_empty)
        } else {
            val recyclerVew: RecyclerView = findViewById(R.id.recycler_view)
            recyclerVew.layoutManager = LinearLayoutManager(this)
            recyclerVew.adapter = ViewAdapter(applicationContext, devices)
            text.visibility = View.INVISIBLE
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        var menuInflater: MenuInflater = menuInflater
        menuInflater.inflate(R.menu.menu_layout, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_reload -> createListView()
        }
        return super.onOptionsItemSelected(item)
    }

    private var bluetoothLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                finish()
                overridePendingTransition(0, 0)
                startActivity(this.intent)
                overridePendingTransition(0, 0)
            } else {
                val text: TextView = findViewById(R.id.textfield)
                text.text = getString(R.string.bluetooth_not_permitted)
            }
        }

    private val requestBluetoothPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { _ ->
            finish()
            overridePendingTransition(0, 0)
            startActivity(this.intent)
            overridePendingTransition(0, 0)
        }
}
