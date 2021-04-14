package Net;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;

import DataManagement.DataCache;
import Requests.LoginRequest;
import Requests.RegisterRequest;
import Results.AllEventsResult;
import Results.AllPersonResult;
import Results.LoginResult;
import Results.RegisterResult;

//10.0.2.2 for emulator

public class ServerProxy {
    DataCache cache = DataCache.getInstance();

    public static void run() {
        DataCache cache = DataCache.getInstance();
        String serverHost = cache.getServerHost();
        String serverPort = cache.getServerPort();
        //persons(serverHost, serverPort);
        //events(serverHost, serverPort);
    }

    public static AllPersonResult persons(String serverHost, String serverPort, String authToken) {

        // This method shows how to send a GET request to a server

        try {
            URL url = new URL("http://" + serverHost + ":" + serverPort + "/person");
            HttpURLConnection http = (HttpURLConnection)url.openConnection();

            // Specify that we are sending an HTTP GET request
            http.setRequestMethod("GET");
            // Indicate that this request will not contain an HTTP request body
            http.setDoOutput(false);

            // Add an auth token to the request in the HTTP "Authorization" header

            http.addRequestProperty("Authorization", authToken);

            // Specify that we would like to receive the server's response in JSON
            // format by putting an HTTP "Accept" header on the request (this is not
            // necessary because our server only returns JSON responses, but it
            // provides one more example of how to add a header to an HTTP request).

            http.addRequestProperty("Accept", "application/json"); //TODO

            // Connect to the server and send the HTTP request
            http.connect();
            Gson gson = new Gson();
            // By the time we get here, the HTTP response has been received from the server.
            // Check to make sure that the HTTP response from the server contains a 200
            // status code, which means "success".  Treat anything else as a failure.
            if (http.getResponseCode() == HttpURLConnection.HTTP_OK) {

                InputStream respBody = http.getInputStream();
                // Extract JSON data from the HTTP response body
                String respData = readString(respBody);
                // Display the JSON data returned from the server
                System.out.println(respData);
                Reader reader = new InputStreamReader(respBody);
                AllPersonResult result = gson.fromJson(respData, AllPersonResult.class);

                System.out.println("persons successful.");

                return result;
            }
            else {
                // The HTTP response status code indicates an error
                // occurred, so print out the message from the HTTP response
                System.out.println("ERROR: " + http.getResponseMessage());
                InputStream respBody = http.getInputStream();
                String respData = readString(respBody);
                // Display the JSON data returned from the server
                System.out.println(respData);
                Reader reader = new InputStreamReader(respBody);
                AllPersonResult result = gson.fromJson(respData, AllPersonResult.class);

                System.out.println("persons failed.");
                return result;
            }
        }
        catch (IOException e) {
            // An exception was thrown, so display the exception's stack trace
            e.printStackTrace();
        }
        return new AllPersonResult();
    }

    public static AllEventsResult events(String serverHost, String serverPort, String authToken) {

        // This method shows how to send a GET request to a server

        try {
            URL url = new URL("http://" + serverHost + ":" + serverPort + "/event");

            HttpURLConnection http = (HttpURLConnection)url.openConnection();

            http.setRequestMethod("GET");
            http.setDoOutput(false);

            http.addRequestProperty("Authorization", authToken);
            http.addRequestProperty("Accept", "application/json");
            Gson gson = new Gson();

            http.connect();
            if (http.getResponseCode() == HttpURLConnection.HTTP_OK) {

                InputStream respBody = http.getInputStream();
                // Extract JSON data from the HTTP response body
                String respData = readString(respBody);
                // Display the JSON data returned from the server
                System.out.println(respData);
                Reader reader = new InputStreamReader(respBody);
                AllEventsResult result = gson.fromJson(respData, AllEventsResult.class);

                System.out.println("events successful.");

                return result;
            }
            else {
                System.out.println("ERROR: " + http.getResponseMessage());
                InputStream respBody = http.getInputStream();
                String respData = readString(respBody);
                // Display the JSON data returned from the server
                System.out.println(respData);
                Reader reader = new InputStreamReader(respBody);
                AllEventsResult result = gson.fromJson(respData, AllEventsResult.class);

                System.out.println("events failed.");
                return result;
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return new AllEventsResult();
    }


    public static RegisterResult register(String serverHost, String serverPort, RegisterRequest request) { //register request

        try {
            URL url = new URL("http://" + serverHost + ":" + serverPort + "/user/register");
            HttpURLConnection http = (HttpURLConnection)url.openConnection();
            http.setRequestMethod("POST");
            http.setDoOutput(true);
            http.connect();
            Gson gson = new Gson();

            String reqData = gson.toJson(request);
            OutputStream reqBody = http.getOutputStream();
            writeString(reqData, reqBody);
            reqBody.close();

            if (http.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStream respBody = http.getInputStream();
                String respData = readString(respBody);
                System.out.println(respData);
                RegisterResult result = gson.fromJson(respData, RegisterResult.class);

                System.out.println("Register successful.");

                return result;
            }
            else {
                InputStream respBody = http.getInputStream();
                String respData = readString(respBody);
                RegisterResult result = gson.fromJson(respData, RegisterResult.class);
                System.out.println("Register failed.");
                System.out.println("ERROR: " + http.getResponseMessage());
                return result;
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return new RegisterResult();
    }

    public static LoginResult login(String serverHost, String serverPort, LoginRequest request) {

        // This method shows how to send a POST request to a server

        try {
            URL url = new URL("http://" + serverHost + ":" + serverPort + "/user/login");
            HttpURLConnection http = (HttpURLConnection)url.openConnection();
            http.setRequestMethod("POST");
            http.setDoOutput(true);

            http.addRequestProperty("Accept", "application/json");

            http.connect();
            Gson gson = new Gson();
            String reqData = gson.toJson(request);

            OutputStream reqBody = http.getOutputStream();
            writeString(reqData, reqBody);
            reqBody.close();

            if (http.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStream respBody = http.getInputStream();
                // Extract JSON data from the HTTP response body
                String respData = readString(respBody);
                // Display the JSON data returned from the server
                System.out.println(respData);
                Reader reader = new InputStreamReader(respBody);
                LoginResult result = gson.fromJson(respData, LoginResult.class);

                System.out.println("login successful.");

                return result;
            }
            else {
                System.out.println("ERROR: " + http.getResponseMessage());
                InputStream respBody = http.getInputStream();
                String respData = readString(respBody);
                // Display the JSON data returned from the server
                System.out.println(respData);
                Reader reader = new InputStreamReader(respBody);
                LoginResult result = gson.fromJson(respData, LoginResult.class);

                System.out.println("login failed.");
                return result;
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return new LoginResult();
    }

    /*
        The readString method shows how to read a String from an InputStream.
    */
    private static String readString(InputStream is) throws IOException {
        StringBuilder sb = new StringBuilder();
        InputStreamReader sr = new InputStreamReader(is);
        char[] buf = new char[1024];
        int len;
        while ((len = sr.read(buf)) > 0) {
            sb.append(buf, 0, len);
        }
        return sb.toString();
    }

    /*
        The writeString method shows how to write a String to an OutputStream.
    */
    private static void writeString(String str, OutputStream os) throws IOException {
        OutputStreamWriter sw = new OutputStreamWriter(os);
        sw.write(str);
        sw.flush();
    }



}