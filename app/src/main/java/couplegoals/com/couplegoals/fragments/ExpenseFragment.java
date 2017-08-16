package couplegoals.com.couplegoals.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import couplegoals.com.couplegoals.R;


public class ExpenseFragment extends Fragment {

    CardView cardViewAddExpense,cardViewViewExpense,cardPersonelExpense;
    Fragment fragment;
    FragmentTransaction fragmentTransaction;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =inflater.inflate(R.layout.fragment_expense, container, false);
        cardViewAddExpense = (CardView) view.findViewById(R.id.cardViewAddExpense);
        cardViewAddExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Setting the Add expense fragment
                fragment = new AddExpenseFragment();
                if (fragment !=null){
                    fragmentTransaction = getFragmentManager().beginTransaction();
                    fragmentTransaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_left);
                    fragmentTransaction.replace(R.id.content_frame,fragment);
                    fragmentTransaction.commit();
                    fragmentTransaction.addToBackStack("base");

                }
            }
        });
        cardViewViewExpense = (CardView) view.findViewById(R.id.cardViewViewExpense);
        cardViewViewExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Setting the View expense fragment
                fragment = new ViewExpenseFragment();
                if (fragment !=null){
                    fragmentTransaction = getFragmentManager().beginTransaction();
                    fragmentTransaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_left);
                    fragmentTransaction.replace(R.id.content_frame,fragment);
                    fragmentTransaction.commit();
                    fragmentTransaction.addToBackStack("base");

                }
            }
        });
        cardPersonelExpense = (CardView) view.findViewById(R.id.cardPersonelExpense);
        cardPersonelExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Setting the personal fragment
                fragment = new PersonalExpenseFragment();
                if (fragment !=null){
                    fragmentTransaction = getFragmentManager().beginTransaction();
                    fragmentTransaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_left);
                    fragmentTransaction.replace(R.id.content_frame,fragment);
                    fragmentTransaction.commit();
                    fragmentTransaction.addToBackStack("base");

                }
            }
        });
        return view;
    }
}
