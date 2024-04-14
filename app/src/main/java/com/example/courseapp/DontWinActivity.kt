package com.example.courseapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.courseapp.databinding.DontwinActivityBinding

class DontWinActivity: AppCompatActivity() {
    private lateinit var binding: DontwinActivityBinding
    private lateinit var options: OptionsActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DontwinActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.restartbtn.setOnClickListener {restartBtn()}
        binding.exitbtnnowin.setOnClickListener { exitGameBtn() }
    }

    private fun restartBtn() {
        val intent = Intent(this, OpenBoxActivity::class.java )
        startActivity(intent)
    }

    private fun exitGameBtn() {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
    }

    companion object {
        const val extra_options = "EXTRA_OPTIONS"

        private const val key_options = "KEY_OPTIONS"
    }


}