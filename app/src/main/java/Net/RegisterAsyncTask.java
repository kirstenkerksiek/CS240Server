package Net;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import DataManagement.DataCache;
import Requests.RegisterRequest;
import Results.AllEventsResult;
import Results.AllPersonResult;
import Results.RegisterResult;

public class RegisterAsyncTask extends AsyncTask<RegisterRequest, Void, RegisterResult> { //pass in a regReq instead
    Context context;
    String name = "";

    public RegisterAsyncTask(String host, String port, Context context) {
        DataCache cache = DataCache.getInstance();
        cache.setServerHost(host);
        cache.setServerPort(port);
        this.context = context;
    }

    @Override
    protected RegisterResult doInBackground(RegisterRequest... registerRequests) {
        RegisterResult result;
        DataCache cache = DataCache.getInstance();
        ServerProxy proxy = new ServerProxy();
        result = proxy.register(cache.getServerHost(), cache.getServerPort(), registerRequests[0]); //switch to reg request
        cache.setRegisterResult(result);
        AllEventsResult eventsResult = proxy.events(cache.getServerHost(), cache.getServerPort(), result.getAuthtoken());
        AllPersonResult personsResult = proxy.persons(cache.getServerHost(), cache.getServerPort(), result.getAuthtoken());
        if (result.isSuccess()) {
            cache.setRegisterResult(result);
            cache.setEventsResultRegister(eventsResult);
            cache.setPersonsResultRegister(personsResult);
            name = personsResult.getData()[0].getfName() + " " + personsResult.getData()[0].getlName();
        }

        return result;
    }

    @Override
    protected void onPostExecute(RegisterResult registerResult) {
        super.onPostExecute(registerResult);
        DataCache cache = DataCache.getInstance();
        if (name.equals("")) {
            Toast.makeText(context,"unable to register",Toast.LENGTH_LONG).show();
        }
        else if(registerResult.isSuccess()) {
            Toast.makeText(context,"able to register: " + name,Toast.LENGTH_LONG).show();
        }
        else {
            Toast.makeText(context,"unable to register",Toast.LENGTH_LONG).show();
        }
    }
}
