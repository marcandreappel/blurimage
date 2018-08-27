package `fun`.appel.blurimage

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.os.AsyncTask
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur
import android.widget.ImageView
import java.lang.ref.WeakReference


/**
 * BlurImage
 * @package     fun.appel.blurimage
 * @project     BlurImage
 * @author      Marc-André Appel <marc-andre@appel.fun>
 * @license     http://opensource.org/licenses/MIT MIT
 * @created     23/08/2018
 */

class BlurImage(private val context: Context)
{
    val blurredImage: Bitmap?
        get() = blur()

    private var source: Bitmap? = null
    private var radius = 08F
    private var async = false
    private var darken = false

    private val MAX_RADIUS = 25F
    private val MIN_RADIUS = 0F

    /**
     * Appliquer le flou à l'image avec tous les paramèteres définis
     */
    private fun blur(): Bitmap?
    {
        if (source == null)
        {
            return source
        }
        val width = Math.round(source!!.width.toFloat())
        val height = Math.round(source!!.height.toFloat())

        val input = Bitmap.createScaledBitmap(source!!, width, height, false)
        var output = Bitmap.createBitmap(input)

        val renderScript = RenderScript.create(context)
        val intrinsicBlur = ScriptIntrinsicBlur.create(renderScript, Element.U8_4(renderScript))

        val inputAllocation = Allocation.createFromBitmap(renderScript, input)
        val outputAllocation = Allocation.createFromBitmap(renderScript, output)

        intrinsicBlur.setRadius(radius)
        intrinsicBlur.setInput(inputAllocation)
        intrinsicBlur.forEach(outputAllocation)

        outputAllocation.copyTo(output)

        if (darken)
        {
            output = darkenBitmap(output)
        }
        return output
    }

    /**
     * Assombrir un peu l'image
     */
    private fun darkenBitmap(bitmap: Bitmap): Bitmap
    {
        val paint = Paint()
        val filter = LightingColorFilter(-0xBBBBBC, 0x66666666)

        paint.colorFilter = filter

        val canvas = Canvas(bitmap)
        canvas.drawBitmap(bitmap, 0F, 0F, paint)

        return bitmap
    }

    /**
     * Méthode de définition s'il faut assombrir l'image
     */
    fun darken(): BlurImage
    {
        this.darken = true

        return this
    }

    fun load(bitmap: Bitmap): BlurImage
    {
        source = bitmap

        return this
    }

    fun load(resource: Int): BlurImage
    {
        source = BitmapFactory.decodeResource(context.resources, resource)

        return this
    }

    fun radius(radius: Float): BlurImage
    {
        this.radius = if (radius < MAX_RADIUS && radius > MIN_RADIUS)
        {
            radius
        }
        else
        {
            MAX_RADIUS
        }
        return this
    }

    fun async(async: Boolean): BlurImage
    {
        this.async = async

        return this
    }

    fun into(imageView: ImageView)
    {
        if (async)
        {
            AsyncBlur(imageView).execute()
        }
        else
        {
            try
            {
                imageView.setImageBitmap(blur())
            }
            catch (exception: NullPointerException)
            {
                exception.printStackTrace()
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    private inner class AsyncBlur(image: ImageView) : AsyncTask<Void, Void, Bitmap>()
    {
        private val weakReference: WeakReference<ImageView> = WeakReference(image)

        override fun doInBackground(vararg voids: Void): Bitmap?
        {
            return blur()
        }

        override fun onPostExecute(bitmap: Bitmap?)
        {
            val imageView = weakReference.get()

            if (imageView != null && bitmap != null)
            {
                imageView.setImageBitmap(bitmap)
            }
        }
    }

    companion object
    {
        fun with(context: Context): BlurImage
        {
            return BlurImage(context)
        }
    }
}
