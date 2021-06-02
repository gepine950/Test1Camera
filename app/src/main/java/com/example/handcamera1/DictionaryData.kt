package com.example.handcamera1

object DictionaryData {
    private val imageList = intArrayOf(
        R.drawable.dc_0,
        R.drawable.dc_1,
        R.drawable.dc_2,
        R.drawable.dc_3,
        R.drawable.dc_4,
        R.drawable.dc_5,
        R.drawable.dc_6,
        R.drawable.dc_7,
        R.drawable.dc_8,
        R.drawable.dc_9,
        R.drawable.dc_a,
        R.drawable.dc_b,
        R.drawable.dc_c,
        R.drawable.dc_d,
        R.drawable.dc_e,
        R.drawable.dc_f,
        R.drawable.dc_g,
        R.drawable.dc_h,
        R.drawable.dc_i,
        R.drawable.dc_j,
        R.drawable.dc_k,
        R.drawable.dc_l,
        R.drawable.dc_m,
        R.drawable.dc_n,
        R.drawable.dc_o,
        R.drawable.dc_p,
        R.drawable.dc_q,
        R.drawable.dc_r,
        R.drawable.dc_s,
        R.drawable.dc_t,
        R.drawable.dc_u,
        R.drawable.dc_v,
        R.drawable.dc_w,
        R.drawable.dc_x,
        R.drawable.dc_y,
        R.drawable.dc_z
    )

    private val alphanumList = arrayOf(
        "0",
        "1",
        "2",
        "3",
        "4",
        "5",
        "6",
        "7",
        "8",
        "9",
        "A",
        "B",
        "C",
        "D",
        "E",
        "F",
        "G",
        "H",
        "I",
        "J",
        "K",
        "L",
        "M",
        "N",
        "O",
        "P",
        "Q",
        "R",
        "S",
        "T",
        "U",
        "V",
        "W",
        "X",
        "Y",
        "Z"
    )

    val listData: ArrayList<Dictionary>
        get() {
            val list = arrayListOf<Dictionary>()
            for (position in alphanumList.indices ){
                val dct = Dictionary()
                dct.alphaNumeric = alphanumList[position]
                dct.photo = imageList[position]
                list.add(dct)
            }
            return list
        }
}