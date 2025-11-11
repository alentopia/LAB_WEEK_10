package com.example.lab_week_10

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import com.example.lab_week_10.database.Total
import com.example.lab_week_10.database.TotalDatabase
import com.example.lab_week_10.database.TotalObject
import com.example.lab_week_10.viewmodels.TotalViewModel
import java.util.*

class MainActivity : AppCompatActivity() {

    // Create an instance of the TotalDatabase
    private val db by lazy { prepareDatabase() }

    // Create an instance of the TotalViewModel
    private val viewModel by lazy {
        ViewModelProvider(this)[TotalViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize the value of the total from the database
        initializeValueFromDatabase()

        // Prepare the ViewModel
        prepareViewModel()
    }

    override fun onStart() {
        super.onStart()
        val totalData = db.totalDao().getTotal(ID)
        if (totalData.isNotEmpty()) {
            val lastDate = totalData.first().total.date
            Toast.makeText(this, lastDate, Toast.LENGTH_LONG).show()
        }
    }

    private fun updateText(total: Int) {
        findViewById<TextView>(R.id.text_total).text =
            getString(R.string.text_total, total)
    }

    private fun prepareViewModel() {
        // Observe the LiveData object
        viewModel.total.observe(this) { total ->
            // Whenever the value of the LiveData object changes,
            // the updateText() is called, with the new value as the parameter
            updateText(total)
        }

        findViewById<Button>(R.id.button_increment).setOnClickListener {
            viewModel.incrementTotal()
        }
    }

    // Create and build the TotalDatabase with the name 'total-database'
    private fun prepareDatabase(): TotalDatabase {
        return Room.databaseBuilder(
            applicationContext,
            TotalDatabase::class.java, "total-database"
        ).allowMainThreadQueries().build()
    }

    // Initialize the value of the total from the database
    private fun initializeValueFromDatabase() {
        val total = db.totalDao().getTotal(ID)
        if (total.isEmpty()) {
            // Insert new Total record with value 0 and current date
            db.totalDao().insert(
                Total(id = 1, total = TotalObject(0, Date().toString()))
            )
            viewModel.setTotal(0)
        } else {
            // If already exists, set the current value to the ViewModel
            viewModel.setTotal(total.first().total.value)
        }
    }

    // Update the value and date in the database whenever the app is paused
    override fun onPause() {
        super.onPause()
        val dateNow = Date().toString()
        val totalValue = viewModel.total.value ?: 0

        db.totalDao().update(
            Total(
                id = 1,
                total = TotalObject(totalValue, dateNow)
            )
        )
    }

    companion object {
        const val ID: Long = 1
    }
}
