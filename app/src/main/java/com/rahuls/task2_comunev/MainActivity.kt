package com.rahuls.task2_comunev

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.gson.JsonSyntaxException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONException
import java.util.*

private const val FETCHURL = "https://randomuser.me/api/?results=100&inc=name"

class MainActivity : AppCompatActivity() {

    private lateinit var recipes: List<FullName>
    private lateinit var recyclerview: RecyclerView
    private lateinit var arrayList: ArrayList<FullName>
    private lateinit var adapter: MyNameAdapter
    private lateinit var pb: ProgressBar
    private val uiScope = CoroutineScope(Dispatchers.IO)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        pb = findViewById(R.id.pb)
        pb.visibility = View.GONE
        recyclerview = findViewById(R.id.recyclerview)
        arrayList = ArrayList<FullName>()
        adapter = MyNameAdapter(arrayList)
        val mLayoutManager: RecyclerView.LayoutManager = LinearLayoutManager(applicationContext)
        recyclerview.layoutManager = mLayoutManager
        recyclerview.itemAnimator = DefaultItemAnimator()
        recyclerview.isNestedScrollingEnabled = false
        recyclerview.adapter = adapter
        if (isNetworkAvailable(this)) {
            fetchFromServer()
        } else {
            fetchFromRoom()
        }
    }

    private fun isNetworkAvailable(context: Context?): Boolean {
        if (context == null) return false
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val capabilities =
                connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
            if (capabilities != null) {
                when {
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {
                        return true
                    }
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> {
                        return true
                    }
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> {
                        return true
                    }
                }
            }
        } else {
            val activeNetworkInfo = connectivityManager.activeNetworkInfo
            if (activeNetworkInfo != null && activeNetworkInfo.isConnected) {
                return true
            }
        }
        return false
    }


    private fun fetchFromRoom() {
        val thread = Thread {
            val recipeList: List<Names> =
                NamesRepository.getInstance(this)!!.getAppDatabase().namesDao()
                    .getAll()
            arrayList.clear()
            for (recipe in recipeList) {
                val repo = FullName()
                repo.id = recipe.id
                repo.title = recipe.tName
                repo.first = recipe.fName
                repo.last = recipe.lName
                arrayList.add(repo)
            }
            // refreshing recycler view
            runOnUiThread { adapter.notifyDataSetChanged() }
        }
        thread.start()
    }

    private fun fetchFromServer() {
        pb.visibility = View.VISIBLE

        val request = JsonObjectRequest(
            Request.Method.GET, FETCHURL, null,
            {
                if (it == null) {
                    pb.visibility = View.GONE
                    Toast.makeText(
                        applicationContext,
                        "Couldn't fetch the menu! Please try again.",
                        Toast.LENGTH_LONG
                    ).show()
                }
                try {

                    val persons = it.getJSONArray("results")

                    for (j in 0 until persons.length()) {
                        val name = persons.getJSONObject(j)
                        val continent = FullName()

                        // Get the current student (json object) data
                        val items = name.getJSONObject("name")

                        // Your code here
                        continent.title = (items.getString("title"))
                        continent.first = (items.getString("first"))
                        continent.last = (items.getString("last"))

                        // Display the formatted json data in text view
                        arrayList.add(continent)

                    }


                } catch (e: JsonSyntaxException) {
                    e.printStackTrace()
                } catch (e: JSONException) {
                    e.printStackTrace()
                }


                // refreshing recycler view
                adapter.notifyDataSetChanged()
                pb.visibility = View.GONE
                uiScope.launch {
                    saveTask()
                }
                Toast.makeText(this, "Names Saved!", Toast.LENGTH_SHORT).show()
            },
            {  // error in getting json
                pb.visibility = View.GONE
                Log.e("TAG", "Error: " + it.message)
                Toast.makeText(applicationContext, "Error: " + it.message, Toast.LENGTH_SHORT)
                    .show()
            })
        val requestQueue = Volley.newRequestQueue(this)
        request.setShouldCache(false)
        requestQueue.add(request)
    }


    private suspend fun saveTask() {
        withContext(Dispatchers.IO) {
            recipes = arrayList

            //creating a task
            for (i in recipes.indices) {
                val recipe = Names()
                recipe.tName = recipes[i].title
                recipe.fName = recipes[i].first
                recipe.lName = recipes[i].last
                NamesRepository.getInstance(applicationContext)!!.getAppDatabase().namesDao()
                    .insert(recipe)
            }
        }
    }

    fun deleteAll(view: View?) {
        uiScope.launch {
            NamesRepository.getInstance(view!!.context)!!.getAppDatabase().namesDao().deleteAll()
            fetchFromRoom()
        }
    }

    fun refresh(view: View?) {
        fetchFromServer()
    }
}