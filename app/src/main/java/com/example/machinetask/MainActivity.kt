package com.example.machinetask

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.machinetask.adapter.QuizPagerAdapter
import com.example.machinetask.databinding.ActivityMainBinding
import com.example.machinetask.model.Question
import com.example.machinetask.model.QuestionsResponse
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import java.io.IOException

class MainActivity : AppCompatActivity(), View.OnClickListener,
    QuizPagerAdapter.OnOptionSelectedListener {

    private var binding: ActivityMainBinding? = null
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var questions: List<Question>
    private lateinit var adapter: QuizPagerAdapter
    private var currentQuestionIndex = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        initialView()
    }

    private fun initialView() {
        binding!!.timerCount.txtSave.setOnClickListener(this)
        sharedPreferences = getSharedPreferences("challenge_prefs", Context.MODE_PRIVATE)

        if (sharedPreferences.contains("hourOne") && sharedPreferences.contains("hourTwo") && sharedPreferences.contains(
                "minuteOne"
            ) && sharedPreferences.contains("minuteTwo") && sharedPreferences.contains(
                "secondOne"
            ) && sharedPreferences.contains(
                "secondTwo"
            )
        ) {
            binding!!.timerCount.linearLayoutTimer.visibility = View.GONE
            binding!!.timerCount.layoutChallengeStart.visibility = View.VISIBLE
            binding!!.timerCount.txtSchedule.visibility = View.GONE
            binding!!.timerCount.txtSave.visibility = View.GONE
        } else {
            binding!!.timerCount.linearLayoutTimer.visibility = View.VISIBLE
            binding!!.timerCount.layoutChallengeStart.visibility = View.GONE
            binding!!.timerCount.txtSchedule.visibility = View.VISIBLE
            binding!!.timerCount.txtSave.visibility = View.VISIBLE
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    override fun onClick(v: View?) {
        when (v!!.id) {

            R.id.txt_save -> {

                //validation
                saveChallengeTime()
                binding!!.timerCount.linearLayoutTimer.visibility = View.GONE
                binding!!.timerCount.layoutChallengeStart.visibility = View.VISIBLE
                binding!!.timerCount.txtSchedule.visibility = View.GONE
                binding!!.timerCount.txtSave.visibility = View.GONE
            }
        }
    }

    private fun saveChallengeTime() {
        val hourOne = binding!!.timerCount.editTextHours.text.toString().toIntOrNull() ?: 0
        val hourTwo = binding!!.timerCount.editTextH.text.toString().toIntOrNull() ?: 0
        val minuteOne = binding!!.timerCount.editTextMinutes.text.toString().toIntOrNull() ?: 0
        val minuteTwo = binding!!.timerCount.editTextM.text.toString().toIntOrNull() ?: 0
        val secondOne = binding!!.timerCount.editTextSeconds.text.toString().toIntOrNull() ?: 0
        val secondTwo = binding!!.timerCount.editTextS.text.toString().toIntOrNull() ?: 0

        with(sharedPreferences.edit()) {
            putInt("hourOne", hourOne)
            putInt("hourTwo", hourTwo)
            putInt("minuteOne", minuteOne)
            putInt("minuteTwo", minuteTwo)
            putInt("secondOne", secondOne)
            putInt("secondTwo", secondTwo)
            apply()
        }

        Toast.makeText(this, "Challenge time saved", Toast.LENGTH_LONG).show()
        loadSavedTime()
    }

    private fun loadSavedTime() {
        if (sharedPreferences.contains("hourOne") && sharedPreferences.contains("hourTwo") && sharedPreferences.contains(
                "minuteOne"
            ) && sharedPreferences.contains("minuteTwo") && sharedPreferences.contains(
                "secondOne"
            ) && sharedPreferences.contains(
                "secondTwo"
            )
        ) {

            val hour = sharedPreferences.getInt("hourOne", 0).toString() + sharedPreferences.getInt(
                "hourTwo",
                0
            ).toString()

            val minute = sharedPreferences.getInt("minuteOne", 0)
                .toString() + sharedPreferences.getInt("minuteTwo", 0).toString()

            val second = sharedPreferences.getInt("secondOne", 0)
                .toString() + sharedPreferences.getInt("secondTwo", 0).toString()

            val timeString = "$hour:$minute:$second"
            binding!!.timerCount.txtTimer.text = timeString
            val totalSeconds = hour.toInt() * 3600 + minute.toInt() * 60 + second.toInt()
            startCountdown(totalSeconds.toLong())

        }
    }


    private fun startCountdown(totalSeconds: Long) {
        val countdownTimer = object : CountDownTimer(totalSeconds * 1000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val secondsRemaining = millisUntilFinished / 1000
                val hours = secondsRemaining / 3600
                val minutes = (secondsRemaining % 3600) / 60
                val seconds = secondsRemaining % 60

                binding!!.timerCount.txtTimer.text =
                    String.format("%02d:%02d:%02d", hours, minutes, seconds)
            }

            override fun onFinish() {
                binding!!.timerCount.txtTimer.text = ""
                // Here you can start the challenge or proceed to the next activity/fragment
                binding!!.timerCount.viewPager.visibility = View.VISIBLE

                binding!!.timerCount.linearLayoutTimer.visibility = View.GONE
                binding!!.timerCount.layoutChallengeStart.visibility = View.GONE
                binding!!.timerCount.txtSchedule.visibility = View.GONE
                binding!!.timerCount.txtSave.visibility = View.GONE

                loadAdapter()
            }
        }
        countdownTimer.start()
    }


    private fun loadAdapter() {
        val jsonString = loadJsonFromAssets(this@MainActivity, "questions.json")
        if (jsonString != null) {
            val parsedQuestions = parseQuestionsJson(jsonString)
            if (parsedQuestions != null) {
                questions = parsedQuestions
                adapter = QuizPagerAdapter(
                    questions,
                    this,
                    binding!!.timerCount.txtChallengeTimer,
                    this@MainActivity
                )
                binding!!.timerCount.viewPager.adapter = adapter

                // Disable swipe to move to next question
                binding!!.timerCount.viewPager.isUserInputEnabled = false
            } else {
                Toast.makeText(this, "Failed to load questions", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "Failed to load JSON", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onOptionSelected(answerId: Int, selectedOptionId: Int, position: Int) {
        val isCorrect = answerId == selectedOptionId
        if (AppConfig.position - 1 == position && !AppConfig.isClicked) {
            if (isCorrect) {
                AppConfig.TotalCorrectedAnswer += 1
            }
            AppConfig.isClicked = true
        }
    }

    @SuppressLint("SetTextI18n")
    fun nextQuestion() {
        if (::questions.isInitialized) {
            Log.d("MainActivity", "Moving to question index: $currentQuestionIndex")
            currentQuestionIndex++
            if (currentQuestionIndex < questions.size - 1) {
                binding!!.timerCount.viewPager.currentItem = currentQuestionIndex
            } else {
                // Handle the quiz finished condition when the last question is reached
                Toast.makeText(this, "Quiz Finished!", Toast.LENGTH_SHORT).show()
                binding!!.timerCount.txtGameOver.visibility = View.VISIBLE
                binding!!.timerCount.viewPager.visibility = View.GONE

                Handler(Looper.getMainLooper()).postDelayed({
                    binding!!.timerCount.txtGameOver.text =
                        "SCORE: ${AppConfig.TotalCorrectedAnswer}/${questions.size - 1}"
                    val editor = sharedPreferences.edit()
                    editor.clear()
                    editor.apply()
                }, 5000)
            }
        } else {
            Toast.makeText(this, "Questions are not initialized", Toast.LENGTH_SHORT).show()
        }
    }

}


private fun loadJsonFromAssets(context: Context, fileName: String): String? {
    return try {
        val inputStream = context.assets.open(fileName)
        val size = inputStream.available()
        val buffer = ByteArray(size)
        inputStream.read(buffer)
        inputStream.close()
        String(buffer, Charsets.UTF_8)
    } catch (e: IOException) {
        e.printStackTrace()
        null
    }
}

private fun parseQuestionsJson(jsonString: String): List<Question>? {
    return try {
        val gson = Gson()
        val questionsResponse = gson.fromJson(jsonString, QuestionsResponse::class.java)
        questionsResponse.questions
    } catch (e: JsonSyntaxException) {
        e.printStackTrace()
        null
    }
}
