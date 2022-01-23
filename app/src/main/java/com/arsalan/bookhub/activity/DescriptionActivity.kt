package com.arsalan.bookhub.activity

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.media.Rating
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.view.textclassifier.ConversationActions
import android.widget.*
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.arsalan.bookhub.R
import com.arsalan.bookhub.util.ConnectionManager
import com.squareup.picasso.Picasso
import org.json.JSONObject

class DescriptionActivity : AppCompatActivity() {

    lateinit var txtBookName:TextView
    lateinit var txtBookAuthor:TextView
    lateinit var txtBookPrice:TextView
    lateinit var txtBookRating: TextView
    lateinit var imgBookImage: ImageView
    lateinit var txtBookDesc:TextView
    lateinit var btnAddToFav: Button
    lateinit var progressBar:ProgressBar
    lateinit var progressLayout: RelativeLayout

    lateinit var toolbar: Toolbar

    var bookId:String? = "100"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_description)

        txtBookName=findViewById(R.id.txtBookName)
        txtBookAuthor=findViewById(R.id.txtBookAuthor)
        txtBookPrice=findViewById(R.id.txtBookPrice)
        txtBookRating=findViewById(R.id.txtBookRating)
        imgBookImage=findViewById(R.id.imgBookImage)
        txtBookDesc=findViewById(R.id.txtBookDesc)
        btnAddToFav=findViewById(R.id.btnAddToFav)
        progressBar=findViewById(R.id.progressBar)
        progressBar.visibility=View.VISIBLE
        progressLayout=findViewById(R.id.progressLayout)
        progressLayout.visibility=View.VISIBLE

        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Book Details"



            if (intent != null){
            bookId=intent.getStringExtra("book_id")
        }else{
            finish()
            Toast.makeText(this@DescriptionActivity, "Some unexpected error occured!", Toast.LENGTH_SHORT).show()
        }
        if (bookId== "100"){
            finish()
            Toast.makeText(this@DescriptionActivity, "some unexpected error occured", Toast.LENGTH_SHORT).show()
        }
        val queue= Volley.newRequestQueue(this@DescriptionActivity)
        val url="http://13.235.250.119/v1/book/get_book/"

        val jsonParams = JSONObject()
        jsonParams.put("book_id",bookId)

        if(ConnectionManager().checkConnectivity(this@DescriptionActivity)){

            val jsonRequest=
                object : JsonObjectRequest(
                    Request.Method.POST, url, jsonParams, Response.Listener{try {
                        val success =it.getBoolean("success")
                        if(success){
                            val  bookJsonObjects= it.getJSONObject("book_data")
                            progressLayout.visibility=View.GONE
                            Picasso.get().load(bookJsonObjects.getString("image")).error(R.drawable.default_book_cover).into(imgBookImage)

                            txtBookName.text=bookJsonObjects.getString("name")
                            txtBookAuthor.text=bookJsonObjects.getString("author")
                            txtBookPrice.text=bookJsonObjects.getString("price")
                            txtBookRating.text=bookJsonObjects.getString("rating")
                            txtBookDesc.text = bookJsonObjects.getString("description")

                        }else{
                            Toast.makeText(this@DescriptionActivity, "some  Error occurred", Toast.LENGTH_SHORT).show()
                        }
                    }catch (e:Exception){
                        Toast.makeText(this@DescriptionActivity, "some  error occurred", Toast.LENGTH_SHORT).show()
                    }
                    },
                    Response.ErrorListener {
                        Toast.makeText(this@DescriptionActivity, "Volley Error $it ", Toast.LENGTH_SHORT).show()
                    }){
                    override fun getHeaders(): MutableMap<String, String> {
                        val headers = HashMap<String,String>()
                        headers["Content-type"] = "application/json"
                        headers["token"]="ca2ceff0d7e496"
                        return headers
                    }
                }
            queue.add(jsonRequest)
        }else{
            val dialog =  AlertDialog.Builder(this@DescriptionActivity)
            dialog.setTitle("Error")
            dialog.setMessage("Internet Connection not Found")
            dialog.setPositiveButton("Open Settings"){text , listner->
                val  settingIntent= Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(settingIntent)
                finish()
            }
            dialog.setNegativeButton("Exit"){text,listner->
                ActivityCompat.finishAffinity(this@DescriptionActivity)
            }
            dialog.create()
            dialog.show()
        }
    }
}