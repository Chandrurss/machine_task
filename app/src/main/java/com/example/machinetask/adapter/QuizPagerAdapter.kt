package com.example.machinetask.adapter

import android.annotation.SuppressLint
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.machinetask.AppConfig
import com.example.machinetask.MainActivity
import com.example.machinetask.R
import com.example.machinetask.model.Question

class QuizPagerAdapter(
    private val questions: List<Question>,
    private val listener: OnOptionSelectedListener,
    private val timerText: TextView,
    private val mainActivity: MainActivity
) : RecyclerView.Adapter<QuizPagerAdapter.QuestionViewHolder>() {

    private var currentTimer: CountDownTimer? = null

    interface OnOptionSelectedListener {
        fun onOptionSelected(answerId: Int, selectedOptionId: Int, position:Int)
    }

    inner class QuestionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtQuestionNo: TextView = itemView.findViewById(R.id.txtQuestionNo)
        val imgFlag: ImageView = itemView.findViewById(R.id.img_flag)
        val txtOptionOne: TextView = itemView.findViewById(R.id.txt_option_one)
        val txtOptionTwo: TextView = itemView.findViewById(R.id.txt_option_two)
        val txtOptionThree: TextView = itemView.findViewById(R.id.txt_option_three)
        val txtOptionFour: TextView = itemView.findViewById(R.id.txt_option_four)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuestionViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.challenge_start_in, parent, false)
        return QuestionViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: QuestionViewHolder,
        @SuppressLint("RecyclerView") position: Int
    ) {
        val question = questions[position]
        Log.d("QuizPagerAdapter", "Binding question at position: $position with data: $question")
        val questionNo = position + 1
        holder.txtQuestionNo.text = questionNo.toString()
        val options = question.countries

        updateUi(holder.imgFlag, getFlagResource(question.countryCode))
        holder.txtOptionOne.text = options[0].countryName
        holder.txtOptionTwo.text = options[1].countryName
        holder.txtOptionThree.text = options[2].countryName
        holder.txtOptionFour.text = options[3].countryName
        AppConfig.position = position
        AppConfig.isClicked = false
        holder.txtOptionOne.setBackgroundResource(R.drawable.rounded_corner_border)
        holder.txtOptionTwo.setBackgroundResource(R.drawable.rounded_corner_border)
        holder.txtOptionThree.setBackgroundResource(R.drawable.rounded_corner_border)
        holder.txtOptionFour.setBackgroundResource(R.drawable.rounded_corner_border)
        holder.txtOptionOne.setOnClickListener {
            listener.onOptionSelected(
                question.answerId,
                options[0].id,position
            )

            holder.txtOptionOne.setBackgroundResource(R.drawable.selection_color)
            holder.txtOptionTwo.setBackgroundResource(R.drawable.rounded_corner_border)
            holder.txtOptionThree.setBackgroundResource(R.drawable.rounded_corner_border)
            holder.txtOptionFour.setBackgroundResource(R.drawable.rounded_corner_border)
        }
        holder.txtOptionTwo.setOnClickListener {
            listener.onOptionSelected(
                question.answerId,
                options[1].id,
                position
            )
            holder.txtOptionOne.setBackgroundResource(R.drawable.rounded_corner_border)
            holder.txtOptionTwo.setBackgroundResource(R.drawable.selection_color)
            holder.txtOptionThree.setBackgroundResource(R.drawable.rounded_corner_border)
            holder.txtOptionFour.setBackgroundResource(R.drawable.rounded_corner_border)
        }
        holder.txtOptionThree.setOnClickListener {
            listener.onOptionSelected(
                question.answerId,
                options[2].id,
                position
            )
            holder.txtOptionOne.setBackgroundResource(R.drawable.rounded_corner_border)
            holder.txtOptionTwo.setBackgroundResource(R.drawable.rounded_corner_border)
            holder.txtOptionThree.setBackgroundResource(R.drawable.selection_color)
            holder.txtOptionFour.setBackgroundResource(R.drawable.rounded_corner_border)
        }
        holder.txtOptionFour.setOnClickListener {
            listener.onOptionSelected(
                question.answerId,
                options[3].id,
                position
            )
            holder.txtOptionOne.setBackgroundResource(R.drawable.rounded_corner_border)
            holder.txtOptionTwo.setBackgroundResource(R.drawable.rounded_corner_border)
            holder.txtOptionThree.setBackgroundResource(R.drawable.rounded_corner_border)
            holder.txtOptionFour.setBackgroundResource(R.drawable.selection_color)
        }

        // Cancel any existing timer before starting a new one
        currentTimer?.cancel()

        // Timer logic (30 seconds)
        currentTimer = object : CountDownTimer(5000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                Log.d("Timer", "Seconds remaining: ${millisUntilFinished / 1000}")
                timerText.text = (millisUntilFinished / 1000).toString()
            }

            override fun onFinish() {
                Log.d("Timer", "Timer finished")
                timerText.text = "0"
                (holder.itemView.context as? AppCompatActivity)?.let {
                    if (it is MainActivity) {
                        it.nextQuestion()
                    }
                }
            }
        }
        currentTimer?.start()

    }

    private fun getFlagResource(countryCode: String): Int {
        return when (countryCode) {
            "NZ" -> R.drawable.new_zealand
            "AW" -> R.drawable.arabu
            "EC" -> R.drawable.ecuador
            "PY" -> R.drawable.paraguay
            "KG" -> R.drawable.kyrgyzstan
            "PM" -> R.drawable.saint_pierre_and_miquelon
            "JP" -> R.drawable.japan
            "TM" -> R.drawable.turkmenistan
            "GA" -> R.drawable.gabon
            "MQ" -> R.drawable.martinique
            "BZ" -> R.drawable.belize
            "CZ" -> R.drawable.czech_republic
            "AE" -> R.drawable.united_arab_emirates
            "JE" -> R.drawable.jersey
            "LS" -> R.drawable.lesotho
            else -> R.drawable.lesotho // Default flag if none match
        }
    }

    override fun getItemCount(): Int = questions.size

    private fun updateUi(imgFlag: ImageView, flagResource: Int) {
        Glide.with(mainActivity).load(flagResource).into(imgFlag)
    }

}