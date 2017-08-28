package couplegoals.com.couplegoals.adapter;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import couplegoals.com.couplegoals.R;
import couplegoals.com.couplegoals.model.Category;

/**
 * Created by Rini Banerjee on 28-08-2017.
 */

public class CategoryListAdapter extends ArrayAdapter<Category> {
    private Activity context;
    private List<Category> categoryList;

    public CategoryListAdapter(Activity context, List<Category> categoryList) {
        super(context, R.layout.card_layout_view_categories, categoryList);
        this.context = context;
        this.categoryList = categoryList;
    }


    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        LayoutInflater layoutInflater = context.getLayoutInflater();
        final View listViewItems = layoutInflater.inflate(R.layout.card_layout_view_categories, null, true);

        TextView textViewCategoryName = (TextView) listViewItems.findViewById(R.id.textViewCategoryName);
        ImageButton ibDeleteCategory = (ImageButton) listViewItems.findViewById(R.id.ibDeleteCategory);

        Category category = categoryList.get(position);

        textViewCategoryName.setText(category.getsCategoryName());

        ibDeleteCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context,"Deleted",Toast.LENGTH_SHORT)
                        .show();
            }
        });

        return listViewItems;
    }
}