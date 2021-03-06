package com.salmaso.riccardo.tictactoe

import android.content.Intent
import android.content.res.Resources
import android.graphics.Typeface
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.support.v4.content.ContextCompat
import android.widget.RelativeLayout
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_game.*
import java.util.*
import android.view.*
import android.widget.TextView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds


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
    var draw    = 0
    var gameFinished = false
    var arrayCamp = IntArray(9) { i -> 0 }
    var singlePlayer = true
    var widthDisplay : Float = 0f
    var difficultRandom = false
    val TEST = false

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
                    arrayOf(1,3),
                    arrayOf(1,4,6,7),
                    arrayOf(1,5),
                    arrayOf(2,3,7),
                    arrayOf(2,4),
                    arrayOf(2,5,6)
            )

    var difficult: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        singlePlayer = intent.getBooleanExtra("singlePlayer",true)
        xFirst = intent.getBooleanExtra("xFirst",true)
        playerX = intent.getIntExtra("playerX",0)
        playerO = intent.getIntExtra("playerO",0)
        draw    = intent.getIntExtra("draw",0)
        difficult = intent.getIntExtra("difficult", 0)

        if(difficult == -1){
            difficultRandom = true
            difficult = Random().nextInt(3)
        }

        if (TEST) Toast.makeText(this, "Difficult: " + difficult, Toast.LENGTH_SHORT).show()

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

        val layoutParams = llrow1.getLayoutParams()
        layoutParams.height = (widthDisplay / 3).toInt()
        layoutParams.width  = widthDisplay.toInt()
        llrow1.setLayoutParams(layoutParams)
        llrow2.setLayoutParams(layoutParams)
        llrow3.setLayoutParams(layoutParams)

        val layoutParamsRelativeLayout = rl_camp.getLayoutParams() as RelativeLayout.LayoutParams
        layoutParamsRelativeLayout.height = layoutParams.height * 3
        rl_camp.setLayoutParams(layoutParamsRelativeLayout)

        rl_camp.addView(CanvasDraw.DrawCamp1(this, rl_camp, widthDisplay))

        tvX.text = if (playerX == 0) "-" else playerX.toString()
        tvO.text = if (playerO == 0) "-" else playerO.toString()
        tvDraw.text = if (draw == 0) "-" else draw.toString()

        if (!xFirst) {
            rl_score_x.setBackgroundResource(R.color.background)
            rl_score_o.setBackgroundResource(R.drawable.my_border_o)
        } else {
            rl_score_o.setBackgroundResource(R.color.background)
            rl_score_x.setBackgroundResource(R.drawable.my_border_x)
        }

        if(singlePlayer && !xFirst){
            addXorO(square1)
        }

        MobileAds.initialize(this, getString(R.string.admob_code))
        val adRequest = AdRequest.Builder()
                //.addTestDevice("B137E9C7BDF0494B7F97E6B2227DC7D1")
                .build()
        adView.loadAd(adRequest)


    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if(item != null && item.itemId == R.id.action_restart_game){
            restartGame()
        }
        return super.onOptionsItemSelected(item)
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
            restartGame()
        } else {
            val index: Int = v.tag.toString().toInt()
            val xTurn= (turn % 2 == 0 && xFirst) || (turn % 2 != 0 && !xFirst)
            val size = (widthDisplay/3)/1.6f
            val startX: Float = ((widthDisplay/3) - size) / 2f
            println("Float: " + startX)
            if (arrayCamp[index] == 0) {
                if (xTurn) {
                    arrayCamp[index] = 1
                    (v as RelativeLayout).addView(CanvasDraw.DrawX(this, v, startX, 0, size))
                } else {
                    arrayCamp[index] = 10
                    (v as RelativeLayout).addView(CanvasDraw.DrawCircle(this, startX, size/0.77f))
                }
                turn++
                var i: Int = 0
                while (i < matrixCheck[index].size) {
                    val result = checkRow(matrixCheck[index][i]);
                    if (result == 3 || result == 30 ||
                            (i+1 == matrixCheck[index].size && turn == 9)) {
                        var mex: String = "";
                        var color = -1
                        var colorDark = -1
                        if (result == 3) {
                            ++playerX
                            color = ContextCompat.getColor(this, R.color.orange)
                            colorDark = ContextCompat.getColor(this, R.color.orangeDark)
                            mex = resources.getString(R.string.win_message,
                                    "X")
                        } else if (result == 30) {
                            color = ContextCompat.getColor(this, R.color.greenLight)
                            colorDark = ContextCompat.getColor(this, R.color.greenDark)
                            ++playerO
                            mex = resources.getString(R.string.win_message,
                                    "O")
                        } else if (turn == 9) {
                            ++draw
                            mex = "Draw"
                        }
                        if(color != -1){
                            val array = matrixSinglePlayer[matrixCheck[index][i]]
                            for (i in 0..2){
                                val id = resources.getIdentifier("square" + (array[i]+1), "id", getPackageName())
                                val layout:RelativeLayout = (findViewById<View>(id) as RelativeLayout)
                                layout.setBackgroundColor(colorDark)
                                if(i == 1){
                                    val tv_dynamic: TextView = TextView(this)
                                    tv_dynamic.textSize = 20f
                                    tv_dynamic.text = "Winner"
                                    tv_dynamic.background = resources.getDrawable(R.drawable.tv_end_game)//getDrawable(R.drawable.tv_end_game)
                                    tv_dynamic.setTypeface(Typeface.DEFAULT_BOLD)
                                    tv_dynamic.setTextColor(color)
                                    tv_dynamic.textSize = 27f
                                    // add TextView to LinearLayout


                                    Handler().postDelayed({
                                        layout.addView(tv_dynamic)
                                        val lp = tv_dynamic.getLayoutParams() as RelativeLayout.LayoutParams
                                        lp.addRule(RelativeLayout.CENTER_IN_PARENT)
                                    }, 500)
                                }
                            }
                            tvEndGame.setTextColor(color)
                        }
                        else{
                            Handler().postDelayed({
                                tvEndGame.visibility = View.VISIBLE;
                                tvEndGame.bringToFront()
                            }, 500)
                        }
                        tvX.text = if (playerX == 0) "-" else playerX.toString()
                        tvO.text = if (playerO == 0) "-" else playerO.toString()
                        tvDraw.text = if (draw == 0) "-" else draw.toString()


                        gameFinished = true


                        if (TEST) Toast.makeText(this, mex, Toast.LENGTH_SHORT).show()
                        break
                    }
                    i++
                }
                if(!gameFinished) {
                    if (xTurn) {
                        rl_score_x.setBackgroundResource(R.color.background)
                        rl_score_o.setBackgroundResource(R.drawable.my_border_o)
                    } else {
                        rl_score_o.setBackgroundResource(R.color.background)
                        rl_score_x.setBackgroundResource(R.drawable.my_border_x)
                    }
                    if (singlePlayer && xTurn && turn < 9) {
                        if(difficult == 2 && turn == 1 &&
                                arrayCamp[4] == 0){
                            val id = resources.getIdentifier("square" + 5, "id", getPackageName())
                            addXorO(findViewById<View>(id))
                        } else {
                            var i = 0
                            var result: Int = -1
                            var nCheck = 0
                            val arrayChecksOrder: Array<Int>
                            if (difficult > 0) {
                                arrayChecksOrder = arrayOf(0, 2, 1)
                            } else {
                                arrayChecksOrder = arrayOf(0, 1, 2)
                            }
                            while (1 == 1) {
                                i = 0
                                while (i < arrCheckSum.size) {
                                    if (arrCheckSum[i] == arrayCheck[nCheck]) {
                                        result = i
                                        if(difficult == 2) break
                                    }
                                    i++
                                }
                                if (result != -1) {
                                    val array = matrixSinglePlayer[result]
                                    for (index in 0..2) {
                                        if (arrayCamp[array[arrayChecksOrder[index]]] == 0) {
                                            var pos = array[arrayChecksOrder[index]];
                                            if(difficult == 2 && turn==3){
                                                if(arrayCamp[5] + arrayCamp[7] == 2) {
                                                    pos = 8
                                                } else if(arrayCamp[2] + arrayCamp[3] == 2 ||
                                                        arrayCamp[1] + arrayCamp[6] == 2) {
                                                    pos = 0
                                                } else if(arrayCamp[0] + arrayCamp[5] == 2){
                                                    pos = 2
                                                }
                                            }
                                            val res = resources
                                            val id = res.getIdentifier(
                                                    "square" + (pos + 1),
                                                    "id",
                                                    getPackageName())
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
        }
    }

    private fun restartGame() {
        val intent = Intent(this, GameActivity::class.java)

        intent.putExtra("singlePlayer", singlePlayer)
        intent.putExtra("xFirst", !xFirst)
        intent.putExtra("playerX", playerX)
        intent.putExtra("playerO", playerO)
        intent.putExtra("draw", draw)
        if (difficultRandom) intent.putExtra("difficult", -1)
        else intent.putExtra("difficult", difficult)

        startActivity(intent)
        overridePendingTransition(0, 0)
        finish()
    }

    fun checkRow(n: Int): Int {
        val array = matrixSinglePlayer[n]
        arrCheckSum[n] = arrayCamp[array[0]] + arrayCamp[array[1]] + arrayCamp[array[2]]
        return arrCheckSum[n]
    }

}
