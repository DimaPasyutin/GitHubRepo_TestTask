package com.example.githubrepo_testtask

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.githubrepo_testtask.presentation.screens.main_page.RepositoriesPageFragment

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .add(R.id.fragment_container, RepositoriesPageFragment())
                .commit()
        }

    }
}