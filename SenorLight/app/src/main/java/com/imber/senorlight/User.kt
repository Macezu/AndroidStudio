package com.imber.senorlight

import android.R.attr.port
import android.util.Log
import android.widget.Toast
import java.io.IOException
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.net.SocketException


class ClientSend : Runnable {
    override fun run() {
        try {
            val udpSocket = DatagramSocket(5000)
            val serverAddr: InetAddress = InetAddress.getByName("IPADRESSHERE")
            val buf = "TEST MESSAGE".toByteArray()
            val packet = DatagramPacket(buf, buf.size, serverAddr, 5000)
            udpSocket.send(packet)
            println("MSG SENT!")
        } catch (e: SocketException) {
            Log.e("Udp:", "Socket Error:", e)
        } catch (e: IOException) {
            Log.e("Udp Send:", "IO Error:", e)
        }
    }
}
