package com.dicoding.sortify.ui.scan.result

import android.net.Uri
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.dicoding.sortify.R
import com.dicoding.sortify.databinding.ActivityResultBinding

class ResultActivity : AppCompatActivity() {

    private lateinit var binding : ActivityResultBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Mengambil data yang dikirimkan lewat Intent
        val className = intent.getStringExtra("class")
        val confidence = intent.getStringExtra("confidence")
        val recommendationTitles: ArrayList<String>? = intent.getStringArrayListExtra("recommendation_titles")
        val recommendationLinks: ArrayList<String>? = intent.getStringArrayListExtra("recommendation_links")
        val imageUriString = intent.getStringExtra("imageUri") // Mengambil Uri gambar sebagai String

        // Menampilkan class dan confidence
        binding.tvClassificationResult.text = className
        binding.tvScore.text = confidence

        // Menampilkan gambar
        imageUriString?.let { uriString ->
            val imageUri = Uri.parse(uriString)  // Mengonversi String kembali ke Uri
            binding.previewImageView.setImageURI(imageUri) // Menampilkan gambar di ImageView
        }

        // Menggabungkan rekomendasi menjadi satu string
        val recommendations = StringBuilder()
        recommendationTitles?.let { titles ->
            recommendationLinks?.let { links ->
                for (i in titles.indices) {
                    val title = titles[i]
                    val link = links.getOrNull(i) ?: ""
                    recommendations.append("${i + 1}. $title - $link\n\n")
                }
            }
        }

        // Menampilkan rekomendasi dalam TextView
        binding.tvDescription.text = recommendations.toString()



    }
}