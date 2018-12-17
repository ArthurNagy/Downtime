package me.arthurnagy.downtime.feature

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import me.arthurnagy.downtime.MainBinding
import me.arthurnagy.downtime.R

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = DataBindingUtil.setContentView<MainBinding>(this, R.layout.activity_main)
    }

    override fun onSupportNavigateUp() = findNavController(R.id.nav_host).navigateUp() || super.onSupportNavigateUp()

}
