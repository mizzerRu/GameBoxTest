package com.example.courseapp

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.courseapp.databinding.ActivityMainBinding
import com.example.courseapp.models.Options

@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var options: Options

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.exitbtn.setOnClickListener { exitFinish() }
        binding.aboutbtn.setOnClickListener { onAboutPressed() }
        binding.startbtn.setOnClickListener { onOpenBoxPressed() }
        options = savedInstanceState?.getParcelable(key_options) ?: Options.DEFAULT
        binding.optionbtn.setOnClickListener { onOptionPressed() }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable(key_options, options)
    }


    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == options_request && resultCode == Activity.RESULT_OK) {
            options = data?.getParcelableExtra(OptionsActivity.extra_options) ?:
                    throw IllegalArgumentException("Can't get the updated data from options activity")
        }
    }

    private fun onOptionPressed() {
        val intent = Intent(this, OptionsActivity::class.java)
        intent.putExtra(OptionsActivity.extra_options, options)
        startActivityForResult(intent, options_request)
    }

    private fun onAboutPressed() {
        val intent = Intent(this, AboutActivity::class.java)
        startActivity(intent)
    }

    private fun onOpenBoxPressed() {
        val intent = Intent(this, OpenBoxActivity::class.java)
        intent.putExtra(OpenBoxActivity.extra_options, options)
        startActivity(intent)
    }

    private fun exitFinish() {
        finish()
    }

    companion object {
        @JvmStatic
        private val key_options = "KEY_OPTIONS"
        @JvmStatic
        private val options_request = 1
    }
}


