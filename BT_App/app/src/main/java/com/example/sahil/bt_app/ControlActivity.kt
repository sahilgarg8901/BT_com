package com.example.sahil.bt_app

import android.app.ProgressDialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kotlinx.android.synthetic.main.activity_control.*
import java.io.IOException
import java.util.*

class ControlActivity : AppCompatActivity() {

    companion object {
        var m_myUUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb")// fill the uuid of your bluetooth device
        var m_bluetoothSocket: BluetoothSocket? = null
        lateinit var m_progress: ProgressDialog
        lateinit var m_bluetoothAdapter: BluetoothAdapter
        var m_isConnected: Boolean = false
        lateinit var m_address: String
        var LightStatus = 0
        var BT_response: String = ""
        var BT_byte_array: ByteArray = ByteArray(1024)
        lateinit var sendClassInstance: ThreadedWrite
        lateinit var ReadThread : ConnectedThread
        lateinit var EsteblishConnection : MakeConnection
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_control)

        m_address = intent.getStringExtra(MainActivity.EXTRA_ADDRESS)

        // ConnectToDevice(this).execute()


        PowerButton.setOnClickListener {
            if (LightStatus == 0) {
                sendCommand("ON")
                LightStatus = 1
            } else {
                sendCommand("OFF")
                LightStatus = 0
            }
        }
        DisconnectButton.setOnClickListener { disconnect() }

        EsteblishConnection = MakeConnection(this)

        /* while (true){

            if (m_bluetoothSocket!= null){
                try {*//*
                    if (m_bluetoothSocket!!.inputStream.available() != 0)*//*
                    m_bluetoothSocket!!.inputStream.read(BT_byte_array)
                    BT_response= BT_byte_array.toString()
                } catch (e: IOException){
                    e.printStackTrace()
                }
            }
        }*/

    }

    private fun sendCommand(input: String) {
        if (m_bluetoothSocket != null) {
            /*try {
                m_bluetoothSocket!!.outputStream.write(input.toByteArray())
                m_bluetoothSocket!!.inputStream.read(BT_byte_array)
                BT_response= BT_byte_array.toString()
            } catch (e: IOException){
                e.printStackTrace()
            }*/
            Log.d("Write", "writing command stage-1")
            sendClassInstance = ThreadedWrite(input)
            var SendRunnableThread = Thread(sendClassInstance)
            SendRunnableThread.start()
        }

    }

    private fun disconnect() {
        if (m_bluetoothSocket != null) {
            try {
                if (ReadThread.isAlive){
                    try {
                        ReadThread.destroy()
                    } catch (e : Exception){
                        Log.d("error", "while destroying the ReadThread=${e.toString()}")
                    }
                    Log.d("stop", "reading thread stopped")
                }
                m_bluetoothSocket!!.close()
                m_bluetoothSocket = null
                m_isConnected = false
                Log.d("stop", "dismissed the connection")

            } catch (e: IOException) {
                e.printStackTrace()
                Log.i("Cancelling Error", e.toString())
            }
        }
        finish()          // to close the current activity and come back to the previous one
    }

    // the following is the subClass responsible for Connecting to the bluetooth device

    /* private class ConnectToDevice(c: Context): AsyncTask<Void, Void, String>(){
        private var connectSuccess: Boolean = true
        private val context: Context
        init {
            this.context= c
        }

        override fun onPreExecute() {
            super.onPreExecute()
            m_progress = ProgressDialog.show(context,"connecting", "please wait")
        }

        override fun doInBackground(vararg params: Void?): String?   {
            try {
                if (m_bluetoothSocket!= null || !m_isConnected){
                    m_bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
                    val device : BluetoothDevice = m_bluetoothAdapter.getRemoteDevice(m_address)
                    m_bluetoothSocket =device.createInsecureRfcommSocketToServiceRecord(m_myUUID)
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery()
                    Log.d("hello-0","hello-0")
                    try {
                        m_bluetoothSocket!!.connect()
                    } catch (e : IOException){
                        Log.i("Socket","Could'nt connect Exception : ${e.toString()}")
                    }

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
    }*/


    /*inner class MakeConnection(address: String) : Thread() {
            private var mmDevice : BluetoothDevice
            private var mm_address : String
            var mm_bluetoothAdapter: BluetoothAdapter
            private var mmSocket : BluetoothSocket? = null
            private val MY_UUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb")// fill the uuid of your bluetooth device

            init {
                var tmp : BluetoothSocket? = null
                mm_address = address
                mm_bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
                mmDevice = mm_bluetoothAdapter.getRemoteDevice(mm_address)
                try {
                    tmp = mmDevice.createInsecureRfcommSocketToServiceRecord(MY_UUID)
                } catch (e: IOException){
                    Log.i("Socket",e.toString())
                }
                mmSocket = tmp
            }
            override fun run() {
                BluetoothAdapter.getDefaultAdapter().cancelDiscovery()
                try {
                    mmSocket!!.connect()
                } catch (e: IOException){
                    try {
                        mmSocket!!.close()
                    }catch (ex : IOException){
                        Log.i("Socket","Could'nt connect Exception : ${e.toString()}")
                    }
                    return
                }
            }
        }*/


    inner class MakeConnection(c: Context) : Thread() {
        private var connectSuccess: Boolean = true
        private val context: Context

        init {
            this.context = c
            m_progress = ProgressDialog.show(context, "connecting", "please wait")
            start()
        }

        override fun run() {
            try {
                if (m_bluetoothSocket != null || !m_isConnected) {
                    m_bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
                    val device: BluetoothDevice = m_bluetoothAdapter.getRemoteDevice(m_address)
                    m_bluetoothSocket = device.createInsecureRfcommSocketToServiceRecord(m_myUUID)
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery()
                    Log.d("hello", "establishing bluetooth connection")
                    try {
                        m_bluetoothSocket!!.connect()
                    } catch (e: IOException) {
                        Log.i("Socket", "Could'nt connect Exception : ${e.toString()}")
                    }

                    Log.d("hello", "bluetooth connection established")
                }
            } catch (e: IOException) {
                connectSuccess = false
                e.printStackTrace()
                Log.d("hello-2", e.toString())
            }
            if (!connectSuccess) {
                Log.i("data", "could'nt connect")
            } else {
                m_isConnected = true
            }
            m_progress.dismiss()
            ReadThread = ConnectedThread()
        }
    }

    inner class ConnectedThread() : Thread() {
        var mm_bluetoothSocket: BluetoothSocket? = null

        init {
            if (m_isConnected) {
                mm_bluetoothSocket = m_bluetoothSocket
                start()
            }
        }

        override fun run() {
            while (true) {
                if (mm_bluetoothSocket != null) {
                    try {
                        if (mm_bluetoothSocket!!.inputStream.available() != 0){
                            Log.d("read", "2-found something to read")
                            mm_bluetoothSocket!!.inputStream.read(BT_byte_array)
                            BT_response = BT_byte_array.toString()
                            Log.d("Received Message", BT_response)
                        }
                        /*if (!BT_byte_array.isEmpty()){

                        }*/
                    } catch (e: IOException) {
                        e.printStackTrace()
                        Log.i("Reading Error", e.toString())
                    }
                }
            }
        }
    }
    inner class ThreadedWrite(msg: String) : Runnable {
        private var Msg: String? = ""
        val sendThread: Thread = Thread()

        init {
            this.Msg = msg
            /*val sendThread : Thread = Thread("sendCommandThread")*/
            this.sendThread.name = "sendCommandThread"

            Log.d("Write", "writing command stage-2")

        }

        override fun run() {
            Log.d("Write", "writing command stage-3")
            if (m_isConnected && m_bluetoothSocket != null) {
                try {
                    m_bluetoothSocket!!.outputStream.write(this.Msg!!.toByteArray())
                } catch (e: IOException) {
                    e.printStackTrace()
                    Log.d("Writing Error-2", e.toString())
                }
            }
        }
    }
}


