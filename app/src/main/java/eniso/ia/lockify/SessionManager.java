package eniso.ia.lockify;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;

public class SessionManager {
    SharedPreferences usersSession;
    SharedPreferences.Editor editor;
    Context context;

    private static final String IS_LOGIN = "IsLoggedIn";

    public static final String KEY_LASTNAME = "lastName";
    public static final String KEY_FIRSTNAME = "firstName";

    public SessionManager(Context _context) {
        context = _context;
        usersSession = _context.getSharedPreferences("userLoginSession", Context.MODE_PRIVATE);
        editor = usersSession.edit();
    }

    public void createLoginSession(String firstName, String lastName) {
        editor.putBoolean(IS_LOGIN, true);
        editor.putString(KEY_FIRSTNAME, firstName);
        editor.putString(KEY_LASTNAME, lastName);
        editor.apply();
    }

    public HashMap<String, String> getUserDetailFromSession() {
        HashMap<String, String> userData = new HashMap<>();
        userData.put(KEY_FIRSTNAME, usersSession.getString(KEY_FIRSTNAME, null));
        userData.put(KEY_LASTNAME, usersSession.getString(KEY_LASTNAME, null));
        return userData;
    }

    public boolean checkLogin() {
        return usersSession.getBoolean(IS_LOGIN, false);

    }

    public void logoutUserFromSession() {
        editor.clear();
        editor.commit();

    }

}
