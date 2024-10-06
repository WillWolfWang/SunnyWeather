package com.will.sunnyweather.ui.place

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.will.sunnyweather.MainActivity
import com.will.sunnyweather.R
import com.will.sunnyweather.WeatherActivity

class PlaceFragment: Fragment() {
    val viewModel: PlaceViewModel by lazy {
        ViewModelProvider(this).get(PlaceViewModel::class.java)
    }

    private lateinit var placeAdapter: PlaceAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_place, container, false)
        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (activity is MainActivity && viewModel.isSavePlace()) {
            val place = viewModel.getPlace()
            WeatherActivity.startWeatherActivity(requireContext(), place.location.lat, place.location.lng, place.name)
            activity?.finish()
            return
        }

        placeAdapter = PlaceAdapter(this, viewModel.placeList)

        val recyclerView = view.findViewById<RecyclerView>(R.id.recycleView).apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = placeAdapter
        }

        val ivBg = view.findViewById<ImageView>(R.id.iv_bg)

        val searchPlaceEdit = view.findViewById<EditText>(R.id.searchPlaceEdit).apply {
            addTextChangedListener {editable->
                val content = editable.toString()
                if(content.isNotEmpty()) {
                    viewModel.searchPlace(content)
                } else {
                    recyclerView.visibility = View.GONE
                    ivBg.visibility = View.VISIBLE
                    viewModel.placeList.clear()
                    placeAdapter.notifyDataSetChanged()
                }
            }
        }

        viewModel.placeLiveData.observe(viewLifecycleOwner, Observer {result->
            val places = result.getOrNull()
            if (places != null) {
                recyclerView.visibility = View.VISIBLE
                ivBg.visibility = View.GONE
                viewModel.placeList.clear()
                viewModel.placeList.addAll(places)
                placeAdapter.notifyDataSetChanged()
            } else {
                Toast.makeText(requireContext(), "未查询到任何地点", Toast.LENGTH_SHORT).show()
                result.exceptionOrNull()?.printStackTrace()
            }
        })
    }
}