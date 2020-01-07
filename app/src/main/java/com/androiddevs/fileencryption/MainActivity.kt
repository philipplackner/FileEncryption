package com.androiddevs.fileencryption

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.security.crypto.EncryptedFile
import androidx.security.crypto.MasterKeys
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.lang.Exception

const val STORAGE_PERMISSION_REQUEST_CODE = 0

class MainActivity : AppCompatActivity() {

    var permissionGranted = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        requestPermission()

        btnReadFromFile.setOnClickListener {
            readFromFile("storage/emulated/0/myFile.txt")
        }

        btnWriteToFile.setOnClickListener {
            writeToFile("storage/emulated/0/myFile.txt")
        }
    }

    private fun requestPermission() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                STORAGE_PERMISSION_REQUEST_CODE
            )
        } else {
            permissionGranted = true
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == STORAGE_PERMISSION_REQUEST_CODE && grantResults.isNotEmpty()) {
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                permissionGranted = true
            }
        }
    }

    private fun writeToFile(filename: String) {
        try {
            getEncryptedFile(filename).openFileOutput().bufferedWriter().use {
                val fileContent = etFileContent.text.toString()
                it.write(fileContent)
            }
        } catch(e: Exception) {
            Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun readFromFile(filename: String) {
        try {
            getEncryptedFile(filename).openFileInput().bufferedReader().useLines { lines ->
                val allLines = lines.fold("") { working, line ->
                    "$line\n"
                }
                etFileContent.setText(allLines)
            }
        } catch(e: Exception) {
            Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun getEncryptedFile(filename: String): EncryptedFile {
        val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)

        return EncryptedFile.Builder(
            File(filename),
            applicationContext,
            masterKeyAlias,
            EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
        ).build()
    }
}
