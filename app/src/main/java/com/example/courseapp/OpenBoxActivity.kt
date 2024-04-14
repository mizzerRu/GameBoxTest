package com.example.courseapp

import android.content.Intent
import android.os.CountDownTimer
import android.os.PersistableBundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.courseapp.databinding.ActivityOpenBoxBinding
import com.example.courseapp.databinding.ItemBoxBinding
import com.example.courseapp.models.Options
import java.lang.IllegalArgumentException
import kotlin.math.max
import kotlin.properties.Delegates
import kotlin.random.Random
import android.os.Bundle as Bundle

@Suppress("DEPRECATION")
class OpenBoxActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOpenBoxBinding
    private lateinit var options: Options
    private lateinit var timer: CountDownTimer
    private var timerStartTimestamp by Delegates.notNull<Long>()
    private var boxIndex by Delegates.notNull<Int>()
    private var timerHandler: TimerHandler? = null


    private var alreadyDone = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOpenBoxBinding.inflate(layoutInflater)
        setContentView(binding.root)

        options = intent.getParcelableExtra(extra_options)
            ?: throw IllegalArgumentException("Can't launch OpenBoxActivity without options")
        boxIndex = savedInstanceState?.getInt(KEY_INDEX) ?: Random.nextInt(options.boxCount)

        if (options.isTimerEnabled) {
            timerStartTimestamp =
                savedInstanceState?.getLong(KEY_START_TIMESTAMP) ?: System.currentTimeMillis()

            setupTimer()
            updateTimerUi()
        }

        timerHandler = if (options.isTimerEnabled) {
            TimerHandler()
        } else {
            null
        }
        timerHandler?.onCreate(savedInstanceState)
        createBoxes()
    }


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(KEY_INDEX, boxIndex)
        if (options.isTimerEnabled) {
            outState.putLong(KEY_START_TIMESTAMP, timerStartTimestamp)
        }


    }

    private fun createBoxes() {
        val boxBindings = (0 until options.boxCount).map { index ->
            val boxBinding = ItemBoxBinding.inflate(layoutInflater)
            boxBinding.root.id = View.generateViewId()
            boxBinding.boxTitleTextView.text = getString(R.string.box_title, index + 1)
            boxBinding.root.setOnClickListener { view -> onBoxSelected(view) }
            boxBinding.root.tag = index
            binding.root.addView(boxBinding.root)
            boxBinding
        }

        binding.flow.referencedIds = boxBindings.map { it.root.id }.toIntArray()
    }


    private var attempts = 3
    private val ATTEMPTS_KEY = "attempts_key"


    @Override
    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        outState.putInt(ATTEMPTS_KEY, attempts)
        outState.putInt(KEY_INDEX, boxIndex)
        timerHandler?.onSaveInstanceState(outState)
        super.onSaveInstanceState(outState, outPersistentState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        attempts = savedInstanceState.getInt(ATTEMPTS_KEY)
    }

    override fun onStart() {
        super.onStart()
        timerHandler?.onStart()
    }

    override fun onStop() {
        super.onStop()
        timerHandler?.onStop()
    }

    private fun onBoxSelected(view: View) {
        if (view.tag as Int == boxIndex) {
            alreadyDone = true
            val intent = Intent(this, BoxActivity::class.java)
            startActivity(intent)
        } else {
            attempts--
            if (attempts > 0) {
                Toast.makeText(
                    this,
                    "Неправильный выбор, у вас осталось $attempts попыток",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                val intent = Intent(this, DontWinActivity::class.java)
                startActivity(intent)
            }
        }
    }


    private fun updateTimerUi() {
        if (getRemainingSeconds() >= 0) {
            binding.timerTextView.visibility = View.VISIBLE
            binding.timerTextView.text = getString(R.string.timer_value, getRemainingSeconds())
        } else {
            binding.timerTextView.visibility = View.GONE
        }
    }

    private fun showTimerEndDialog() {
        val dialog = AlertDialog.Builder(this)
            .setTitle(getString(R.string.the_end))
            .setMessage(getString(R.string.timer_end_message))
            .setCancelable(false)
            .setPositiveButton(android.R.string.ok) { _, _ -> finish() }
            .create()
        dialog.show()
    }


    private fun setupTimer() {
        timer = object : CountDownTimer(getRemainingSeconds() * 1000, 1000) {
            override fun onFinish() {
                updateTimerUi()
                showTimerEndDialog()
            }

            override fun onTick(millisUntilFinished: Long) {
                updateTimerUi()
            }
        }
    }


    private fun getRemainingSeconds(): Long {
        val finishedAt = timerStartTimestamp + TIMER_DURATION
        return max(0, (finishedAt - System.currentTimeMillis()) / 1000)
    }

    inner class TimerHandler {

        private lateinit var timer: CountDownTimer

        fun onCreate(savedInstanceState: Bundle?) {
            timerStartTimestamp = savedInstanceState?.getLong(KEY_START_TIMESTAMP)
                ?: System.currentTimeMillis()
            alreadyDone = savedInstanceState?.getBoolean(KEY_ALREADY_DONE) ?: false
        }

        fun onSaveInstanceState(outState: Bundle) {
            outState.putLong(KEY_START_TIMESTAMP, timerStartTimestamp)
            outState.putBoolean(KEY_ALREADY_DONE, alreadyDone)
        }

        fun onStart() {
            if (alreadyDone) return
            timer = object : CountDownTimer(getRemainingSeconds() * 1000, 1000) {
                override fun onFinish() {
                    updateTimerUi()
                    showTimerEndDialog()
                }
                override fun onTick(millisUntilFinished: Long) {
                    updateTimerUi()
                }
            }
            updateTimerUi()
            timer.start()
        }

        fun onStop() {
            timer.cancel()
        }

    }

    companion object {
        @JvmStatic
        val extra_options = "EXTRA_OPTIONS"
        @JvmStatic
        private val TIMER_DURATION = 10000
        private const val KEY_INDEX = "KEY_INDEX"
        private const val KEY_ALREADY_DONE = "KEY_ALREADY_DONE"
        private const val KEY_START_TIMESTAMP = "KEY_START_TIMESTAMP"
    }
}