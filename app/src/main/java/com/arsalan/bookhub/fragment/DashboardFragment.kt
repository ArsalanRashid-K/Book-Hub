package com.arsalan.bookhub.fragment

import android.app.Activity
import android.app.AlertDialog
import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.textclassifier.TextLinks
import android.widget.Button
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.arsalan.bookhub.R
import com.arsalan.bookhub.adapter.DashboardRecyclerAdapter
import com.arsalan.bookhub.model.Book
import com.arsalan.bookhub.util.ConnectionManager
import org.json.JSONException
import org.json.JSONObject
import java.util.*
import kotlin.collections.HashMap


class DashboardFragment : Fragment() {

    lateinit var recyclerDashboard: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager

    lateinit var btnCheckInternet:Button

    lateinit var progressLayout: RelativeLayout
    lateinit var progressBar: ProgressBar

    val  bookInfoList = arrayListOf<Book>()

/*
//    val bookList = arrayListOf(
//        "P.S. I Love You",
//        "The Great Gatsby",
//        "Anna Karenina",
//        "Madame Bovary",
//        "War and Peace",
//        "Lolita",
//        "The Adventure of Huckleberry Finn",
//        "Moby-Dick",
//        "The Lord Of The Rings"
//    )

 */

    lateinit var recyclerAdapter: DashboardRecyclerAdapter
/*
//    val bookInfoList= arrayListOf<Book>(
//        Book("P.S. I love You", "Cecelia Ahern", "Rs. 299", "4.5", R.drawable.ps_ily),
//        Book("The Great Gatsby", "F. Scott Fitzgerald", "Rs. 399", "4.1", R.drawable.great_gatsby),
//        Book("Anna Karenina", "Leo Tolstoy", "Rs. 199", "4.3", R.drawable.anna_kare),
//        Book("Madame Bovary", "Gustave Flaubert", "Rs. 500", "4.0", R.drawable.madame),
//        Book("War and Peace", "Leo Tolstoy", "Rs. 249", "4.8", R.drawable.war_and_peace),
//        Book("Lolita", "Vladimir Nabokov", "Rs. 349", "3.9", R.drawable.lolita),
//        Book("Middlemarch", "George Eliot", "Rs. 599", "4.2", R.drawable.middlemarch),
//        Book("The Adventures of Huckleberry Finn", "Mark Twain", "Rs. 699", "4.5", R.drawable.adventures_finn),
//        Book("Moby-Dick", "Herman Melville", "Rs. 499", "4.5", R.drawable.moby_dick),
//        Book("The Lord of the Rings", "J.R.R Tolkien", "Rs. 749", "5.0", R.drawable.lord_of_rings)
//    )


 */



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment

        val view = inflater.inflate(R.layout.fragment_dashboard,container,false)

        //view is used because it is built in oncreatview
        recyclerDashboard=view.findViewById(R.id.recyclerDashboard)

        btnCheckInternet=view.findViewById(R.id.btnCheckInternet)
        progressLayout=view.findViewById(R.id.progressLayout)
        progressBar=view.findViewById(R.id.progressBar)

        progressLayout.visibility= View.VISIBLE

        btnCheckInternet.setOnClickListener {
            if (ConnectionManager().checkConnectivity(activity as Context)) {
                // Internet is available
                val dialog =  AlertDialog.Builder(activity as Context)
                dialog.setTitle("Success")
                dialog.setMessage("Internet Connection Found")
                dialog.setPositiveButton("ok"){
                    text , listner->// do nothing
                }
                dialog.setNegativeButton("Cancel"){
                    text,listner->
                    //do nothing
                }
                dialog.create()
                dialog.show()
            } else {
                //Internet is not available
                val dialog =  AlertDialog.Builder(activity as Context)
                dialog.setTitle("Error")
                dialog.setMessage("Internet Connection not Found")
                dialog.setPositiveButton("ok"){
                        text , listner->// do nothing
                }
                dialog.setNegativeButton("Cancel"){
                        text,listner->
                    //do nothing
                }
                dialog.create()
                dialog.show()
            }
        }

        //activity- cos doing it in fragment not activity
        layoutManager=LinearLayoutManager(activity)




        val queue = Volley.newRequestQueue(activity as Context)

         val url = "http://13.235.250.119/v1/book/fetch_books/"

        if(ConnectionManager().checkConnectivity(activity as Context)) {

            val jsonObjectRequest =
                object : JsonObjectRequest(Request.Method.GET, url, null, Response.Listener {

                    // here we will handle the response
                    try {
                        progressLayout.visibility=View.GONE
                        val success = it.getBoolean("success")

                        if (success) {
                            val data = it.getJSONArray("data")
                            for (i in 0 until data.length()) {
                                val bookJsonObject = data.getJSONObject(i)
                                val bookObject = Book(
                                    bookJsonObject.getString("book_id"),
                                    bookJsonObject.getString("name"),
                                    bookJsonObject.getString("author"),
                                    bookJsonObject.getString("rating"),
                                    bookJsonObject.getString("price"),
                                    bookJsonObject.getString("image")
                                )
                                bookInfoList.add(bookObject)
                                recyclerAdapter =
                                    DashboardRecyclerAdapter(activity as Context, bookInfoList)


                                recyclerDashboard.adapter = recyclerAdapter

                                recyclerDashboard.layoutManager = layoutManager

                                recyclerDashboard.addItemDecoration(
                                    DividerItemDecoration(
                                        recyclerDashboard.context,
                                        (layoutManager as LinearLayoutManager).orientation
                                    )
                                )
                            }
                        } else {
                            Toast.makeText(
                                activity as Context,
                                "Some Error Occurred!!!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }catch (e:JSONException){
                        Toast.makeText(activity as Context , "some unexpected error occured!!!",Toast.LENGTH_SHORT).show()
                    }




                }, Response.ErrorListener {

                    // here we will handle the errors
                    Toast.makeText(activity as Context, " volley error occurred", Toast.LENGTH_SHORT).show()



                }) {
                    override fun getHeaders(): MutableMap<String, String> {
                        val headers = HashMap<String, String>()
                        headers["Content-type"] = "application/json"
                        headers["token"] = "ca2ceff0d7e496"
                        return headers
                    }
                }

            queue.add(jsonObjectRequest)
        }else{
            val dialog =  AlertDialog.Builder(activity as Context)
            dialog.setTitle("Error")
            dialog.setMessage("Internet Connection not Found")
            dialog.setPositiveButton("Open Settings"){text , listner->
                val  settingIntent= Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(settingIntent)
                activity?.finish()
            }
            dialog.setNegativeButton("Exit"){text,listner->
                ActivityCompat.finishAffinity(activity as Activity)
            }
            dialog.create()
            dialog.show()
        }


        return view
    }

}