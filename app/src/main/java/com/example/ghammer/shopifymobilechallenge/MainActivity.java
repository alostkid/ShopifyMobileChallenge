package com.example.ghammer.shopifymobilechallenge;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    private RecyclerView mainRecyclerView;
    private EditText setMatches, setPairs;
    private Button resetBoardButton;
    private RecyclerView.Adapter mainAdapter;
    private RecyclerView.LayoutManager mainLayoutManager;
    private int numberOfPairs = 6;
    private int matchCount = 2;

    private String url = "https://shopicruit.myshopify.com/admin/products.json?page=1&access_token=c32313df0d0ef512ca64d5b336a0d7c6";
    private List<String> imageUrls = new ArrayList<>();
    RequestQueue requestQueue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainRecyclerView = findViewById(R.id.mainRecyclerView);
        setMatches = findViewById(R.id.num_pair_edit);
        setPairs = findViewById(R.id.num_pics);
        resetBoardButton = findViewById(R.id.reset_button);

        resetBoardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String numPairString = setPairs.getText().toString();
                if (!numPairString.isEmpty())
                    numberOfPairs = Integer.parseInt(numPairString);
                String matchCountString = setMatches.getText().toString();
                if (!matchCountString.isEmpty())
                    matchCount = Integer.parseInt(matchCountString);

                if (matchCount < 2) {
                    setMatches.setText("2");
                    matchCount = 2;
                    Toast.makeText(MainActivity.this, "Need to match at least 2 cards.", Toast.LENGTH_LONG).show();
                }
                if (numberOfPairs < 2) {
                    setPairs.setText("2");
                    numberOfPairs = 2;
                    Toast.makeText(MainActivity.this, "Come on...don't do that.", Toast.LENGTH_LONG).show();
                }

                List gameList = imageUrls.subList(0, numberOfPairs);
                List stackedGameList = new ArrayList();
                for (int i = 0; i < matchCount;i++) {
                    stackedGameList.addAll(gameList);
                }
                Collections.shuffle(stackedGameList);
                mainAdapter = new MainRecyclerViewAdapter(MainActivity.this, numberOfPairs, stackedGameList, matchCount);
                mainRecyclerView.setAdapter(mainAdapter);
            }
        });

        mainLayoutManager = new GridLayoutManager(this, numberOfPairs/2);
        mainAdapter = new MainRecyclerViewAdapter(this, numberOfPairs, imageUrls, matchCount);
        mainRecyclerView.setLayoutManager(mainLayoutManager);
        mainRecyclerView.setAdapter(mainAdapter);

        requestQueue = Volley.newRequestQueue(this);
        StringRequest jsonArrayRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject responseJSON = new JSONObject(response);
                    JSONArray productsJSON = (JSONArray) responseJSON.get("products");
                    for(int i = 0; i < productsJSON.length();i++) {
                        JSONObject jsonObject = (JSONObject) productsJSON.get(i);
                        JSONArray productDetails = (JSONArray) jsonObject.get("images");
                        JSONObject productDetailsMap = (JSONObject) productDetails.get(0);
                        String imageSrc = productDetailsMap.getString("src");
                        imageUrls.add(imageSrc);
                    }
                    List gameList = imageUrls.subList(0, numberOfPairs);
                    gameList.addAll(gameList);
                    Collections.shuffle(gameList);
                    mainAdapter.notifyDataSetChanged();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.print(error);

            }
        });
        requestQueue.add(jsonArrayRequest);


    }
}
