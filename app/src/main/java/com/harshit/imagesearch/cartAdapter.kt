package com.harshit.imagesearch

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.core.graphics.drawable.toBitmap
import androidx.recyclerview.widget.RecyclerView
import butterknife.ButterKnife
import butterknife.Unbinder
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.OnSuccessListener
import com.google.common.eventbus.EventBus
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.lang.Exception
import java.net.CookieHandler.getDefault

class cartAdapter(private val context: Context, cartModelList: List<CartModel>) :
    RecyclerView.Adapter<cartAdapter.MyCartViewHolder>() {
    private var cartModelList: List<CartModel> = cartModelList

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyCartViewHolder {
        return MyCartViewHolder(
            LayoutInflater.from(context).inflate(R.layout.cart_list_item_design, parent, false)
        )
    }


    override fun onBindViewHolder(holder: MyCartViewHolder, position: Int) {

        val currentItem = cartModelList[position]
        Glide.with(holder.imgProductCart).load(currentItem.imgUrl).into(holder.imgProductCart)

        holder.btnRemove.setOnClickListener(View.OnClickListener {
            removeFromCart(cartModelList[position])
            Toast.makeText(context, "Item removed from cart", Toast.LENGTH_SHORT).show()
        })


        holder.btnTryOn.setOnClickListener {
            val photo = currentItem.imgUrl
            val tryOnIntent = Intent(context, TryOnActivity::class.java)
            tryOnIntent.putExtra("TryOnImage", photo)
            context.startActivity(tryOnIntent)
        }


    }

    private fun removeFromCart(cartModel: CartModel) {
        FirebaseDatabase.getInstance()
            .getReference("cart")
            .child(FirebaseAuth.getInstance().currentUser!!.uid)
            .child(cartModel.key!!)
            .removeValue()
    }



    override fun getItemCount(): Int {
        return cartModelList.size
    }

    inner class MyCartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {


        val imgProductCart: ImageView = itemView.findViewById(R.id.imgProductCart)
        val btnTryOn: Button = itemView.findViewById(R.id.btnTryOn)
        val btnRemove: Button  = itemView.findViewById(R.id.btnRemove)


        var unbinder: Unbinder = ButterKnife.bind(this, itemView)

    }

}