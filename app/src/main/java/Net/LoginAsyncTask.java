package Net;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import DataManagement.DataCache;
import Requests.LoginRequest;
import Results.AllEventsResult;
import Results.AllPersonResult;
import Results.LoginResult;

public class LoginAsyncTask extends AsyncTask<LoginRequest, Void, LoginResult>  {
    Context context;
    String name = "";

    public LoginAsyncTask(String host, String port, Context context) {
        DataCache cache = DataCache.getInstance();
        cache.setServerHost(host);
        cache.setServerPort(port);
        this.context = context;
    }

    @Override
    protected LoginResult doInBackground(LoginRequest... loginRequests) {
        DataCache cache = DataCache.getInstance();
        ServerProxy proxy = new ServerProxy();
        LoginResult result = proxy.login(cache.getServerHost(), cache.getServerPort(), loginRequests[0]); //switch to login request
        AllEventsResult eventsResult = proxy.events(cache.getServerHost(), cache.getServerPort(), result.getAuthtoken());
        AllPersonResult personsResult = proxy.persons(cache.getServerHost(), cache.getServerPort(), result.getAuthtoken());
        if(result.isSuccess()) {
            cache.setLoginResult(result);
            cache.setEventsResultLogin(eventsResult);
            cache.setPersonsResultLogin(personsResult);
            cache.setLoggedIn(true);
            name = personsResult.getData()[0].getfName() + " " + personsResult.getData()[0].getlName();
        }
        return result;
    }

    @Override
    protected void onPostExecute(LoginResult loginResult) {
        super.onPostExecute(loginResult);
        if (name.equals("")) {
            Toast.makeText(context,"server error",Toast.LENGTH_LONG).show();
        }
        else if(loginResult.isSuccess()) {
            DataCache cache = DataCache.getInstance();
            Toast.makeText(context,"login successful: " + name,Toast.LENGTH_LONG).show();
            cache.getMain().navigateToMap();
        }
        else {
            Toast.makeText(context,"unable to login",Toast.LENGTH_LONG).show();
        }
    }
}
