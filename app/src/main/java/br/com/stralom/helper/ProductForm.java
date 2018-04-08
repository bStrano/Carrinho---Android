package br.com.stralom.helper;

import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;

import br.com.stralom.compras.R;
import br.com.stralom.entities.Product;

/**
 * Created by Bruno Strano on 02/01/2018.
 */

public class ProductForm {
    private final EditText name;
    private final EditText price;
    private final Spinner category;


    public ProductForm(View view){
        name = view.findViewById(R.id.form_productName);
        price = view.findViewById(R.id.form_productPrice);
        category = view.findViewById(R.id.form_productCategory);
    }

    public Product getProduct(){
        String name = this.name.getText().toString();
        double price = Double.parseDouble(this.price.getText().toString());
        String category = this.category.getSelectedItem().toString();

        return new Product(name,price,category);
    }
}
