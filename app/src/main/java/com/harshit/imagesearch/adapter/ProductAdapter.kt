package com.harshit.imagesearch.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import butterknife.ButterKnife
import butterknife.Unbinder
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.harshit.imagesearch.R
import com.harshit.imagesearch.models.CartModel
import com.harshit.imagesearch.models.ProductModel
import java.lang.Exception


class productAdapter(private val context: Context, productModelList: List<ProductModel>) :
    RecyclerView.Adapter<productAdapter.MyProductViewHolder>() {
    private var productModelList: List<ProductModel> = productModelList

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyProductViewHolder {
        return MyProductViewHolder(
            LayoutInflater.from(context).inflate(R.layout.product_list_item_design, parent, false)
        )
    }


    override fun onBindViewHolder(holder: MyProductViewHolder, position: Int) {

        val currentItem = productModelList[position]
        Glide.with(holder.imgProduct).load(currentItem.imgUrl).into(holder.imgProduct)

        holder.btnAddToCart.setOnClickListener(View.OnClickListener {
            addToCart(productModelList[position])
        })





    }

    private fun addToCart(productModel: ProductModel) {
        val userFavs = FirebaseDatabase
            .getInstance()
            .getReference("cart")
            .child(FirebaseAuth.getInstance().currentUser!!.uid)
        userFavs.child(productModel.key!!)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        Toast.makeText(context, "Product already in cart", Toast.LENGTH_SHORT).show()
                    } else {
                        val cartModel = CartModel()
                        cartModel.key = productModel.key

                        cartModel.imgUrl = productModel.imgUrl
                        userFavs.child(productModel.key!!)
                            .setValue(cartModel)
                            .addOnSuccessListener { aVoid: Void? ->

                                    Toast.makeText(context, "Item added to cart", Toast.LENGTH_SHORT).show()
                            }
                            .addOnFailureListener { e: Exception ->
                                Toast.makeText(context, "Couldn't add to Cart", Toast.LENGTH_SHORT).show()

                            }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(context, "Database error", Toast.LENGTH_SHORT).show()
                }
            })
    }

    override fun getItemCount(): Int {
        return productModelList.size
    }

    inner class MyProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val imgProduct: ImageView = itemView.findViewById(R.id.imgProduct1)
        val btnAddToCart: Button = itemView.findViewById(R.id.btnAddToCart)



        var unbinder: Unbinder

        init {
            unbinder = ButterKnife.bind(this, itemView)
        }
    }

}
