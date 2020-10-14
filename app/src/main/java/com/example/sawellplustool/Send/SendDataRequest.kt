package com.example.bytearraylesson.Send


class SendDataRequest {
    //55 FE 03 FE 01 01 43 90
    val b1 = ByteArray(8)

    init {
        //55 FE 04 FF 02 01 01 05 90
        b1.set(0, 0x55)
        b1.set(1, 0xfe - 256)
        b1.set(2, 0x03)
        b1.set(3, 0xfe - 256)
        b1.set(4, 0x01)
        b1.set(5, 0x01)
        b1.set(6, 0x43)
        b1.set(7, 0x90 - 256)
    }

    fun getReadySendData(): ByteArray {
        return b1               // 把初值送回去
    }
}

