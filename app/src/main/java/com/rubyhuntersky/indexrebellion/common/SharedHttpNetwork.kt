package com.rubyhuntersky.indexrebellion.common

import android.util.Log
import com.rubyhuntersky.stockcatalog.HttpNetwork
import com.rubyhuntersky.stockcatalog.HttpNetworkRequest
import com.rubyhuntersky.stockcatalog.HttpNetworkResponse
import okhttp3.OkHttpClient
import okhttp3.Request

object SharedHttpNetwork : HttpNetwork {

    override fun request(request: HttpNetworkRequest): HttpNetworkResponse {
        Log.d(this.javaClass.simpleName, "REQUEST: $request")
        val okRequest = Request.Builder().url(request.url).build()
        val response = try {
            val okResponse = okClient.newCall(okRequest).execute()
            HttpNetworkResponse.Text(
                url = request.url,
                text = okResponse.body()!!.string(),
                httpCode = okResponse.code()
            )
        } catch (t: Throwable) {
            HttpNetworkResponse.ConnectionError(request.url, t)
        }
        Log.d(this.javaClass.simpleName, "RESPONSE: $response")
        return response
    }

    private val okClient = OkHttpClient()
}
