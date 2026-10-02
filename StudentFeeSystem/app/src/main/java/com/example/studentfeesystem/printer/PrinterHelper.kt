package com.example.studentfeesystem.printer

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import java.io.OutputStream
import java.util.UUID

/**
 * Handles connecting to a paired Bluetooth thermal (ESC/POS) printer
 * and sending a formatted fee receipt to it.
 *
 * Works with common 58mm / 80mm Bluetooth SPP thermal printers.
 */
object PrinterHelper {

    private val SPP_UUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")

    private var socket: BluetoothSocket? = null
    private var outputStream: OutputStream? = null

    /** Returns Bluetooth devices already paired in phone settings. */
    @SuppressLint("MissingPermission")
    fun getPairedDevices(): List<BluetoothDevice> {
        val adapter = BluetoothAdapter.getDefaultAdapter() ?: return emptyList()
        if (!adapter.isEnabled) return emptyList()
        return adapter.bondedDevices?.toList() ?: emptyList()
    }

    /** Blocking call - run on a background thread. Returns true if connected. */
    @SuppressLint("MissingPermission")
    fun connect(device: BluetoothDevice): Boolean {
        return try {
            disconnect()
            val sock = device.createRfcommSocketToServiceRecord(SPP_UUID)
            sock.connect()
            socket = sock
            outputStream = sock.outputStream
            true
        } catch (e: Exception) {
            e.printStackTrace()
            disconnect()
            false
        }
    }

    fun isConnected(): Boolean = socket?.isConnected == true

    /** Blocking call - run on a background thread. Returns true if the receipt was sent. */
    fun printReceipt(
        schoolName: String,
        studentName: String,
        rollNumber: String,
        studentClass: String,
        fatherName: String,
        month: String,
        amount: Double,
        status: String,
        date: String
    ): Boolean {
        val out = outputStream ?: return false
        return try {
            fun cmd(vararg bytes: Int) {
                out.write(bytes.map { it.toByte() }.toByteArray())
            }

            val esc = 0x1B
            val gs = 0x1D

            // Initialize printer
            cmd(esc, 0x40)

            // Center align + bold for header
            cmd(esc, 0x61, 1)
            cmd(esc, 0x45, 1)
            out.write("$schoolName\n".toByteArray())
            cmd(esc, 0x45, 0)
            out.write("Fee Receipt\n".toByteArray())
            out.write("--------------------------------\n".toByteArray())

            // Left align for details
            cmd(esc, 0x61, 0)
            out.write("Student : $studentName\n".toByteArray())
            out.write("Roll No : $rollNumber\n".toByteArray())
            out.write("Class   : $studentClass\n".toByteArray())
            out.write("Father  : $fatherName\n".toByteArray())
            out.write("Month   : $month\n".toByteArray())
            out.write("Amount  : Rs. $amount\n".toByteArray())
            out.write("Status  : $status\n".toByteArray())
            out.write("Date    : $date\n".toByteArray())
            out.write("--------------------------------\n".toByteArray())

            // Center align footer
            cmd(esc, 0x61, 1)
            out.write("Thank you!\n".toByteArray())

            // Feed paper then cut (ignored by printers without a cutter)
            out.write("\n\n\n".toByteArray())
            cmd(gs, 0x56, 1)

            out.flush()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun disconnect() {
        try {
            outputStream?.close()
            socket?.close()
        } catch (e: Exception) {
            // ignore
        }
        outputStream = null
        socket = null
    }
}
