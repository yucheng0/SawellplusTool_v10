package com.example.bytearraylesson.Send
class SendConnectioStatus{
    val b1 = ByteArray(9)           //固定9
    init {
        //55 FE 04 FF 02 01 01 05 90
        b1.set(0,0x55)
        b1.set(1,0xfe-256)
        b1.set(2,0x04)
        b1.set(3,0xff-256)
        b1.set(4,0x02)
        b1.set(5,0x01)
        b1.set(6,0x01)
        b1.set(7,0x05)
        b1.set(8,0x90-256)

    }
    fun getReadySendData():ByteArray {
        return b1               // 把初值送回去
    }
}

