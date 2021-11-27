package com.harshit.imagesearch

interface IProductLoadListener {
    fun onProductLoadSuccess(productModelList: List<ProductModel?>?)

    fun onProductLoadFailed(message: String?)
}