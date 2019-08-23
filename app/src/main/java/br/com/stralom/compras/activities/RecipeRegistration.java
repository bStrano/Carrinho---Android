package br.com.stralom.compras.activities;


import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import androidx.core.content.FileProvider;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import br.com.stralom.compras.adapters.IngredientsDisplayAdapter;
import br.com.stralom.compras.R;
import br.com.stralom.compras.dao.RecipeDAO;
import br.com.stralom.compras.entities.ItemRecipe;
import br.com.stralom.compras.entities.Recipe;

import br.com.stralom.compras.helper.RecipeForm;

public class RecipeRegistration extends AppCompatActivity {
    private static final String TAG = "RecipeRegistration";
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_INGREDIENTS = 2;
    private String mCurrentPhotoPath;

    private RecipeDAO recipeDAO;
    private RecipeForm recipeForm;
    private ArrayList<ItemRecipe> ingredients;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_recipe_registration);


        Button btn_newImage = findViewById(R.id.registration_recipe_btn_newImage);
        Button btn_newIngredient = findViewById(R.id.registration_recipe_btn_addIngredient);
        RecyclerView ingredientsView = findViewById(R.id.registration_recipe_ingredients);

        recipeDAO = new RecipeDAO(this);

        ingredients = new ArrayList<>();

        recipeForm = new RecipeForm(this, ingredients);

        // Toolbar
        Toolbar mToolbar = findViewById(R.id.registration_toolbar);

        mToolbar.setTitle(R.string.title_RecipeRegistration);
        Objects.requireNonNull(this).setSupportActionBar(mToolbar);
        //setHasOptionsMenu(true);

        // Toolbar - Back
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // ToolBar - Save


        // Add Image
        btn_newImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                capturePhotoIntent();
            }
        });
        // Add Ingredient
        btn_newIngredient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(),RecipeIngredients.class);
                if(ingredients == null){
                    ingredients = new ArrayList<>();
                }
                intent.putParcelableArrayListExtra("selectedIngredients",ingredients);



                startActivityForResult(intent,REQUEST_INGREDIENTS);
            }
        });
        // Update Ingredient List
        IngredientsDisplayAdapter ingredientsDisplayAdapter= new IngredientsDisplayAdapter(this, ingredients);
        ingredientsView.setAdapter(ingredientsDisplayAdapter);
        ingredientsView.setLayoutManager(new LinearLayoutManager(this));
        ingredientsView.setHasFixedSize(true);
    }



    private void capturePhotoIntent() {
        Intent intentCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intentCamera.resolveActivity(Objects.requireNonNull(this).getPackageManager()) != null) {
            File photo = null;
            try {
                photo = createImageFile();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (photo != null) {
                Uri photoURI = FileProvider.getUriForFile(this, "br.com.stralom.compras", photo);
                intentCamera.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(intentCamera, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Objects.requireNonNull(this).getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case REQUEST_IMAGE_CAPTURE:
                if (resultCode == Activity.RESULT_OK) {
                    recipeForm.loadImage(mCurrentPhotoPath);
                }
                break;
            case REQUEST_INGREDIENTS:
                if (resultCode == Activity.RESULT_OK){

                    if (data.getParcelableArrayListExtra("selectedIngredients") != null){
                        recipeForm.restoreButtonValidState();
                        this.ingredients.clear();
                        ArrayList<ItemRecipe> ingredients = data.getParcelableArrayListExtra("selectedIngredients");
                        this.ingredients.addAll(ingredients);
                    }
                }
                break;

        }


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.secundary_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.registration_save:
                recipeForm.getValidator().validate();
                Recipe recipe = recipeForm.getRecipe();
                if (recipeForm.isValidationSuccessful()) {
                    try {
                        if(registerRecipe(recipe)){
                            Intent data = new Intent();
                            data.putExtra("recipe",recipe);
                            setResult(RESULT_OK,data);
                            finish();


                        }
                    } catch (Exception e) {
                        Log.e(TAG,"Refatoração em progresso");
                    }
                }


        }

        return super.onOptionsItemSelected(item);
    }

    private boolean registerRecipe(Recipe recipe) throws Exception {
        if (recipeDAO.findByName(recipe.getName()) == null) {
            Long idRecipe = recipeDAO.add(recipeDAO.getContentValues(recipe));
            recipe.setId(idRecipe);
            for (ItemRecipe ingredient : ingredients) {
                ingredient.setRecipe(recipe);
                throw new Exception("Refatoração em progresso");
//                itemRecipeDAO.add(itemRecipeDAO.getContentValues(ingredient));
            }
            return true;
        } else {
            Toast.makeText(this, R.string.toast_recipe_alreadyRegistered, Toast.LENGTH_LONG).show();
            return false;
        }
    }


}

