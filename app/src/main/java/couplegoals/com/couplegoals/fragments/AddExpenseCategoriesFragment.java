package couplegoals.com.couplegoals.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import couplegoals.com.couplegoals.R;
import couplegoals.com.couplegoals.adapter.CategoryListAdapter;
import couplegoals.com.couplegoals.database.DatabaseValues;
import couplegoals.com.couplegoals.model.Category;


public class AddExpenseCategoriesFragment extends Fragment {

    EditText etCategoryName;
    Button btSaveCategoryName;
    ListView listViewExpenseCategories;

    String sCategoryName,sCategoryId;

    //
    DatabaseReference databaseReference;
    List<Category> categoryList;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View viewAddExpenseCategoriesFragment = inflater.inflate(R.layout.fragment_add_expense_categories, container, false);
        initializeUIComponents(viewAddExpenseCategoriesFragment);

        btSaveCategoryName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveCategoryToDatabase();
            }
        });

        loadCategoriesFromDatabase();
        return viewAddExpenseCategoriesFragment;
    }

    private void loadCategoriesFromDatabase() {
        databaseReference = DatabaseValues.getCategoryReference();
        databaseReference.keepSynced(true);
        final CategoryListAdapter categoryListAdapter = new CategoryListAdapter(getActivity(),categoryList);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()){
                    categoryList.clear();
                    for (DataSnapshot  expenseDetailSnapshot : dataSnapshot.getChildren()){
                        Category categoryDetails = expenseDetailSnapshot.getValue(Category.class);
                        if (categoryDetails.getsCoupleName()!= null){
                            if (categoryDetails.getsCoupleName().equalsIgnoreCase(DatabaseValues.getCOUPLENAME())){
                                categoryList.add(0, categoryDetails);
                                categoryListAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                    //Collections.reverse(expenseList);
                    listViewExpenseCategories.post(new Runnable() {
                        @Override
                        public void run() {
                            listViewExpenseCategories.smoothScrollToPosition(0);
                        }
                    });

                    listViewExpenseCategories.setAdapter(categoryListAdapter);
                    categoryListAdapter.notifyDataSetChanged();
                }
                else {
                    Toast.makeText(getActivity(),"No expense exist",Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void saveCategoryToDatabase() {
        getUserEnteredValues();
        if(validateUserEnterData()){
            processCategoryNameToDb();

        }
    }

    private void processCategoryNameToDb() {
        DatabaseReference databaseReference = DatabaseValues.getCategoryReference();
        sCategoryId = databaseReference.push().getKey();
        Category category = new Category(sCategoryId, DatabaseValues.getCOUPLENAME(),sCategoryName);
        databaseReference.child(sCategoryId).setValue(category).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(getActivity(),"Category added",Toast.LENGTH_SHORT).show();
                    resetUiComponents();
                }
            }
        });

    }

    private void resetUiComponents() {
        etCategoryName.setText("");
    }

    private boolean validateUserEnterData() {
        boolean isValid = true;
        View focusView = null;
        if(sCategoryName.isEmpty()){
            etCategoryName.setError(getString(R.string.categoryerror));
            isValid = false;
            focusView = etCategoryName;
        }
        return isValid;
    }


    private void getUserEnteredValues() {
        sCategoryName = etCategoryName.getText().toString().trim();
    }

    private void initializeUIComponents(View viewAddExpenseCategoriesFragment) {
        etCategoryName = (EditText) viewAddExpenseCategoriesFragment.findViewById(R.id.etCategoryName);
        btSaveCategoryName = (Button) viewAddExpenseCategoriesFragment.findViewById(R.id.btSaveCategoryName);
        listViewExpenseCategories = (ListView) viewAddExpenseCategoriesFragment.findViewById(R.id.listViewExpenseCategories);
        categoryList = new ArrayList<>();
    }

}
