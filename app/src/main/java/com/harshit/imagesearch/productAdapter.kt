package com.harshit.imagesearch

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.Unbinder
import com.bumptech.glide.Glide
import java.lang.StringBuilder


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





    }

    override fun getItemCount(): Int {
        return productModelList.size
    }

    inner class MyProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val imgProduct: ImageView = itemView.findViewById(R.id.imgProduct1)


        var unbinder: Unbinder

        init {
            unbinder = ButterKnife.bind(this, itemView)
        }
    }

}
