package eniso.ia.lockify

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.home_fragment.*


class HomeFragment: Fragment(R.layout.home_fragment) {


    private lateinit var realtimeDatabase: DatabaseReference
    private lateinit var mContext: Context
    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        return super.onCreateView(inflater, container, savedInstanceState)

    }


    val CHANNEL_ID = "channelId"
    val CHANNEL_NAME = "channelName"
    val NOTIFICATION_ID = 0
    var getdata: ValueEventListener? = null


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        var checkState=true
      val sessionManager=SessionManager(activity);
       val userDetails=sessionManager.userDetailFromSession;
        val firstName= userDetails[SessionManager.KEY_FIRSTNAME];
        val lastName= userDetails[SessionManager.KEY_LASTNAME];
        tvName.text="${firstName}  ${lastName}"



         realtimeDatabase= FirebaseDatabase.getInstance().reference

        getdata = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if(dataSnapshot.exists())
                {
                    // Get Post object and use the values to update the UI
                    //val data = dataSnapshot.getValue(String::class.java)
                    val data = dataSnapshot.child("lock").getValue(String::class.java).toString()

                    if(data=="false")
                    {
                        tvStateLol.setTextColor(Color.parseColor("#1BDF30"))
                        ivCheckState.setImageResource(R.drawable.circle_home_2)
                        tvStateLol.text = "Unlocked"
                        ivLockedAndUnlocked.setImageResource(R.drawable.ic_unlock_new_2)
                        checkState=false
                    }
                    else if (data=="true")
                    {
                        ivCheckState.setImageResource(R.drawable.circle_home)
                        tvStateLol.setText("Locked")
                        tvStateLol.setTextColor(Color.parseColor("#FF2D2D"))
                        ivLockedAndUnlocked.setImageResource(R.drawable.ic_lock_new)
                        checkState=true
                    }
                    // ...
                }


            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        }
        realtimeDatabase.addValueEventListener(getdata as ValueEventListener)
       /* if(!checkState)
        {

                tvStateLol.setTextColor(Color.parseColor("#1BDF30"))
                ivCheckState.setImageResource(R.drawable.circle_home_2)
                tvStateLol.text = "Unlocked"
                ivLockedAndUnlocked.setImageResource(R.drawable.ic_unlock_new_2)
                checkState=false

        }
        else
        {
            ivCheckState.setImageResource(R.drawable.circle_home)
            tvStateLol.setText("Locked")
            tvStateLol.setTextColor(Color.parseColor("#FF2D2D"))
            ivLockedAndUnlocked.setImageResource(R.drawable.ic_lock_new)
            checkState=true
        }*/


        var notificationImage = true
        ivNotifications.setOnClickListener {
            if (notificationImage) {
                ivNotifications.setImageResource(R.drawable.ic_baseline_notifications_off_24);
                Toast.makeText(activity, "Notifications are off", Toast.LENGTH_LONG).show()
                notificationImage = false
            } else {
                ivNotifications.setImageResource(R.drawable.ic_notifications);
                Toast.makeText(activity, "Notifications are on", Toast.LENGTH_LONG).show()
                notificationImage = true
            }

        }

        ivCall.setOnClickListener {
            val uri = Uri.parse("tel:197")
            val appel = Intent(Intent.ACTION_DIAL, uri)
            startActivity(appel)
        }
        createNotificationChannel()
        // a pending intent allows other app to execute apiece of code from our app
        val intent = Intent(mContext, BottomNavMenuActivity::class.java)
        val pendingIntent = TaskStackBuilder.create(mContext).run {
            addNextIntentWithParentStack(intent)
            // flag when the pending intent exist we want to update the intent with new data
            getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
        }

        val notification =
            NotificationCompat.Builder(mContext, CHANNEL_ID).setContentTitle("Lock the door !")
                .setContentText("Apparently you didn't lock the door after you opened it ")
                .setSmallIcon(R.drawable.ic_baseline_lock_24)
                // how important this specific notification to other notifications you created
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .build()
        val notificationManager = NotificationManagerCompat.from(mContext)

        /*ivCheckState.setOnClickListener {

            //notificationManager.notify(NOTIFICATION_ID, notification)
            if (checkState) {
                tvStateLol.setTextColor(Color.parseColor("#1BDF30"))
                ivCheckState.setImageResource(R.drawable.circle_home_2)
                tvStateLol.text = "Unlocked"
                ivLockedAndUnlocked.setImageResource(R.drawable.ic_unlock_new_2)
                checkState=false
            } else {


                ivCheckState.setImageResource(R.drawable.circle_home)
                tvStateLol.setText("Locked")
                tvStateLol.setTextColor(Color.parseColor("#FF2D2D"))
                ivLockedAndUnlocked.setImageResource(R.drawable.ic_lock_new)
                checkState=true
            }
        }*/










        super.onViewCreated(view, savedInstanceState)
    }

    override fun onPause() {

        getdata?.let { realtimeDatabase.removeEventListener(it) }

        super.onPause()
    }

    fun createNotificationChannel() {
        //check if the phone is running on android oreo or greater
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                lightColor = Color.GREEN
                enableLights(true)
            }
            val manager =
                activity!!.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)

        }
    }




  /*  private fun retrievePersons() = CoroutineScope(Dispatchers.IO).launch {

        try {
            val currentuser = FirebaseAuth.getInstance().currentUser.uid
            val querySnapshot = personCollectionRef.get().await()
            val sb = StringBuilder()
            for(document in querySnapshot.documents) {
                if(document.id==currentuser) {
                    val person = document.toObject<Person>()
                    if(person!=null)
                    {
                        sb.append("${person.firstName}  ${person.lastName}")
                    }
                }

            }
            withContext(Dispatchers.Main) {
                tvName.text=sb.toString()
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(mContext, e.message, Toast.LENGTH_LONG).show()
            }
        }
    }*/


}


