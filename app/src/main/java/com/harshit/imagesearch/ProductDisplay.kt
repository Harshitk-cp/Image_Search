package com.harshit.imagesearch

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.ButterKnife
import com.bumptech.glide.Glide
import com.google.firebase.database.*
import java.util.ArrayList

class ProductDisplay : AppCompatActivity(), IProductLoadListener {

    lateinit var rvProductMain: RecyclerView
    lateinit var productLoadListener: IProductLoadListener
    lateinit var iv_query_image: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_display)

        rvProductMain = findViewById(R.id.rvProductMain)
        iv_query_image = findViewById(R.id.iv_query_image)

        init()
        loadProductFromFirebase()

        Glide.with(iv_query_image).load("https://i.postimg.cc/Kc0G2y0R/cappuccino-2-tcm87-29616-w1024-n.jpg").into(iv_query_image)

    }

    private fun init() {
        ButterKnife.bind(this)
        productLoadListener  = this
        val layoutManager = GridLayoutManager(this, 2)
        rvProductMain.layoutManager = layoutManager
        rvProductMain.addItemDecoration(DividerItemDecoration(this, layoutManager.orientation))
    }

    private fun loadProductFromFirebase(){
        val coffees: MutableList<ProductModel> = ArrayList<ProductModel>()

        val instance = FirebaseDatabase.getInstance()
        val product = instance.getReference("product")

        val listener: ValueEventListener = object : ValueEventListener {
            override fun onDataChange(collection: DataSnapshot) {
                if (collection.exists()) {
                    for (ProductItem in collection.children) {
                        val key = ProductItem.key
                        val name = ProductItem.child("name").getValue(String::class.java)
                        val imgUrl = ProductItem.child("imgUrl").getValue(
                            String::class.java
                        )
                        val coffee = ProductModel(key, name,  imgUrl)
                        coffees.add(coffee)
                    }
                    productLoadListener.onProductLoadSuccess(coffees)
                } else {
                    // TODO: Just directly show error toast
                    productLoadListener.onProductLoadFailed("Error: Firebase menu list not found.")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // TODO: Just directly show error toast
                productLoadListener.onProductLoadFailed(error.message)
            }
        }

        product.addListenerForSingleValueEvent(listener)
    }

    override fun onProductLoadSuccess(productModelList: List<ProductModel?>?) {
        val productAdapter = productAdapter(this, productModelList as List<ProductModel>)
        rvProductMain.adapter = productAdapter
    }


    override fun onProductLoadFailed(message: String?) {
        Toast.makeText(this, "error", Toast.LENGTH_SHORT).show()
    }

}