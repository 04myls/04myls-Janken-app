package com.example.janken

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import androidx.preference.PreferenceManager
import com.example.janken.databinding.ActivityResultBinding

class ResultActivity : AppCompatActivity() {

    val gu = 0
    val choki = 1
    val pa = 2

    private lateinit var binding: ActivityResultBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val id = intent.getIntExtra("MY_HAND", 0)
//intentからデータを取り出す
        val myHand: Int
        myHand = when (id) {
            R.id.gu -> {
                binding.myHandImage.setImageResource(R.drawable.gu)
                gu
            }
            R.id.choki -> {
                binding.myHandImage.setImageResource(R.drawable.choki)
                choki
            }
            R.id.pa -> {
                binding.myHandImage.setImageResource(R.drawable.pa)
                pa

                //画像のリソースidは　R.drawable.id名
            }
            else -> gu
        }
        // コンピュータの手を決める
        val comHand = getHand()
        when (comHand) {
            gu -> binding.comHandImage.setImageResource(R.drawable.com_gu)
            choki -> binding.comHandImage.setImageResource(R.drawable.com_choki)
            pa -> binding.comHandImage.setImageResource(R.drawable.com_pa)
        }

        // 勝敗を判定する
        val gameResult = (comHand - myHand + 3) % 3

        when (gameResult) {
            0 -> binding.resultLabel.setText(R.string.result_draw) // あいこの場合
            1 -> binding.resultLabel.setText(R.string.result_win)  // 勝った場合
            2 -> binding.resultLabel.setText(R.string.result_lose) // 負けた場合
        }
        binding.backButton.setOnClickListener { finish() }

        // じゃんけんの結果を保存する
        saveData(myHand, comHand, gameResult)

    }

    private fun saveData(myHand: Int, comHand: Int, gameResult: Int) {
        val pref = PreferenceManager.getDefaultSharedPreferences(this)
        val gameCount = pref.getInt("GAME_COUNT", 0)
        val winningStreakCount = pref.getInt("WINNING_STREAK_COUNT", 0)
        val lastComHand = pref.getInt("LAST_COM_HAND", 0)
        val lastGameResult = pref.getInt("GAME_RESULT", -1)

        val edtWinningStreakCount: Int =
            when {
                lastGameResult == 2 && gameResult == 2 ->
                    winningStreakCount + 1
                else ->
                    0
            }

//        val editor = pref.edit()
//        editor.putInt("GAME_COUNT", gameCount + 1)
//            .putInt("WINNING_STREAK_COUNT", edtWinningStreakCount)
//            .putInt("LAST_MY_HAND", myHand)
//            .putInt("LAST_COM_HAND", comHand)
//            .putInt("BEFORE_LAST_COM_HAND", lastComHand)
//            .putInt("GAME_RESULT", gameResult)
//            .apply()
        pref.edit {
            putInt("GAME_COUNT", gameCount + 1)
            putInt("WINNING_STREAK_COUNT", edtWinningStreakCount)
            putInt("LAST_MY_HAND", myHand)
            putInt("LAST_COM_HAND", comHand)
            putInt("BEFORE_LAST_COM_HAND", lastComHand)
            putInt("GAME_RESULT", gameResult)
        }
    }

    private fun getHand(): Int {
        var hand = (Math.random() * 3).toInt()
        val pref = PreferenceManager.getDefaultSharedPreferences(this)
        val gameCount = pref.getInt("GAME_COUNT", 0)
        val winningStreakCount = pref.getInt("WINNING_STREAK_COUNT", 0)
        val lastMyHand = pref.getInt("LAST_MY_HAND", 0)
        val lastComHand = pref.getInt("LAST_COM_HAND", 0)
        val beforeLastComHand = pref.getInt("BEFORE_LAST_COM_HAND", 0)
        val gameResult = pref.getInt("GAME_RESULT", -1)

        if (gameCount == 1) {
            if (gameResult == 2) {
                // 前回の勝負が1回目で、コンピュータが勝った場合、
                // コンピュータは次に出す手を変える
                while (lastComHand == hand) {
                    hand = (Math.random() * 3).toInt()
                }
            } else if (gameResult == 1) {
                // 前回の勝負が1回目で、コンピュータが負けた場合
                // 相手の出した手に勝つ手を出す
                hand = (lastMyHand - 1 + 3) % 3
            }
        } else if (winningStreakCount > 0) {
            if (beforeLastComHand == lastComHand) {
                // 同じ手で連勝した場合は手を変える
                while (lastComHand == hand) {
                    hand = (Math.random() * 3).toInt()
                }
            }
        }
        return hand
    }
}