package com.vaanigoel.vanaspati

import com.vaanigoel.vanaspati.databinding.ActivityHomeDashboardBinding
import android.content.Intent
import android.os.Bundle
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import android.widget.*
import android.view.View


class HomeDashboard : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_dashboard)

        val tvPlantCount = findViewById<TextView>(R.id.tvPlantCount)
        val btnAddPlant = findViewById<Button>(R.id.btnAddPlant)
        val spinner = findViewById<Spinner>(R.id.spinnerplants)
        val plants = arrayOf("My Plants ⬇\uFE0F", "Rose")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, plants)
        spinner.adapter= adapter
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {

                if (position == 0) return

                when (position) {
                    1-> {
                        val intent = Intent(this@HomeDashboard, RoseActivity::class.java)
                        startActivity(intent)
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }


        //Temporary data
        tvPlantCount.text = "You have 1 plants"

        btnAddPlant.setOnClickListener {
            startActivity(Intent(this, AddPlantActivity::class.java))
        }
        }
    }

