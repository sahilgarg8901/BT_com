package com.example.sahil.bt_app

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.AdapterView
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.toast

class MainActivity : AppCompatActivity() {
    private var mBluetoothAdapter: BluetoothAdapter? = null
    private lateinit var mPairedDevices: Set<BluetoothDevice>
    private val requestEnableBluetooth = 5 // it can be any nu /*val AdapTer = ArrayAdapter(this,android.R.layout.simple_list_item_1,DeviceList)*/mber greater than 0

    companion object {
        const val EXTRA_ADDRESS : String = "Device_address"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        if (mBluetoothAdapter == null){
            toast("This device doesn't support Bluetooth.")
            return
        }
        if (!mBluetoothAdapter!!.isEnabled){
            val enableKBluetoothIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableKBluetoothIntent, requestEnableBluetooth)
        }
        SelectDeviceRefresh.setOnClickListener { PairedDeviceList() }
    }
    private fun PairedDeviceList(){
        mPairedDevices = mBluetoothAdapter!!.bondedDevices
        val DeviceList : ArrayList<BluetoothDevice> = ArrayList()
        if (!mPairedDevices.isEmpty()){
            for (device : BluetoothDevice in mPairedDevices){
                DeviceList.add(device)
                Log.i("device",""+device)
            }
        } else {
            toast("No paired devices found")
        }
        val AdapTer = ArrayAdapter(this,android.R.layout.simple_list_item_1,DeviceList)
        selectDeviceList.adapter = AdapTer
        selectDeviceList.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            val deVice : BluetoothDevice = DeviceList[position]
            val address : String = deVice.address
            val ControlActivityIntent = Intent(this,ControlActivity::class.java)
            ControlActivityIntent.putExtra(EXTRA_ADDRESS,address)
            startActivity(ControlActivityIntent)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
            super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == requestEnableBluetooth){
            if (resultCode == Activity.RESULT_OK){
                if (mBluetoothAdapter!!.isEnabled){
                    toast("bluetooth has been enabled")
                } else {
                    toast("bluetooth has been disabled")
                }
            } else   /* if (resultCode == Activity.RESULT_CANCELED) */   {
                toast("bluetooth enabling has been canceled.")
            }
        }
    }
}
