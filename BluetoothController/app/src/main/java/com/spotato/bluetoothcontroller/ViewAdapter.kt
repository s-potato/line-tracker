package com.spotato.bluetoothcontroller

import android.Manifest
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.RecyclerView

class ViewAdapter(private var context: Context, private var devices: Set<BluetoothDevice>) :
    RecyclerView.Adapter<ViewAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {     // create item
        var view: View = LayoutInflater.from(context).inflate(R.layout.item_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {          // bind item with data
        val selectedDv = devices.elementAt(position)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.BLUETOOTH_CONNECT
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
            holder.textView.text = selectedDv.alias
        } else {
            holder.textView.text = selectedDv.name
        }
        holder.imageView.setImageResource(R.drawable.ic_bluetooth)

        holder.itemView.setOnClickListener{         // set onClick: open controller activity for selected device
            val intent = Intent(context, ControllerActivity::class.java)
            intent.putExtra("DEVICE", selectedDv)           // put selected device to intent
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return devices.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {        // 1 item class
        var textView: TextView
        var imageView: ImageView

        init {
            textView = itemView.findViewById(R.id.device_name_view)
            imageView = itemView.findViewById(R.id.icon_view)
        }
    }
}