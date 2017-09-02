package couplegoals.com.couplegoals.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import couplegoals.com.couplegoals.R;

public class ViewDividedCoupleExpenseFragment extends Fragment {

    //UI componenets

    Spinner spinnerSelectDateRange;
    ArrayAdapter<String> adapterSelectedDateRange;
    private static final String[] DATE_SELECTION_TYPE = new String[]{"All","Today","Current Month"};

    TextView expenseSummary,tvPaidNyOneTotal,tvPaidByTwoTotal,tvDifference;

    ListView listViewPaidByOne,listViewPaidByTwo;

    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
           
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View ViewDividedCoupleExpenseFragment = inflater.inflate(R.layout.fragment_view_divided_couple_expense, container, false);
        intializeUiComponents(ViewDividedCoupleExpenseFragment);
        return ViewDividedCoupleExpenseFragment;
    }

    private void intializeUiComponents(View viewDividedCoupleExpenseFragment) {

        adapterSelectedDateRange = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1,DATE_SELECTION_TYPE);
        spinnerSelectDateRange = (Spinner) viewDividedCoupleExpenseFragment.findViewById(R.id.spinnerSelectDateRange);
        spinnerSelectDateRange.setAdapter(adapterSelectedDateRange);

        expenseSummary = (TextView) viewDividedCoupleExpenseFragment.findViewById(R.id.expenseSummary);
        tvPaidNyOneTotal = (TextView) viewDividedCoupleExpenseFragment.findViewById(R.id.tvPaidNyOneTotal);
        tvPaidByTwoTotal = (TextView) viewDividedCoupleExpenseFragment.findViewById(R.id.tvPaidByTwoTotal);
        expenseSummary = (TextView) viewDividedCoupleExpenseFragment.findViewById(R.id.expenseSummary);
        tvDifference = (TextView) viewDividedCoupleExpenseFragment.findViewById(R.id.tvDifference);
        listViewPaidByOne = (ListView) viewDividedCoupleExpenseFragment.findViewById(R.id.listViewPaidByOne);
        listViewPaidByTwo = (ListView) viewDividedCoupleExpenseFragment.findViewById(R.id.listViewPaidByTwo);

    }
}
