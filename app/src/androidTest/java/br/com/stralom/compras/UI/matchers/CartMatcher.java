package br.com.stralom.compras.UI.matchers;

import android.support.test.espresso.matcher.BoundedMatcher;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.TextView;

import org.hamcrest.Description;
import org.hamcrest.Matcher;

import br.com.stralom.adapters.CartAdapter;
import br.com.stralom.compras.R;

public class CartMatcher {

    public static Matcher<RecyclerView.ViewHolder> withCartHolder(final String text) {
        return new BoundedMatcher<RecyclerView.ViewHolder, CartAdapter.cartViewHolder>(CartAdapter.cartViewHolder.class) {
            @Override
            public void describeTo(Description description) {
                description.appendText("No ViewHolder found [" + text + "]");
            }

            @Override
            protected boolean matchesSafely(CartAdapter.cartViewHolder item) {
                TextView name = item.itemView.findViewById(R.id.itemCart_itemList_name);
                if (name == null) {
                    return false;
                }
                return name.getText().toString().contains(text);
            }
        };
    }

    public static Matcher<RecyclerView.ViewHolder> withCartHolder(final String name, final String amount) {
        return new BoundedMatcher<RecyclerView.ViewHolder, CartAdapter.cartViewHolder>(CartAdapter.cartViewHolder.class) {
            @Override
            public void describeTo(Description description) {
                description.appendText("No ViewHolder found [" + name + "/" + amount + "]");
            }

            @Override
            protected boolean matchesSafely(CartAdapter.cartViewHolder item) {
                TextView nameView = item.itemView.findViewById(R.id.itemCart_itemList_name);
                TextView amountView = item.itemView.findViewById(R.id.itemCart_itemList_amount);
                if ((name == null)) {
                    return false;
                }
                Log.e("TESTE", amountView.getText().toString());
                if(amount == null){
                    return (nameView.getText().toString().equals(name));
                }else{
                    Log.e("TESTEX", String.valueOf(nameView.getText().toString().equals(name)));
                    Log.e("TESTEX",nameView.getText().toString());
                    Log.e("TESTEX",name);
                    Log.e("TESTEX", String.valueOf(amountView.getText().toString().equals(amount)));
                    Log.e("TESTEX",amountView.getText().toString());
                    Log.e("TESTEX",amount);
                    return (nameView.getText().toString().equals(name) && amountView.getText().toString().equals(amount +"x"));
                }

            }
        };
    }
}