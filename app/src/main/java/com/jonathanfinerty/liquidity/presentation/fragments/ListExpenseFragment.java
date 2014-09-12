package com.jonathanfinerty.liquidity.presentation.fragments;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.jonathanfinerty.liquidity.R;
import com.jonathanfinerty.liquidity.loaders.ExpensesViewModelLoader;
import com.jonathanfinerty.liquidity.presentation.ExpenseViewModelAdapter;
import com.jonathanfinerty.liquidity.presentation.SwipeDetector;
import com.jonathanfinerty.liquidity.presentation.activities.ExpenseActivity;
import com.jonathanfinerty.liquidity.presentation.viewmodel.ExpenseViewModel;
import com.jonathanfinerty.liquidity.services.DeleteExpenseService;

import java.util.ArrayList;

public class ListExpenseFragment extends Fragment
                                 implements LoaderManager.LoaderCallbacks<ArrayList<ExpenseViewModel>> {

    private static final String TAG = "List Expense Fragment";
    private ExpenseViewModelAdapter expenseViewModelAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View listFragmentView = inflater.inflate(R.layout.fragment_list_expenses, container, false);

        final ListView expenseList = (ListView) listFragmentView.findViewById(R.id.listview_expenses);

        expenseViewModelAdapter = new ExpenseViewModelAdapter(listFragmentView.getContext(), new ArrayList<ExpenseViewModel>());

        expenseList.setAdapter(expenseViewModelAdapter);

        SwipeDetector swipeDetector = new SwipeDetector(
                        expenseList,
                        new SwipeDetector.DismissCallback() {
                            public void onDismiss(ListView listView, int position) {

                                ExpenseViewModel expenseViewModel = expenseViewModelAdapter.getItem(position);

                                expenseViewModelAdapter.remove(expenseViewModel);
                                expenseViewModelAdapter.notifyDataSetChanged();

                                Intent deleteExpense = new Intent(getActivity(), DeleteExpenseService.class);
                                deleteExpense.putExtra(DeleteExpenseService.EXPENSE_ID_EXTRA, expenseViewModel.getId());
                                getActivity().startService(deleteExpense);
                            }
                        });

        expenseList.setOnTouchListener(swipeDetector);
        expenseList.setOnScrollListener(swipeDetector.makeScrollListener());
        expenseList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ExpenseViewModel expenseViewModel = expenseViewModelAdapter.getItem(position);
                Intent expenseActivity = new Intent(getActivity(), ExpenseActivity.class);
                expenseActivity.putExtra(ExpenseActivity.EXPENSE_ID_EXTRA, expenseViewModel.getId());
                startActivity(expenseActivity);
            }
        });



        getLoaderManager().initLoader(0, null, this);

        // todo: put functionality to load expenses by budget period back in
        /*View loadMoreButtonView = inflater.inflate(R.layout.fragment_load_more_button, expenseList, false);
        Button button = (Button) loadMoreButtonView.findViewById(R.id.button_load_more);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentPage++;
                LoadExpenses(currentPage);
            }
        });

        expenseList.addFooterView(loadMoreButtonView);*/



        return listFragmentView;
    }


    @Override
    public Loader<ArrayList<ExpenseViewModel>> onCreateLoader(int id, Bundle args) {
        Log.d(TAG, "ExpenseViewModel Loader Created");
        return new ExpensesViewModelLoader(getActivity());
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<ExpenseViewModel>> loader, ArrayList<ExpenseViewModel> data) {
        Log.d(TAG, "ExpenseViewModel Loader Finished");
        expenseViewModelAdapter.clear();
        expenseViewModelAdapter.addAll(data);
        expenseViewModelAdapter.notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<ExpenseViewModel>> loader) {
        Log.d(TAG, "ExpenseViewModel Loader Reset");
    }
}
