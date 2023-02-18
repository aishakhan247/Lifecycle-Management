package com.example.lifecyclemanagement

import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

//Implement View.onClickListener to listen to button clicks. This means we have to override onClick().
class MainActivity : AppCompatActivity(), View.OnClickListener {
    //Create variables to hold the three strings
    private var mFullName: String? = null
    private var mFirstName: String? = null
    private var mLastName: String? = null
    private var mFilePathString: String? = null


    //Create variables for the UI elements that we need to control
    private var mButtonSubmit: Button? = null
    private var mButtonCamera: Button? = null
    private var mEtFirstName: EditText? = null
    private var mEtLastName: EditText? = null
    private var mImageView: ImageView? = null


    //Define a bitmap
    private var mThumbnailImage: Bitmap? = null

    //Define a global intent variable
    private var mDisplayIntent: Intent? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Get the buttons
        mButtonSubmit = findViewById<View>(R.id.submit) as Button
        mButtonCamera = findViewById<View>(R.id.takephoto) as Button

        //Say that this class itself contains the listener.
        mButtonSubmit!!.setOnClickListener(this)
        mButtonCamera!!.setOnClickListener(this)

        //Create the intent but don't start the activity yet
        mDisplayIntent = Intent(this, DisplayActivity::class.java)

        mImageView = findViewById<View>(R.id.profile) as ImageView
    }


    //Handle clicks for ALL buttons here
    override fun onClick(view: View) {
        when (view.id) {
            R.id.submit -> {

                mEtFirstName = findViewById<View>(R.id.firstname) as EditText
                mEtLastName = findViewById<View>(R.id.lastname) as EditText
                mFirstName = mEtFirstName!!.text.toString()
                mLastName = mEtLastName!!.text.toString()

                //Check if the EditText string is empty
                if (mFirstName.isNullOrBlank() || mLastName.isNullOrBlank()) {
                    //Complain that there's no text
                    Toast.makeText(this@MainActivity, "Enter a name first!", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    //Reward them for submitting their names
                    Toast.makeText(this@MainActivity, "Good job!", Toast.LENGTH_SHORT).show()

                    mFullName = mFirstName + " " + mLastName + " is logged in!"
                    mDisplayIntent!!.putExtra("N_DATA", mFullName)
                    startActivity(mDisplayIntent) //explicit intent
                }
            }
            R.id.takephoto -> {

                //The button press should open a camera
                val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                try{
                    cameraLauncher.launch(cameraIntent)
                }catch(ex:ActivityNotFoundException){
                    //Do something here
                }
            }
        }
    }
    private var cameraLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val extras = result.data!!.extras
            mThumbnailImage = extras!!["data"] as Bitmap?
            mImageView!!.setImageBitmap(mThumbnailImage)

            //Open a file and write to it
            if (isExternalStorageWritable) {
                mFilePathString = saveImage(mThumbnailImage)
                mDisplayIntent!!.putExtra("IMAGEPATH", mFilePathString)

            } else {
                Toast.makeText(this, "External storage not writable.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveImage(finalBitmap: Bitmap?): String {
        val root = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val myDir = File("$root/saved_images")
        myDir.mkdirs()
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val fname = "Thumbnail_$timeStamp.jpg"
        val file = File(myDir, fname)
        if (file.exists()) file.delete()


        try {
            val out = FileOutputStream(file)
            finalBitmap!!.compress(Bitmap.CompressFormat.JPEG, 90, out)
            out.flush()
            out.close()
            Toast.makeText(this, "file saved!", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return file.absolutePath
    }

    private val isExternalStorageWritable: Boolean
        get() {
            val state = Environment.getExternalStorageState()
            return Environment.MEDIA_MOUNTED == state
        }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("IMAGEPATH", mFilePathString)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        mFilePathString = savedInstanceState!!.getString("IMAGEPATH")
        val thumbnailImage = BitmapFactory.decodeFile(mFilePathString)
        mImageView!!.setImageBitmap(thumbnailImage)
    }
}