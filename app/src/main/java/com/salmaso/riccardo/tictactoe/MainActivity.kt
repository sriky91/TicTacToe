package com.salmaso.riccardo.tictactoe

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val x = 17;
        println("Ciao $x");
        val numList = 1..20
        val eventList = numList.filter{ it % 2 == 0}
        eventList.forEach { n -> println("Numero: " + n) }

        val multiply2 = {num1: Int -> num1 * 2}
        val numList2 = arrayOf(1,2,3,4,5)
        mathOnList(numList2, multiply2)

        btnSinglePlayer.setOnClickListener{
            val intent = Intent(this, GameActivity::class.java)
            intent.putExtra("singlePlayer",true)
            startActivity(intent)
        }

        btnMultiPlayer.setOnClickListener{
            val intent = Intent(this, GameActivity::class.java)
            intent.putExtra("singlePlayer",false)
            startActivity(intent)
        }

    }

    fun mathOnList(numList: Array<Int>, myFunc: (num: Int) -> Int){
        for (num in numList){
            println("Math ${myFunc(num)}")
        }
    }
}

