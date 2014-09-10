package com.jonathanfinerty.liquidity.operations;

import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.jonathanfinerty.liquidity.persistence.BudgetContract;

public class SetBudgetOperation extends BaseOperation {
    public static final String BUDGET_AMOUNT_EXTRA = "budget amount";
    public static final String DATE_EXTRA = "budget date";

    private int budgetAmount;
    private int budgetDate;

    public SetBudgetOperation() {
        super("Set Budget Operation");
    }

    @Override
    protected boolean ValidateIntentExtras(Intent intent) {

        int defaultBudgetAmount = -1;
        budgetAmount = intent.getIntExtra(BUDGET_AMOUNT_EXTRA, defaultBudgetAmount);

        int defaultDateExtra = -1;
        budgetDate = intent.getIntExtra(DATE_EXTRA, defaultDateExtra);

        if (budgetDate <= 0 && budgetAmount <= 0) {
            Log.e("SetBudgetOperation", "Both budget date and amount were not set");
            return false;
        }

        return true;
    }

    @Override
    protected void ExecuteOperation() {

        Uri budgetUri = BudgetContract.URI;

        ContentValues budgetValues = new ContentValues();

        if (budgetAmount > 0) {
            budgetValues.put(BudgetContract.COLUMN_NAME_AMOUNT, budgetAmount);
        }

        if (budgetDate > 0){
            budgetValues.put(BudgetContract.COLUMN_NAME_DATE, budgetDate);
        }

        this.getContentResolver().update(budgetUri, budgetValues, null, null);
    }
}
