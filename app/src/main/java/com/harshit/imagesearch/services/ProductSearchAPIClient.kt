package com.harshit.imagesearch.services

import android.content.Context
import android.graphics.Bitmap
import android.util.Base64
import android.util.Log
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.TaskCompletionSource
import com.google.android.gms.tasks.Tasks
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.harshit.imagesearch.ProductSearchActivity
import com.harshit.imagesearch.ProductSearchResult
import com.harshit.imagesearch.SearchResultResponse
import org.json.JSONObject
import java.io.ByteArrayOutputStream





class ProductSearchAPIClient(context: Context){

    companion object {
        const val VISION_API_PRODUCT_MAX_RESULT = 5

        // Fill in the const below with your project info.
        const val VISION_API_URL = "https://vision.googleapis.com/v1"

        const val VISION_API_PROJECT_ID = "searchndtry"
        const val VISION_API_LOCATION_ID = "asia-east1"
        const val VISION_API_PRODUCT_SET_ID = "product_set0"
        const val VISION_API_KEY = "AIzaSyA-OWenpxVC-YX6MCwexLGLxotAkR6F9Zo"
    }

    // Instantiate the RequestQueue.
    private val requestQueue = Volley.newRequestQueue(context)

    /**
     * Convert an image to its Base64 representation
     */
    private fun convertBitmapToBase64(bitmap: Bitmap): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
        val byteArray: ByteArray = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }

    /**
     * Use Product Search API to search with the given query image
     * Call the projects.locations.images.annotate endpoint.
     */
    fun annotateImage(image: Bitmap): Task<List<ProductSearchResult>> {
        // Initialization to use the Task API
        val apiSource = TaskCompletionSource<List<ProductSearchResult>>()
        val apiTask = apiSource.task

        // Convert the query image to its Base64 representation to call the Product Search API.
        val base64: String = convertBitmapToBase64(image)

        // Craft the request body JSON.
        val requestJson = """
            {
  "requests": [
    {
      "image": {
        "content": "base64-encoded-image"
      },
      "features": [
        {
          "maxResults": 10,
          "type": "WEB_DETECTION"
        },
      ]
    }
  ]
}
        """.trimIndent()

        // Add a new request to the queue

        requestQueue.add(object :
            JsonObjectRequest(
                Method.POST,
                "POST https://vision.googleapis.com/v1/images:annotate",
                JSONObject(requestJson),
                { response ->
                    // Parse the API JSON response to a list of ProductSearchResult object.
                    val productList = apiResponseToObject(response)

                    // Loop through the product list and create tasks to load reference images.
                    // We will call the projects.locations.products.referenceImages.get endpoint
                    // for each product.
                    val fetchReferenceImageTasks = productList.map { fetchReferenceImage(it) }

                    // When all reference image fetches have completed,
                    // return the ProductSearchResult list
                    Tasks.whenAllComplete(fetchReferenceImageTasks)
                        // Return the list of ProductSearchResult with product images' HTTP URLs.
                        .addOnSuccessListener { apiSource.setResult(productList) }
                        // An error occurred so returns it to the caller.
                        .addOnFailureListener { apiSource.setException(it) }
                },
                // Return the error
                { error -> apiSource.setException(error) }
            ) {
            override fun getBodyContentType() = "application/json"
        }.apply {
            setShouldCache(false)
        })

        return apiTask
    }

    /**
     * Fetch and transform product image
     * Call the projects.locations.products.referenceImages.get endpoint
     */
    private fun fetchReferenceImage(searchResult: ProductSearchResult): Task<ProductSearchResult> {
        // Initialization to use the Task API
        val apiSource = TaskCompletionSource<ProductSearchResult>()
        val apiTask = apiSource.task

        // Craft the API request to get details about the reference image of the product
        val stringRequest = object : StringRequest(
            Method.GET,
            "$VISION_API_URL/${searchResult.imageId}?key=$VISION_API_KEY",
            { response ->
                val responseJson = JSONObject(response)
                val gcsUri = responseJson.getString("uri")

                // Convert the GCS URL to its HTTPS representation
                val httpUri = gcsUri.replace("gs://", "https://storage.googleapis.com/")

                // Save the HTTPS URL to the search result object
                searchResult.imageUri = httpUri

                // Invoke the listener to continue with processing the API response (eg. show on UI)
                apiSource.setResult(searchResult)
            },
            { error -> apiSource.setException(error) }
        ) {

            override fun getBodyContentType(): String {
                return "application/json; charset=utf-8"
            }
        }
        Log.d(ProductSearchActivity.TAG, "Sending API request.")

        // Add the request to the RequestQueue.
        requestQueue.add(stringRequest)

        return apiTask
    }

    /**
     * Convert the JSON API response to a list of ProductSearchResult objects
     */
    @Throws(JsonSyntaxException::class)
    private fun apiResponseToObject(response: JSONObject): List<ProductSearchResult> {
        // Parse response JSON string into objects.
        val productSearchResults = mutableListOf<ProductSearchResult>()

        val searchResult =
            Gson().fromJson(response.toString(), SearchResultResponse::class.java)
        Log.d(ProductSearchActivity.TAG, "results: $searchResult")
        searchResult.responses?.get(0)?.productSearchResults?.results?.forEach { result ->
            productSearchResults.add(
                ProductSearchResult(
                    result.image,
                    result.score,
                    result.product.productLabels.joinToString { "${it.key} - ${it.value}" },
                    result.product.name
                )
            )
        }

        return productSearchResults
    }
}