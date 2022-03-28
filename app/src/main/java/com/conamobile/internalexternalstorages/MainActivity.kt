package com.conamobile.internalexternalstorages

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.text.TextUtils
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import java.io.*
import java.nio.charset.Charset

//Internal storage - private
//External storage - public
lateinit var externalText: TextView

class MainActivity : AppCompatActivity() {
    var isPersistent = true
    lateinit var b_save_int: Button
    lateinit var b_read_int: Button
    private var readText: String = ""
    var readPermissionGranted = false
    var writePermissionGranted = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        b_save_int = findViewById(R.id.b_save_int)
        b_read_int = findViewById(R.id.b_read_int)
        externalText = findViewById(R.id.internalText)

        requestPermission()
//        checkStoragePaths()
        //       createInternalFile()

        b_save_int.setOnClickListener {
            saveInternalFile("Android Developer")
        }
//
        b_read_int.setOnClickListener {
            readInternalFile()
        }
    }

    private fun requestPermission() {
        val hasReadPermission = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
        val haswritePermission = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
        val minSdk29 = hasReadPermission
        writePermissionGranted = haswritePermission || minSdk29

        var permissionsToRequest = mutableListOf<String>()
        if (!readPermissionGranted)
            permissionsToRequest.add(Manifest.permission.READ_EXTERNAL_STORAGE)

        if (!writePermissionGranted)
            permissionsToRequest.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)

        if (permissionsToRequest.isNotEmpty())
            permissionLauncher.launch(permissionsToRequest.toTypedArray())
    }

    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permission ->
            readPermissionGranted =
                permission[Manifest.permission.READ_EXTERNAL_STORAGE] ?: readPermissionGranted
            writePermissionGranted =
                permission[Manifest.permission.WRITE_EXTERNAL_STORAGE] ?: writePermissionGranted

            if (readPermissionGranted) toast("READ_EXTERNAL_STORAGE")
            if (writePermissionGranted) toast("WRITE_EXTERNAL_STORAGE")
        }

    //ne nada
    private fun checkStoragePaths() {
        val internal_m1 = getDir("custom", 0)
        val internal_m2 = filesDir

        val external_m1 = getExternalFilesDir(null)
        val external_m2 = externalCacheDir
        val external_m3 = getExternalFilesDir(Environment.DIRECTORY_PICTURES)

        Log.d("StorageActivity", internal_m1.absolutePath)
        Log.d("StorageActivity", internal_m2.absolutePath)
        Log.d("StorageActivity", external_m1!!.absolutePath)
        Log.d("StorageActivity", external_m2!!.absolutePath)
        Log.d("StorageActivity", external_m3!!.absolutePath)
    }

    private fun createInternalFile() {
        val fileName = "pdp_internal2.txt"
        var file: File = if (isPersistent) {
            File(cacheDir, fileName)
        } else {
            File(cacheDir, fileName)
        }
        if (!file.exists()) {
            try {
                file.createNewFile()
                Toast.makeText(this, "File %s has been created $fileName", Toast.LENGTH_SHORT)
                    .show()
            } catch (e: IOException) {
                Toast.makeText(this, "File %s already exists $fileName", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "File %s already exists $fileName", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveInternalFile(data: String) {
        val fileName = "pdp_internal2.txt"
        try {
            val fileOutputStream: FileOutputStream = if (isPersistent) {
                openFileOutput(fileName, MODE_PRIVATE)
            } else {
                val file = File(cacheDir, fileName)
                FileOutputStream(file)
            }
            fileOutputStream.write(data.toByteArray(Charset.forName("UTF-8")))
            toast(String.format(("Write to %s successful"), fileName))
        } catch (e: Exception) {
            e.printStackTrace()
            toast(String.format(("Write to file %s failed"), fileName))
        }
    }

    private fun toast(text: String) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
    }

    private fun readInternalFile() {
        val fileName = "pdp_internal2.txt"
        try {
            val fileInputStream: FileInputStream = if (isPersistent) {
                openFileInput(fileName)
            } else {
                val file = File(cacheDir, fileName)
                FileInputStream(file)
            }
            var inputStreamReader = InputStreamReader(
                fileInputStream,
                Charset.forName("UTF-8")
            )
            val lines: MutableList<String?> = ArrayList()
            val reader = BufferedReader(inputStreamReader)
            var line = reader.readLine()
            while (line != null) {
                lines.add(line)
                line = reader.readLine()
            }
            readText = TextUtils.join("\n", lines)
            toast(String.format("Read from file %successful", fileName))
            externalText.text = readText
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            toast(String.format("Read from file %s failed $e", fileName))
        }
    }

}