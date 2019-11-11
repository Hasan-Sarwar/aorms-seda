package com.example.aorms_seda;
import android.os.Bundle;

import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ItemActivity extends AppCompatActivity {

    private RecyclerView ingredientRecyclerView;
    private List<Ingredient> IngredientList;
    private IngredientAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ingredient);

        this.IngredientList = getDummyData();
        Log.e("onCreate: ", "size: " + this.IngredientList.size());
        ingredientRecyclerView = (RecyclerView) findViewById(R.id.IngredientsList);
        ingredientRecyclerView.addItemDecoration(new DividerItemDecoration(ItemActivity.this, LinearLayoutManager.HORIZONTAL));
        adapter = new IngredientAdapter(this, this.IngredientList);
        Log.e("onCreate: ", "size: " + adapter.getItemCount() );
        LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(ItemActivity.this, LinearLayoutManager.HORIZONTAL, false);
        ingredientRecyclerView.setLayoutManager(horizontalLayoutManager);
        ingredientRecyclerView.setAdapter(adapter);

    }

    private List<Ingredient> getDummyData(){
        List<Ingredient> temp = new ArrayList<Ingredient>();
        Ingredient ingredient;
        String name;
        for (int i=0; i<7; i++){
            name = "Ingredient " + (i+1);
            ingredient = new Ingredient(name);
            temp.add(ingredient);
            Log.e("DATA", "getDummyData: "+ name);
        }

        return temp;
    }
}
