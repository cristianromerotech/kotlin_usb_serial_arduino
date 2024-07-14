package com.example.usbserialrepaired


import android.content.ContentValues
import android.hardware.usb.UsbDeviceConnection
import android.hardware.usb.UsbManager
import android.os.Bundle

import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.hoho.android.usbserial.driver.UsbSerialDriver
import com.hoho.android.usbserial.driver.UsbSerialPort
import com.hoho.android.usbserial.driver.UsbSerialProber
import com.hoho.android.usbserial.util.HexDump
import java.util.Arrays






class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val varTextView = findViewById<TextView>(R.id.inputText)
        val varButton = findViewById<Button>(R.id.button)
        // Find all available drivers from attached devices.
        // Find all available drivers from attached devices.
        val manager : UsbManager = getSystemService(USB_SERVICE) as UsbManager
        val availableDrivers : List<UsbSerialDriver> = UsbSerialProber.getDefaultProber().findAllDrivers(manager)
        if (availableDrivers.isEmpty()) {
            return
        }

        // Open a connection to the first available driver.

        // Open a connection to the first available driver.
        val driver : UsbSerialDriver = availableDrivers[0]
        val connection : UsbDeviceConnection = manager.openDevice(driver.device)
            ?: // add UsbManager.requestPermission(driver.getDevice(), ..) handling here
            return

        val port : UsbSerialPort = driver.getPorts().get(0) // Most devices have just one port (port 0)

        port.open(connection)
        port.setParameters(9600, 8, UsbSerialPort.STOPBITS_1, UsbSerialPort.PARITY_NONE)

        //val usbSerialPort = driver.getPorts().get(0);


          varButton.setOnClickListener {
             val dataText = varTextView.text.toString()
              try {
                  //val data: ByteArray = (dataText + '\n').toByteArray();
                  val data: ByteArray = (dataText).toByteArray();

                  port.write(data, 20)
                  var printVar = HexDump.dumpHexString(data)
                  Toast.makeText(this, "Sent: $printVar", Toast.LENGTH_SHORT).show()


                  val buffer = ByteArray(8192)
                  val len = port.read(buffer, 2000)
                  val array = Arrays.copyOf(buffer, len)
                  val stringReceived = HexDump.dumpHexString(array)
                  Log.i(ContentValues.TAG, "Read $len bytes: $stringReceived")
                  Toast.makeText(this, "Received: $stringReceived", Toast.LENGTH_SHORT).show()

              } catch (e: Exception) {
                  Log.e(ContentValues.TAG, "Error: $e")
              }
          }

    }
}