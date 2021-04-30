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
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.home_fragment.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class HomeFragment: Fragment(R.layout.home_fragment) {



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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

      val sessionManager=SessionManager(activity);
       val userDetails=sessionManager.userDetailFromSession;
        val firstName= userDetails[SessionManager.KEY_FIRSTNAME];
        val lastName= userDetails[SessionManager.KEY_LASTNAME];
        tvName.text="${firstName}  ${lastName}"


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

        btnCheckState.setOnClickListener {
            //notificationManager.notify(NOTIFICATION_ID, notification)
            if (tvState.text.toString() == "Locked") {
                tvState.text = "Unlocked"
                ivLockedAndUnlocked.setImageResource(R.drawable.ic_locked_home)
            } else {
                tvState.text = "Locked"
                ivLockedAndUnlocked.setImageResource(R.drawable.ic_unlocked_home)
            }
        }


        super.onViewCreated(view, savedInstanceState)
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


