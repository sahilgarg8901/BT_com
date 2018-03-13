package com.example.sahil.bt_app

import android.app.ProgressDialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.graphics.Color
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kotlinx.android.synthetic.main.activity_control.*
import yuku.ambilwarna.AmbilWarnaDialog
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList

class ControlActivity : AppCompatActivity() {

    companion object {
        var m_myUUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb")// fill the uuid of your bluetooth device
        var m_bluetoothSocket: BluetoothSocket? = null
        lateinit var m_progress: ProgressDialog
        lateinit var m_bluetoothAdapter: BluetoothAdapter
        var m_isConnected : Boolean = false
        lateinit var m_address :String
        var DefaultColor : Int = Color.WHITE
        var rgb: ArrayList<Int> = arrayListOf(255,255,255)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_control)

        m_address = intent.getStringExtra(MainActivity.EXTRA_ADDRESS)

        ConnectToDevice(this).execute()
        // sent msg format  : "status#r_value#g_value#b_value#"
        OnButton.setOnClickListener {
            sendCommand(ColorTOrgb(DefaultColor))
        }
        OffButton.setOnClickListener { sendCommand("0") }
        ColorButton.setOnClickListener {
            openColorPicker()
        }

        DisconnectButton.setOnClickListener{ disconnect() }

    }
    private fun sendCommand(input: String){
        if (m_bluetoothSocket!= null){
            try {
                Log.d("color", input)
                m_bluetoothSocket!!.outputStream.write(input.toByteArray())
            } catch (e: IOException){
                e.printStackTrace()
            }
        }
    }
    private fun disconnect() {
        if (m_bluetoothSocket != null) {
            try {
                m_bluetoothSocket!!.close()
                m_bluetoothSocket = null
                m_isConnected = false
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        finish()          // to close the current activity and come back to the previous one
    }
    private fun openColorPicker() {
        val ColorPicker = AmbilWarnaDialog(this,DefaultColor, object : AmbilWarnaDialog.OnAmbilWarnaListener{
            override fun onCancel(dialog: AmbilWarnaDialog){

            }
            override fun onOk(dialog: AmbilWarnaDialog, color: Int) {
                DefaultColor = color
                sendCommand(ColorTOrgb(DefaultColor))
                MainLayout.setBackgroundColor(DefaultColor)
            }
        })
        ColorPicker.show()
    }
    private fun ColorTOrgb(color : Int):String {
        rgb[0] = DefaultColor shr 16 and 0xff
        rgb[1] = DefaultColor shr 8 and 0xff
        rgb[2] = DefaultColor and 0xff
        return "1#${rgb[0]}#${rgb[1]}#${rgb[2]}#"

    }

   // the following is the subClass responsible for Connecting to the bluetooth device

    private class ConnectToDevice(c: Context): AsyncTask<Void, Void, String>(){
        private var connectSuccess: Boolean = true
        private val context: Context
        init {
            this.context= c
        }

        override fun onPreExecute() {
            super.onPreExecute()
            m_progress = ProgressDialog.show(context,"Connecting", "Please Wait")
        }

        override fun doInBackground(vararg params: Void?): String?   {
            try {
                if (m_bluetoothSocket!= null || !m_isConnected){
                    m_bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
                    val device : BluetoothDevice = m_bluetoothAdapter.getRemoteDevice(m_address)
                    m_bluetoothSocket =device.createInsecureRfcommSocketToServiceRecord(m_myUUID)
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery()
                    Log.d("hello-0","hello-0")
                    m_bluetoothSocket!!.connect()
                    Log.d("hello-1","hello-1")
                }
            } catch (e: IOException){
                connectSuccess = false
                e.printStackTrace()
                Log.d("hello-2",e.toString())
            }

            return null
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            if (!connectSuccess){
                Log.i("data", "could'nt connect")
            } else {
                m_isConnected = true
            }
            m_progress.dismiss()
        }
    }
}


