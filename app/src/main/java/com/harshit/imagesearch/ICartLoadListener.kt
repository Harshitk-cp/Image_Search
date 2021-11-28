package com.harshit.imagesearch

interface ICartLoadListener {

    fun onCartLoadSuccess(cartModelList: List<CartModel?>?)

    fun onCartLoadFailed(message: String?)

}