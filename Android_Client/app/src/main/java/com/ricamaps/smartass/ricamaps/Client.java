package com.ricamaps.smartass.ricamaps;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client extends AsyncTask<String,Void,String> {

    Socket s;
    String message;
    Context c;
    Handler h = new Handler();
    String response = "";
    TextView textResponse;


    Client(Context c)
    {
        //this.textResponse = textResponse;
        this.c=c;

    }


    @Override
    protected String doInBackground(String... params) {
        try
        {
            message = params[0];
            System.out.println(message);
            response = "empty";

            System.out.println(response);
            s = new Socket("192.168.0.87",7900);
            PrintWriter pw = new PrintWriter(s.getOutputStream());
            BufferedReader in = new BufferedReader( new InputStreamReader(s.getInputStream()));

            pw.println(message);
            pw.flush();

            h.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(c,"Message sent",Toast.LENGTH_LONG).show();
                }
            });

            //pw.close();
            response = in.readLine();
            while(response != "" && response !="empty") {
                response = in.readLine();
                System.out.println(response);
            }
            System.out.println(response);
            pw.close();
            in.close();
            return response;

        }catch(IOException e)
        {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        //do stuff
        //textResponse.setText(response);
        super.onPostExecute(result);
    }

    private String getRes(String str) {
        //handle value
        return str;
    }

}
