package com.example.bytearraylesson.Send

import android.util.Log

var TAG = "myTag"
open class SendKeepAliveWithCounter  {

    val b1 = ByteArray(9)
    init {
        //55 FE 04 FD 02 01 00 02 90 (Client send keep alive No.1)
        b1.set(0, 0x55)
        b1.set(1, 0xfe - 256)
        b1.set(2, 0x04)
        b1.set(3, 0xfd - 256)
        b1.set(4, 0x02)
        b1.set(5, 0x01)             // 固定01
        b1.set(6, 0x00)             //需要計算  （用靜態值）
        b1.set(7,0x02)              // 需要計算
        b1.set(8, 0x90 - 256)
    }

companion object {
    var counter = 0
}

    fun send(): ByteArray {
        b1.set(6,counter.toByte())
        //計算cs
        var r = 0
        for (i in 1..6)
        {
            if (b1[i] <0 ) {r = r+ 256+b1[i] } else {r=r+b1[i]}
        }

        //取16進制最後2byte值
        var z = r.toString(16)
        var cs = z.subSequence(z.length - 2, z.length).toString().toInt(16)
        Log.d(TAG, "cs: $cs ")
        b1.set(7,cs.toByte())
        counter++               // 下一次的值
        counter = counter % 256
        Log.d(TAG, "send counter : $counter ")
        return b1               // 把初值送回去
    }
}

