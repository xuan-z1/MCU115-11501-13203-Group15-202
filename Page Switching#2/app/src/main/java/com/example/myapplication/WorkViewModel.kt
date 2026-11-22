package com.example.myapplication

import android.os.Handler
import android.os.HandlerThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class WorkViewModel : ViewModel() {
    private val handlerThread = HandlerThread("VM-Work").apply { start() }
    private val worker = Handler(handlerThread.looper)

    private val _progress = MutableLiveData(0)
    val progress: LiveData<Int> = _progress

    private val _status = MutableLiveData("Idle")
    val status: LiveData<String> = _status

    @Volatile private var running = false

    fun start() {
        if (running) return
        running = true
        _status.postValue("Preparing…")
        _progress.postValue(0)
        worker.post {
            try {
                // 準備階段 (設為 2 秒，方便測試觀察並避免等待 10 秒過久沒聽到聲音)
                Thread.sleep(2000)
                if (!running) {
                    _status.postValue("Canceled")
                    return@post
                }
                _status.postValue("Working…")
                for (i in 1..100) {
                    if (!running) break
                    Thread.sleep(300) // 真正的背景工作放這裏
                    _progress.postValue(i)
                }
                _status.postValue(if (running) "背景工作結束！" else "Canceled")
                running = false
            } catch (_: InterruptedException) {
                _status.postValue("Canceled")
                running = false
            }
        }
    }

    fun cancel() { running = false }

    override fun onCleared() {
        running = false
        handlerThread.quitSafely()
        super.onCleared()
    }
}