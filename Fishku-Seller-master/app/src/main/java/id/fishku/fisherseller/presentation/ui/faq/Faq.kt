package id.fishku.fisherseller.presentation.ui.faq

import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract.Data
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import id.fishku.fisherseller.R
import id.fishku.fisherseller.databinding.ActivityFaqBinding
import java.net.URLEncoder

class Faq : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var faqList: ArrayList<DataFaq>
    private lateinit var binding: ActivityFaqBinding

    lateinit var question: Array<String>
    lateinit var answer: Array<String>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFaqBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        delete action bar
        supportActionBar?.hide()

        question = arrayOf(
            getString(R.string.faq_1),
            getString(R.string.faq_2),
            getString(R.string.faq_3),
            getString(R.string.faq_4),
            getString(R.string.faq_5),
            getString(R.string.faq_6),
            getString(R.string.faq_7),
            getString(R.string.faq_8),
            getString(R.string.faq_9)
        )
        answer = arrayOf(
            getString(R.string.ans_1),
            getString(R.string.ans_2),
            getString(R.string.ans_3),
            getString(R.string.ans_4),
            getString(R.string.ans_5),
            getString(R.string.ans_6),
            getString(R.string.ans_7),
            getString(R.string.ans_8),
            getString(R.string.ans_9)
        )

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)

        faqList = arrayListOf<DataFaq>()
        getData()

        binding.btnWa.setOnClickListener {
            val phoneNumber =
                "6281213676438" // share to fishku number
            val message =
                "Halo, Bisa kah kamu membantu saya ?"
            val uri = Uri.parse(
                "https://api.whatsapp.com/send?phone=$phoneNumber&text=${
                    URLEncoder.encode(
                        message,
                        "UTF-8"
                    )
                }"
            )
            val intent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(intent)
        }

        binding.btnIg.setOnClickListener {
            val username = "fishku.id"
            val uri = Uri.parse("http://instagram.com/_u/$username")
            val intent = Intent(Intent.ACTION_VIEW, uri)
            intent.setPackage("com.instagram.android")
            try {
                startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                val webIntent =
                    Intent(Intent.ACTION_VIEW, Uri.parse("http://instagram.com/$username"))
                startActivity(webIntent)
            }
        }

    }

    private fun getData() {
        for (i in question.indices) {
            val data = DataFaq(question[i], answer[i])
            faqList.add(data)
        }

        val adapter = FaqAdapter(faqList)
        recyclerView.adapter = adapter
    }
}