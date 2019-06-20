package com.techticz.app.repo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import android.content.Context
import android.content.ContextWrapper
import com.techticz.networking.model.DataSource
import com.techticz.networking.model.Resource
import com.techticz.networking.model.Status
import com.techticz.app.base.BaseDIRepository
import timber.log.Timber
import javax.inject.Inject
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.media.MediaScannerConnection
import android.os.Environment
import android.text.TextUtils
import android.util.Log
import com.cloudinary.Cloudinary
import com.techticz.app.constants.CloudinaryConstants
import com.techticz.app.model.ImageResponse
import com.techticz.app.util.ImageCache
import com.techticz.app.util.RecyclingBitmapDrawable
import com.techticz.app.util.Utils
import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import java.util.*
import javax.inject.Singleton


/**
 * Created by YATRAONLINE\gyanendra.sirohi on 31/8/18.
 */
@Singleton
class ImageRepository @Inject constructor(val context:Context) : BaseDIRepository() {
    private val IMAGE_CACHE_DIR = "DietCalendar"

    init {
        Timber.d("Injecting:" + this)
        initImageCache()
    }


    private lateinit var mImageCache: ImageCache

    public fun initImageCache() {
        val cacheParams = ImageCache.ImageCacheParams(context, IMAGE_CACHE_DIR)

        cacheParams.setMemCacheSizePercent(0.25f) // Set memory cache to 25% of app memory
        mImageCache = ImageCache.getInstance(null, cacheParams)
    }

    fun fetchImageResponse(url: String?): LiveData<Resource<ImageResponse>> {
        Timber.d("IMAGE","Fetching image :"+url)
        var dummyRes = ImageResponse(url,"",null)
        var resource = Resource<ImageResponse>(Status.LOADING, dummyRes, "Loading Image:" + url, DataSource.REMOTE)
        var live : MediatorLiveData<Resource<ImageResponse>> = MediatorLiveData<Resource<ImageResponse>>()
        live.value = resource

        var bitmap:Bitmap? = null
        var status = Status.ERROR
        var source = DataSource.LOCAL

        try {
            bitmap = fetchBitmapFromMemCache(url)
            if (bitmap != null) {
                status = Status.SUCCESS
                source = DataSource.LOCAL
                updateLiveImageData(url,bitmap,live,source,status)
            } else {
                appExecutors.diskIO().execute({
                    bitmap = fetchBitmapFromDiskCache(url)
                    if (bitmap != null) {
                        status = Status.SUCCESS
                        source = DataSource.LOCAL
                        updateLiveImageData(url,bitmap,live,source,status)
                    } else {
                        if(url!!.contains("com.techticz.dietcalendar")){
                            bitmap = fetchBitmapFromDevice(url)
                            if (bitmap != null) {
                                addBitmapToCache(bitmap, url)
                                status = Status.SUCCESS
                                source = DataSource.REMOTE
                                updateLiveImageData(url, bitmap, live, source, status)
                            } else {
                                status = Status.EMPTY
                                source = DataSource.REMOTE
                                updateLiveImageData(url, bitmap, live, source, status)
                            }
                        } else {
                            appExecutors.networkIO().execute({
                                bitmap = fetchImageFromNetwork(url)
                                if (bitmap != null) {
                                    addBitmapToCache(bitmap, url)
                                    status = Status.SUCCESS
                                    source = DataSource.REMOTE
                                    updateLiveImageData(url, bitmap, live, source, status)
                                } else {
                                    status = Status.EMPTY
                                    source = DataSource.REMOTE
                                    updateLiveImageData(url, bitmap, live, source, status)
                                }
                            })
                        }

                    }
                })
            }
        } catch (e:Exception){
            e.printStackTrace()
        } finally {

        }

    return live
    }

    private fun fetchBitmapFromDevice(url: String): Bitmap? {
        Timber.d("Fetching Image from device:"+url)
        var sd: File = Environment.getExternalStorageDirectory()
        var image: File = File(url);
        var bmOptions: BitmapFactory.Options = BitmapFactory.Options();
        try {
            var bitmap = BitmapFactory.decodeFile(image.getAbsolutePath(), bmOptions);
            return bitmap
        } catch (e:java.lang.Exception){
            e.printStackTrace()
            return null
        }
    }

    private fun updateLiveImageData(url:String?,bitmap:Bitmap?,live: MediatorLiveData<Resource<ImageResponse>>, source: DataSource, status: Status) {
        appExecutors.mainThread().execute({
            Timber.d("IMAGE","Updating live image:"+url)
            var imageResp = ImageResponse(url,"",bitmap)
            var resource = Resource<ImageResponse>(status, imageResp, "Loading Image:"+status.name+"->" + url,source )
            live.value = resource
        })
    }

    private fun addBitmapToCache(bitmap: Bitmap?, url: String?) {
        if (bitmap != null) {
            var drawable:BitmapDrawable
            if (Utils.hasHoneycomb()) {
                // Running on Honeycomb or newer, so wrap in a standard BitmapDrawable
                drawable = BitmapDrawable(context?.resources, bitmap)
            } else {
                // Running on Gingerbread or older, so wrap in a RecyclingBitmapDrawable
                // which will recycle automagically
                drawable = RecyclingBitmapDrawable(context?.resources, bitmap)
            }

            if (mImageCache != null) {
                mImageCache.addBitmapToCache(url, drawable)
            }
        }
    }

    private fun fetchBitmapFromDiskCache(key: String?): Bitmap? {
        Timber.d("IMAGE","Fetching image from DiskCache:"+key)
        return mImageCache?.getBitmapFromDiskCache(key);
    }

    private fun fetchBitmapFromMemCache(key: String?): Bitmap? {
        Timber.d("IMAGE","Fetching image from MemCache:"+key)
        return mImageCache?.getBitmapFromMemCache(key)?.bitmap;
    }


    interface Callback {
        fun onImageUploaded(url: String)
    }

    fun saveImageOnDevice(myBitmap: Bitmap,fileName:String):String {
        val wrapper = ContextWrapper(context)

        // Initializing a new file
        // The bellow line return a directory in internal storage
        var file = wrapper.getDir("images", Context.MODE_PRIVATE)


        // Create a file to save the image
        file = File(file, fileName+"${UUID.randomUUID()}.jpg")

        try {
            // Get the file output stream
            val stream: OutputStream = FileOutputStream(file)

            // Compress bitmap
            myBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)

            // Flush the stream
            stream.flush()

            // Close stream
            stream.close()
        } catch (e: IOException){ // Catch the exception
            e.printStackTrace()
        }

        // Return the saved image uri
        return file.absolutePath
    }

        fun fetchImageFromNetwork(servingUrl:String?):Bitmap?{
    Timber.d("IMAGE","Fetching image from network:"+servingUrl)
    val imageURL: URL
    try {
        if (!TextUtils.isEmpty(servingUrl)) {
            imageURL = URL(servingUrl)
        } else {
            imageURL = URL("https://www.google.co.in/url?sa=i&source=images&cd=&cad=rja&uact=8&ved=2ahUKEwiarLvBocreAhXGT30KHYKoD9oQjRx6BAgBEAU&url=http%3A%2F%2Fchittagongit.com%2Ficon%2Ficon-for-food-20.html&psig=AOvVaw3-5kk1QtqHPqofO83xRTA6&ust=1541953942815942")
        }

        var connection: HttpURLConnection? = null
        connection = imageURL.openConnection() as HttpURLConnection
        connection!!.setRequestProperty("serving-url", servingUrl)
        connection!!.doInput = true
        connection!!.connect()
        val inputStream = connection!!.inputStream
        val bis = BufferedInputStream(inputStream)
        val bitmap = BitmapFactory.decodeStream(bis)
        bis.close()
        inputStream.close()
        return bitmap
    } catch(e:Exception){
        e.printStackTrace()
    }
    Timber.d("IMAGE","Fetching network image Successful:"+servingUrl)
        return null
    }

fun uploadImage(bitmap: Bitmap, imageName: String):String?{
    if (bitmap == null) return null
    val config = HashMap<String,String>()
    config.put("cloud_name", CloudinaryConstants.CLOUDINARY_CLOUD_NAME)
    config.put("api_key", CloudinaryConstants.CLOUDINARY_API_KEY)
    config.put("api_secret", CloudinaryConstants.CLOUDINARY_API_SECRET)
    val cloudinary = Cloudinary(config)

    val bos = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.JPEG, 100 /*ignored for PNG*/, bos)
    val bitmapdata = bos.toByteArray()
    val bs = ByteArrayInputStream(bitmapdata)

    var reqMap = HashMap<String,String>()
    reqMap.put("public_id",imageName)
    try {
        cloudinary.uploader().upload(bs,reqMap)
        val imageUrl = cloudinary.url().generate(imageName + ".jpg")
        return imageUrl
    } catch (e: IOException) {
        e.printStackTrace()
    }
    return null
}

}