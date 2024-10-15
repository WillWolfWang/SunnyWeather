package com.will.sunnyweather.pagetest

import android.util.Log
import androidx.paging.PositionalDataSource
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class WanDataSource: PositionalDataSource<WanAndroidModel>() {
    companion object {
        const val PAGE_SIZE = 8
    }

    // 第一次加载时调用
    override fun loadInitial(
        params: LoadInitialParams,
        callback: LoadInitialCallback<WanAndroidModel>
    ) {
        val start = 0
        RetrofitClient.getWanAndroidApi().getWanAndroidData(start, PAGE_SIZE)
            .enqueue(object : Callback<WanResult> {
                override fun onResponse(call: Call<WanResult>, response: Response<WanResult>) {
                    if (response.body() != null) {
//                        Log.e("WillWolf", "onResponse-->" + response.body())
                        callback.onResult(response.body()!!.data.datas, response.body()!!.data.offset, response.body()!!.data.total)
                    }
                }

                override fun onFailure(call: Call<WanResult>, response: Throwable) {

                }

            })
    }

    // 上拉加载更多时调用
    override fun loadRange(params: LoadRangeParams, callback: LoadRangeCallback<WanAndroidModel>) {
        RetrofitClient.getWanAndroidApi().getWanAndroidData(params.startPosition, PAGE_SIZE)
            .enqueue(object : Callback<WanResult> {
                override fun onResponse(
                    call: Call<WanResult>,
                    response: Response<WanResult>
                ) {
                   if (response.body() != null) {
                       callback.onResult(response.body()!!.data.datas)
                   }
                }

                override fun onFailure(call: Call<WanResult>, e: Throwable) {

                }

            })
    }
}