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
    var m_BluetoothAdapter: BluetoothAdapter? = null
    lateinit var m_PairedDevices: Set<BluetoothDevice>
    val REQUEST_ENABLE_BLUETOOTH = 5 // it can be any number greater than 0

    companion object {
        val EXTRA_ADDRESS : String = "Device_address"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        m_BluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        if (m_BluetoothAdapter == null){
            toast("This device doesn't support Bluetooth.")
            return
        }
        if (!m_BluetoothAdapter!!.isEnabled){
            val enableKBluetoothIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableKBluetoothIntent,REQUEST_ENABLE_BLUETOOTH)
        }
        SelectDeviceRefresh.setOnClickListener { PairedDeviceList() }
    }
    private fun PairedDeviceList(){
        m_PairedDevices = m_BluetoothAdapter!!.bondedDevices
        val DeviceList : ArrayList<BluetoothDevice> = ArrayList()
        if (m_PairedDevices.isEmpty()){
            for (device : BluetoothDevice in m_PairedDevices){
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
        if (requestCode == REQUEST_ENABLE_BLUETOOTH){
            if (resultCode == Activity.RESULT_OK){
                if (m_BluetoothAdapter!!.isEnabled){
                    toast("bluetooth has been enabled")
                } else {
                    toast("bluetooth has been disabled")
                }
            } else if (resultCode == Activity.RESULT_CANCELED){
                toast("bluetooth enabling has been canceled.")
            }
        }
    }
}
