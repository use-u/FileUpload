package com.example.fileupload

import android.Manifest
import android.annotation.TargetApi
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import cn.bmob.v3.Bmob
import kotlinx.android.synthetic.main.activity_main.*
import cn.bmob.v3.BmobObject
import cn.bmob.v3.datatype.BmobFile
import cn.bmob.v3.exception.BmobException
import cn.bmob.v3.listener.SaveListener
import cn.bmob.v3.listener.UploadBatchListener
import java.io.File

class MainActivity : AppCompatActivity() {
    private var count = 0
    var total = 184

    @TargetApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val permissions = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_PHONE_STATE)
        requestPermissions(permissions, 1)

        Bmob.initialize(this, "35a21ee65f184b928506cc68bd28cfeb")

        button.setOnClickListener {
            count += 1
            if (count >= 3) {
                upload()
            }
        }
    }

    private fun upload() {
        //手机文件路径下picture的文件夹
        val dir = File("${Environment.getExternalStorageDirectory().path}/picture/")
        val filesPath = ArrayList<String>()
        for (file in dir.listFiles()) {
            filesPath.add(file.absolutePath)
        }
        BmobFile.uploadBatch(filesPath.toTypedArray(), object : UploadBatchListener {
            override fun onSuccess(files: MutableList<BmobFile>?, urls: MutableList<String>?) {
                if (urls?.size == total) {
                    runOnUiThread {
                        saveToDatabase(urls)
                    }
                }
            }

            override fun onProgress(curIndex: Int, curPercent: Int, total: Int, totalPercent: Int) {
                Log.e("xkf123456789", "current:$curIndex")
            }

            override fun onError(p0: Int, p1: String?) {

            }
        })

    }

    fun saveToDatabase(urls: MutableList<String>) {
        for (i in urls.indices) {
            val picture = Picture()
            picture.itemId = i
            picture.imageUrl = urls[i]
            picture.save(object : SaveListener<String>() {
                override fun done(objectId: String?, e: BmobException?) {
                    if (e == null) {
                        Log.e("xkf123456789", "Ok $i")
                    } else {
                        Log.e("xkf123456789", e.message)
                    }
                }
            })
        }
    }
}

class Picture : BmobObject() {
    var itemId = 0
    var imageUrl: String? = null
}