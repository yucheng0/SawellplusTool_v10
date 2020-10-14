package com.example.bytearraylesson.Send

class SendResetHasClientCsButFix {

    val b1 = ByteArray(8)

    init {
        //55 FE 03 FC 01 00 FE 90 (Client send reset request)
        b1.set(0, 0x55)
        b1.set(1, 0xfe - 256)
        b1.set(2, 0x03)
        b1.set(3, 0xfc - 256)
        b1.set(4, 0x01)
        b1.set(5, 0x00)
        b1.set(6, 0xfe - 256)
        b1.set(8, 0x90 - 256)
    }

    fun send(): ByteArray {
        return b1               // 把初值送回去
    }
}

