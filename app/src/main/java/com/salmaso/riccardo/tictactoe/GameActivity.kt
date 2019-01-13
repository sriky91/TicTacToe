package com.salmaso.riccardo.tictactoe

import android.content.Intent
import android.content.res.Resources
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.RelativeLayout
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_game.*
import android.widget.LinearLayout





class GameActivity : AppCompatActivity(), View.OnClickListener {
    /*

    0-1-2
    3-4-5
    6-7-8

    */
    var turn = 0
    var xFirst = false
    var playerX = 0
    var playerO = 0
    var gameFinished = false
    var arrayCamp = IntArray(9) { i -> 0 }
    var singlePlayer = true
    var widthDisplay : Float = 0f

    val arrayCheck : Array<Int> = arrayOf(
        20,2,10,0,1,11
    )

    val matrixSinglePlayer : Array<Array<Int>> = arrayOf(
            arrayOf(0,1,2),
            arrayOf(3,4,5),
            arrayOf(6,7,8),
            arrayOf(0,3,6),
            arrayOf(1,4,7),
            arrayOf(2,5,8),
            arrayOf(0,4,8),
            arrayOf(2,4,6)
    )

    var arrCheckSum = IntArray(8) { i -> 0 }

    var matrixCheck : Array<Array<Int>> =
            arrayOf(
                    arrayOf(0,3,6),
                    arrayOf(0,4),
                    arrayOf(0,5,7),
                    arrayOf(3,1),
                    arrayOf(6,4,7,1),
                    arrayOf(5,1),
                    arrayOf(3,7,2),
                    arrayOf(4,2),
                    arrayOf(5,2,6)
            )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        singlePlayer = intent.getBooleanExtra("singlePlayer",true)
        xFirst = intent.getBooleanExtra("xFirst",true)
        playerX = intent.getIntExtra("playerX",0)
        playerO = intent.getIntExtra("playerO",0)

        square1.setOnClickListener(this)
        square2.setOnClickListener(this)
        square3.setOnClickListener(this)
        square4.setOnClickListener(this)
        square5.setOnClickListener(this)
        square6.setOnClickListener(this)
        square7.setOnClickListener(this)
        square8.setOnClickListener(this)
        square9.setOnClickListener(this)


        widthDisplay = Resources.getSystem().getDisplayMetrics().widthPixels.toFloat()
        println("widthDisplay: " + widthDisplay)

        val layoutParams = llrow1.getLayoutParams() as LinearLayout.LayoutParams
        layoutParams.height = (widthDisplay / 3).toInt()
        layoutParams.width  = widthDisplay.toInt()
        llrow1.setLayoutParams(layoutParams)
        llrow2.setLayoutParams(layoutParams)
        llrow3.setLayoutParams(layoutParams)

        rl_camp.addView(CanvasDraw.DrawCamp1(this, rl_camp, widthDisplay))

        tvX.text = playerX.toString()
        tvO.text = playerO.toString()

        if(singlePlayer && !xFirst){
            addXorO(square1)
        }

    }

override fun onClick(v: View) {
when (v.id) {
    R.id.square1,
    R.id.square2,
    R.id.square3,
    R.id.square4,
    R.id.square5,
    R.id.square6,
    R.id.square7,
    R.id.square8,
    R.id.square9-> {
        addXorO(v)
    }
}

}

    private fun addXorO(v: View) {
        if (gameFinished) {
            val intent = Intent(this, GameActivity::class.java)

            intent.putExtra("singlePlayer",singlePlayer)
            intent.putExtra("xFirst",!xFirst)
            intent.putExtra("playerX",playerX)
            intent.putExtra("playerO",playerO)

            startActivity(intent)
            overridePendingTransition(0, 0)
            finish()
        } else {
            val index: Int = v.tag.toString().toInt()
            val xTurn= (turn % 2 == 0 && xFirst) || (turn % 2 != 0 && !xFirst)
            val startX: Float = ((widthDisplay/3) - 300) / 2f
            println("Float: " + startX)
            if (arrayCamp[index] == 0) {
                if (xTurn) {
                    arrayCamp[index] = 1
                    (v as RelativeLayout).addView(CanvasDraw.DrawX(this, v, startX, 0))
                } else {
                    arrayCamp[index] = 10
                    (v as RelativeLayout).addView(CanvasDraw.DrawCircle(this, startX))
                }
                turn++
                var i: Int = 0
                while (i < matrixCheck[index].size) {
                    val result = checkRow(matrixCheck[index][i]);
                    if (result == 3 || result == 30 ||
                            (i+1 == matrixCheck[index].size && turn == 9)) {
                        var mex = "Ha vinto player ";
                        if (result == 3) {
                            ++playerX
                            mex += "X"
                        } else if (result == 30) {
                            ++playerO
                            mex += "O"
                        } else if (turn == 9) {
                            mex = "Pareggio"
                        }
                        tvX.text = playerX.toString()
                        tvO.text = playerO.toString()
                        gameFinished = true
                        Toast.makeText(this, mex, Toast.LENGTH_SHORT).show()
                        break
                    }
                    i++
                }
                if(singlePlayer && xTurn && !gameFinished && turn < 9){
                    var i = 0
                    var result : Int = -1
                    var nCheck = 0
                    while(1==1) {
                        i=0
                        while (i < arrCheckSum.size) {
                            if(arrCheckSum[i] == arrayCheck[nCheck]) {
                                result = i
                            }
                            i++
                        }
                        if (result != -1) {
                            val array = matrixSinglePlayer[result]
                            for(index in 0..2){
                                if(arrayCamp[array[index]] == 0){
                                    val res = resources
                                    val id = res.getIdentifier("square"+(array[index]+1), "id", getPackageName())
                                    addXorO(findViewById<View>(id))
                                    break
                                }
                            }
                            return
                        }
                        nCheck++
                    }

                }
            }
        }
    }

    fun checkRow(n: Int): Int {
        val array = matrixSinglePlayer[n]
        arrCheckSum[n] = arrayCamp[array[0]] + arrayCamp[array[1]] + arrayCamp[array[2]]
        return arrCheckSum[n]
    }

}
