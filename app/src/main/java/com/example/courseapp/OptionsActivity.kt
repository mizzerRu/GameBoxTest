package com.example.courseapp

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.example.courseapp.databinding.ActivityOptionsBinding
import com.example.courseapp.models.Options

@Suppress("DEPRECATION")
class OptionsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOptionsBinding
    private lateinit var options: Options
    private lateinit var boxCountItems: List<BoxCountItem>
    private lateinit var adapter: ArrayAdapter<BoxCountItem>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOptionsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        options =
            savedInstanceState?.getParcelable<Options>(key_options) ?: intent?.getParcelableExtra(
                extra_options
            )
                    ?: throw IllegalAccessException("You need to specify EXTRA_OPTIONS to launch this activity")

        setupSpinner()
        updateUi()
        binding.cancelButton.setOnClickListener { finish() }
        binding.confirmButton.setOnClickListener { onConfirm() }

    }

    private fun setupSpinner() {
        boxCountItems =
            (3..9).map { BoxCountItem(it, resources.getQuantityString(R.plurals.boxes, it, it)) }
        adapter = ArrayAdapter(
            this,
            R.layout.item_spinner,
            boxCountItems
        )
        adapter.setDropDownViewResource(android.R.layout.simple_list_item_1)
        binding.spinnerID.adapter = adapter
        binding.spinnerID.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) = Unit

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val count = boxCountItems[position].count
                options = options.copy(boxCount = count)
            }
        }
    }

    private fun updateUi() {
        binding.timercheckbox.isChecked = options.isTimerEnabled
        val currentIndex = boxCountItems.indexOfFirst { it.count == options.boxCount }
        binding.spinnerID.setSelection(currentIndex)
    }


    private fun onConfirm() {
        options = options.copy(isTimerEnabled = binding.timercheckbox.isChecked)
        val intent = Intent()
        intent.putExtra(extra_options, options)
        setResult(Activity.RESULT_OK, intent)
        finish()

    }

    companion object {
        const val extra_options = "EXTRA_OPTIONS"

        private const val key_options = "KEY_OPTIONS"
    }

    class BoxCountItem(
        val count: Int,
        private val optionTitle: String
    ) {
        override fun toString(): String {
            return optionTitle
        }
    }
}