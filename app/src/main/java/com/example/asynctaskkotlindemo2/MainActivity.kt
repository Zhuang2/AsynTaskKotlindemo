package com.example.asynctaskkotlindemo2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import com.example.asynctaskkotlindemo2.databinding.ActivityMainBinding
import kotlinx.coroutines.*
import java.lang.Exception

class MainActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityMainBinding
    private lateinit var job: Job

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        mBinding.buttonStart.setOnClickListener {
            if (mBinding.buttonStart.text == "暂停加载") {
                mBinding.buttonStart.text = "继续加载"
                job.cancel()
            } else {
                mBinding.buttonStart.text = "暂停加载"
                job = Job()
                doOnStart()
            }
            mBinding.buttonCancel.isEnabled = true
        }

        mBinding.buttonCancel.setOnClickListener {
            mBinding.buttonStart.isEnabled = true
            mBinding.buttonCancel.isEnabled = false
            job.cancel()
            mBinding.textView.text = "已取消加载"
            mBinding.progressBar.progress = 0
            mBinding.buttonStart.text = "开始加载"
        }
    }

    private fun doOnStart() {
        CoroutineScope(job).launch {
            withContext(Dispatchers.IO) {
                try {
                    val i = mBinding.progressBar.progress
                    var count = if (i == 100) 0 else i
                    val length = 1
                    while (count < 100) {
                        count += length
                        doOnUiCode(count)
                        //模拟耗时任务，使相关协程休眠，进入阻塞状态（暂停执行50毫秒）
                        delay(50)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    private suspend fun doOnUiCode(value: Int) {
        withContext(Dispatchers.Main) {
            mBinding.progressBar.progress = value

            //更新进度文字
            if (value != 100) {
                mBinding.textView.text = "加载中···${value}%"
            } else {
                mBinding.textView.text = "加载完成"
                //使相关按钮不可用
                mBinding.buttonStart.isEnabled = true
                mBinding.buttonStart.text = "重新加载"
                mBinding.buttonCancel.isEnabled = false
            }
        }
    }
}