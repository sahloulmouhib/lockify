package eniso.ia.lockify

import android.app.ProgressDialog
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_edit_profile.*
import kotlinx.android.synthetic.main.home_fragment.*
import java.util.*

class EditProfileActivity : AppCompatActivity() {
    lateinit var auth: FirebaseAuth
    lateinit var storage: FirebaseStorage
    lateinit var storageReference:StorageReference
    lateinit var imageUri:Uri

    val personCollectionRef= Firebase.firestore.collection("persons")



    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)
        auth = FirebaseAuth.getInstance()
         storage=FirebaseStorage.getInstance()
         storageReference=storage.reference
        downloadImage()
        btnUpdateName.setOnClickListener {
            val firstName=etFirstNameUpdated.editText?.text.toString()
            val lastName=etLastNameUpdated.editText?.text.toString()
            if(firstName.isNotEmpty() &&  lastName.isNotEmpty())
            {
                val currentUser = auth.currentUser.uid
                val name = getNewName()
                personCollectionRef.document(currentUser)
                    // setOptions. merge will assure taht the non updated field won't get deleted
                    .set(name, SetOptions.merge())
                    .addOnSuccessListener { Toast.makeText(this, "You successfully changed your name ", Toast.LENGTH_SHORT).show()
                        val sessionManager=SessionManager(this);
                        sessionManager.createLoginSession(name["firstName"] as String?,name["lastName"]as String)}
                    .addOnFailureListener { e -> Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show() }
            }
            else
            {
                Toast.makeText(this, "Fill in the blanks first ", Toast.LENGTH_SHORT).show()
            }



        }

        ivProfileImage.setOnClickListener {
          choosePicture()
        }
    }

    private fun getOldName(): Person
    {
        val sessionManager=SessionManager(this);
        val userDetails=sessionManager.userDetailFromSession;
        val firstName= userDetails[SessionManager.KEY_FIRSTNAME];
        val lastName= userDetails[SessionManager.KEY_LASTNAME];
        return Person("aziz","ijoui")
    }
    private fun getNewName(): Map<String,Any>
    {
        val sessionManager=SessionManager(this);
        val userDetails=sessionManager.userDetailFromSession;
        val oldFirstName= userDetails[SessionManager.KEY_FIRSTNAME];
        val oldLastName= userDetails[SessionManager.KEY_LASTNAME];
        val firstName=etFirstNameUpdated.editText?.text.toString()
        val lastName=etLastNameUpdated.editText?.text.toString()
        val map= mutableMapOf<String,Any>()
        if(firstName.isNotEmpty())
        {
            map["firstName"]=firstName
        }
        if(lastName.isNotEmpty())
        {
            map["lastName"]=lastName
        }

        return map
    }
    private fun  choosePicture()
    {
        val intent= Intent()
        intent.type = "image/*"
        intent.action = (Intent.ACTION_GET_CONTENT)
        startActivityForResult(intent,1)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
            if(requestCode==1 && resultCode==RESULT_OK  && data!=null && data.data !=null)
            {
                imageUri= data.data!!
                ivProfileImage.setImageURI(imageUri)
                uploadPicture();
            }

        super.onActivityResult(requestCode, resultCode, data)
    }

   private fun uploadPicture()
    {
       val pd=ProgressDialog(this@EditProfileActivity)
        pd.setTitle("Uploading image.....")
        pd.show()
        val  randomKey= UUID.randomUUID().toString()

        val fileRef = storageReference.child("images/${auth.currentUser.uid}")
       fileRef.putFile(imageUri).addOnSuccessListener {
            pd.dismiss()
            Snackbar.make(findViewById(android.R.id.content),"Image uploaded",Snackbar.LENGTH_LONG).show()
           downloadImage()
        }.addOnFailureListener{
        taskSnapshot ->
            pd.dismiss()
            Toast.makeText(this,"Failed to upload",Toast.LENGTH_SHORT).show()
         }.addOnProgressListener {
                val progressPercent=(100.00* it.bytesTransferred/it.totalByteCount).toInt()
            pd.setMessage("Percentage:${progressPercent} %")

        }
        //: if request.auth != null

    }

  /*  private fun downloadImage()
    {
        val maxDownlaodsize=5L*1024*1024
        val bytes=storageReference.child("images/${auth.currentUser.uid}").getBytes(maxDownlaodsize).addOnSuccessListener {
            val bmp= BitmapFactory.decodeByteArray(it, 0, it.size)
                ivProfileImage.setImageBitmap(bmp)
            Snackbar.make(findViewById(android.R.id.content),"Image downloaded",Snackbar.LENGTH_LONG).show()
        }.addOnFailureListener{
                taskSnapshot ->
            //pd.dismiss()
            //Toast.makeText(this,"Failed to upload",Toast.LENGTH_SHORT).show()
        }

    }*/
    private fun downloadImage()
    {
       /* val bytes=storageReference.child("images/${auth.currentUser.uid}")
        Glide.with(this /* context */)
            .load(bytes)
            .into(ivProfileImage)
        Snackbar.make(findViewById(android.R.id.content),"noiiiice",Snackbar.LENGTH_LONG).show()*/

       val image= storageReference.child("images/${auth.currentUser.uid}").downloadUrl.addOnSuccessListener {

           Glide.with(this)
               .load(it)
               .into(ivProfileImage)
               .onlyRetrieveFromCache(true)
           //Snackbar.make(findViewById(android.R.id.content),"noiiiice",Snackbar.LENGTH_LONG).show()
        }.addOnFailureListener {
           ivProfileImage.setImageResource(R.drawable.profile)
        }
    }
}
